package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.modules.Module;
import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.settings.Setting;
import net.minecraft.client.Minecraft;

public class FullBright extends Module {

    public Setting.NumberSetting gamma = new Setting.NumberSetting("Gamma", 1000, 1, 1000, 1);

    public FullBright() {
        super("Fullbright", Category.Render);
        this.description = "Changes the brightness of the world.";
        settings.add(gamma);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Minecraft.getMinecraft().gameSettings.gammaSetting = gamma.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Minecraft.getMinecraft().gameSettings.gammaSetting = 1f;
    }
}
