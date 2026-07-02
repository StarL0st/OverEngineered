package com.starl0stgaming.overengineered.core.fluxa.item;

import com.starl0stgaming.overengineered.client.OverengineeredClient;
import com.starl0stgaming.overengineered.core.fluxa.item.mode.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;


public abstract class ModeBasedItem extends Item {
    private Mode currentMode;
    private List<Mode> modes;
    private int modeIndex;

    private boolean wasDown = false;
    private boolean isHolding = false;
    private int heldTicks = 0;

    private static final int HOLD_THRESHOLD = 1;

    public ModeBasedItem(Properties properties) {
        super(properties);
        this.modes = new ArrayList<>();
        this.modes.add(new CreateMode());
        this.modes.add(new EditMode());
        this.modes.add(new DeleteMode());
        this.modes.add(new DataMode());

        this.modeIndex = 0;
        this.currentMode = this.modes.get(this.modeIndex);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!context.getLevel().isClientSide) return InteractionResult.PASS;
        return super.useOn(context);
    }

    public Mode getCurrentMode() {
        return this.currentMode;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(entity instanceof Player player && player.isHolding(this)) {
            if (!level.isClientSide) return;
            this.updateMode();

            boolean down = GLFW.glfwGetMouseButton(
                    OverengineeredClient.MC_CLIENT.getWindow().getWindow(),
                    GLFW.GLFW_MOUSE_BUTTON_RIGHT
            ) == GLFW.GLFW_PRESS;

            if(down) {
                if(!wasDown) {
                    heldTicks = 0;
                    isHolding = false;
                } else {
                    heldTicks++;

                    if(heldTicks >= HOLD_THRESHOLD) {
                        isHolding = true;
                        //holding
                        this.currentMode.onUse(new Mode.ModeContext(
                                player,
                                level,
                                Mode.InteractionState.HOLDING
                        ));
                    }
                }
            } else {
                if(wasDown) {
                    if(!isHolding) {
                        //single click
                        this.currentMode.onUse(new Mode.ModeContext(
                                player,
                                level,
                                Mode.InteractionState.SINGLE_CLICK
                        ));
                    } else {
                        //just released after holding
                        this.currentMode.onUse(new Mode.ModeContext(
                                player,
                                level,
                                Mode.InteractionState.RELEASED_AFTER_HOLDING
                        ));
                    }
                }

                heldTicks = 0;
                isHolding = false;
            }

            wasDown = down;
        }
        this.currentMode.inventoryTick(
                stack,
                level,
                entity,
                slotId,
                isSelected
        );

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    private void updateMode() {
        if(GLFW.glfwGetKey(OverengineeredClient.MC_CLIENT.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS &&
                OverengineeredClient.getMouseScrollDelta() != 0) {
            double scroll = OverengineeredClient.getMouseScrollDelta();

            if(scroll > 0) {
                modeIndex++;
            } else if(scroll < 0) {
                modeIndex--;
            }

            if(modeIndex >= modes.size()) {
                modeIndex = 0;
            } else if(modeIndex < 0) {
                modeIndex = modes.size() - 1;
            }

            currentMode = modes.get(modeIndex);
            OverengineeredClient.setMouseScrollDelta(0.0);
        }
    }

}
