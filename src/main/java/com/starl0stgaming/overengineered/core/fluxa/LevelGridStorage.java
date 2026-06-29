package com.starl0stgaming.overengineered.core.fluxa;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridNode;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import com.starl0stgaming.overengineered.core.fluxa.network.GridNetwork;
import com.starl0stgaming.overengineered.core.networking.payload.UpdateClientRenderNodesPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class LevelGridStorage extends SavedData {
    private HashMap<UUID, GridNetwork> gridNetworks;
    private HashMap<GridPosition, GridNode> spatialNodeIndex;

    private LevelGridStorage() {
        this.gridNetworks = new HashMap<>();
        this.spatialNodeIndex = new HashMap<>();
    }

    public void updatePlayer(ServerPlayer player, Set<GridNode> nodes) {
        PacketDistributor.sendToPlayer(player, new UpdateClientRenderNodesPayload(
                nodes
        ));
    }

    public void clearPlayer(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new UpdateClientRenderNodesPayload(
                new HashSet<>()
        ));
    }

    /**
     * Creates a new network from the passed node.
     * @param node Node to create network from.
     * @return The new network
     */
    public GridNetwork newNetwork(GridNode node) {
        var network = new GridNetwork();
        network.addNode(node);
        this.gridNetworks.put(network.identifier, network);

        this.setDirty();
        return network;
    }

    public void updateSpatialIndex(GridPosition gridPos, GridNode node) {
        this.spatialNodeIndex.put(gridPos, node);
        this.setDirty();
    }

    public GridNetwork getNetwork(UUID networkId) {
        return this.gridNetworks.get(networkId);
    }

    public GridNetwork getNetwork(GridNode node) {
        return this.gridNetworks.get(node.getParentNetwork());
    }

    public GridNode getNodeFromPos(GridPosition position) {
        return this.spatialNodeIndex.get(position);
    }

    public GridNode getNodeFromPos(BlockPos blockPos, double gridX, double gridY, double gridZ) {
        return this.spatialNodeIndex.get(
                new GridPosition(
                        blockPos,
                        gridX,
                        gridY,
                        gridZ
                )
        );
    }

    public Set<GridNode> getNodesInBlock(BlockPos pos) {
        return this.spatialNodeIndex.entrySet().stream()
                .filter(entry -> entry.getKey().pos().equals(pos))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        Overengineered.LOGGER.info("[GRID STORAGE] Saving Grid Data");
        CompoundTag gridData = new CompoundTag();
        if(!this.gridNetworks.isEmpty()) {
            for(Map.Entry<UUID, GridNetwork> entry : this.gridNetworks.entrySet()) {
                var result = GridNetwork.GRID_NETWORK_CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue());
                result
                        .resultOrPartial(Overengineered.LOGGER::error)
                                .ifPresent(tag -> gridData.put(entry.getKey().toString(), tag));
            }
        }
        CompoundTag spatialData = new CompoundTag();
        if(!this.spatialNodeIndex.isEmpty()) {
            for(Map.Entry<GridPosition, GridNode> entry : this.spatialNodeIndex.entrySet()) {
                CompoundTag indexData = new CompoundTag();
                var result1 = GridPosition.GRID_POSITION_CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey());
                result1
                        .resultOrPartial(Overengineered.LOGGER::error)
                                .ifPresent(tag -> indexData.put("gridPos", tag));
                var result2 = GridNode.GRID_NODE_CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue());
                result2.resultOrPartial(Overengineered.LOGGER::error)
                                .ifPresent(tag -> indexData.put("gridNode", tag));
                spatialData.put(entry.getValue().getIdentifier().toString(), indexData);
            }
        }
        compoundTag.put("gridData", gridData);
        compoundTag.put("spatialData", spatialData);
        return compoundTag;
    }

    public static LevelGridStorage load(CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        LevelGridStorage gridStorage = new LevelGridStorage();
        CompoundTag gridData = tag.getCompound("gridData");
        if(!gridData.isEmpty()) {
            HashMap<UUID, GridNetwork> tempGrid = new HashMap<>();
            for(String stringUUID : gridData.getAllKeys()) {
                CompoundTag networkData = gridData.getCompound(stringUUID);
                var result = GridNetwork.GRID_NETWORK_CODEC.parse(NbtOps.INSTANCE, networkData);
                result
                        .resultOrPartial(Overengineered.LOGGER::error)
                        .ifPresent(gridNetwork -> tempGrid.put(UUID.fromString(stringUUID), gridNetwork));
            }
            gridStorage.setGridNetworks(tempGrid);
        }
        CompoundTag spatialData = tag.getCompound("spatialData");
        if(!spatialData.isEmpty()) {
            HashMap<GridPosition, GridNode> tempIndex = new HashMap<>();
            for(String stringUUID : spatialData.getAllKeys()) {
                CompoundTag data = spatialData.getCompound(stringUUID);
                var result1 = GridPosition.GRID_POSITION_CODEC.parse(NbtOps.INSTANCE, data.getCompound("gridPos"));
                var result2 = GridNode.GRID_NODE_CODEC.parse(NbtOps.INSTANCE, data.getCompound("gridNode"));
                tempIndex.put(
                        result1
                                .resultOrPartial(Overengineered.LOGGER::error)
                                .get(),
                        result2
                                .resultOrPartial(Overengineered.LOGGER::error)
                                .get()
                );
            }
            gridStorage.setSpatialNodeIndex(tempIndex);
        }
        return gridStorage;
    }

    public static LevelGridStorage get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(LevelGridStorage::new, LevelGridStorage::load), "overengineered_grid_data");
    }

    public HashMap<UUID, GridNetwork> getGridNetworks() {
        return gridNetworks;
    }

    private void setGridNetworks(HashMap<UUID, GridNetwork> gridNetworks) {
        this.gridNetworks = gridNetworks;
    }

    public HashMap<GridPosition, GridNode> getSpatialNodeIndex() {
        return spatialNodeIndex;
    }

    private void setSpatialNodeIndex(HashMap<GridPosition, GridNode> spatialNodeIndex) {
        this.spatialNodeIndex = spatialNodeIndex;
    }
}
