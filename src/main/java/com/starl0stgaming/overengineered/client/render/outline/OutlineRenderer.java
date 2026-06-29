package com.starl0stgaming.overengineered.client.render.outline;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.starl0stgaming.overengineered.client.OverengineeredClient;
import com.starl0stgaming.overengineered.util.client.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class OutlineRenderer {
    private HashMap<String, OutlineRenderInfo> outlines;

    public OutlineRenderer() {
        this.outlines = new HashMap<>();
    }

    public void outlineAABB(String identifier, AABB aabb, @Nullable Color color, boolean randomizeColor) {
        OutlineRenderInfo renderInfo = new OutlineRenderInfo();
        if(randomizeColor && color == null) {
            Random random = new Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            renderInfo.setColor(new Color(r, g, b));
        } else if(color != null) {
            renderInfo.setColor(color);
        }
        renderInfo.setStart(aabb.getMinPosition());
        renderInfo.setEnd(aabb.getMaxPosition());
        this.outlines.put(identifier, renderInfo);
    }

    public void clearOutlines() {
        this.outlines.clear();
    }

    public void remove(String identifier) {
        this.outlines.remove(identifier);
    }

    public void renderOutlines(PoseStack poseStack, Camera camera) {
        for(OutlineRenderInfo renderInfo : this.getOutlines().values()) {
            this.renderOutline(poseStack, camera, renderInfo.getStart(), renderInfo.getEnd(), renderInfo.getColor());
        }
    }

    private void renderOutlineThick(PoseStack poseStack, Camera camera, Vec3 point0, Vec3 point1, Color color, double thickness) {
        this.renderOutline(poseStack, camera,
                point0, point1, color);
        this.renderOutline(poseStack, camera,
                new Vec3(
                        point0.x - thickness,
                        point0.y - thickness,
                        point0.z - thickness
                        ),
                new Vec3(
                        point1.x + thickness,
                        point1.y + thickness,
                        point1.z + thickness
                ), color);
    }

    private void renderOutline(PoseStack poseStack, Camera camera, Vec3 point0, Vec3 point1, Color color) {
        MultiBufferSource bufferSource = OverengineeredClient.MC_CLIENT.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());
        poseStack.pushPose();

        Vec3 cameraPos = camera.getPosition();

        RenderSystem.depthFunc(GL11.GL_EQUAL);
        RenderSystem.depthMask(true);

        Vec3 tpos1 = new Vec3(
                point0.x - cameraPos.x,
                point0.y - cameraPos.y,
                point0.z - cameraPos.z
        );

        Vec3 tpos2 = new Vec3(
                point1.x - cameraPos.x,
                point1.y - cameraPos.y,
                point1.z - cameraPos.z
        );


        Matrix4f matrix = poseStack.last().pose();
        //outside outline
        RenderUtil.vertexBox(buffer, matrix, color.getRed(), color.getGreen(), color.getBlue(),
                tpos1, tpos2, poseStack.last());

        //edges
        /*
        RenderUtil.vertexBox(buffer, matrix, color.getRed(), color.getGreen(), color.getBlue(),
                tpos1.add(-0.0625, -0.0625, -0.0625),
                new Vec3(tpos2.x(), tpos1.y(), tpos1.z()).add(0.0625, 0, 0), poseStack.last());
        RenderUtil.vertexBox(buffer, matrix, color.getRed(), color.getGreen(), color.getBlue(),
                tpos1.add(-0.0625, -0.0625, -0.0625),
                new Vec3(tpos1.x(), tpos1.y(), tpos2.z()).add(0, 0, 0.0625), poseStack.last());
        RenderUtil.vertexBox(buffer, matrix, color.getRed(), color.getGreen(), color.getBlue(),
                new Vec3(tpos2.x(), tpos1.y(), tpos1.z()),
                new Vec3(tpos2.x(), tpos1.y(), tpos2.z()).add(0.0625, -0.0625, -0.0625), poseStack.last());
        RenderUtil.vertexBox(buffer, matrix, color.getRed(), color.getGreen(), color.getBlue(),
                new Vec3(tpos2.x(), tpos1.y(), tpos2.z()).add(0.0625, 0, 0),
                new Vec3(tpos1.x(), tpos1.y(), tpos2.z()).add(-0.0625, -0.0625, 0.0625), poseStack.last());
         */
        poseStack.popPose();

    }


    public HashMap<String, OutlineRenderInfo> getOutlines() {
        return outlines;
    }

    public static class OutlineRenderInfo {
        private Vec3 start;
        private Vec3 end;
        private Color color;

        public OutlineRenderInfo() {

        }

        public Vec3 getStart() {
            return start;
        }

        public void setStart(Vec3 start) {
            this.start = start;
        }

        public Vec3 getEnd() {
            return end;
        }

        public void setEnd(Vec3 end) {
            this.end = end;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }
}