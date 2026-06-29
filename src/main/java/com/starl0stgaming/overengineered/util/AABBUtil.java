package com.starl0stgaming.overengineered.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class AABBUtil {
    public static final Codec<AABB> AABB_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("min_x").forGetter(aabb -> aabb.minX ),
                    Codec.DOUBLE.fieldOf("min_y").forGetter(aabb -> aabb.minY ),
                    Codec.DOUBLE.fieldOf("min_z").forGetter(aabb -> aabb.minZ ),
                    Codec.DOUBLE.fieldOf("max_x").forGetter(aabb -> aabb.maxX ),
                    Codec.DOUBLE.fieldOf("max_y").forGetter(aabb -> aabb.maxY ),
                    Codec.DOUBLE.fieldOf("max_z").forGetter(aabb -> aabb.maxZ )
            ).apply(instance, AABB::new));
}
