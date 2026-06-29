package com.starl0stgaming.overengineered.core.fluxa.grid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starl0stgaming.overengineered.core.fluxa.LevelGridStorage;
import com.starl0stgaming.overengineered.core.fluxa.network.GridNetwork;
import com.starl0stgaming.overengineered.core.networking.GridNetworking;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class GridNode {
    private UUID identifier;
    private UUID parentNetwork; //used for reference

    private GridPosition position;

    private Set<UUID> neighbors;

    public static final Codec<GridNode> GRID_NODE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("identifier").forGetter(GridNode::getIdentifier),
                    UUIDUtil.CODEC.fieldOf("parentNetwork").forGetter(GridNode::getParentNetwork),
                    GridPosition.GRID_POSITION_CODEC.fieldOf("position").forGetter(GridNode::getPosition),
                    UUIDUtil.CODEC_SET.fieldOf("neighbors").forGetter(GridNode::getNeighbors)
            ).apply(instance, GridNode::new));

    public static final StreamCodec<FriendlyByteBuf, GridNode> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, GridNode::getIdentifier,
            UUIDUtil.STREAM_CODEC, GridNode::getParentNetwork,
            GridPosition.GRID_POSITION_STREAM_CODEC, GridNode::getPosition,
            ByteBufCodecs.collection(
                    HashSet::new,
                    UUIDUtil.STREAM_CODEC,
                    6
            ), GridNode::getNeighbors,
            GridNode::new
    );

    private GridNode(UUID identifier, UUID parentNetwork, GridPosition gridPos, Set<UUID> neighbors) {
        this.identifier = identifier;
        this.parentNetwork = parentNetwork;
        this.position = gridPos;
        this.neighbors = neighbors;
    }

    public GridNode(BlockPos referenceBlockPos, double gridX, double gridY, double gridZ) {
        this.neighbors = new HashSet<>();
        this.position = new GridPosition(referenceBlockPos, gridX, gridY, gridZ);
        this.identifier = UUID.randomUUID();
    }

    /**
     * Creates a new {@code GridNode} on the passed {@code GridPosition}
     * whilst also creating a new {@code GridNetwork} on the passed {@code ServerLevel}.
     * @param gridPos Position to create the node at.
     * @param level Level to create the node & network in.
     * @return The created {@code GridNode}
     */
    public static GridNode of(GridPosition gridPos, ServerLevel level) {
        var node = new GridNode(gridPos.pos(), gridPos.x(), gridPos.y(), gridPos.z());
        var storage = LevelGridStorage.get(level);
        storage.newNetwork(node);
        storage.updateSpatialIndex(gridPos, node);
        GridNetworking.updateNearbyPlayers(level, storage, gridPos);
        return node;
    }

    /**
     * Creates a new {@code GridNode} on the passed {@code GridPosition}
     * and adds it to the passed network connecting it to {@code connectTo}.
     * @param gridPos Position to create the node at.
     * @param network The network to add the node to.
     * @param level The ServerLevel the node is in.
     * @param connectTo The node to connect to (MUST BE IN SAME NETWORK AS {@code network})
     * @return The created {@code GridNode}
     */
    public static GridNode withNetwork(GridPosition gridPos, GridNetwork network, ServerLevel level, @Nonnull GridNode connectTo) {
        var node = new GridNode(gridPos.pos(), gridPos.x(), gridPos.y(), gridPos.z());
        network.addNode(node);
        if(!network.getNodes().containsValue(connectTo)) throw new IllegalArgumentException("Passed connectTo node is not in same network as passed network!");
        if(!network.addConnection(node.getIdentifier(), connectTo.getIdentifier())) throw new RuntimeException("Failed to connect both nodes");
        var storage = LevelGridStorage.get(level);
        storage.updateSpatialIndex(gridPos, node);
        GridNetworking.updateNearbyPlayers(level, storage, gridPos);
        return node;
    }

    /**
     * Creates a new {@code GridNode} on the passed {@code GridPosition}
     * and connects it to {@code connectTo}
     * @param gridPos Position to create the node at.
     * @param connectTo The grid node to connect the node to.
     * @param level The ServerLevel the node is in.
     * @return The created {@code GridNode}
     */
    public static GridNode withNetwork(GridPosition gridPos, @Nonnull GridNode connectTo, ServerLevel level) {
        var node = new GridNode(gridPos.pos(), gridPos.x(), gridPos.y(), gridPos.z());
        var storage = LevelGridStorage.get(level);
        var network = storage.getNetwork(connectTo.getParentNetwork());
        network.addNode(node);
        if(!network.addConnection(node.getIdentifier(), connectTo.getIdentifier())) throw new RuntimeException("Failed to connect both nodes");
        storage.updateSpatialIndex(gridPos, node);
        GridNetworking.updateNearbyPlayers(level, storage, gridPos);
        return node;
    }

    /**
     * Creates a new {@code GridNode} on the passed {@code GridPosition}
     * and connects it to {@code connectTo}
     * @param gridPos Position to create the node at.
     * @param connectTo The grid node to connect the node to.
     * @param level The ServerLevel the node is in.
     * @return The created {@code GridNode}
     */
    public static GridNode withNetwork(GridPosition gridPos, @Nonnull GridPosition connectTo, ServerLevel level) {
        var node = new GridNode(gridPos.pos(), gridPos.x(), gridPos.y(), gridPos.z());
        var storage = LevelGridStorage.get(level);
        GridNetwork network = null;
        if(storage.getNodeFromPos(connectTo) == null && storage.getNodeFromPos(gridPos) != null) {
            network = storage.getNetwork(storage.getNodeFromPos(gridPos));
            network.addNode(node);
            if(!network.addConnection(node.getIdentifier(), GridNode.withNetwork(connectTo, network.getIdentifier(), level).getIdentifier())) throw new RuntimeException("Failed to connect both nodes");
        } else if(storage.getNodeFromPos(connectTo) != null) {
            network = storage.getNetwork(storage.getNodeFromPos(connectTo));
            network.addNode(node);
            if(!network.addConnection(node.getIdentifier(), storage.getNodeFromPos(connectTo).getIdentifier())) throw new RuntimeException("Failed to connect both nodes");
        }
        storage.updateSpatialIndex(gridPos, node);
        GridNetworking.updateNearbyPlayers(level, storage, gridPos);
        return node;
    }

    private static GridNode withNetwork(GridPosition gridPos, @Nonnull UUID parentNetwork, ServerLevel level) {
        var node = new GridNode(gridPos.pos(), gridPos.x(), gridPos.y(), gridPos.z());
        var storage = LevelGridStorage.get(level);
        storage.getNetwork(parentNetwork).addNode(node);
        storage.updateSpatialIndex(gridPos, node);
        return node;
    }

    public void addNeighbor(UUID node) {
        this.neighbors.add(node);
    }

    public GridPosition getPosition() {
        return position;
    }

    public void setPosition(GridPosition position) {
        this.position = position;
    }

    public void addConnection(UUID otherNode) {
        this.neighbors.add(otherNode);
    }

    public void removeConnection(UUID otherNode) {
        this.neighbors.remove(otherNode);
    }

    public Set<UUID> getNeighbors() {
        return this.neighbors;
    }

    public UUID getParentNetwork() {
        return parentNetwork;
    }

    public void setParentNetwork(UUID parentNetwork) {
        this.parentNetwork = parentNetwork;
    }

    public UUID getIdentifier() {
        return identifier;
    }

}
