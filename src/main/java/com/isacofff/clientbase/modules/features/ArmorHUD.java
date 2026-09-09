package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ArmorHUD extends Module {

    protected final Minecraft mc = Minecraft.getMinecraft();

    public ArmorHUD() {
        super(
            "ArmorHUD",
            "Displays your equipped armor",
            Category.Render
        );
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        for (int i = 0; i < mc.player.inventory.armorInventory.size(); i++) {
            ItemStack armorStack = mc.player.inventory.armorInventory.get(i);
            
            if (!armorStack.isEmpty()) {
                
            }
        }
    }
}
