package me.ichun.mods.guilttrip.common;

import me.ichun.mods.guilttrip.client.core.ConfigClient;
import me.ichun.mods.guilttrip.client.core.EventHandlerClient;
import me.ichun.mods.guilttrip.common.core.ConfigCommon;
import me.ichun.mods.guilttrip.common.core.EventHandlerServer;
import me.ichun.mods.guilttrip.common.packet.PacketKills;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GuiltTrip.MOD_ID)
public class GuiltTrip //TODO add iChunUtil dependency
{
    public static final String MOD_ID = "guilttrip";
    public static final String MOD_NAME = "Guilt Trip";
    public static final String PROTOCOL = "1";

    public static final Logger LOGGER = LogManager.getLogger();

    public static ConfigClient configClient;
    public static ConfigCommon configCommon;

    public static EventHandlerClient eventHandlerClient;
    public static EventHandlerServer eventHandlerServer;

    public static PacketChannel channel;

    public GuiltTrip()
    {
        configCommon = new ConfigCommon().init();

        MinecraftForge.EVENT_BUS.register(eventHandlerServer = new EventHandlerServer());

        channel = new PacketChannel(new ResourceLocation(MOD_ID, "channel"), PROTOCOL, PacketKills.class);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            configClient = new ConfigClient().init();

            MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClient());

            FMLJavaModLoadingContext.get().getModEventBus().addListener(eventHandlerClient::addGuiltTripLayer);

            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> me.ichun.mods.ichunutil.client.core.EventHandlerClient::getConfigGui);
        });
    }
}
