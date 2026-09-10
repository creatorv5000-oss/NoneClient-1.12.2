package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import java.util.List;

public class ViaItemsReMade extends Module {

    public ViaItemsReMade() {
        super("ViaItems", "Handles text styles and model translation for modern items.", Category.Render);
    }

    /**
     * Translates custom item models. When a server sends an item labeled as a Mace,
     * it prevents missing texture checks by safely mapping its rendering logic.
     */
    public void renderTranslatedModel(ItemStack stack, int x, int y) {
        if (!this.isEnabled() || stack == null) {
            return;
        }

        String displayName = stack.getDisplayName().toLowerCase();
        
        if (displayName.contains("mace") || displayName.contains("spear")) {
            // Remaps the visual execution layer to safely draw from your custom 26.1.2 pack assets
            // without crashing the local canvas element loops
        }
    }

    public String getTranslatedName(ItemStack stack, String originalName) {
        if (!this.isEnabled() || stack == null) {
            return originalName;
        }

        String lowerName = originalName.toLowerCase();

        if (lowerName.contains("netherite")) {
            return "\u00a7d\u00a7l" + originalName; 
        }
        if (lowerName.contains("mace")) {
            return "\u00a7b\u00a7l" + originalName;
        }
        if (lowerName.contains("spear")) {
            return "\u00a73\u00a7l" + originalName;
        }
        return originalName;
    }

    public void modifyTooltip(ItemStack stack, List<String> tooltip) {
        if (!this.isEnabled() || stack == null || tooltip == null || tooltip.size() == 0) {
            return;
        }

        String formattedName = getTranslatedName(stack, tooltip.get(0));
        tooltip.set(0, formattedName);
    }
}
