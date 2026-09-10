package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.item.ItemStack;
import java.util.List;

public class ViaItems extends Module {

    public ViaItems() {
        super("ViaItems", "Handles text styles and formatting for modern 1.21 items.", Category.Render);
    }

    public String getTranslatedName(ItemStack stack, String originalName) {
        if (!this.isEnabled() || stack.isEmpty()) {
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

        if (lowerName.contains("wind charge")) {
            return "\u00a7f\u00a7l" + originalName;
        }

        return originalName;
    }

    public void modifyTooltip(ItemStack stack, List<String> tooltip) {
        if (!this.isEnabled() || stack.isEmpty() || tooltip.isEmpty()) {
            return;
        }

        String formattedName = getTranslatedName(stack, tooltip.get(0));
        tooltip.set(0, formattedName);

        String nameCheck = stack.getDisplayName().toLowerCase();

        if (nameCheck.contains("netherite")) {
            tooltip.add(1, "\u00a77Tier: \u00a75Modern 1.16+");
        } else if (nameCheck.contains("mace")) {
            tooltip.add(1, "\u00a77Weapon: \u00a7eModern 1.21 Smash Attack");
        } else if (nameCheck.contains("wind charge")) {
            tooltip.add(1, "\u00a77Projectile: \u00a7aKnockback Blast");
        }
    }
}
