package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;

public class Hitboxes extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public Hitboxes() {
        super("Combat Hitboxes", "Renders dynamic combat-responsive outlines around targets.", Category.Render);
    }

    /**
     * Renders the custom combat wireframes into the game world.
     * Call this inside your world render hook pass (e.g., inside EntityRenderer).
     */
    public void onRenderWorld(float partialTicks) {
        if (!this.isEnabled() || mc.world == null || mc.getRenderManager() == null || mc.player == null) {
            return;
        }

        // Get the active viewer positions to align calculations with camera location
        double renderPosX = mc.getRenderManager().viewerPosX;
        double renderPosY = mc.getRenderManager().viewerPosY;
        double renderPosZ = mc.getRenderManager().viewerPosZ;

        // Isolate OpenGL states using a safety matrix push
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();

        // Loop through all loaded entities in the active world chunk
        for (Entity entity : mc.world.loadedEntityList) {
            
            // Only draw lines for living targets (other players, mobs), never for our own player model
            if (entity == mc.player || !(entity instanceof EntityLivingBase)) {
                continue;
            }

            EntityLivingBase target = (EntityLivingBase) entity;

            // Interpolate position coordinate blocks for perfectly smooth 60fps tracking without jittering
            double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks - renderPosX;
            double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks - renderPosY;
            double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks - renderPosZ;

            float width = target.width / 2.0F;
            AxisAlignedBB bb = new AxisAlignedBB(x - width, y, z - width, x + width, y + target.height, z + width);

            // Default tint: Calm Light Blue (Target out of reach range)
            float red = 0.2F;
            float green = 0.6F;
            float blue = 1.0F;

            // Combat Range Metric: Check distance against the vanilla 3-block survival interaction limit
            double distance = mc.player.getDistance(target);
            if (distance <= 3.0D) {
                // Aggressive Red indicator if the target is within hit execution range
                red = 1.0F;
                green = 0.0F;
                blue = 0.0F;
            }

            // Damage Timing Indicator: Flash target orange if they are currently inside hurt invincibility ticks
            if (target.hurtTime > 0) {
                red = 1.0F;
                green = 0.5F;
                blue = 0.0F;
            }

            // Draw the structural wireframe box directly into the GL context
            RenderGlobal.drawSelectionBoundingBox(bb, red, green, blue, 1.0F);
        }

        // Restore core OpenGL graphics settings safely
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }
}
