package com.starl0stgaming.overengineered.core.registry;

import com.starl0stgaming.overengineered.Overengineered;
import com.starl0stgaming.overengineered.core.fluxa.item.DebugWand;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class OEItemRegistry {

   public static final DeferredItem<DebugWand> DEBUG_WAND = OERegistries.ITEMS.registerItem(
           "debug_wandio",
                  DebugWand::new,
                  new Item.Properties()
   );

   public static void initializeRegistry() {
      Overengineered.LOGGER.info("registry zeit");
   }
}
