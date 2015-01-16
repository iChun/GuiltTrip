package us.ichun.mods.guilttrip.common;

import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.ichun.mods.guilttrip.common.core.CommonProxy;
import us.ichun.mods.ichunutil.common.core.config.Config;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.config.IConfigUser;
import us.ichun.mods.ichunutil.common.core.network.PacketChannel;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;

@Mod(modid = "GuiltTrip", name = "GuiltTrip",
        version = GuiltTrip.version,
        dependencies = "required-after:iChunUtil@["+ iChunUtil.versionMC + ".0.0,)"
            )
public class GuiltTrip
        implements IConfigUser
{
    public static final String version = iChunUtil.versionMC + ".0.0";

    private static final Logger logger = LogManager.getLogger("GuiltTrip");

    @Mod.Instance("GuiltTrip")
    public static GuiltTrip instance;

    @SidedProxy(clientSide = "us.ichun.mods.guilttrip.client.core.ClientProxy", serverSide = "us.ichun.mods.guilttrip.common.core.CommonProxy")
    public static CommonProxy proxy;

    public static Config config;

    public static PacketChannel channel;

    @Override
    public boolean onConfigChange(Config cfg, Property prop) { return true; }

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.createConfig(event.getSuggestedConfigurationFile(), "guilttrip", "GuiltTrip", logger, instance);

        config.createIntProperty("maxGhosts", true, false, 20, 1, Integer.MAX_VALUE);
        config.createIntProperty("maxGhostAge", true, false, 24000, 0, Integer.MAX_VALUE);
        config.createIntProperty("animalGuiltMultiplier", true, false, 1, 1, Integer.MAX_VALUE);

        config.createIntBoolProperty("allKills", true, false, true);
        config.createIntBoolProperty("playerKills", true, false, true);
        config.createIntBoolProperty("animalKills", true, false, true);
        config.createIntBoolProperty("bossKills", true, false, true);
        config.createIntBoolProperty("mobKills", true, false, true);

        config.setCurrentCategory("clientOnly", "ichun.config.cat.clientOnly.name", "ichun.config.cat.clientOnly.comment");
        config.createIntBoolProperty("ghostWalkAnim", true, false, true);
        config.createIntBoolProperty("ghostLookAnim", true, false, true);

        proxy.preInit();

        ModVersionChecker.register_iChunMod(new ModVersionInfo("GuiltTrip", iChunUtil.versionOfMC, version, false));
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

    public static void console(String s, boolean warning)
    {
        StringBuilder sb = new StringBuilder();
        logger.log(warning ? Level.WARN : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
    }
}
