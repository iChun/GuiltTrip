package us.ichun.mods.guilttrip.common.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import us.ichun.mods.guilttrip.client.core.TickHandlerClient;
import us.ichun.mods.guilttrip.common.GuiltTrip;
import us.ichun.mods.guilttrip.common.packet.PacketKills;
import us.ichun.mods.ichunutil.common.core.network.ChannelHandler;

public class CommonProxy
{
    public void preInit()
    {
        EventHandler handler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);

        tickHandlerServer = new TickHandlerServer();
        FMLCommonHandler.instance().bus().register(tickHandlerServer);

        GuiltTrip.channel = ChannelHandler.getChannelHandlers("GuiltTrip", PacketKills.class);
    }

    public void init(){}

    public void postInit(){}

    public TickHandlerClient tickHandlerClient;
    public TickHandlerServer tickHandlerServer;
}
