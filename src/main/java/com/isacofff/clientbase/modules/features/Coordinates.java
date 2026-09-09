package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class Coordinates extends Module {

    public Coordinates() {
        super(
                "Coordinates",
                "Displays your current coordinates",
                Category.Render
        );
    }

    @Override
    public void onUpdate() {
        // Coordinate display will be connected to the HUD render hook later.
    }
}
