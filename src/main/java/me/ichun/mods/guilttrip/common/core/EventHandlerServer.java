package me.ichun.mods.guilttrip.common.core;

import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.guilttrip.common.packet.PacketKills;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventHandlerServer
{
    public HashMap<String, ArrayList<KillInfo>> playerKills = new HashMap<>();

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
        if(!event.getEntityLiving().getEntityWorld().isRemote && event.getSource().getTrueSource() instanceof EntityPlayerMP && event.getEntityLiving() != event.getSource().getTrueSource()) // player killed something, server side
        {
            EntityPlayerMP player = (EntityPlayerMP)event.getSource().getTrueSource();

            EntityLivingBase living = event.getEntityLiving();

            if(GuiltTrip.eventHandlerServer.addPlayerKill(player, living))
            {
                GuiltTrip.eventHandlerServer.updatePlayersOnKill(player.getName(), null);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        NBTTagCompound tag = EntityHelper.getPlayerPersistentData(event.player, "GuiltTripSave");
        int size = tag.getInteger("size");
        for(int i = 0; i < size; i++)
        {
            ArrayList<KillInfo> kills = GuiltTrip.eventHandlerServer.playerKills.get(event.player.getName());
            if(kills == null)
            {
                kills = new ArrayList<>();
                GuiltTrip.eventHandlerServer.playerKills.put(event.player.getName(), kills);
            }

            kills.add(KillInfo.createKillInfoFromTag(tag.getCompoundTag("kill_" + i)));
        }
        GuiltTrip.eventHandlerServer.updatePlayersOnKill(event.player.getName(), null);
        GuiltTrip.eventHandlerServer.updatePlayersOnKill(null, event.player.getName());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        GuiltTrip.eventHandlerServer.playerKills.remove(event.player.getName());
    }

    public boolean addPlayerKill(EntityPlayer player, EntityLivingBase killed)//returns true if the kill was added
    {
        if(GuiltTrip.config.allKills != 1)
        {
            if(killed instanceof EntityPlayer && GuiltTrip.config.playerKills != 1 || killed instanceof EntityAgeable && !(killed instanceof IMob) && GuiltTrip.config.animalKills != 1 || killed instanceof EntityCreature && !(killed instanceof EntityAgeable && !(killed instanceof IMob)) && GuiltTrip.config.mobKills != 1)
            {
                return false;
            }
        }
        ArrayList<KillInfo> kills = playerKills.get(player.getName());
        if(kills == null)
        {
            kills = new ArrayList<>();
            playerKills.put(player.getName(), kills);
        }
        KillInfo info = KillInfo.createKillInfoFromEntity(killed);
        if(info != null)
        {
            if(killed instanceof EntityAgeable && !(killed instanceof IMob))
            {
                float mag = GuiltTrip.config.animalGuiltMultiplier;
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
            if(killed instanceof EntityPlayer && GuiltTrip.config.playerKillsLasting == 1)
            {
                info.maxAge = 0;
            }
            kills.add(info);
            while(kills.size() > GuiltTrip.config.maxGhosts)
            {
                kills.remove(0);
            }

            NBTTagCompound tag = EntityHelper.getPlayerPersistentData(player, "GuiltTripSave");
            tag.setInteger("size", kills.size());
            for(int i = 0; i < kills.size(); i++)
            {
                tag.setTag("kill_" + i, kills.get(i).getTag());
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
                if(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(e.getKey()) != null)
                {
                    killsToSend.add(new PacketKills(e.getKey(), e.getValue()));
                }
            }
        }
        for(PacketKills kills : killsToSend)
        {
            if(direct != null)
            {
                EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(direct);
                if(player != null)
                {
                    GuiltTrip.channel.sendTo(kills, player);
                }
            }
            else
            {
                GuiltTrip.channel.sendToAll(kills);
            }
        }
    }
}
