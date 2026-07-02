package com.starl0stgaming.overengineered.client.render.fluxa;

import com.starl0stgaming.overengineered.core.fluxa.grid.GridCalculator;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridNode;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import com.mojang.datafixers.util.Pair;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class GridRenderer {
    private final HashMap<UUID, GridNode> renderNodes = new HashMap<>();
    private final Set<Pair<GridPosition, GridPosition>> connections = new HashSet<>();
    private final HashMap<UUID, Color> networkColors = new HashMap<>();

    private static final int DEFAULT_TEMP_TIMEOUT = 20;

    private final Map<GridPosition, TempNode> temporaryNodes = new HashMap<>();
    private final Set<TempConnection> temporaryConnections = new HashSet<>();

    private int clientTick;

    private static final GridRenderer instance = new GridRenderer();
    public static GridRenderer getInstance() { return instance; }

    public GridRenderer() {
    }

    public void addRenderNode(GridNode node) {
        this.renderNodes.put(node.getIdentifier(), node);
    }

    public HashMap<UUID, GridNode> getRenderNodes() {
        return this.renderNodes;
    }

    public void removeRenderNode(GridNode node) {
        this.renderNodes.remove(node);
    }

    public void removeRenderNode(UUID identifier) {
        this.renderNodes.remove(identifier);
    }

    public void addTemporaryNode(GridPosition pos, UUID network) {
        addTemporaryNode(pos, network, DEFAULT_TEMP_TIMEOUT);
    }

    public void addTemporaryNode(GridPosition pos, UUID network, Integer timeoutTicks) {
        temporaryNodes.put(
                pos,
                new TempNode(
                        pos,
                        network,
                        clientTick + (timeoutTicks == null ? DEFAULT_TEMP_TIMEOUT : timeoutTicks)
                )
        );
    }

    public void addTemporaryNode(
            GridPosition position,
            ConnectionTarget connectTo) {

        addTemporaryNode(position, connectTo.network());

        addTemporaryConnection(
                connectTo.position(),
                position,
                connectTo.network());
    }

    public void addTemporaryNode(
            GridPosition position,
            GridNode connectTo) {

        addTemporaryNode(
                position,
                getConnectionTarget(connectTo)
        );
    }

    public void addTemporaryNode(
            GridPosition position,
            GridPosition connectTo,
            UUID network) {

        addTemporaryNode(position, network);
        addTemporaryConnection(connectTo, position, network);
    }

    public void addTemporaryNode(
            GridPosition position,
            GridPosition connectTo) {

        GridNode node = renderNodes.values().stream()
                .filter(n -> n.getPosition().equals(connectTo))
                .findFirst()
                .orElse(null);

        if (node == null)
            return;

        addTemporaryNode(position, node);
    }

    public void removeTemporaryNode(GridPosition pos) {
        temporaryNodes.remove(pos);

        temporaryConnections.removeIf(c ->
                c.from().equals(pos) || c.to().equals(pos));
    }

    public void clearTemporaryPreview() {
        temporaryNodes.clear();
        temporaryConnections.clear();
    }

    public void updateTemporaryNode(
            GridPosition oldPos,
            GridPosition newPos,
            GridNode connectTo) {

        removeTemporaryNode(oldPos);
        addTemporaryNode(newPos, connectTo);
    }

    public void keepAlive(GridPosition pos) {
        TempNode node = temporaryNodes.get(pos);

        if (node == null)
            return;

        temporaryNodes.put(
                pos,
                new TempNode(
                        pos,
                        node.network(),
                        clientTick + DEFAULT_TEMP_TIMEOUT
                )
        );
    }

    public void addTemporaryConnection(
            GridPosition from,
            GridPosition to,
            UUID network) {

        addTemporaryConnection(from, to, network, DEFAULT_TEMP_TIMEOUT);
    }

    public void addTemporaryConnection(
            GridPosition from,
            GridPosition to,
            UUID network,
            Integer timeoutTicks) {

        temporaryConnections.add(
                new TempConnection(
                        from,
                        to,
                        network,
                        clientTick + (timeoutTicks == null ? DEFAULT_TEMP_TIMEOUT : timeoutTicks)
                )
        );
    }

    public void clearTemporaryNetwork(UUID network) {
        temporaryNodes.values().removeIf(n -> n.network().equals(network));
        temporaryConnections.removeIf(c -> c.network().equals(network));
    }

    public ConnectionTarget getConnectionTarget(GridNode node) {
        return new ConnectionTarget(
                node.getPosition(),
                node.getParentNetwork()
        );
    }

    public ConnectionTarget getConnectionTarget(UUID nodeId) {
        GridNode node = renderNodes.get(nodeId);
        if (node == null)
            return null;

        return getConnectionTarget(node);
    }

    /**
     * Checks against local connection storage to determine which are new or old connections.
     * If the connection doesn't exist locally, a new one is created BASED on the GridNode
     * If the connection does exist locally between the two same nodes, it is skipped.
     */
    public void refreshConnections() {
        this.connections.clear();
        for(GridNode node : renderNodes.values()) {
            for (UUID neighborId : node.getNeighbors()) {
                GridNode neighbor = renderNodes.get(neighborId);
                if(neighbor == null || neighbor == node) {
                    continue;
                }

                Pair<GridPosition, GridPosition> connection =
                        new Pair<>(node.getPosition(), neighbor.getPosition());

                Pair<GridPosition, GridPosition> reverse =
                        new Pair<>(neighbor.getPosition(), node.getPosition());

                if (!connections.contains(connection) && !connections.contains(reverse)) {
                    connections.add(connection);
                }
            }
        }
    }

    public void clearAll() {
        this.renderNodes.clear();
        this.connections.clear();
        this.temporaryNodes.clear();
        this.temporaryConnections.clear();
    }

    public void renderGridOverlay(BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        Direction face = hit.getDirection();

        Vec3 origin = Vec3.atLowerCornerOf(pos);

        final double step = 1.0 / 8.0;
        final double offset = 0.001; // Prevent z-fighting

        for (int i = 0; i <= 8; i++) {
            double t = i * step;

            Vec3 start1 = null, end1 = null;
            Vec3 start2 = null, end2 = null;

            switch (face) {
                case UP -> {
                    double y = 1 + offset;

                    // X lines
                    start1 = origin.add(t, y, 0);
                    end1   = origin.add(t, y, 1);

                    // Z lines
                    start2 = origin.add(0, y, t);
                    end2   = origin.add(1, y, t);
                }

                case DOWN -> {
                    double y = -offset;

                    start1 = origin.add(t, y, 0);
                    end1   = origin.add(t, y, 1);

                    start2 = origin.add(0, y, t);
                    end2   = origin.add(1, y, t);
                }

                case NORTH -> {
                    double z = -offset;

                    start1 = origin.add(t, 0, z);
                    end1   = origin.add(t, 1, z);

                    start2 = origin.add(0, t, z);
                    end2   = origin.add(1, t, z);
                }

                case SOUTH -> {
                    double z = 1 + offset;

                    start1 = origin.add(t, 0, z);
                    end1   = origin.add(t, 1, z);

                    start2 = origin.add(0, t, z);
                    end2   = origin.add(1, t, z);
                }

                case WEST -> {
                    double x = -offset;

                    start1 = origin.add(x, 0, t);
                    end1   = origin.add(x, 1, t);

                    start2 = origin.add(x, t, 0);
                    end2   = origin.add(x, t, 1);
                }

                case EAST -> {
                    double x = 1 + offset;

                    start1 = origin.add(x, 0, t);
                    end1   = origin.add(x, 1, t);

                    start2 = origin.add(x, t, 0);
                    end2   = origin.add(x, t, 1);
                }
            }

            Outliner.getInstance()
                    .showLine("grid_a_" + face + "_" + i, start1, end1)
                    .lineWidth(1/128f)
                    .colored(Color.TRANSPARENT_BLACK);

            Outliner.getInstance()
                    .showLine("grid_b_" + face + "_" + i, start2, end2)
                    .lineWidth(1/128f)
                    .colored(Color.TRANSPARENT_BLACK);
        }
    }

    public void onTick() {
        clientTick++;

        temporaryConnections.removeIf(c ->
                c.expiryTick() <= clientTick);

        for(GridNode node : renderNodes.values()) {
            var pos = GridCalculator.gridPosToWorldPos(node.getPosition());
            var colortemp = Color.generateFromLong(new Random().nextLong());
            if(!networkColors.containsKey(node.getParentNetwork())) {
                this.networkColors.put(node.getParentNetwork(), colortemp);
            } else {
                colortemp = this.networkColors.get(node.getParentNetwork());
            }

            Outliner.getInstance().showAABB(
                    "render_node_" + node.getIdentifier().toString(),
                    new AABB(
                            pos.x,
                            pos.y,
                            pos.z,
                            pos.x + GridCalculator.GRID_WIDTH,
                            pos.y + GridCalculator.GRID_WIDTH,
                            pos.z + GridCalculator.GRID_WIDTH
                    )).colored(colortemp).disableLineNormals().lineWidth(1/64f);

        }

        for (TempNode node : temporaryNodes.values()) {

            Vec3 pos = GridCalculator.gridPosToWorldPos(node.position());

            Color color = networkColors.computeIfAbsent(
                    node.network(),
                    id -> Color.generateFromLong(new Random().nextLong()));

            Outliner.getInstance().showAABB(
                            "temp_node_" + node.position().hashCode(),
                            new AABB(
                                    pos.x,
                                    pos.y,
                                    pos.z,
                                    pos.x + GridCalculator.GRID_WIDTH,
                                    pos.y + GridCalculator.GRID_WIDTH,
                                    pos.z + GridCalculator.GRID_WIDTH
                            )
                    ).colored(color)
                    .disableLineNormals()
                    .lineWidth(1 / 64f);
        }

        for (Pair<GridPosition, GridPosition> connection : connections) {
            GridPosition fromPos = connection.getFirst();
            GridPosition toPos = connection.getSecond();

            // Find either endpoint's node to determine the network color.
            GridNode node = Objects.requireNonNull(renderNodes.entrySet().stream()
                    .filter(entry -> entry.getValue().getPosition().equals(fromPos))
                    .findFirst()
                    .orElse(null)).getValue();

            if (node == null) {
                continue;
            }

            Color color = networkColors.get(node.getParentNetwork());
            if (color == null) {
                continue;
            }

            // Center of each grid node.
            Vec3 from = GridCalculator.gridPosToWorldPos(fromPos)
                    .add(
                            GridCalculator.GRID_WIDTH / 2.0,
                            GridCalculator.GRID_WIDTH / 2.0,
                            GridCalculator.GRID_WIDTH / 2.0
                    );

            Vec3 to = GridCalculator.gridPosToWorldPos(toPos)
                    .add(
                            GridCalculator.GRID_WIDTH / 2.0,
                            GridCalculator.GRID_WIDTH / 2.0,
                            GridCalculator.GRID_WIDTH / 2.0
                    );

            Outliner.getInstance().showLine(
                    new Pair<>(fromPos, toPos),
                    from,
                    to
                   ).lineWidth(1/64f).colored(color);
        }

        for (TempConnection connection : temporaryConnections) {

            Color color = networkColors.computeIfAbsent(
                    connection.network(),
                    id -> Color.generateFromLong(new Random().nextLong()));

            if(color == null)
                continue;

            Vec3 from = GridCalculator.gridPosToWorldPos(connection.from())
                    .add(
                            GridCalculator.GRID_WIDTH / 2.0,
                            GridCalculator.GRID_WIDTH / 2.0,
                            GridCalculator.GRID_WIDTH / 2.0);

            Vec3 to = GridCalculator.gridPosToWorldPos(connection.to())
                    .add(
                            GridCalculator.GRID_WIDTH / 2.0,
                            GridCalculator.GRID_WIDTH / 2.0,
                            GridCalculator.GRID_WIDTH / 2.0);

            Outliner.getInstance().showLine(
                            new Pair<>(connection.from(), connection.to()),
                            from,
                            to
                    ).colored(color)
                    .lineWidth(1 / 64f);
        }

    }

    public record ConnectionTarget(
            GridPosition position,
            UUID network
    ) {}

    private record TempNode(
            GridPosition position,
            UUID network,
            int expiryTick
    ) {}

    private record TempConnection(
            GridPosition from,
            GridPosition to,
            UUID network,
            int expiryTick
    ) {}
}
