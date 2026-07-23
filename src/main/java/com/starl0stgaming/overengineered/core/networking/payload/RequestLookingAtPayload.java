package com.starl0stgaming.overengineered.core.networking.payload;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record RequestLookingAtPayload(
        GridPosition position,
        ResourceKey<Level> levelResourceKey,
        UUID requester
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestLookingAtPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Overengineered.MODID, "request_node_update_payload")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestLookingAtPayload> STREAM_CODEC = StreamCodec.composite(
            GridPosition.GRID_POSITION_STREAM_CODEC, RequestLookingAtPayload::position,
            ResourceKey.streamCodec(Registries.DIMENSION), RequestLookingAtPayload::levelResourceKey,
            UUIDUtil.STREAM_CODEC, RequestLookingAtPayload::requester,
            RequestLookingAtPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type();
    }
}
