package com.starl0stgaming.overengineered.core.fluxa.item;


import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import net.minecraft.world.phys.Vec3;


public class DebugWand extends ModeBasedItem {
    public DebugWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {

        return super.useOn(pContext);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {

        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }


}
