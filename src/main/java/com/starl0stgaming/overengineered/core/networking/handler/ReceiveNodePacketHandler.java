package com.starl0stgaming.overengineered.core.networking.handler;

import com.starl0stgaming.overengineered.core.networking.payload.UpdateClientLookingAtPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ReceiveNodePacketHandler {
    public static final ReceiveNodePacketHandler INSTANCE = new ReceiveNodePacketHandler();

    public static ReceiveNodePacketHandler get() {
        return INSTANCE;
    }

    public void handle(final UpdateClientLookingAtPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {

        });
    }
}
