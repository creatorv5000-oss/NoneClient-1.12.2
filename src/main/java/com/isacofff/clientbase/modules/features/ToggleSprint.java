package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;

public class ToggleSprint extends Module {

    public ToggleSprint() {
        super(
            "ToggleSprint",
            "Automatically keeps sprinting",
            Category.Movement
        );
    }

    @Override
    public void onUpdate() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.thePlayer.onGround) {
            mc.thePlayer.setSprinting(true);
        }
    }
}
