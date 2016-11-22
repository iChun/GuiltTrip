package us.ichun.mods.guilttrip.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import us.ichun.mods.guilttrip.client.layer.LayerGuiltTrip;
import us.ichun.mods.guilttrip.common.core.CommonProxy;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();

        tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);
    }
}
