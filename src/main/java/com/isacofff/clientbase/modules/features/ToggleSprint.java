package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

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
        // Sprint functionality will be added after the correct
        // Eaglercraft player API is confirmed.
    }
}
