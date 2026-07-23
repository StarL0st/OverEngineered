package com.starl0stgaming.overengineered.core.networking.payload;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridNode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record UpdateClientLookingAtPayload(
       @Nullable GridNode node
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateClientLookingAtPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Overengineered.MODID, "send_node_to_client_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, UpdateClientLookingAtPayload> STREAM_CODEC = StreamCodec.composite(
            GridNode.STREAM_CODEC, UpdateClientLookingAtPayload::node,
            UpdateClientLookingAtPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type();
    }
}
