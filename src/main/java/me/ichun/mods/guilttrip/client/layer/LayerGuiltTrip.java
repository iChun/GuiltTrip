package me.ichun.mods.guilttrip.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.guilttrip.common.core.KillInfo;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Random;

public class LayerGuiltTrip extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
{
    public Random rand = new Random();
    public int depth = 0;

    public LayerGuiltTrip(PlayerRenderer renderer)
    {
        super(renderer);
    }

    //func_177093_a(entity, limb stuff, limb stuff, partialTicks, f5, yaw stuff, pitch stuff, 0.0625F);
    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float f, float f1, float renderTick, float f2, float f3, float f4)
    {
        if(!GuiltTrip.configClient.renderGhosts/* || iChunUtil.hasMorphMod() && MorphApi.getApiImpl().hasMorph(player.getName(), Side.CLIENT) && !(MorphApi.getApiImpl().getMorphEntity(player.world, player.getName(), Side.CLIENT) instanceof EntityPlayer)*/) //TODO readd morph support
        {
            return;
        }
        //DO RENDERING HERE.
        if(GuiltTrip.eventHandlerClient.playerKills.containsKey(player.getName().getUnformattedComponentText()) && !player.isInvisible() && depth < 2)
        {
            depth++;
            ArrayList<KillInfo> kills = GuiltTrip.eventHandlerClient.playerKills.get(player.getName().getUnformattedComponentText());
            matrixStack.push();
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-MathHelper.rotLerp(player.prevRenderYawOffset, player.renderYawOffset, renderTick))); //-EntityHelper.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, renderTick)
            if(player.isSneaking())
            {
                matrixStack.translate(0.0F, 0.25F, 0.0F);
            }
            matrixStack.scale(-1.0F, -1.0F, 1.0F);
            float scale = 0.15F;
            matrixStack.scale(scale, scale, scale);
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.003921569F);

            for(KillInfo info : kills)
            {
                if(info.validateInstance(true)) //TODO fix the alpha issue - might need a PR? Coremod?
                {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    matrixStack.push();

                    float alpha = 0.5F + (0.4F * (float)Math.sin(Math.toRadians(MathHelper.clamp(((float)(info.age % 200) + renderTick) / 200F, 0.0F, 1.0F) * 180F)));
                    if(info.maxAge > 0)
                    {
                        float prog = ((float)info.age + renderTick) / (float)info.maxAge;
                        alpha *= Math.pow(1.0F - MathHelper.clamp(prog, 0.0F, 1.0F), 2);
                        if(prog > 0.8F)
                        {
                            float scale1 = (float)Math.pow(1F - ((prog - 0.8F) / 0.2F), 2F);
                            matrixStack.scale(scale1, scale1, scale1);
                        }
                    }
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);

                    rand.setSeed(Math.abs(info.identifier.hashCode()));

                    matrixStack.rotate(Vector3f.YP.rotationDegrees((0.1F + 0.5F * rand.nextFloat()) * (rand.nextFloat() < 0.5F ? 1F : -1F) * ((float)info.age + renderTick)));

                    float distY = 0.75F / scale * (float)Math.pow(rand.nextFloat(), 2) + (0.2F * (float)Math.sin(Math.toRadians(MathHelper.clamp(((float)(info.age % 200) + renderTick) / 200F, 0.0F, 1.0F) * 180F)));
                    float dist = 0.75F / scale * (1.0F - distY / (0.5F / scale));
                    float mag = (float)Math.pow(rand.nextFloat(), 2);
                    matrixStack.translate(dist * mag * (rand.nextFloat() < 0.5F ? 1F : -1F), distY, dist * (1.0F - mag) * (rand.nextFloat() < 0.5F ? 1F : -1F));

                    info.entInstance.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
                    info.forceRender(renderTick, matrixStack, bufferIn, packedLightIn);
                    info.entInstance.setPosition(0D, -500D, 0D);

                    matrixStack.pop();

                    RenderSystem.disableBlend();
                }
            }
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);

            matrixStack.pop();
            depth--;
        }
    }
}
