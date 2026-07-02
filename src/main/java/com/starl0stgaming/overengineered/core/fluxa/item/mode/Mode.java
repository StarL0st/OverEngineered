package com.starl0stgaming.overengineered.core.fluxa.item.mode;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public abstract class Mode {
    private ResourceLocation identifier;

    public Mode(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public abstract InteractionResult onUse(ModeContext ctx);
    public abstract void inventoryTick(ItemStack stack, Level level, Entity player, int slotId, boolean isSelected);

    public enum InteractionState {
        SINGLE_CLICK,
        HOLDING,
        RELEASED_AFTER_HOLDING
    }

    public record ModeContext(
            Player player,
            Level level,
            InteractionState state
    ) {

    }
}
