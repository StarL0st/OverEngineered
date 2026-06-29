package com.starl0stgaming.overengineered.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridCalculator;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class OEUtil {
    public static ServerLevel getLevelByKey(ResourceKey<Level> key) {
        assert ServerLifecycleHooks.getCurrentServer() != null;
        return ServerLifecycleHooks.getCurrentServer().getLevel(key);
    }

    public static Set<Player> getNearbyPlayers(Vec3 pos, ServerLevel level, double radius) {
        return new HashSet<>(level.getEntitiesOfClass(
                Player.class,
                new AABB(pos, pos).inflate(radius),
                player -> player.position().distanceToSqr(pos) <= radius * radius
        ));
    }

    public static Set<Player> getNearbyPlayers(GridPosition pos, ServerLevel level, double radius) {
        return getNearbyPlayers(GridCalculator.gridPosToWorldPos(pos), level, radius);
    }

    /**
     * NEOFORGE 26.2.x CODE
     * Creates a codec for an {@linkplain Codec#unboundedMap(Codec, Codec) unbounded map} whose underlying representation is a list of maps, with the given names for each key-element entry. Each key-element entry is encoded as a map with the given key and element names respectively.
     * <p>
     * This is useful for maps where the key does not encode to a string, which causes errors when trying to serialize to a format that requires maps to have string keys (such as JSON and NBT).
     *
     * @param keyName      the name of the key in the encoded map for each key-element entry
     * @param keyCodec     codec for the key
     * @param elementName  the name of the element in the encoded map for each key-element entry
     * @param elementCodec codec for the element
     * @param <K>          the key type
     * @param <V>          the element type
     * @return a codec for an unbounded map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Codec<Map<K, V>> unboundedMapAsList(String keyName, Codec<K> keyCodec, String elementName, Codec<V> elementCodec) {
        Codec<Map.Entry<K, V>> entryCodec = RecordCodecBuilder.create(
                instance -> instance.group(
                        keyCodec.fieldOf(keyName).forGetter(Map.Entry::getKey),
                        elementCodec.fieldOf(elementName).forGetter(Map.Entry::getValue)).apply(instance, Map::entry));
        return Codec.list(entryCodec)
                .xmap(
                        entries -> Map.ofEntries(entries.toArray(Map.Entry[]::new)),
                        map -> List.copyOf(map.entrySet()));
    }
}
