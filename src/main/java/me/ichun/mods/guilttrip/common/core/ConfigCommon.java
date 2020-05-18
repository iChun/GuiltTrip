package me.ichun.mods.guilttrip.common.core;

import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import net.minecraftforge.fml.ModLoadingContext;

import javax.annotation.Nonnull;

public class ConfigCommon extends ConfigBase
{
    @CategoryDivider(name = "general")
    @Prop(min = 1)
    public int maxGhosts = 20;

    @Prop(min = 0)
    public int maxGhostAge = 24000;

    @Prop(min = 1)
    public double animalGuiltMultiplier = 1D;

    @Prop
    public boolean allKills = true;

    @Prop
    public boolean playerKills = true;

    @Prop
    public boolean playerKillsLasting = true;

    @Prop
    public boolean animalKills = true;

    @Prop
    public boolean mobKills = true;

    public ConfigCommon()
    {
        super(ModLoadingContext.get().getActiveContainer().getModId() + "-common.toml");
    }

    @Nonnull
    @Override
    public String getModId()
    {
        return GuiltTrip.MOD_ID;
    }

    @Nonnull
    @Override
    public String getConfigName()
    {
        return GuiltTrip.MOD_NAME;
    }
}
