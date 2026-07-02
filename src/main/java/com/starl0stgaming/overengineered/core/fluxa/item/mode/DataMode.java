package com.starl0stgaming.overengineered.core.fluxa.item.mode;

import com.starl0stgaming.overengineered.Overengineered;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DataMode extends Mode {

    public DataMode() {
        super(ResourceLocation.fromNamespaceAndPath(
                Overengineered.MODID,
                "grid_item_data_mode"
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
