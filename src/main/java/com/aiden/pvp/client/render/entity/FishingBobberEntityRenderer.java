package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.client.render.entity.state.FishingBobberEntityState;
import com.aiden.pvp.entities.FishingBobberEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class FishingBobberEntityRenderer extends EntityRenderer<FishingBobberEntity, FishingBobberEntityState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fishing_hook.png");
    private static final RenderLayer LAYER = RenderLayers.entityCutout(TEXTURE);

    public FishingBobberEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(FishingBobberEntity entity, Frustum frustum, double x, double y, double z) {
        return super.shouldRender(entity, frustum, x, y, z) && entity.getPlayerOwner() != null;
    }

    @Override
    public void render(
            FishingBobberEntityState fishingBobberEntityState,
            MatrixStack matrixStack,
            OrderedRenderCommandQueue orderedRenderCommandQueue,
            CameraRenderState cameraRenderState
    ) {
        matrixStack.push();
        matrixStack.push();
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        matrixStack.multiply(cameraRenderState.orientation);
        orderedRenderCommandQueue.submitCustom(matrixStack, LAYER, (matricesEntry, vertexConsumer) -> {
            vertex(vertexConsumer, matricesEntry, fishingBobberEntityState.light, 0.0F, 0, 0, 1);
            vertex(vertexConsumer, matricesEntry, fishingBobberEntityState.light, 1.0F, 0, 1, 1);
            vertex(vertexConsumer, matricesEntry, fishingBobberEntityState.light, 1.0F, 1, 1, 0);
            vertex(vertexConsumer, matricesEntry, fishingBobberEntityState.light, 0.0F, 1, 0, 0);
        });
        matrixStack.pop();
        float f = (float)fishingBobberEntityState.pos.x;
        float g = (float)fishingBobberEntityState.pos.y;
        float h = (float)fishingBobberEntityState.pos.z;
        float i = MinecraftClient.getInstance().getWindow().getMinimumLineWidth();
        orderedRenderCommandQueue.submitCustom(matrixStack, RenderLayers.lines(), (matricesEntry, vertexConsumer) -> {
            int j = 16;

            for (int k = 0; k < 16; k++) {
                float l = percentage(k, 16);
                float m = percentage(k + 1, 16);
                renderFishingLine(f, g, h, vertexConsumer, matricesEntry, l, m, i);
                renderFishingLine(f, g, h, vertexConsumer, matricesEntry, m, l, i);
            }
        });
        matrixStack.pop();
        super.render(fishingBobberEntityState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public static Arm getArmHoldingRod(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof FishingRodItem ? player.getMainArm() : player.getMainArm().getOpposite();
    }

    private Vec3d getHandPos(PlayerEntity player, float handRotation, float tickProgress) {
        int i = getArmHoldingRod(player) == Arm.RIGHT ? 1 : -1;
        if (this.dispatcher.gameOptions.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player) {
            double l = 960.0 / this.dispatcher.gameOptions.getFov().getValue().intValue();
            Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition(i * 0.525F, -0.1F).multiply(l).rotateY(handRotation * 0.5F).rotateX(-handRotation * 0.7F);
            return player.getCameraPosVec(tickProgress).add(vec3d);
        } else {
            float f = MathHelper.lerp(tickProgress, player.lastBodyYaw, player.bodyYaw) * (float) (Math.PI / 180.0);
            double d = MathHelper.sin(f);
            double e = MathHelper.cos(f);
            float g = player.getScale();
            double h = i * 0.35 * g;
            double j = 0.8 * g;
            float k = player.isInSneakingPose() ? -0.1875F : 0.0F;
            return player.getCameraPosVec(tickProgress).add(-e * h - d * j, k - 0.45 * g, -d * h + e * j);
        }
    }

    private static float percentage(int value, int denominator) {
        return (float)value / denominator;
    }

    private static void vertex(VertexConsumer buffer, MatrixStack.Entry matrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5F, y - 0.5F, 0.0F)
                .color(Colors.WHITE)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(matrix, 0.0F, 1.0F, 0.0F);
    }

    private static void renderFishingLine(
            float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd, float getMinimumLineWidth
    ) {
        float f = x * segmentStart;
        float g = y * (segmentStart * segmentStart + segmentStart) * 0.5F + 0.25F;
        float h = z * segmentStart;
        float i = x * segmentEnd - f;
        float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5F + 0.25F - g;
        float k = z * segmentEnd - h;
        float l = MathHelper.sqrt(i * i + j * j + k * k);
        i /= l;
        j /= l;
        k /= l;
        buffer.vertex(matrices, f, g, h).color(Colors.BLACK).normal(matrices, i, j, k).lineWidth(getMinimumLineWidth);
    }

    @Override
    public FishingBobberEntityState createRenderState() {
        return new FishingBobberEntityState();
    }

    public void updateRenderState(FishingBobberEntity fishingBobberEntity, FishingBobberEntityState fishingBobberEntityState, float f) {
        super.updateRenderState(fishingBobberEntity, fishingBobberEntityState, f);
        PlayerEntity playerEntity = fishingBobberEntity.getPlayerOwner();
        if (playerEntity == null) {
            fishingBobberEntityState.pos = Vec3d.ZERO;
        } else {
            float g = playerEntity.getHandSwingProgress(f);
            float h = MathHelper.sin(MathHelper.sqrt(g) * (float) Math.PI);
            Vec3d vec3d = this.getHandPos(playerEntity, h, f);
            Vec3d vec3d2 = fishingBobberEntity.getLerpedPos(f).add(0.0, 0.25, 0.0);
            fishingBobberEntityState.pos = vec3d.subtract(vec3d2);
        }
    }

    protected boolean canBeCulled(net.minecraft.entity.projectile.FishingBobberEntity fishingBobberEntity) {
        return false;
    }
}
