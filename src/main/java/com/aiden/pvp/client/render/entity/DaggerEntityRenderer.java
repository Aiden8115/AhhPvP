package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.render.entity.state.DaggerEntityRenderState;
import com.aiden.pvp.entities.DaggerEntity;
import com.aiden.pvp.items.ModItems;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class DaggerEntityRenderer<T extends DaggerEntity> extends EntityRenderer<T, DaggerEntityRenderState> {
    private final ItemModelManager itemModelManager;
    private final float scale;
    private final boolean lit;

    public DaggerEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelManager = ctx.getItemModelManager();
        this.scale = scale;
        this.lit = lit;
    }

    public DaggerEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0F, false);
    }

    @Override
    protected int getBlockLight(T entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }

    public void render(
            DaggerEntityRenderState daggerEntityRenderState,
            MatrixStack matrixStack,
            OrderedRenderCommandQueue orderedRenderCommandQueue,
            CameraRenderState cameraRenderState
    ) {
        matrixStack.push();

        matrixStack.translate(0.0F, 0.25F, 0.0F);
        matrixStack.scale(this.scale, this.scale, this.scale);

        Vec3d velocity = daggerEntityRenderState.velocity;
        if (velocity.lengthSquared() > 0.001) { // 避免速度为0时计算错误
            // 计算速度向量的Yaw角度（水平旋转角度）
            float yaw = (float) (MathHelper.atan2(velocity.z, velocity.x) * 180.0 / Math.PI) - 90.0F;
            // 应用Y轴旋转：面朝实体运动方向
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        }

        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(95.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0F));

        daggerEntityRenderState.itemRenderState
                .render(matrixStack, orderedRenderCommandQueue, daggerEntityRenderState.light, OverlayTexture.DEFAULT_UV, daggerEntityRenderState.outlineColor);

        matrixStack.pop();
        super.render(daggerEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public DaggerEntityRenderState createRenderState() {
        return new DaggerEntityRenderState();
    }

    public void updateRenderState(T entity, DaggerEntityRenderState daggerEntityRenderState, float f) {
        super.updateRenderState(entity, daggerEntityRenderState, f);
        daggerEntityRenderState.velocity = entity.getVelocity().multiply(-1, 1, -1);
        this.itemModelManager.updateForNonLivingEntity(
                daggerEntityRenderState.itemRenderState,
                new ItemStack(ModItems.THROWABLE_DAGGER),
                ItemDisplayContext.GROUND,
                entity
        );
    }
}
