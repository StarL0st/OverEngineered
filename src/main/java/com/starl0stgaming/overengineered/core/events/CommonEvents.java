package com.starl0stgaming.overengineered.core.events;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.commands.OECommands;
import com.starl0stgaming.overengineered.core.registry.InteractioRegistry;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = Overengineered.MODID)
public class CommonEvents {

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {

    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        OECommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(InteractioRegistry.TYPE_REGISTRY);
    }


}
