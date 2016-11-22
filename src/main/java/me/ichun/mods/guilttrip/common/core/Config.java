package me.ichun.mods.guilttrip.common.core;

import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.guilttrip.common.GuiltTrip;

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
    public int mobKills = 1;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int ghostWalkAnim = 1;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int ghostLookAnim = 1;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int renderGhosts = 1;

    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return GuiltTrip.MOD_ID;
    }

    @Override
    public String getModName()
    {
        return "Guilt Trip";
    }
}
