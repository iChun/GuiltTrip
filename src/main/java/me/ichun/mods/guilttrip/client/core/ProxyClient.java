package me.ichun.mods.guilttrip.client.core;

import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.guilttrip.common.core.ProxyCommon;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        GuiltTrip.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(GuiltTrip.eventHandlerClient);
    }
}
