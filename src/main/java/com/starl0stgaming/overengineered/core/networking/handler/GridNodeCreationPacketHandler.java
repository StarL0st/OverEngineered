package com.starl0stgaming.overengineered.core.networking.handler;

import com.starl0stgaming.overengineered.core.fluxa.LevelGridStorage;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridNode;
import com.starl0stgaming.overengineered.core.networking.payload.CreateGridNodePayload;
import com.starl0stgaming.overengineered.util.OEUtil;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class GridNodeCreationPacketHandler {
    public static final GridNodeCreationPacketHandler INSTANCE = new GridNodeCreationPacketHandler();

    public static GridNodeCreationPacketHandler get() {
        return INSTANCE;
    }

    public void handle(final CreateGridNodePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            payload.otherPos()
                    .ifPresentOrElse(
                            (pos) -> {
                                GridNode.withNetwork(pos, payload.position(), OEUtil.getLevelByKey(payload.levelKey()));
                            },
                            () -> {
                                GridNode.of(payload.position(), OEUtil.getLevelByKey(payload.levelKey()));
                            }
                    );
        });
    }
}
