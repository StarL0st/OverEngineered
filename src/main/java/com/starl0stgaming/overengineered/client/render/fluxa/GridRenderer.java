package com.starl0stgaming.overengineered.client.render.fluxa;

import com.starl0stgaming.overengineered.core.fluxa.grid.GridCalculator;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridNode;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import com.mojang.datafixers.util.Pair;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class GridRenderer {
    private final HashMap<UUID, GridNode> renderNodes = new HashMap<>();
    private final Set<Pair<GridPosition, GridPosition>> connections = new HashSet<>();
    private final HashMap<UUID, Color> networkColors = new HashMap<>();

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
        this.renderNodes.forEach((uuid, node) -> {
            if(uuid.equals(identifier)) this.renderNodes.remove(node);
        });
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

    }

    public void onTick() {
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

    }



}
