package com.starl0stgaming.overengineered.core.events;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.client.OverengineeredClient;
import com.starl0stgaming.overengineered.client.render.fluxa.GridRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = Overengineered.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        OverengineeredClient.OUTLINE_RENDERER.renderOutlines(event.getPoseStack(), event.getCamera());
    }

    @SubscribeEvent
    public static void onTickPre(ClientTickEvent.Pre event) {
        GridRenderer.getInstance().onTick();
    }
}
