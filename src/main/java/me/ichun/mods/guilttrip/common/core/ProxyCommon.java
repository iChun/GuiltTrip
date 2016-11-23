package me.ichun.mods.guilttrip.common.core;

import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.guilttrip.common.packet.PacketKills;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import net.minecraftforge.common.MinecraftForge;

public class ProxyCommon
{
    public void preInit()
    {
        GuiltTrip.eventHandlerServer = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(GuiltTrip.eventHandlerServer);

        GuiltTrip.channel = new PacketChannel("GuiltTrip", PacketKills.class);
    }
}
