package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class ArmorHUD extends Module {

    public ArmorHUD() {
        super(
                "ArmorHUD",
                "Displays your equipped armor",
                Category.Render
        );
    }

    @Override
    public void onUpdate() {
        // Armor HUD rendering will be connected to the HUD render hook later.
    }
}
