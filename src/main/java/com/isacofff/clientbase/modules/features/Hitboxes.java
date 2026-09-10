package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class Hitboxes extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public Hitboxes() {
        super("Combat Hitboxes", "Renders dynamic combat-responsive outlines around targets.", Category.Render);
    }

    public void onRenderWorld(float partialTicks) {
        if (!this.isEnabled() || mc.world == null || mc.getRenderManager() == null || mc.player == null) {
            return;
        }

        double renderPosX = mc.getRenderManager().viewerPosX;
        double renderPosY = mc.getRenderManager().viewerPosY;
        double renderPosZ = mc.getRenderManager().viewerPosZ;

        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity == mc.player || !(entity instanceof EntityLivingBase)) {
                continue;
            }

            EntityLivingBase target = (EntityLivingBase) entity;

            double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks - renderPosX;
            double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks - renderPosY;
            double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks - renderPosZ;

            float width = target.width / 2.0F;
            AxisAlignedBB bb = new AxisAlignedBB(x - width, y, z - width, x + width, y + target.height, z + width);

            float red = 0.2F;
            float green = 0.6F;
            float blue = 1.0F;

            double distance = mc.player.getDistance(target.posX, target.posY, target.posZ);
            if (distance <= 3.0D) {
                red = 1.0F;
                green = 0.0F;
                blue = 0.0F;
            }

            if (target.hurtTime > 0) {
                red = 1.0F;
                green = 0.5F;
                blue = 0.0F;
            }

            RenderGlobal.drawSelectionBoundingBox(bb, red, green, blue, 1.0F);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
}
