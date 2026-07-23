package com.starl0stgaming.overengineered.core.networking.handler;

import com.starl0stgaming.overengineered.core.fluxa.LevelGridStorage;
import com.starl0stgaming.overengineered.core.networking.payload.RequestLookingAtPayload;
import com.starl0stgaming.overengineered.core.networking.payload.UpdateClientLookingAtPayload;
import com.starl0stgaming.overengineered.util.OEUtil;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestNodeUpdateHandler {
    public static final RequestNodeUpdateHandler INSTANCE = new RequestNodeUpdateHandler();

    public static RequestNodeUpdateHandler get() {
        return INSTANCE;
    }

    public void handle(final RequestLookingAtPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = OEUtil.getLevelByKey(payload.levelResourceKey());
            var node = LevelGridStorage.get(level).getNodeFromPos(payload.position());
            if(node != null) {
                PacketDistributor.sendToPlayer(
                        (ServerPlayer) level.getPlayerByUUID(payload.requester()),
                        new UpdateClientLookingAtPayload(node)
                );
            } else {
                PacketDistributor.sendToPlayer(
                        (ServerPlayer) level.getPlayerByUUID(payload.requester()),
                        new UpdateClientLookingAtPayload(null)
                );
            }
        });
    }
}
