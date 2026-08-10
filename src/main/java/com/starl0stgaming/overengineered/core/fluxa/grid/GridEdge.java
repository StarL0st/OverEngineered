package com.starl0stgaming.overengineered.core.fluxa.grid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starl0stgaming.overengineered.util.AABBUtil;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public record GridEdge(UUID a, UUID b, AABB bounds, ResourceLocation material) {
    public static final Codec<GridEdge> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("a").forGetter(GridEdge::a),
                    UUIDUtil.CODEC.fieldOf("b").forGetter(GridEdge::b),
                    AABBUtil.AABB_CODEC.fieldOf("bounds").forGetter(GridEdge::bounds),
                    ResourceLocation.CODEC.fieldOf("material").forGetter(GridEdge::material)
            ).apply(instance, GridEdge::new));
}
