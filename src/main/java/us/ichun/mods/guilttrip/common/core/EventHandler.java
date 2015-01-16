package us.ichun.mods.guilttrip.common.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.guilttrip.common.GuiltTrip;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;

import java.util.ArrayList;

public class EventHandler
{
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(!event.entityLiving.getEntityWorld().isRemote && event.source.getEntity() instanceof EntityPlayerMP && event.entityLiving != event.source.getEntity()) // player killed something, server side
        {
            EntityPlayerMP player = (EntityPlayerMP)event.source.getEntity();

            EntityLivingBase living = event.entityLiving;

            if(GuiltTrip.proxy.tickHandlerServer.addPlayerKill(player, living))
            {
                GuiltTrip.proxy.tickHandlerServer.updatePlayersOnKill(player.getName(), null);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
//        NBTTagCompound tag = EntityHelperBase.getPlayerPersistentData(event.player, "GuiltTripSave");
//        int size = tag.getInteger("size");
//        for(int i = 0; i < size; i++)
//        {
//            ArrayList<KillInfo> kills = GuiltTrip.proxy.tickHandlerServer.playerKills.get(event.player.getName());
//            if(kills == null)
//            {
//                kills = new ArrayList<KillInfo>();
//                GuiltTrip.proxy.tickHandlerServer.playerKills.put(event.player.getName(), kills);
//            }
//
//            kills.add(KillInfo.createKillInfoFromTag(tag.getCompoundTag("kill_" + i)));
//        }
        GuiltTrip.proxy.tickHandlerServer.updatePlayersOnKill(event.player.getName(), null);
        GuiltTrip.proxy.tickHandlerServer.updatePlayersOnKill(null, event.player.getName());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        GuiltTrip.proxy.tickHandlerServer.playerKills.remove(event.player.getName());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        GuiltTrip.proxy.tickHandlerClient.playerKills.clear();
    }
}
