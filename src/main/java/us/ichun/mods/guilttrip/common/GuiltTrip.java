package us.ichun.mods.guilttrip.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import us.ichun.mods.guilttrip.common.core.CommonProxy;
import us.ichun.mods.guilttrip.common.core.Config;
import us.ichun.mods.ichunutil.common.core.Logger;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.network.PacketChannel;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;

@Mod(modid = GuiltTrip.MOD_NAME, name = GuiltTrip.MOD_NAME,
        version = GuiltTrip.VERSION,
        guiFactory = "us.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:iChunUtil@[" + iChunUtil.versionMC +".5.0," + (iChunUtil.versionMC + 1) + ".0.0)",
        acceptableRemoteVersions = "[" + iChunUtil.versionMC +".1.0," + iChunUtil.versionMC + ".2.0)"
            )
public class GuiltTrip
{
    public static final String MOD_NAME = "GuiltTrip";
    public static final String VERSION = iChunUtil.versionMC + ".1.0";

    public static final Logger logger = Logger.createLogger(MOD_NAME);

    @Mod.Instance(MOD_NAME)
    public static GuiltTrip instance;

    @SidedProxy(clientSide = "us.ichun.mods.guilttrip.client.core.ClientProxy", serverSide = "us.ichun.mods.guilttrip.common.core.CommonProxy")
    public static CommonProxy proxy;

    public static Config config;

    public static PacketChannel channel;

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        config = (Config)ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInit();

        ModVersionChecker.register_iChunMod(new ModVersionInfo(MOD_NAME, iChunUtil.versionOfMC, VERSION, false));
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @Mod.EventHandler
    public void serverShuttingDown(FMLServerStoppingEvent event)
    {
        proxy.tickHandlerServer.playerKills.clear();
    }
}
