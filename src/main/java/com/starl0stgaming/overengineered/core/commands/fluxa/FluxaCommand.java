package com.starl0stgaming.overengineered.core.commands.fluxa;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.starl0stgaming.overengineered.client.OverengineeredClient;
import com.starl0stgaming.overengineered.core.fluxa.grid.GridCalculator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;

public class FluxaCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("fluxa")
                .requires(cs -> cs.hasPermission(3))
                .then(Commands.literal("son")
                        .executes(ctx -> {
                            ctx.getSource().sendSystemMessage(Component.literal("son"));
                            if(ctx.getSource().isPlayer()) {
                                if(OverengineeredClient.MC_CLIENT.hitResult != null) {
                                    var pos = GridCalculator.getNearestWorldPos(OverengineeredClient.MC_CLIENT.hitResult.getLocation());
                                    OverengineeredClient.OUTLINE_RENDERER.outlineAABB(
                                            "sonion",
                                            new AABB(pos.x,
                                                    pos.y,
                                                    pos.z,
                                                    pos.x + 0.125,
                                                    pos.y + 0.125,
                                                    pos.z + 0.125
                                            ), null, true
                                    );
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("update_client")
                        .executes(ctx -> {
                            ctx.getSource().sendSystemMessage(Component.literal("Updating client..."));

                            return Command.SINGLE_SUCCESS;
                        }));
    }
}
