package us.ichun.mods.guilttrip.common.core;

import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp
    @IntMinMax(min = 1)
    public int maxGhosts = 20;

    @ConfigProp
    @IntMinMax(min = 0)
    public int maxGhostAge = 24000;

    @ConfigProp
    @IntMinMax(min = 1)
    public int animalGuiltMultiplier = 1;

    @ConfigProp
    @IntBool
    public int allKills = 1;

    @ConfigProp
    @IntBool
    public int playerKills = 1;

    @ConfigProp
    @IntBool
    public int playerKillsLasting = 1;

    @ConfigProp
    @IntBool
    public int animalKills = 1;

    @ConfigProp
    @IntBool
    public int bossKills = 1;

    @ConfigProp
    @IntBool
    public int mobKills = 1;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int ghostWalkAnim = 1;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int ghostLookAnim = 1;

    public Config(File file, String... unhide)
    {
        super(file, unhide);
    }

    @Override
    public String getModId()
    {
        return "guilttrip";
    }

    @Override
    public String getModName()
    {
        return "GuiltTrip";
    }
}
