package com.starl0stgaming.overengineered.core.networking.handler;

import com.starl0stgaming.overengineered.client.render.fluxa.GridRenderer;
import com.starl0stgaming.overengineered.core.networking.payload.UpdateClientRenderNodesPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientUpdateRenderNodesHandler {
    public static final ClientUpdateRenderNodesHandler INSTANCE = new ClientUpdateRenderNodesHandler();

    public static ClientUpdateRenderNodesHandler get() {
        return INSTANCE;
    }

    public void handle(final UpdateClientRenderNodesPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            GridRenderer.getInstance().clearAll();
            payload.renderNodes().forEach(GridRenderer.getInstance()::addRenderNode);
            GridRenderer.getInstance().refreshConnections();
        });
    }
}
