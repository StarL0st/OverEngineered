package com.starl0stgaming.overengineered.client;

import com.starl0stgaming.overengineered.client.render.outline.OutlineRenderer;
import net.minecraft.client.Minecraft;

public class OverengineeredClient {
    public static final Minecraft MC_CLIENT = Minecraft.getInstance();

    public static final OutlineRenderer OUTLINE_RENDERER = new OutlineRenderer();
}
