package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.client.render.entity.model.MurdererEntityModel;
import com.aiden.pvp.client.render.entity.state.MurdererEntityRenderState;
import com.aiden.pvp.entities.MurdererEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Identifier;

public class MurdererEntityRenderer extends MobEntityRenderer<MurdererEntity, MurdererEntityRenderState, MurdererEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/murderer.png");

    public MurdererEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MurdererEntityModel(ctx.getPart(EntityModelLayers.VINDICATOR)), 0.5F);
        this.addFeature(new HeadFeatureRenderer<>(this, ctx.getEntityModels(), ctx.getPlayerSkinCache()));
        this.addFeature(
                new HeldItemFeatureRenderer<>(this) {
                    public void render(
                            MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue,
                            int i, MurdererEntityRenderState murdererEntityRenderState, float f, float g
                    ) {
                        if (murdererEntityRenderState.attacking) {
                            super.render(matrixStack, orderedRenderCommandQueue, i, murdererEntityRenderState, f, g);
                        }
                    }
                }
        );
    }

    public void updateRenderState(MurdererEntity murdererEntity, MurdererEntityRenderState murdererEntityRenderState, float f) {
        super.updateRenderState(murdererEntity, murdererEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState(murdererEntity, murdererEntityRenderState, this.itemModelResolver, f);
        murdererEntityRenderState.hasVehicle = murdererEntity.hasVehicle();
        murdererEntityRenderState.illagerMainArm = murdererEntity.getMainArm();
        murdererEntityRenderState.murdererState = murdererEntity.getState();
        murdererEntityRenderState.crossbowPullTime = murdererEntityRenderState.murdererState == MurdererEntity.State.CROSSBOW_CHARGE
                ? CrossbowItem.getPullTime(murdererEntity.getActiveItem(), murdererEntity)
                : 0;
        murdererEntityRenderState.itemUseTime = murdererEntity.getItemUseTime(f);
        murdererEntityRenderState.handSwingProgress = murdererEntity.getHandSwingProgress(f);
        murdererEntityRenderState.attacking = murdererEntity.isAttacking();
    }

    @Override
    public Identifier getTexture(MurdererEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public MurdererEntityRenderState createRenderState() {
        return new MurdererEntityRenderState();
    }
}
