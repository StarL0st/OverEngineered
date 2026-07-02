package com.starl0stgaming.overengineered.core.networking.payload;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridNode;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridPosition;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public record UpdateClientRenderNodesPayload(
        Set<GridNode> renderNodes
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateClientRenderNodesPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Overengineered.MODID, "update_client_render_nodes")
    );

    public static final StreamCodec<FriendlyByteBuf, UpdateClientRenderNodesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(
                    HashSet::new,
                    GridNode.STREAM_CODEC,
                    16545
            ), UpdateClientRenderNodesPayload::renderNodes,
            UpdateClientRenderNodesPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
