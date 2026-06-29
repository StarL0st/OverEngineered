package com.starl0stgaming.overengineered.core.fluxa.item;

import com.starl0stgaming.overengineered.client.OverengineeredClient;
import com.starl0stgaming.overengineered.client.render.fluxa.GridRenderer;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridCalculator;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import com.starl0stgaming.overengineered.core.networking.payload.CreateGridNodePayload;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class DebugWand extends Item {
    private boolean selectionMode = false;

    private Vec3 pos1;
    private Vec3 pos2;

    public DebugWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Vec3 pos = OverengineeredClient.MC_CLIENT.hitResult.getLocation();
        if(!pContext.getLevel().isClientSide) return InteractionResult.PASS;
        if(!selectionMode && pos1 == null) {
            var closestNode = GridCalculator.getNearestWorldPos(pos);
            pos1 = closestNode;
            this.selectionMode = true;
        } else if (pos2 == null) {
            var closestNode = GridCalculator.getNearestWorldPos(pos);
            pos2 = closestNode;
            this.selectionMode = false;
        }
        if(pos1 != null && pos2 != null && !selectionMode) {
            Outliner.getInstance().chaseAABB(UUID.randomUUID(),
                    new AABB(
                            pos1.x,
                            pos1.y,
                            pos1.z,
                            pos2.x + GridCalculator.GRID_WIDTH,
                            pos2.y + GridCalculator.GRID_WIDTH,
                            pos2.z + GridCalculator.GRID_WIDTH
                    )).colored(0x68c586).disableLineNormals().lineWidth(1/64f);

            if(GridRenderer.getInstance().getRenderNodes().values().stream().anyMatch(node -> Objects.equals(node.getPosition(), GridCalculator.toGridPosition(pos1)))) {
                PacketDistributor.sendToServer(
                        new CreateGridNodePayload(
                                pContext.getLevel().dimension(),
                                GridCalculator.toGridPosition(pos1),
                                Optional.of(GridCalculator.toGridPosition(pos2))
                        )
                );
            } else {
                PacketDistributor.sendToServer(
                        new CreateGridNodePayload(
                                pContext.getLevel().dimension(),
                                GridCalculator.toGridPosition(pos1),
                                Optional.empty()
                        )
                );
            }

            pos1 = null;
            pos2 = null;
        }

        return super.useOn(pContext);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if(pEntity instanceof Player player && player.isHolding(this)) {
            if(!pLevel.isClientSide) return;
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
                        )).colored(0x68c586).disableLineNormals().lineWidth(1/64f);
            } else if(selectionMode && pos1 != null) {
                Outliner.getInstance().remove(player.getStringUUID());
                Outliner.getInstance().chaseAABB(player.getStringUUID(),
                        new AABB(
                                pos1.x,
                                pos1.y,
                                pos1.z,
                                closestLookingNode.x + GridCalculator.GRID_WIDTH,
                                closestLookingNode.y + GridCalculator.GRID_WIDTH,
                                closestLookingNode.z + GridCalculator.GRID_WIDTH
                        )).colored(0x68c586).disableLineNormals().lineWidth(1/64f);

            }
        } else if(pEntity instanceof Player player1 && !player1.isHolding(this)) {
            if(OverengineeredClient.OUTLINE_RENDERER.getOutlines().containsKey(player1.getStringUUID())) OverengineeredClient.OUTLINE_RENDERER.remove(player1.getStringUUID());
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }
}
