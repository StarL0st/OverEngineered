package com.starl0stgaming.overengineered.core.registry;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.api.type.InteractableType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class InteractioRegistry {
    public static final ResourceKey<Registry<InteractableType>> TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
            ResourceLocation.fromNamespaceAndPath(Overengineered.MODID, "interactable_types")
    );

    public static final Registry<InteractableType> TYPE_REGISTRY = new RegistryBuilder<>(TYPE_REGISTRY_KEY)
            .sync(false)
            .maxId(256)
            .create();
}
