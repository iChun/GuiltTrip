package me.ichun.mods.guilttrip.client.layer;

import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.guilttrip.common.core.KillInfo;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Random;

public class LayerGuiltTrip implements LayerRenderer<EntityPlayer>
{
    public Random rand = new Random();
    public int depth = 0;

    //func_177093_a(entity, limb stuff, limb stuff, partialTicks, f5, yaw stuff, pitch stuff, 0.0625F);
    public void doRenderLayer(EntityPlayer player, float f, float f1, float renderTick, float f2, float f3, float f4, float f5)
    {
        if(GuiltTrip.config.renderGhosts != 1 || iChunUtil.hasMorphMod() && MorphApi.getApiImpl().hasMorph(player.getName(), Side.CLIENT) && !(MorphApi.getApiImpl().getMorphEntity(player.worldObj, player.getName(), Side.CLIENT) instanceof EntityPlayer))
        {
            return;
        }
        //DO RENDERING HERE.
        if(GuiltTrip.eventHandlerClient.playerKills.containsKey(player.getName()) && !player.isInvisible() && depth < 2)
        {
            depth++;
            ArrayList<KillInfo> kills = GuiltTrip.eventHandlerClient.playerKills.get(player.getName());
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-EntityHelper.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, renderTick), 0.0F, 1.0F, 0.0F);
            if(player.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.25F, 0.0F);
            }
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            float scale = 0.15F;
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

            for(KillInfo info : kills)
            {
                if(info.validateInstance(true))
                {
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    GlStateManager.pushMatrix();

                    float alpha = 0.5F + (0.4F * (float)Math.sin(Math.toRadians(MathHelper.clamp_float(((float)(info.age % 200) + renderTick) / 200F, 0.0F, 1.0F) * 180F)));
                    if(info.maxAge > 0)
                    {
                        float prog = ((float)info.age + renderTick) / (float)info.maxAge;
                        alpha *= Math.pow(1.0F - MathHelper.clamp_float(prog, 0.0F, 1.0F), 2);
                        if(prog > 0.8F)
                        {
                            float scale1 = (float)Math.pow(1F - ((prog - 0.8F) / 0.2F), 2F);
                            GlStateManager.scale(scale1, scale1, scale1);
                        }
                    }
                    GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

                    rand.setSeed(Math.abs(info.identifier.hashCode()));

                    GlStateManager.rotate((0.1F + 0.5F * rand.nextFloat()) * (rand.nextFloat() < 0.5F ? 1F : -1F) * ((float)info.age + renderTick), 0.0F, 1.0F, 0.0F);

                    float distY = 0.75F / scale * (float)Math.pow(rand.nextFloat(), 2) + (0.2F * (float)Math.sin(Math.toRadians(MathHelper.clamp_float(((float)(info.age % 200) + renderTick) / 200F, 0.0F, 1.0F) * 180F)));
                    float dist = 0.75F / scale * (1.0F - distY / (0.5F / scale));
                    float mag = (float)Math.pow(rand.nextFloat(), 2);
                    GlStateManager.translate(dist * mag * (rand.nextFloat() < 0.5F ? 1F : -1F), distY, dist * (1.0F - mag) * (rand.nextFloat() < 0.5F ? 1F : -1F));

                    info.entInstance.setPosition(player.posX, player.posY, player.posZ);
                    info.forceRender(0, 0, 0, 0, 0);
                    info.entInstance.setPosition(0D, -500D, 0D);

                    GlStateManager.popMatrix();

                    GlStateManager.disableBlend();
                }
            }
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

            GlStateManager.popMatrix();
            depth--;
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
