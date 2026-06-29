package com.starl0stgaming.overengineered.core.fluxa.network;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridCalculator;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridNode;

import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import com.starl0stgaming.overengineered.util.AABBUtil;
import com.starl0stgaming.overengineered.util.OEUtil;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.stream.Collectors;

public class GridNetwork {
    private HashMap<UUID, GridNode> nodes;
    public UUID identifier;

    private HashMap<UUIDPair, AABB> connectionAABBs;

    public static final Codec<GridNetwork> GRID_NETWORK_CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, GridNode.GRID_NODE_CODEC).fieldOf("nodes").forGetter(GridNetwork::getNodes),
                UUIDUtil.CODEC.fieldOf("identifier").forGetter(GridNetwork::getIdentifier),
                OEUtil.unboundedMapAsList("connection_pair", UUIDPair.CODEC, "aabb", AABBUtil.AABB_CODEC).fieldOf("connectionAABBs").forGetter(GridNetwork::getConnectionAABBs)
        ).apply(instance, GridNetwork::new));


    private GridNetwork(Map<UUID, GridNode> nodes, UUID identifier, Map<UUIDPair, AABB> connectionAABBs) {
        this.nodes = new HashMap<>(nodes);
        this.identifier = identifier;
        this.connectionAABBs = new HashMap<>(connectionAABBs);
    }

    public GridNetwork() {
        this.nodes = new HashMap<>();
        this.connectionAABBs = new HashMap<>();

        this.identifier = UUID.randomUUID();
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public HashMap<UUID, GridNode> getNodes() {
        return nodes;
    }

    public HashMap<UUIDPair, AABB> getConnectionAABBs() {
        return connectionAABBs;
    }

    public Set<UUIDPair> connectionPairs() {
        return new HashSet<>(this.connectionAABBs.keySet());
    }

    public UUIDPair checkIntercept(Vec3 pos) {
        return this.connectionAABBs.entrySet().stream()
                .filter(entry -> entry.getValue().intersects(pos, pos))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public boolean addConnection(UUID id1, UUID id2) {
        if(!this.nodes.containsKey(id1) && !this.nodes.containsKey(id2)) {
            Overengineered.LOGGER.error("[NETWORK] ERROR: Neither nodes are in network! (" + this.getIdentifier().toString() +")");
            return false;
        }

        var node1 = this.nodes.get(id1);
        var node2 = this.nodes.get(id2);

        var pos1 = GridCalculator.gridPosToWorldPos(node1.getPosition());
        var pos2 = GridCalculator.gridPosToWorldPos(node2.getPosition());

        if(GridCalculator.getDirectionBetween(pos1, pos2) == null) return false;

        node1.addNeighbor(node2.getIdentifier());
        node2.addNeighbor(node1.getIdentifier());

        AABB connectionAABB = new AABB(
                pos1.x,
                pos1.y,
                pos1.z,
                pos2.x + GridCalculator.GRID_WIDTH,
                pos2.y + GridCalculator.GRID_WIDTH,
                pos2.z + GridCalculator.GRID_WIDTH
        );
        this.connectionAABBs.put(new UUIDPair(node1.getIdentifier(), node2.getIdentifier()), connectionAABB);
        return true;
    }

    private void handleMergingConnection(UUID ourNode, UUID otherNode) {

    }

    public boolean addNode(GridNode node) {
        node.setParentNetwork(this.identifier);
        return this.nodes.put(node.getIdentifier(), node) != null;
    }

    public boolean removeNode(UUID identifier) {
        nodes.get(identifier).setParentNetwork(null);
        return this.nodes.remove(identifier) instanceof GridNode;
    }

    public GridNode getNode(UUID identifier) {
        return this.nodes.get(identifier);
    }

    public boolean hasNode(UUID identifier) {
        return this.nodes.containsKey(identifier);
    }

    public boolean hasNode(GridNode node) {
        return this.nodes.containsValue(node);
    }

    public record UUIDPair(UUID first, UUID second) {
        public static final Codec<UUIDPair> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        UUIDUtil.CODEC.fieldOf("first").forGetter(UUIDPair::first),
                        UUIDUtil.CODEC.fieldOf("second").forGetter(UUIDPair::second)
                ).apply(instance, UUIDPair::new));
    }
}
