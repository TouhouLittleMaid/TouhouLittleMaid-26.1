package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoBone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoMesh;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.EModelRenderCycle;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.IRenderCycle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.LightCoordsUtil;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public interface IGeoRenderer<TState extends EntityRenderState, TData extends GeckoRenderData> {
    Vector3f C000 = new Vector3f();
    Vector3f C100 = new Vector3f();
    Vector3f C110 = new Vector3f();
    Vector3f C010 = new Vector3f();
    Vector3f C001 = new Vector3f();
    Vector3f C101 = new Vector3f();
    Vector3f C111 = new Vector3f();
    Vector3f C011 = new Vector3f();
    Vector3f dx = new Vector3f();
    Vector3f dy = new Vector3f();
    Vector3f dz = new Vector3f();
    Vector3f nx = new Vector3f();
    Vector3f ny = new Vector3f();
    Vector3f nz = new Vector3f();

    default void preSubmit(TState state, TData data, RenderContext ctx, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL) {
            if (data.transform != null) {
                poseStack.mulPose(data.transform);
            }
        }
    }

    default void submit(TState state, TData data, RenderContext ctx, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, RenderType type) {
        submitNodeCollector.submitCustomGeometry(poseStack, type, (pose, vertexConsumer) -> {
            if (data.isClosed()) {
                return;
            }
            if (ctx.level() && !ctx.irisShadow()
                    && state.outlineColor != 0 && (type.outline().isPresent() || type.isOutline())) {
                var outlineBufferSource = Minecraft.getInstance().renderBuffers().outlineBufferSource();
                outlineBufferSource.setColor(state.outlineColor);
                var outlineBuffer = outlineBufferSource.getBuffer(type);
                if (type.isOutline()) {
                    vertexConsumer = outlineBuffer;
                } else {
                    vertexConsumer = VertexMultiConsumer.create(vertexConsumer, outlineBuffer);
                }
            }
            VertexConsumer finalVertexConsumer = vertexConsumer;
            data.modelState.visitRenderBones(pose, (bone, poseState) -> {
                renderCubesOfBone(bone, poseState, finalVertexConsumer, state, data);
            });
            data.close();
        });
        // 由于此时我们至少渲染了一次，因此让我们将循环设置为重复
        setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
    }

    default void renderCubesOfBone(GeoBone bone, PoseStack.Pose poseState, VertexConsumer buffer, TState state, GeckoRenderData data) {
        GeoMesh mesh = bone.cubes();
        
        var packedLight = bone.glow() ? LightCoordsUtil.FULL_BRIGHT : state.lightCoords;
        var packedOverlay = data.overlayUV;
        var color = data.color;

        for (int i = 0; i < mesh.getCubeCount(); i++) {
            Matrix4f pose = poseState.pose();
            mesh.position(i).mulPosition(pose, C000);
            mesh.dx(i).mulDirection(pose, dx);
            mesh.dy(i).mulDirection(pose, dy);
            mesh.dz(i).mulDirection(pose, dz);

            C000.add(dx, C100);
            C100.add(dy, C110);
            C000.add(dy, C010);
            C000.add(dz, C001);
            C100.add(dz, C101);
            C110.add(dz, C111);
            C010.add(dz, C011);

            dx.cross(dy, nz).normalize();
            dy.cross(dz, nx).normalize();
            dz.cross(dx, ny).normalize();

            int faces = mesh.faces(i);
            boolean mirrored = (faces & 0b1000000) != 0;
            if (RenderSystem.getModelViewMatrix().m32() != 0) {
                Matrix3f normal = poseState.normal();
                mesh.dx(i).cross(mesh.dy(i), nz);
                mesh.dy(i).cross(mesh.dz(i), nx);
                mesh.dz(i).cross(mesh.dx(i), ny);
                nx.mul(normal).normalize();
                ny.mul(normal).normalize();
                nz.mul(normal).normalize();
            }

            if (mirrored != data.ctx.inventory()) {
                nx.mul(-1);
                ny.mul(-1);
                nz.mul(-1);
            }

            if ((faces & 0b000001) != 0) // DOWN
            {
                buffer.addVertex(C101.x, C101.y, C101.z)
                        .setColor(color)
                        .setUv(mesh.downU0(i), mesh.downV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-ny.x, -ny.y, -ny.z);
                buffer.addVertex(C001.x, C001.y, C001.z)
                        .setColor(color)
                        .setUv(mesh.downU1(i), mesh.downV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-ny.x, -ny.y, -ny.z);
                buffer.addVertex(C000.x, C000.y, C000.z)
                        .setColor(color)
                        .setUv(mesh.downU1(i), mesh.downV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-ny.x, -ny.y, -ny.z);
                buffer.addVertex(C100.x, C100.y, C100.z)
                        .setColor(color)
                        .setUv(mesh.downU0(i), mesh.downV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-ny.x, -ny.y, -ny.z);
            }
            if ((faces & 0b000010) != 0) // UP
            {
                buffer.addVertex(C110.x, C110.y, C110.z)
                        .setColor(color)
                        .setUv(mesh.upU0(i), mesh.upV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(ny.x, ny.y, ny.z);
                buffer.addVertex(C010.x, C010.y, C010.z)
                        .setColor(color)
                        .setUv(mesh.upU1(i), mesh.upV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(ny.x, ny.y, ny.z);
                buffer.addVertex(C011.x, C011.y, C011.z)
                        .setColor(color)
                        .setUv(mesh.upU1(i), mesh.upV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(ny.x, ny.y, ny.z);
                buffer.addVertex(C111.x, C111.y, C111.z)
                        .setColor(color)
                        .setUv(mesh.upU0(i), mesh.upV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(ny.x, ny.y, ny.z);
            }
            if ((faces & 0b000100) != 0) // NORTH
            {
                buffer.addVertex(C100.x, C100.y, C100.z)
                        .setColor(color)
                        .setUv(mesh.northU0(i), mesh.northV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-nz.x, -nz.y, -nz.z);
                buffer.addVertex(C000.x, C000.y, C000.z)
                        .setColor(color)
                        .setUv(mesh.northU1(i), mesh.northV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-nz.x, -nz.y, -nz.z);
                buffer.addVertex(C010.x, C010.y, C010.z)
                        .setColor(color)
                        .setUv(mesh.northU1(i), mesh.northV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-nz.x, -nz.y, -nz.z);
                buffer.addVertex(C110.x, C110.y, C110.z)
                        .setColor(color)
                        .setUv(mesh.northU0(i), mesh.northV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-nz.x, -nz.y, -nz.z);
            }
            if ((faces & 0b001000) != 0) // SOUTH
            {
                buffer.addVertex(C001.x, C001.y, C001.z)
                        .setColor(color)
                        .setUv(mesh.southU0(i), mesh.southV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(nz.x, nz.y, nz.z);
                buffer.addVertex(C101.x, C101.y, C101.z)
                        .setColor(color)
                        .setUv(mesh.southU1(i), mesh.southV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(nz.x, nz.y, nz.z);
                buffer.addVertex(C111.x, C111.y, C111.z)
                        .setColor(color)
                        .setUv(mesh.southU1(i), mesh.southV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(nz.x, nz.y, nz.z);
                buffer.addVertex(C011.x, C011.y, C011.z)
                        .setColor(color)
                        .setUv(mesh.southU0(i), mesh.southV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(nz.x, nz.y, nz.z);
            }
            if ((faces & 0b010000) != 0) { // WEST
                buffer.addVertex(C000.x, C000.y, C000.z)
                        .setColor(color)
                        .setUv(mesh.westU0(i), mesh.westV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-nx.x, -nx.y, -nx.z);
                buffer.addVertex(C001.x, C001.y, C001.z)
                        .setColor(color)
                        .setUv(mesh.westU1(i), mesh.westV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-nx.x, -nx.y, -nx.z);
                buffer.addVertex(C011.x, C011.y, C011.z)
                        .setColor(color)
                        .setUv(mesh.westU1(i), mesh.westV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-nx.x, -nx.y, -nx.z);
                buffer.addVertex(C010.x, C010.y, C010.z)
                        .setColor(color)
                        .setUv(mesh.westU0(i), mesh.westV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(-nx.x, -nx.y, -nx.z);
            }
            if ((faces & 0b100000) != 0) { // EAST
                buffer.addVertex(C101.x, C101.y, C101.z)
                        .setColor(color)
                        .setUv(mesh.eastU0(i), mesh.eastV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(nx.x, nx.y, nx.z);
                buffer.addVertex(C100.x, C100.y, C100.z)
                        .setColor(color)
                        .setUv(mesh.eastU1(i), mesh.eastV1(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(nx.x, nx.y, nx.z);
                buffer.addVertex(C110.x, C110.y, C110.z)
                        .setColor(color)
                        .setUv(mesh.eastU1(i), mesh.eastV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(nx.x, nx.y, nx.z);
                buffer.addVertex(C111.x, C111.y, C111.z)
                        .setColor(color)
                        .setUv(mesh.eastU0(i), mesh.eastV0(i))
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(nx.x, nx.y, nx.z);
            }
        }
    }

    @Nullable
    default RenderType getRenderType(GeckoRenderData data, boolean visible, boolean glowing) {
        var ctx = data.ctx;
        if (visible || (!ctx.level()) || ctx.irisShadow()) {
            return RenderTypes.entityCutout(data.texture);
        }
        if (glowing) {
            return RenderTypes.outline(data.texture);
        }
        return null;
    }

    @NotNull
    default IRenderCycle getCurrentModelRenderCycle() {
        return EModelRenderCycle.INITIAL;
    }

    default void setCurrentModelRenderCycle(IRenderCycle cycle) {
    }
}
