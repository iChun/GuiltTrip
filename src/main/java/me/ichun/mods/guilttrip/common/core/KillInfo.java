package me.ichun.mods.guilttrip.common.core;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.ichunutil.common.entity.EntityHelper;
import me.ichun.mods.ichunutil.common.util.EventCalendar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Optional;
import java.util.Random;

public class KillInfo
{
    public LivingEntity entInstance;

    public String identifier;

    public String playerName;

    public CompoundNBT tag;

    public int age;
    public int maxAge;

    public Random rand;
    public int walkTimeout;
    public boolean isWalking;
    public int lookTimeout;
    public float targetYaw;
    public float targetPitch;

    public boolean invalid;

    private KillInfo()
    {
        identifier = RandomStringUtils.randomAscii(20);
        playerName = "";
        tag = new CompoundNBT();
        rand = new Random();
        walkTimeout = 10;
        lookTimeout = 10;
        maxAge = GuiltTrip.configCommon.maxGhostAge;
    }//use the static method to create the instances.

    @OnlyIn(Dist.CLIENT)
    public boolean validateInstance(boolean recreate)//returns true is entity is null or is okay..
    {
        if(entInstance != null)
        {
            if(entInstance.getEntityWorld() != Minecraft.getInstance().world)
            {
                return false;
            }
        }
        else if(recreate)
        {
            if(playerName.isEmpty() && tag.getString("id").isEmpty())
            {
                invalid = true;
            }
            if(!invalid)
            {
                if(!playerName.isEmpty())
                {
                    //RemoteClientPlayerEntity
                    entInstance = new RemoteClientPlayerEntity(Minecraft.getInstance().world, EntityHelper.getGameProfile(null, playerName));
                    entInstance.read(tag);
                }
                else
                {
                    try
                    {
                        Optional<Entity> entity = EntityType.loadEntityUnchecked(tag, Minecraft.getInstance().world);
                        if(entity.isPresent() && entity.get() instanceof LivingEntity)
                        {
                            entInstance = (LivingEntity)entity.get();
                        }
                        if(entInstance == null)
                        {
                            invalid = true;
                        }
                    }
                    catch(Throwable e)
                    {
                        invalid = true;
                        GuiltTrip.LOGGER.warn("A mob is throwing an error when being read from NBT! You should report this to the mod author of the mob!");
                        e.printStackTrace();
                    }
                }
                if(EventCalendar.isAFDay())
                {
                    entInstance = EntityType.PIG.create(Minecraft.getInstance().world);
                }
                else if(EventCalendar.isChristmas())
                {
                    entInstance = EntityType.SNOW_GOLEM.create(Minecraft.getInstance().world);
                }
                else if(EventCalendar.isHalloween())
                {
                    entInstance = (Minecraft.getInstance().world.rand.nextFloat() < 0.5F ? EntityType.ENDERMAN : EntityType.BLAZE).create(Minecraft.getInstance().world);
                }
                if(Minecraft.getInstance().getSession().getUsername().equalsIgnoreCase("direwolf20"))
                {
                    entInstance = EntityType.ENDERMAN.create(Minecraft.getInstance().world);
                }
                else if(Minecraft.getInstance().getSession().getUsername().equalsIgnoreCase("lomeli12"))
                {
                    entInstance = EntityType.CHICKEN.create(Minecraft.getInstance().world);
                }
                if(entInstance != null)
                {
                    entInstance.rotationYawHead = rand.nextFloat() * 360F;
                }
                return !invalid;
            }
        }
        return !invalid;
    }

    @OnlyIn(Dist.CLIENT)
    public void forceRender(float partialTick, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        if(Minecraft.getInstance().getRenderViewEntity() != null) //in world?
        {
            try
            {
                Minecraft.getInstance().getRenderManager().getRenderer(entInstance).render(entInstance, entInstance.rotationYawHead, partialTick, matrixStackIn, bufferIn, packedLightIn); //TODO fix this with alpha
                //TODO boss mobs??
            }
            catch(Throwable e)
            {
                GuiltTrip.LOGGER.warn("A mob is causing an exception when GuiltTrip tries to render it! You might want to report this to the author of the mob");
                e.printStackTrace();
                invalid = true;
            }
        }
    }

    public void update()
    {
        age++;
        if(entInstance != null)
        {
            entInstance.setPosition(0, -500D, 0);
            entInstance.setAir(300);
            entInstance.setHealth(1);
            if(entInstance instanceof AgeableEntity && entInstance.isChild())
            {
                AgeableEntity living = (AgeableEntity)entInstance;
                living.setGrowingAge(living.getGrowingAge() - 1);
            }
            if(walkTimeout > 0)
            {
                walkTimeout--;
                if(walkTimeout == 0 && GuiltTrip.configClient.ghostWalkAnim)
                {
                    isWalking = !isWalking;
                    walkTimeout = 100 + (int)(isWalking ? 200 * rand.nextFloat() : 100 * rand.nextFloat());
                }
            }

            if(lookTimeout > 0)
            {
                lookTimeout--;
                if(lookTimeout == 0 && GuiltTrip.configClient.ghostLookAnim)
                {
                    targetYaw = 360F * rand.nextFloat();
                    targetPitch = 90F * rand.nextFloat() - 45F;
                    lookTimeout = 100 + (rand.nextInt(100));
                }
            }

            if(isWalking)
            {
                entInstance.limbSwingAmount += (1.0F - entInstance.limbSwingAmount) * 0.4F;
            }
            entInstance.rotationYaw = entInstance.rotationYawHead = EntityHelper.updateRotation(entInstance.rotationYawHead, targetYaw, 1.5F);
            entInstance.rotationPitch = EntityHelper.updateRotation(entInstance.rotationPitch, targetPitch, 1F);

            float f3 = MathHelper.wrapDegrees(entInstance.rotationYawHead - entInstance.renderYawOffset);

            if(f3 < -75.0F)
            {
                f3 = -75.0F;
            }

            if(f3 >= 75.0F)
            {
                f3 = 75.0F;
            }

            if(rand.nextFloat() < 0.01F)
            {
                entInstance.swingArm(Hand.MAIN_HAND);
            }

            entInstance.renderYawOffset = entInstance.rotationYawHead - f3;

            entInstance.ticksExisted++;
            entInstance.tick(); //should i do this or not?
        }
    }

    public CompoundNBT getTag()
    {
        CompoundNBT tag1 = new CompoundNBT();

        tag1.putString("identifier", identifier);
        tag1.putString("playerName", playerName);
        tag1.put("entTag", tag);
        tag1.putInt("age", age);
        tag1.putInt("maxAge", maxAge);

        return tag1;
    }

    public void readTag(CompoundNBT tag1)
    {
        identifier = tag1.getString("identifier");
        playerName = tag1.getString("playerName");
        tag = tag1.getCompound("entTag");
        age = tag1.getInt("age");
        maxAge = tag1.getInt("maxAge");
    }

    public static KillInfo createKillInfoFromEntity(LivingEntity living)
    {
        CompoundNBT tag = new CompoundNBT();
        if(living instanceof PlayerEntity || living.writeUnlessRemoved(tag))
        {
            KillInfo info = new KillInfo();
            if(living instanceof PlayerEntity)
            {
                CompoundNBT persistent = EntityHelper.getPlayerPersistentData((PlayerEntity)living, null);
                CompoundNBT tempTag = persistent.getCompound("GuiltTripSave");
                persistent.remove("GuiltTripSave");

                living.writeWithoutTypeId(tag); //override the persistent tag with new tag.
                tag = tag.copy();

                persistent.put("GuiltTripSave", tempTag);
                info.playerName = living.getName().getUnformattedComponentText();
            }
            tag.putShort("HurtTime", (short)0);
            tag.putShort("DeathTime", (short)0);
            tag.putFloat("HealF", 1);
            tag.putShort("Health", (short)1);
            tag.putBoolean("CustomNameVisible", true);

            info.tag = tag;
            return info;
        }
        return null;
    }

    public static KillInfo createKillInfoFromTag(CompoundNBT tag)
    {
        KillInfo info = new KillInfo();
        info.readTag(tag);
        return info;
    }
}
