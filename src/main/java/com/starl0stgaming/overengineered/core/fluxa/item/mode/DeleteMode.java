package com.starl0stgaming.overengineered.core.fluxa.item.mode;

import com.starl0stgaming.overengineered.Overengineered;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DeleteMode extends Mode {

    public DeleteMode() {
        super(ResourceLocation.fromNamespaceAndPath(
                Overengineered.MODID,
                "grid_item_delete_mode"
        ));
    }

    @Override
    public InteractionResult onUse(ModeContext ctx) {
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {

    }
}
