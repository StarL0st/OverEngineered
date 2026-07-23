package com.starl0stgaming.overengineered.core.networking;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.networking.handler.ClientUpdateRenderNodesHandler;
import com.starl0stgaming.overengineered.core.networking.handler.GridNodeCreationPacketHandler;
import com.starl0stgaming.overengineered.core.networking.handler.ReceiveNodePacketHandler;
import com.starl0stgaming.overengineered.core.networking.handler.RequestNodeUpdateHandler;
import com.starl0stgaming.overengineered.core.networking.payload.CreateGridNodePayload;
import com.starl0stgaming.overengineered.core.networking.payload.RequestLookingAtPayload;
import com.starl0stgaming.overengineered.core.networking.payload.UpdateClientLookingAtPayload;
import com.starl0stgaming.overengineered.core.networking.payload.UpdateClientRenderNodesPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketRegistry {

    public static void onPayloadRegistrar(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Overengineered.MODID);

        registrar.playToServer(
                CreateGridNodePayload.TYPE,
                CreateGridNodePayload.STREAM_CODEC,
                GridNodeCreationPacketHandler.get()::handle
        );

        registrar.playToClient(
                UpdateClientRenderNodesPayload.TYPE,
                UpdateClientRenderNodesPayload.STREAM_CODEC,
                ClientUpdateRenderNodesHandler.get()::handle
        );

        registrar.playToServer(
                RequestLookingAtPayload.TYPE,
                RequestLookingAtPayload.STREAM_CODEC,
                RequestNodeUpdateHandler.get()::handle
        );

        registrar.playToClient(
                UpdateClientLookingAtPayload.TYPE,
                UpdateClientLookingAtPayload.STREAM_CODEC,
                ReceiveNodePacketHandler.get()::handle
        );
    }
}
