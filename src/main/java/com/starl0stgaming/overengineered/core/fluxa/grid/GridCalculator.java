package com.starl0stgaming.overengineered.core.fluxa.grid;

import com.starl0stgaming.overengineered.core.fluxa.LevelGridStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

public class GridCalculator {

    public static final double GRID_WIDTH = 0.125;

    /**
     *
     * @param hitResult player hitresult
     * @return {@code Vec3} Nearest BOTTOM corner of the grid node
     */
    public static Vec3 getNearestWorldPos(HitResult hitResult) {
        return getNearestWorldPos(hitResult.getLocation());
    }

    /**
     *
     * @param pos {@code Vec3} pos to check
     * @return {@code Vec3} Nearest BOTTOM corner of the grid node
     */
    public static Vec3 getNearestWorldPos(Vec3 pos) {
        return new Vec3(
                Math.floor(pos.x / GRID_WIDTH) * GRID_WIDTH,
                Math.floor(pos.y / GRID_WIDTH) * GRID_WIDTH,
                Math.floor(pos.z / GRID_WIDTH) * GRID_WIDTH
        );
    }

    /**
     *
     * @param pos World pos
     * @return {@code Vec3} Nearest grid position within the containing block.
     */
    public static Vec3 getNearestGridPos(Vec3 pos) {
        return new Vec3(
                Math.round((pos.x - Math.floor(pos.x)) / GRID_WIDTH),
                Math.round((pos.y - Math.floor(pos.y)) / GRID_WIDTH),
                Math.round((pos.z - Math.floor(pos.z)) / GRID_WIDTH)
        );
    }

    /**
     * @param blockPos World block pos to check for.
     * @param pos World pos
     * @return {@code Vec3} Nearest grid position within the provided block.
     */
    public static Vec3 getNearestGridPos(BlockPos blockPos, Vec3 pos) {
        return new Vec3(
                Math.round((pos.x - blockPos.getX()) / GRID_WIDTH),
                Math.round((pos.y - blockPos.getY()) / GRID_WIDTH),
                Math.round((pos.z - blockPos.getZ()) / GRID_WIDTH)
        );
    }

    /**
     * Converts grid pos to physical world pos
     * @param blockPos reference block pos
     * @param gridPos gridpos to convert (x, y, z are within 0-8, depending on {@code GRID_WIDTh})
     * @return {@code Vec3} Bottom corner pos of the grid pos in world pos.
      */
    public static Vec3 gridPosToWorldPos(BlockPos blockPos, Vec3 gridPos) {
        return new Vec3(
            blockPos.getX() + gridPos.x * GRID_WIDTH,
                blockPos.getY() + gridPos.y * GRID_WIDTH,
                blockPos.getZ() + gridPos.z * GRID_WIDTH
        );
    }

    public static Vec3 gridPosToWorldPos(GridPosition gridPos) {
        return new Vec3(
                gridPos.pos().getX() + gridPos.x() * GRID_WIDTH,
                gridPos.pos().getY() + gridPos.y() * GRID_WIDTH,
                gridPos.pos().getZ() + gridPos.z() * GRID_WIDTH
        );
    }

    /**
     * Converts a world position into the nearest GridPosition.
     *
     * The grid coordinates are in the range [0, 8], where
     * 0 is the block's minimum face and 8 is the maximum face.
     * @param worldPos The {@code Vec3} to convert from.
     * @return {@code GridPosition} The converted position.
     */
    public static GridPosition toGridPosition(Vec3 worldPos) {
        BlockPos blockPos = BlockPos.containing(worldPos);

        double gridX = Math.round((worldPos.x - blockPos.getX()) / GRID_WIDTH);
        double gridY = Math.round((worldPos.y - blockPos.getY()) / GRID_WIDTH);
        double gridZ = Math.round((worldPos.z - blockPos.getZ()) / GRID_WIDTH);

        // Clamp in case floating-point precision produces 9 or -1.
        gridX = Mth.clamp(gridX, 0, 8);
        gridY = Mth.clamp(gridY, 0, 8);
        gridZ = Mth.clamp(gridZ, 0, 8);

        return new GridPosition(blockPos, gridX, gridY, gridZ);
    }

    public static Set<GridPosition> getNodesInRadius(ServerLevel level, Vec3 center, double radius) {
        LevelGridStorage storage = LevelGridStorage.get(level);

        double radiusSqr = radius * radius;

        return storage.getSpatialNodeIndex().keySet().stream()
                .filter(gridPos ->
                        gridPosToWorldPos(gridPos).distanceToSqr(center) <= radiusSqr)
                .collect(Collectors.toSet());
    }

    /**
     * Determines whether two grid positions are aligned along a single axis.
     *
     * <p>The positions are considered aligned only if exactly one axis differs
     * while the other two are identical. If aligned, the returned
     * {@link Direction} indicates the direction from {@code from} to {@code to}.
     * If the positions are identical or diagonal to each other, {@code null} is
     * returned.</p>
     *
     * @param from The starting grid position.
     * @param to   The target grid position.
     * @return The direction from {@code from} to {@code to}, or {@code null} if
     * they are not aligned on a single axis.
     */
    @Nullable
    public static Direction getDirectionBetween(GridPosition from, GridPosition to) {
        return getDirectionBetween(
                gridPosToWorldPos(from),
                gridPosToWorldPos(to)
        );
    }

    /**
     * Determines whether a grid position and a world position are aligned along a
     * single axis.
     *
     * @param from The starting grid position.
     * @param to   The target world position.
     * @return The direction from {@code from} to {@code to}, or {@code null} if
     * they are not aligned on a single axis.
     */
    @Nullable
    public static Direction getDirectionBetween(GridPosition from, Vec3 to) {
        return getDirectionBetween(gridPosToWorldPos(from), to);
    }

    /**
     * Determines whether a world position and a grid position are aligned along a
     * single axis.
     *
     * @param from The starting world position.
     * @param to   The target grid position.
     * @return The direction from {@code from} to {@code to}, or {@code null} if
     * they are not aligned on a single axis.
     */
    @Nullable
    public static Direction getDirectionBetween(Vec3 from, GridPosition to) {
        return getDirectionBetween(from, gridPosToWorldPos(to));
    }

    /**
     * Determines whether two world positions are aligned along a single axis.
     *
     * <p>The positions are considered aligned only if exactly one coordinate
     * differs while the remaining two coordinates are equal. The returned
     * {@link Direction} indicates the direction from {@code from} to
     * {@code to}.</p>
     *
     * @param from The starting world position.
     * @param to   The target world position.
     * @return The direction from {@code from} to {@code to}, or {@code null} if
     * they are not aligned on a single axis.
     */
    @Nullable
    public static Direction getDirectionBetween(Vec3 from, Vec3 to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;

        boolean xDiff = !Mth.equal((float) dx, 0.0F);
        boolean yDiff = !Mth.equal((float) dy, 0.0F);
        boolean zDiff = !Mth.equal((float) dz, 0.0F);

        if ((xDiff ? 1 : 0) + (yDiff ? 1 : 0) + (zDiff ? 1 : 0) != 1) {
            return null;
        }

        if (xDiff) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        }

        if (yDiff) {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        }

        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }
}
