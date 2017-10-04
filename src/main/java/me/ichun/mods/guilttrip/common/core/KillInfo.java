package me.ichun.mods.guilttrip.common.core;

import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class KillInfo
{
    public EntityLivingBase entInstance;

    public String identifier;

    public String playerName;

    public NBTTagCompound tag;

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
        tag = new NBTTagCompound();
        rand = new Random();
        walkTimeout = 10;
        lookTimeout = 10;
        maxAge = GuiltTrip.config.maxGhostAge;
    }//use the static method to create the instances.

    @SideOnly(Side.CLIENT)
    public boolean validateInstance(boolean recreate)//returns true is entity is null or is okay..
    {
        if(entInstance != null)
        {
            if(entInstance.getEntityWorld() != Minecraft.getMinecraft().world)
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
                    entInstance = new EntityOtherPlayerMP(Minecraft.getMinecraft().world, EntityHelper.getGameProfile(playerName));
                    entInstance.readFromNBT(tag);
                }
                else
                {
                    try
                    {
                        entInstance = (EntityLivingBase)EntityList.createEntityFromNBT(tag, Minecraft.getMinecraft().world);
                        if(entInstance == null)
                        {
                            invalid = true;
                        }
                    }
                    catch(NullPointerException e)
                    {
                        invalid = true;
                        GuiltTrip.LOGGER.warn("A mob is throwing an error when being read from NBT! You should report this to the mod author of the mob!");
                        e.printStackTrace();
                    }
                    catch(Exception e)
                    {
                        invalid = true;
                        GuiltTrip.LOGGER.warn("A mob is throwing an error when being read from NBT! You should report this to the mod author of the mob!");
                        e.printStackTrace();
                    }
                }
                if(EventCalendar.isAFDay())
                {
                    entInstance = (EntityLivingBase)EntityList.createEntityByIDFromName(new ResourceLocation("minecraft", "pig"), Minecraft.getMinecraft().world);
                }
                else if(EventCalendar.isChristmas())
                {
                    entInstance = (EntityLivingBase)EntityList.createEntityByIDFromName(new ResourceLocation("minecraft", "snowman"), Minecraft.getMinecraft().world);
                }
                else if(EventCalendar.isHalloween())
                {
                    entInstance = Minecraft.getMinecraft().world.rand.nextFloat() < 0.5F ? (EntityLivingBase)EntityList.createEntityByIDFromName(new ResourceLocation("minecraft", "enderman"), Minecraft.getMinecraft().world) : (EntityLivingBase)EntityList.createEntityByIDFromName(new ResourceLocation("minecraft", "blaze"), Minecraft.getMinecraft().world);
                }
                if(Minecraft.getMinecraft().getSession().getUsername().equalsIgnoreCase("direwolf20"))
                {
                    entInstance = (EntityLivingBase)EntityList.createEntityByIDFromName(new ResourceLocation("minecraft", "enderman"), Minecraft.getMinecraft().world);
                }
                else if(Minecraft.getMinecraft().getSession().getUsername().equalsIgnoreCase("lomeli12"))
                {
                    entInstance = (EntityLivingBase)EntityList.createEntityByIDFromName(new ResourceLocation("minecraft", "chicken"), Minecraft.getMinecraft().world);
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

    @SideOnly(Side.CLIENT)
    public void forceRender(double d, double d1, double d2, float f, float f1)
    {
        //        float bossHealthScale = BossStatus.healthScale;
        //        int bossStatusBarTime = BossStatus.statusBarTime;
        //        String bossName = BossStatus.bossName;
        //        boolean hasColorModifier = BossStatus.hasColorModifier;

        if(Minecraft.getMinecraft().getRenderManager().renderEngine != null && Minecraft.getMinecraft().getRenderManager().renderViewEntity != null)
        {
            try
            {
                Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entInstance).doRender(entInstance, d, d1, d2, entInstance.rotationYawHead, entInstance.rotationPitch); // not the right params but it causes some glitchiness, so it works.

                if(entInstance.hasCustomName())
                {
                    String str = entInstance.getCustomNameTag();

                    FontRenderer fontrenderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entInstance).getFontRendererFromRenderManager();
                    float f11 = 0.016666668F * 1.6F;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.0F, entInstance.height + 0.5F, 0.0F);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(-f11, -f11, f11);
                    GlStateManager.disableLighting();
                    GlStateManager.depthMask(false);
                    GlStateManager.disableDepth();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();
                    byte b0 = 0;
                    GlStateManager.disableTexture2D();
                    int j = fontrenderer.getStringWidth(str) / 2;
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    bufferbuilder.pos((double)(-j - 1), (double)(-1 + b0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    bufferbuilder.pos((double)(-j - 1), (double)(8 + b0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    bufferbuilder.pos((double)(j + 1), (double)(8 + b0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    bufferbuilder.pos((double)(j + 1), (double)(-1 + b0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, b0, 553648127);
                    GlStateManager.enableDepth();
                    GlStateManager.depthMask(true);
                    fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, b0, -1);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.popMatrix();
                }
            }
            catch(Exception e)
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
            if(entInstance instanceof EntityAgeable && entInstance.isChild())
            {
                EntityAgeable living = (EntityAgeable)entInstance;
                living.setGrowingAge(living.getGrowingAge() - 1);
            }
            if(walkTimeout > 0)
            {
                walkTimeout--;
                if(walkTimeout == 0 && GuiltTrip.config.ghostWalkAnim == 1)
                {
                    isWalking = !isWalking;
                    walkTimeout = 100 + (int)(isWalking ? 200 * rand.nextFloat() : 100 * rand.nextFloat());
                }
            }

            if(lookTimeout > 0)
            {
                lookTimeout--;
                if(lookTimeout == 0 && GuiltTrip.config.ghostLookAnim == 1)
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
                entInstance.swingArm(EnumHand.MAIN_HAND);
            }

            entInstance.renderYawOffset = entInstance.rotationYawHead - f3;

            entInstance.ticksExisted++;
            entInstance.onUpdate(); //should i do this or not?
        }
    }

    public NBTTagCompound getTag()
    {
        NBTTagCompound tag1 = new NBTTagCompound();

        tag1.setString("identifier", identifier);
        tag1.setString("playerName", playerName);
        tag1.setTag("entTag", tag);
        tag1.setInteger("age", age);
        tag1.setInteger("maxAge", maxAge);

        return tag1;
    }

    public void readTag(NBTTagCompound tag1)
    {
        identifier = tag1.getString("identifier");
        playerName = tag1.getString("playerName");
        tag = tag1.getCompoundTag("entTag");
        age = tag1.getInteger("age");
        maxAge = tag1.getInteger("maxAge");
    }

    public static KillInfo createKillInfoFromEntity(EntityLivingBase living)
    {
        NBTTagCompound tag = new NBTTagCompound();
        if(living instanceof EntityPlayer || living.writeToNBTOptional(tag))
        {
            KillInfo info = new KillInfo();
            if(living instanceof EntityPlayer)
            {
                NBTTagCompound persistent = EntityHelper.getPlayerPersistentData((EntityPlayer)living);
                NBTTagCompound tempTag = persistent.getCompoundTag("GuiltTripSave");
                persistent.setTag("GuiltTripSave", new NBTTagCompound());

                living.writeToNBT(tag);
                tag = tag.copy();

                persistent.setTag("GuiltTripSave", tempTag);
                info.playerName = living.getName();
            }
            tag.setShort("HurtTime", (short)0);
            tag.setShort("DeathTime", (short)0);
            tag.setFloat("HealF", 1);
            tag.setShort("Health", (short)1);

            info.tag = tag;
            return info;
        }
        return null;
    }

    public static KillInfo createKillInfoFromTag(NBTTagCompound tag)
    {
        KillInfo info = new KillInfo();
        info.readTag(tag);
        return info;
    }
}
