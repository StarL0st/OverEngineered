package com.starl0stgaming.overengineered.core.events;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.client.OverengineeredClient;
import com.starl0stgaming.overengineered.client.render.fluxa.GridRenderer;
import com.starl0stgaming.overengineered.core.fluxa.item.ModeBasedItem;
import com.starl0stgaming.overengineered.core.fluxa.item.mode.CreateMode;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.glfw.GLFW;

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

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if(OverengineeredClient.MC_CLIENT.player.isHolding(stack -> stack.getItem() instanceof ModeBasedItem)) {
            var item = (ModeBasedItem) OverengineeredClient.MC_CLIENT.player.getMainHandItem().getItem();
            OverengineeredClient.setMouseScrollDelta(event.getScrollDeltaY());
            if(GLFW.glfwGetKey(OverengineeredClient.MC_CLIENT.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) == 1) {
                event.setCanceled(true);
            }
        }
    }
}
