package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class Zoom extends Module {

    public Zoom() {
        super(
                "Zoom",
                "Zooms the camera view",
                Category.Render
        );
    }

    @Override
    public void onUpdate() {
        // Camera zoom will be connected to the Minecraft FOV hook later.
    }
}
