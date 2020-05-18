package me.ichun.mods.guilttrip.client.core;

import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

public class ConfigClient extends ConfigBase
{
    @CategoryDivider(name = "clientOnly")
    @Prop
    public boolean ghostWalkAnim = true;

    @Prop
    public boolean ghostLookAnim = true;

    @Prop
    public boolean renderGhosts = true;

    public ConfigClient()
    {
        super(ModLoadingContext.get().getActiveContainer().getModId() + "-client.toml");
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

    @Nonnull
    @Override
    public ModConfig.Type getConfigType()
    {
        return ModConfig.Type.CLIENT;
    }
}
