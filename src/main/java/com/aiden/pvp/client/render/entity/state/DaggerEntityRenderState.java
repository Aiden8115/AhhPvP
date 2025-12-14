package com.aiden.pvp.client.render.entity.state;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.util.math.Vec3d;

public class DaggerEntityRenderState extends EntityRenderState {
    public final ItemRenderState itemRenderState = new ItemRenderState();
    public Vec3d velocity = Vec3d.ZERO;
}
