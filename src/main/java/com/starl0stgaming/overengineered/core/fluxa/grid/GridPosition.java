package com.starl0stgaming.overengineered.core.fluxa.grid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GridPosition(
        BlockPos pos,
        double x,
        double y,
        double z
) {
    public static final Codec<GridPosition> GRID_POSITION_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(GridPosition::pos),
                    Codec.DOUBLE.fieldOf("x").forGetter(GridPosition::x),
                    Codec.DOUBLE.fieldOf("y").forGetter(GridPosition::y),
                    Codec.DOUBLE.fieldOf("z").forGetter(GridPosition::z)
            ).apply(instance, GridPosition::new));

    public static final StreamCodec<FriendlyByteBuf, GridPosition> GRID_POSITION_STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, GridPosition::pos,
            ByteBufCodecs.DOUBLE, GridPosition::x,
            ByteBufCodecs.DOUBLE, GridPosition::y,
            ByteBufCodecs.DOUBLE, GridPosition::z,
            GridPosition::new
    );
}
