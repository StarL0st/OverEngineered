package com.starl0stgaming.overengineered.util.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RenderUtil {
    public static void vertex(VertexConsumer consumer, Matrix4f matrix, int r, int g, int b,
                              float x, float y, float z, int u, int v, PoseStack.Pose pose, float n0, float n1, float n2) {
        consumer.addVertex(matrix, x, y, z)
                .setColor(r, g, b, 255)
                .setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(pose, n0, n1, n2)
                .setLight(LightTexture.FULL_BRIGHT);
    }

    public static void vertexBox(VertexConsumer consumer, Matrix4f matrix, int r, int g, int b, Vec3 start, Vec3 end,
                                 PoseStack.Pose pose) {

        float x0 = (float) start.x;
        float y0 = (float) start.y;
        float z0 = (float) start.z;

        float x1 = (float) end.x;
        float y1 = (float) end.y;
        float z1 = (float) end.z;

        //front
        vertex(consumer, matrix, r, g, b, x0, y0, z0, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y0, z1, 1, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y1, z1, 1, 1, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y1, z0, 0, 1, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y0, z0, 0, 0, pose, 0, 0, 0);

        //right
        vertex(consumer, matrix, r, g, b, x1, y0, z0, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y0, z0, 1, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y1, z0, 1, 1, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y1, z0, 0, 1, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y0, z0, 0, 0, pose, 0, 0, 0);

        //back
        vertex(consumer, matrix, r, g, b, x1, y0, z0, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y0, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y1, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y1, z0, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y0, z0, 0, 0, pose, 0, 0, 0);

        //left
        vertex(consumer, matrix, r, g, b, x1, y0, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y0, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y1, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y1, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y0, z1, 0, 0, pose, 0, 0, 0);

        //top
        vertex(consumer, matrix, r, g, b, x0, y1, z0, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y1, z0, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y1, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y1, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y1, z0, 0, 0, pose, 0, 0, 0);

        //bottom
        vertex(consumer, matrix, r, g, b, x0, y0, z0, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y0, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y0, z1, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x1, y0, z0, 0, 0, pose, 0, 0, 0);
        vertex(consumer, matrix, r, g, b, x0, y0, z0, 0, 0, pose, 0, 0, 0);


    }

    public static void renderBox(VertexConsumer buffer, PoseStack m, float x0, float y0, float z0, float x1, float y1, float z1)
    {
        Matrix4f transform = m.last().pose();
        buffer.addVertex(transform, x0, y0, z1);
        buffer.addVertex(transform, x1, y0, z1);
        buffer.addVertex(transform, x1, y1, z1);
        buffer.addVertex(transform, x0, y1, z1);

        buffer.addVertex(transform, x0, y1, z0);
        buffer.addVertex(transform, x1, y1, z0);
        buffer.addVertex(transform, x1, y0, z0);
        buffer.addVertex(transform, x0, y0, z0);

        buffer.addVertex(transform, x0, y0, z0);
        buffer.addVertex(transform, x1, y0, z0);
        buffer.addVertex(transform, x1, y0, z1);
        buffer.addVertex(transform, x0, y0, z1);

        buffer.addVertex(transform, x0, y1, z1);
        buffer.addVertex(transform, x1, y1, z1);
        buffer.addVertex(transform, x1, y1, z0);
        buffer.addVertex(transform, x0, y1, z0);

        buffer.addVertex(transform, x0, y0, z0);
        buffer.addVertex(transform, x0, y0, z1);
        buffer.addVertex(transform, x0, y1, z1);
        buffer.addVertex(transform, x0, y1, z0);

        buffer.addVertex(transform, x1, y1, z0);
        buffer.addVertex(transform, x1, y1, z1);
        buffer.addVertex(transform, x1, y0, z1);
        buffer.addVertex(transform, x1, y0, z0);
    }

    public static void renderQuad(VertexConsumer consumer, Matrix4f matrix, int r, int g, int b,
                                  float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3,
                                  PoseStack.Pose pose, float n0, float n1, float n2) {
        vertex(consumer, matrix, r, g, b, x0, y0, z0, 0, 0, pose, n0, n1, n2);
        vertex(consumer, matrix, r, g, b, x1, y1, z1, 1, 0, pose, n0, n1, n2);
        vertex(consumer, matrix, r, g, b, x2, y2, z2, 1, 1, pose, n0, n1, n2);
        vertex(consumer, matrix, r, g, b, x3, y3, z3, 0, 1, pose, n0, n1, n2);
    }
}
