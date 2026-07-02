package com.starl0stgaming.overengineered.core.fluxa.item.mode;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.client.OverengineeredClient;
import com.starl0stgaming.overengineered.client.render.fluxa.GridRenderer;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridCalculator;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridNode;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import com.starl0stgaming.overengineered.core.fluxa.item.ModeBasedItem;
import com.starl0stgaming.overengineered.core.networking.payload.CreateGridNodePayload;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CreateMode extends Mode {
    private boolean selectionMode;
    private GridPosition start;


    public CreateMode() {
        super(ResourceLocation.fromNamespaceAndPath(
                Overengineered.MODID,
                "grid_item_create_mode"
        ));
    }

    public boolean isInSelectionMode() {
        return selectionMode;
    }

    @Override
    public InteractionResult onUse(ModeContext ctx) {
        var pos = GridCalculator.getNearestWorldPos(OverengineeredClient.MC_CLIENT.hitResult.getLocation());
        if (!ctx.level().isClientSide) return InteractionResult.PASS;
        switch (ctx.state()) {
            case SINGLE_CLICK -> {
                if (!GridRenderer.getInstance().getRenderNodes().values().stream().anyMatch(node -> Objects.equals(node.getPosition(), GridCalculator.toGridPosition(pos)))) {
                    PacketDistributor.sendToServer(
                            new CreateGridNodePayload(
                                    ctx.level().dimension(),
                                    GridCalculator.toGridPosition(GridCalculator.getNearestWorldPos(pos)),
                                    Optional.empty()
                            )
                    );
                } else {
                    ctx.player().displayClientMessage(Component.literal("Can't place node here"), true);
                }
                break;
            }
            case HOLDING -> {
               if(start == null) {
                   start = GridCalculator.toGridPosition(pos);
               }

                this.selectionMode = true;

                GridPosition hover = GridCalculator.toGridPosition(pos);

                GridRenderer.getInstance().clearTemporaryPreview();

                GridRenderer.getInstance().addTemporaryNode(
                        hover,
                        start
                );
            }
            case RELEASED_AFTER_HOLDING -> {

                if (start == null) break;

                GridPosition end = GridCalculator.toGridPosition(pos);

                GridRenderer.getInstance().clearTemporaryPreview();

                boolean startExists = GridRenderer.getInstance()
                        .getRenderNodes()
                        .values()
                        .stream()
                        .anyMatch(n -> n.getPosition().equals(start));

                boolean endExists = GridRenderer.getInstance()
                        .getRenderNodes()
                        .values()
                        .stream()
                        .anyMatch(n -> n.getPosition().equals(end));

                // CASE 1: single new node
                if (!selectionMode || start.equals(end)) {

                    PacketDistributor.sendToServer(
                            new CreateGridNodePayload(
                                    ctx.level().dimension(),
                                    start,
                                    Optional.empty()
                            )
                    );

                }
                // CASE 2: start is existing node → connect new node to its network
                else if (startExists && !endExists) {

                    PacketDistributor.sendToServer(
                            new CreateGridNodePayload(
                                    ctx.level().dimension(),
                                    start,
                                    Optional.of(end)
                            )
                    );

                }
                // CASE 3: both exist → connect existing nodes
                else if (startExists && endExists) {

                    PacketDistributor.sendToServer(
                            new CreateGridNodePayload(
                                    ctx.level().dimension(),
                                    start,
                                    Optional.of(end)
                            )
                    );
                }
                // CASE 4: neither exist → create start, then connect
                else {

                    PacketDistributor.sendToServer(
                            new CreateGridNodePayload(
                                    ctx.level().dimension(),
                                    start,
                                    Optional.empty()
                            )
                    );

                    PacketDistributor.sendToServer(
                            new CreateGridNodePayload(
                                    ctx.level().dimension(),
                                    start,
                                    Optional.of(end)
                            )
                    );
                }

                start = null;
                selectionMode = false;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(entity instanceof Player player && player.isHolding(iStack -> iStack.getItem() instanceof ModeBasedItem)) {
            if(!level.isClientSide) return;

            Vec3 closestLookingNode = GridCalculator.getNearestWorldPos(OverengineeredClient.MC_CLIENT.hitResult);
            if(!selectionMode) {
                Outliner.getInstance().chaseAABB(player.getStringUUID(),
                        new AABB(
                                closestLookingNode.x,
                                closestLookingNode.y,
                                closestLookingNode.z,
                                closestLookingNode.x + GridCalculator.GRID_WIDTH,
                                closestLookingNode.y + GridCalculator.GRID_WIDTH,
                                closestLookingNode.z + GridCalculator.GRID_WIDTH
                        )).colored(Color.WHITE).disableLineNormals().lineWidth(1/64f);
            } else if (start != null) {
                Outliner.getInstance().remove(player.getStringUUID());

            }
            if(OverengineeredClient.MC_CLIENT.hitResult instanceof BlockHitResult blockHitResult && level.getBlockState(blockHitResult.getBlockPos()) != Blocks.AIR.defaultBlockState()) {
                GridRenderer.getInstance().renderGridOverlay(blockHitResult);
            }
        } else {
            GridRenderer.getInstance().clearTemporaryPreview();
            this.selectionMode = false;
            this.start = null;
        }
    }
}
