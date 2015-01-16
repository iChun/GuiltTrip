package us.ichun.mods.guilttrip.common.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.guilttrip.common.GuiltTrip;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.core.util.EventCalendar;

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
        maxAge = GuiltTrip.config.getInt("maxGhostAge");
    }//use the static method to create the instances.

    @SideOnly(Side.CLIENT)
    public boolean validateInstance(boolean recreate)//returns true is entity is null or is okay..
    {
        if(entInstance != null)
        {
            if(entInstance.getEntityWorld() != Minecraft.getMinecraft().theWorld)
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
                    entInstance = new EntityOtherPlayerMP(Minecraft.getMinecraft().theWorld, EntityHelperBase.getFullGameProfileFromName(playerName));
                }
                else
                {
                    try
                    {
                        entInstance = (EntityLivingBase)EntityList.createEntityFromNBT(tag, Minecraft.getMinecraft().theWorld);
                        if(entInstance == null)
                        {
                            invalid = true;
                        }
                    }
                    catch(NullPointerException e)
                    {
                        invalid = true;
                        GuiltTrip.console("A mob is throwing an error when being read from NBT! You should report this to the mod author of the mob!", true);
                        e.printStackTrace();
                    }
                    catch(Exception e)
                    {
                        invalid = true;
                        GuiltTrip.console("A mob is throwing an error when being read from NBT! You should report this to the mod author of the mob!", true);
                        e.printStackTrace();
                    }
                }
                if(EventCalendar.isAFDay())
                {
                    entInstance = (EntityLivingBase)EntityList.createEntityByName("Pig",  Minecraft.getMinecraft().theWorld);
                }
                else if(EventCalendar.isChristmas())
                {
                    entInstance = (EntityLivingBase)EntityList.createEntityByName("SnowMan",  Minecraft.getMinecraft().theWorld);
                }
                else if(EventCalendar.isHalloween())
                {
                    entInstance = Minecraft.getMinecraft().theWorld.rand.nextFloat() < 0.5F ? (EntityLivingBase)EntityList.createEntityByName("Enderman",  Minecraft.getMinecraft().theWorld) : (EntityLivingBase)EntityList.createEntityByName("Blaze",  Minecraft.getMinecraft().theWorld);
                }
                if(Minecraft.getMinecraft().getSession().getUsername().equalsIgnoreCase("direwolf20"))
                {
                    entInstance = (EntityLivingBase)EntityList.createEntityByName("Enderman",  Minecraft.getMinecraft().theWorld);
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
        float bossHealthScale = BossStatus.healthScale;
        int bossStatusBarTime = BossStatus.statusBarTime;
        String bossName = BossStatus.bossName;
        boolean hasColorModifier = BossStatus.hasColorModifier;

        if(Minecraft.getMinecraft().getRenderManager().renderEngine != null && Minecraft.getMinecraft().getRenderManager().livingPlayer != null)
        {
            try
            {
                Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entInstance).doRender(entInstance, d, d1, d2, entInstance.rotationYawHead, entInstance.rotationPitch);

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
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    byte b0 = 0;
                    GlStateManager.disableTexture2D();
                    worldrenderer.startDrawingQuads();
                    int j = fontrenderer.getStringWidth(str) / 2;
                    worldrenderer.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    worldrenderer.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
                    worldrenderer.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
                    worldrenderer.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
                    worldrenderer.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
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
                GuiltTrip.console("A mob is causing an exception when GuiltTrip tries to render it! You might want to report this to the author of the mob", true);
                e.printStackTrace();
                invalid = true;
            }
        }

        BossStatus.healthScale = bossHealthScale;
        BossStatus.statusBarTime = bossStatusBarTime;
        BossStatus.bossName = bossName;
        BossStatus.hasColorModifier = hasColorModifier;
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
                if(walkTimeout == 0 && GuiltTrip.config.getInt("ghostWalkAnim") == 1)
                {
                    isWalking = !isWalking;
                    walkTimeout = 100 + (int)(isWalking ? 200 * rand.nextFloat() : 100 * rand.nextFloat());
                }
            }

            if(lookTimeout > 0)
            {
                lookTimeout--;
                if(lookTimeout == 0 && GuiltTrip.config.getInt("ghostLookAnim") == 1)
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
            entInstance.rotationYaw = entInstance.rotationYawHead = EntityHelperBase.updateRotation(entInstance.rotationYawHead, targetYaw, 1.5F);
            entInstance.rotationPitch = EntityHelperBase.updateRotation(entInstance.rotationPitch, targetPitch, 1F);

            float f3 = MathHelper.wrapAngleTo180_float(entInstance.rotationYawHead - entInstance.renderYawOffset);

            if (f3 < -75.0F)
            {
                f3 = -75.0F;
            }

            if (f3 >= 75.0F)
            {
                f3 = 75.0F;
            }

            if(rand.nextFloat() < 0.01F)
            {
                entInstance.swingItem();
            }

            entInstance.renderYawOffset = entInstance.rotationYawHead - f3;

            entInstance.ticksExisted++;
            entInstance.onUpdate(); //should i do this or not?
        }
    }

    public NBTTagCompound getTag()
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("identifier", identifier);
        tag.setString("playerName", playerName);
        tag.setTag("tag", this.tag);
        tag.setInteger("age", age);
        tag.setInteger("maxAge", maxAge);

        return tag;
    }

    public void readTag(NBTTagCompound tag)
    {
        identifier = tag.getString("identifier");
        playerName = tag.getString("playerName");
        this.tag = tag.getCompoundTag("tag");
        age = tag.getInteger("age");
        maxAge = tag.getInteger("maxAge");
    }

    public static KillInfo createKillInfoFromEntity(EntityLivingBase living)
    {
        NBTTagCompound tag = new NBTTagCompound();
        if(living instanceof EntityPlayer || living.writeToNBTOptional(tag))
        {
            KillInfo info = new KillInfo();
            if(living instanceof EntityPlayer)
            {
                living.writeToNBT(tag);
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
