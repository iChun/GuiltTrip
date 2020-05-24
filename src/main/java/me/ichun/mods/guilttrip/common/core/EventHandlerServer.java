package me.ichun.mods.guilttrip.common.core;

import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.guilttrip.common.packet.PacketKills;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventHandlerServer
{
    public HashMap<String, ArrayList<KillInfo>> playerKills = new HashMap<>();

    @SubscribeEvent
    public void onServerShuttingDown(FMLServerStoppingEvent event)
    {
        playerKills.clear();
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            for(ArrayList<KillInfo> kills : playerKills.values())
            {
                for(int i = kills.size() - 1; i >= 0; i--)
                {
                    KillInfo info = kills.get(i);
                    info.update();
                    if(info.maxAge != 0 && info.age > info.maxAge + 100)
                    {
                        kills.remove(i);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(!event.getEntityLiving().getEntityWorld().isRemote && event.getSource().getTrueSource() instanceof ServerPlayerEntity && event.getEntityLiving() != event.getSource().getTrueSource()) // player killed something, server side
        {
            ServerPlayerEntity player = (ServerPlayerEntity)event.getSource().getTrueSource();

            LivingEntity living = event.getEntityLiving();

            if(GuiltTrip.eventHandlerServer.addPlayerKill(player, living))
            {
                GuiltTrip.eventHandlerServer.updatePlayersOnKill(player.getName().getUnformattedComponentText(), null);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        CompoundNBT tag = EntityHelper.getPlayerPersistentData(event.getPlayer(), "GuiltTripSave");
        int size = tag.getInt("size");
        for(int i = 0; i < size; i++)
        {
            ArrayList<KillInfo> kills = GuiltTrip.eventHandlerServer.playerKills.computeIfAbsent(event.getPlayer().getName().getUnformattedComponentText(), k -> new ArrayList<>());
            kills.add(KillInfo.createKillInfoFromTag(tag.getCompound("kill_" + i)));
        }
        GuiltTrip.eventHandlerServer.updatePlayersOnKill(event.getPlayer().getName().getUnformattedComponentText(), null);
        GuiltTrip.eventHandlerServer.updatePlayersOnKill(null, event.getPlayer().getName().getUnformattedComponentText());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        GuiltTrip.eventHandlerServer.playerKills.remove(event.getPlayer().getName().getUnformattedComponentText());
    }

    public boolean addPlayerKill(PlayerEntity player, LivingEntity killed)//returns true if the kill was added
    {
        if(!GuiltTrip.configCommon.allKills)
        {
            if(killed instanceof PlayerEntity && !GuiltTrip.configCommon.playerKills
                    || killed instanceof AgeableEntity && !(killed instanceof IMob) && !GuiltTrip.configCommon.animalKills
                    || killed instanceof CreatureEntity && !(killed instanceof AgeableEntity && !(killed instanceof IMob)) && !GuiltTrip.configCommon.mobKills)
            {
                return false;
            }
        }
        ArrayList<KillInfo> kills = playerKills.computeIfAbsent(player.getName().getUnformattedComponentText(), k -> new ArrayList<>());
        KillInfo info = KillInfo.createKillInfoFromEntity(killed);
        if(info != null)
        {
            if(killed instanceof AgeableEntity && !(killed instanceof IMob))
            {
                float mag = (float)GuiltTrip.configCommon.animalGuiltMultiplier;
                if(killed.hasCustomName())
                {
                    mag *= 1.5F;
                }
                if(killed.isChild())
                {
                    mag *= 1.5F;
                }
                info.maxAge = (int)((float)info.maxAge * mag);
            }
            if(killed instanceof PlayerEntity && GuiltTrip.configCommon.playerKillsLasting)
            {
                info.maxAge = 0;
            }
            kills.add(info);
            while(kills.size() > GuiltTrip.configCommon.maxGhosts)
            {
                kills.remove(0);
            }

            CompoundNBT tag = EntityHelper.getPlayerPersistentData(player, "GuiltTripSave");
            tag.putInt("size", kills.size());
            for(int i = 0; i < kills.size(); i++)
            {
                tag.put("kill_" + i, kills.get(i).getTag());
            }
            return true;
        }
        return false;
    }

    /**
     * Update clients on a kill
     *
     * @param name   - Which player to reference, null for all
     * @param direct - Which player to send to, null for all
     */
    public void updatePlayersOnKill(String name, String direct)
    {
        ArrayList<PacketKills> killsToSend = new ArrayList<>();
        if(name != null)
        {
            if(playerKills.get(name) != null)
            {
                killsToSend.add(new PacketKills(name, playerKills.get(name)));
            }
        }
        else
        {
            for(Map.Entry<String, ArrayList<KillInfo>> e : playerKills.entrySet())
            {
                if(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(e.getKey()) != null)
                {
                    killsToSend.add(new PacketKills(e.getKey(), e.getValue()));
                }
            }
        }
        for(PacketKills kills : killsToSend)
        {
            if(direct != null)
            {
                ServerPlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(direct);
                if(player != null)
                {
                    GuiltTrip.channel.sendTo(kills, player);
                }
            }
            else
            {
                GuiltTrip.channel.sendTo(kills, PacketDistributor.ALL.noArg());
            }
        }
    }
}
