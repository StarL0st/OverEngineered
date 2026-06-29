package com.starl0stgaming.overengineered.core.networking.payload;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record CreateGridNodePayload(
        ResourceKey<Level> levelKey,
        GridPosition position,
        Optional<GridPosition> otherPos
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CreateGridNodePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Overengineered.MODID, "create_grid_node")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CreateGridNodePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), CreateGridNodePayload::levelKey,
            GridPosition.GRID_POSITION_STREAM_CODEC, CreateGridNodePayload::position,
            GridPosition.GRID_POSITION_STREAM_CODEC.apply(ByteBufCodecs::optional), CreateGridNodePayload::otherPos,
            CreateGridNodePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
