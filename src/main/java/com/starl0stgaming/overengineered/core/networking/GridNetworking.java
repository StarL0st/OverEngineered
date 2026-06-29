package com.starl0stgaming.overengineered.core.networking;

import com.starl0stgaming.overengineered.core.fluxa.LevelGridStorage;

import com.starl0stgaming.overengineered.core.fluxa.grid.GridCalculator;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridNode;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import com.starl0stgaming.overengineered.util.OEUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GridNetworking {

    /**
     * Radius to utilize when checking for nearby players
     */
    public static final double SPATIAL_LOOKUP_RADIUS = 16.0f;

    public static void updateNearbyPlayers(ServerLevel level, GridPosition position) {
        updateNearbyPlayers(level, LevelGridStorage.get(level), position);
    }

    public static void updateNearbyPlayers(ServerLevel level, LevelGridStorage storage, GridPosition position) {
        var players = OEUtil.getNearbyPlayers(
                position, level, SPATIAL_LOOKUP_RADIUS
        );
        players.forEach((player -> {
            var positions = GridCalculator.getNodesInRadius(level, player.position(), SPATIAL_LOOKUP_RADIUS);
            var nodes = positions.stream()
                    .map(storage::getNodeFromPos)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            storage.updatePlayer((ServerPlayer) player, nodes);
        }));
    }

    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if(event.getLevel().isClientSide) return;
        if(!(event.getEntity() instanceof Player)) return;
        var level = (ServerLevel) event.getLevel();
        var player = (ServerPlayer) event.getEntity();
        var positions = GridCalculator.getNodesInRadius(level, player.position(), SPATIAL_LOOKUP_RADIUS);
        var nodes = positions.stream()
                .map(LevelGridStorage.get(level)::getNodeFromPos)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        LevelGridStorage.get(level).updatePlayer(player, nodes);
    }

    public static void onLeaveLevel(EntityLeaveLevelEvent event) {
        if(event.getLevel().isClientSide) return;
        if(!(event.getEntity() instanceof Player)) return;
        var level = (ServerLevel) event.getLevel();
        LevelGridStorage.get(level).clearPlayer((ServerPlayer) event.getEntity());
    }
 }
