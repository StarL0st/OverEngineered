package com.starl0stgaming.overengineered.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class VecUtils {
    //create code :troll:
    public static Vec3 getTraceTarget(Player playerIn, double range, Vec3 origin) {
        float f = playerIn.getXRot();
        float f1 = playerIn.getYRot();
        float f2 = Mth.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = Mth.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -Mth.cos(-f * 0.017453292F);
        float f5 = Mth.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        return origin.add((double) f6 * range, (double) f5 * range, (double) f7 * range);
    }

    public static Vec3 getTraceOrigin(Player player) {
        return new Vec3(player.getX(), player.getY() + (double) player.getEyeHeight(), player.getZ());
    }
}
