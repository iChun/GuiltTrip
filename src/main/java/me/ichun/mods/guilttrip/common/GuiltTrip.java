package me.ichun.mods.guilttrip.common;

import me.ichun.mods.guilttrip.client.core.EventHandlerClient;
import me.ichun.mods.guilttrip.common.core.Config;
import me.ichun.mods.guilttrip.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.core.Logger;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import me.ichun.mods.guilttrip.common.core.EventHandlerServer;

@Mod(modid = GuiltTrip.MOD_ID, name = GuiltTrip.MOD_NAME,
        version = GuiltTrip.VERSION,
        guiFactory = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_MAJOR + ".0.0," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0)",
        acceptableRemoteVersions = "[" + iChunUtil.VERSION_MAJOR +".0.0," + iChunUtil.VERSION_MAJOR + ".1.0)"
)
public class GuiltTrip
{
    public static final String VERSION = iChunUtil.VERSION_MAJOR + ".0.0";
    public static final String MOD_NAME = "GuiltTrip";
    public static final String MOD_ID = "guilttrip";

    public static final Logger LOGGER = Logger.createLogger(MOD_NAME);

    @Mod.Instance(MOD_ID)
    public static GuiltTrip instance;

    @SidedProxy(clientSide = "me.ichun.mods.guilttrip.client.core.ProxyClient", serverSide = "me.ichun.mods.guilttrip.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    public static Config config;

    public static PacketChannel channel;

    public static EventHandlerClient eventHandlerClient;
    public static EventHandlerServer eventHandlerServer;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInit();

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, false));
    }

    @Mod.EventHandler
    public void serverShuttingDown(FMLServerStoppingEvent event)
    {
        eventHandlerServer.playerKills.clear();
    }
}
