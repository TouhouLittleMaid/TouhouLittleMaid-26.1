package com.github.tartaricacid.touhoulittlemaid.client.model;


import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class AltarModel<T extends Entity> extends EntityModel<T> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "main"), "altar");
    private final ModelPart bone;
    private final ModelPart bone56;
    private final ModelPart bone65;
    private final ModelPart bone57;
    private final ModelPart bone59;
    private final ModelPart bone60;
    private final ModelPart bone61;
    private final ModelPart bone63;
    private final ModelPart bone62;
    private final ModelPart bone64;
    private final ModelPart bone66;
    private final ModelPart bone58;
    private final ModelPart pillar;
    private final ModelPart bone2;
    private final ModelPart bone3;
    private final ModelPart bone4;
    private final ModelPart bone5;
    private final ModelPart bone10;
    private final ModelPart bone6;
    private final ModelPart bone7;
    private final ModelPart bone8;
    private final ModelPart bone9;
    private final ModelPart pillar2;
    private final ModelPart bone11;
    private final ModelPart bone12;
    private final ModelPart bone13;
    private final ModelPart bone14;
    private final ModelPart bone15;
    private final ModelPart bone16;
    private final ModelPart bone17;
    private final ModelPart bone18;
    private final ModelPart bone19;
    private final ModelPart pillar3;
    private final ModelPart bone20;
    private final ModelPart bone21;
    private final ModelPart bone22;
    private final ModelPart bone23;
    private final ModelPart bone24;
    private final ModelPart bone25;
    private final ModelPart bone26;
    private final ModelPart bone27;
    private final ModelPart bone28;
    private final ModelPart pillar4;
    private final ModelPart bone29;
    private final ModelPart bone30;
    private final ModelPart bone31;
    private final ModelPart bone32;
    private final ModelPart bone33;
    private final ModelPart bone34;
    private final ModelPart bone35;
    private final ModelPart bone36;
    private final ModelPart bone37;
    private final ModelPart pillar5;
    private final ModelPart bone38;
    private final ModelPart bone39;
    private final ModelPart bone40;
    private final ModelPart bone41;
    private final ModelPart bone42;
    private final ModelPart bone43;
    private final ModelPart bone44;
    private final ModelPart bone45;
    private final ModelPart bone46;
    private final ModelPart pillar6;
    private final ModelPart bone47;
    private final ModelPart bone48;
    private final ModelPart bone49;
    private final ModelPart bone50;
    private final ModelPart bone51;
    private final ModelPart bone52;
    private final ModelPart bone53;
    private final ModelPart bone54;
    private final ModelPart bone55;

    public AltarModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.bone56 = root.getChild("bone56");
        this.bone65 = this.bone56.getChild("bone65");
        this.bone57 = this.bone56.getChild("bone57");
        this.bone59 = this.bone56.getChild("bone59");
        this.bone60 = this.bone56.getChild("bone60");
        this.bone61 = this.bone56.getChild("bone61");
        this.bone63 = this.bone61.getChild("bone63");
        this.bone62 = this.bone56.getChild("bone62");
        this.bone64 = this.bone62.getChild("bone64");
        this.bone66 = this.bone62.getChild("bone66");
        this.bone58 = this.bone56.getChild("bone58");
        this.pillar = root.getChild("pillar");
        this.bone2 = this.pillar.getChild("bone2");
        this.bone3 = this.pillar.getChild("bone3");
        this.bone4 = this.pillar.getChild("bone4");
        this.bone5 = this.pillar.getChild("bone5");
        this.bone10 = this.pillar.getChild("bone10");
        this.bone6 = this.bone10.getChild("bone6");
        this.bone7 = this.bone10.getChild("bone7");
        this.bone8 = this.bone10.getChild("bone8");
        this.bone9 = this.bone10.getChild("bone9");
        this.pillar2 = root.getChild("pillar2");
        this.bone11 = this.pillar2.getChild("bone11");
        this.bone12 = this.pillar2.getChild("bone12");
        this.bone13 = this.pillar2.getChild("bone13");
        this.bone14 = this.pillar2.getChild("bone14");
        this.bone15 = this.pillar2.getChild("bone15");
        this.bone16 = this.bone15.getChild("bone16");
        this.bone17 = this.bone15.getChild("bone17");
        this.bone18 = this.bone15.getChild("bone18");
        this.bone19 = this.bone15.getChild("bone19");
        this.pillar3 = root.getChild("pillar3");
        this.bone20 = this.pillar3.getChild("bone20");
        this.bone21 = this.pillar3.getChild("bone21");
        this.bone22 = this.pillar3.getChild("bone22");
        this.bone23 = this.pillar3.getChild("bone23");
        this.bone24 = this.pillar3.getChild("bone24");
        this.bone25 = this.bone24.getChild("bone25");
        this.bone26 = this.bone24.getChild("bone26");
        this.bone27 = this.bone24.getChild("bone27");
        this.bone28 = this.bone24.getChild("bone28");
        this.pillar4 = root.getChild("pillar4");
        this.bone29 = this.pillar4.getChild("bone29");
        this.bone30 = this.pillar4.getChild("bone30");
        this.bone31 = this.pillar4.getChild("bone31");
        this.bone32 = this.pillar4.getChild("bone32");
        this.bone33 = this.pillar4.getChild("bone33");
        this.bone34 = this.bone33.getChild("bone34");
        this.bone35 = this.bone33.getChild("bone35");
        this.bone36 = this.bone33.getChild("bone36");
        this.bone37 = this.bone33.getChild("bone37");
        this.pillar5 = root.getChild("pillar5");
        this.bone38 = this.pillar5.getChild("bone38");
        this.bone39 = this.pillar5.getChild("bone39");
        this.bone40 = this.pillar5.getChild("bone40");
        this.bone41 = this.pillar5.getChild("bone41");
        this.bone42 = this.pillar5.getChild("bone42");
        this.bone43 = this.bone42.getChild("bone43");
        this.bone44 = this.bone42.getChild("bone44");
        this.bone45 = this.bone42.getChild("bone45");
        this.bone46 = this.bone42.getChild("bone46");
        this.pillar6 = root.getChild("pillar6");
        this.bone47 = this.pillar6.getChild("bone47");
        this.bone48 = this.pillar6.getChild("bone48");
        this.bone49 = this.pillar6.getChild("bone49");
        this.bone50 = this.pillar6.getChild("bone50");
        this.bone51 = this.pillar6.getChild("bone51");
        this.bone52 = this.bone51.getChild("bone52");
        this.bone53 = this.bone51.getChild("bone53");
        this.bone54 = this.bone51.getChild("bone54");
        this.bone55 = this.bone51.getChild("bone55");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(-47, 65).addBox(-24.0F, -1.0F, -24.0F, 48.0F, 0.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone56 = partdefinition.addOrReplaceChild("bone56", CubeListBuilder.create().texOffs(132, 112).addBox(-35.0F, -12.0F, -64.0F, 16.0F, 12.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(110, 182).addBox(-21.5F, -95.0F, -64.0F, 43.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 222).addBox(-45.0F, -66.3F, -57.0F, 90.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(132, 0).addBox(-27.5F, -93.0F, -63.0F, 14.0F, 8.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(84, 201).addBox(-25.5F, -92.0F, -60.0F, 51.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(188, 0).addBox(13.5F, -93.0F, -63.0F, 14.0F, 8.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(196, 112).addBox(19.0F, -12.0F, -64.0F, 16.0F, 12.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone56_r1 = bone56.addOrReplaceChild("bone56_r1", CubeListBuilder.create().texOffs(60, 214).mirror().addBox(-3.5F, -2.0F, -1.5F, 7.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(31.5152F, -67.1264F, -56.0F, 0.0F, 0.0F, -0.1745F));

        PartDefinition bone57_r1 = bone56.addOrReplaceChild("bone57_r1", CubeListBuilder.create().texOffs(40, 214).mirror().addBox(-3.5F, -2.0F, -1.5F, 7.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(14.5152F, -67.1264F, -56.0F, 0.0F, 0.0F, 0.1745F));

        PartDefinition bone56_r2 = bone56.addOrReplaceChild("bone56_r2", CubeListBuilder.create().texOffs(184, 22).addBox(-6.5F, -39.0F, -6.5F, 13.0F, 77.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(24.0F, -47.0F, -56.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition bone57_r2 = bone56.addOrReplaceChild("bone57_r2", CubeListBuilder.create().texOffs(202, 201).addBox(-4.5F, -2.75F, -4.0F, 34.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(28.625F, -90.0F, -56.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition bone57_r3 = bone56.addOrReplaceChild("bone57_r3", CubeListBuilder.create().texOffs(228, 182).addBox(-5.5F, -1.5F, -8.0F, 39.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(26.0F, -94.0F, -56.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition bone57_r4 = bone56.addOrReplaceChild("bone57_r4", CubeListBuilder.create().texOffs(0, 201).addBox(-29.5F, -2.75F, -4.0F, 34.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-28.625F, -90.0F, -56.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition bone57_r5 = bone56.addOrReplaceChild("bone57_r5", CubeListBuilder.create().texOffs(20, 214).addBox(-3.5F, -2.0F, -1.5F, 7.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.5152F, -67.1264F, -56.0F, 0.0F, 0.0F, -0.1745F));

        PartDefinition bone56_r3 = bone56.addOrReplaceChild("bone56_r3", CubeListBuilder.create().texOffs(0, 214).addBox(-3.5F, -2.0F, -1.5F, 7.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-31.5152F, -67.1264F, -56.0F, 0.0F, 0.0F, 0.1745F));

        PartDefinition bone56_r4 = bone56.addOrReplaceChild("bone56_r4", CubeListBuilder.create().texOffs(132, 22).addBox(-6.5F, -39.0F, -6.5F, 13.0F, 77.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-24.0F, -47.0F, -56.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition bone57_r6 = bone56.addOrReplaceChild("bone57_r6", CubeListBuilder.create().texOffs(0, 182).addBox(-33.5F, -1.5F, -8.0F, 39.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-26.0F, -94.0F, -56.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition bone65 = bone56.addOrReplaceChild("bone65", CubeListBuilder.create().texOffs(98, 156).addBox(-20.0F, -4.2941F, -2.0F, 40.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -96.7059F, -56.0F));

        PartDefinition bone58_r1 = bone65.addOrReplaceChild("bone58_r1", CubeListBuilder.create().texOffs(110, 140).addBox(-20.5F, -99.0F, -55.0F, 41.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 107.7311F, 28.7446F, -0.2618F, 0.0F, 0.0F));

        PartDefinition bone57_r7 = bone65.addOrReplaceChild("bone57_r7", CubeListBuilder.create().texOffs(110, 166).addBox(-20.5F, -99.0F, -71.0F, 41.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 78.7433F, 79.4391F, 0.2618F, 0.0F, 0.0F));

        PartDefinition bone57 = bone56.addOrReplaceChild("bone57", CubeListBuilder.create().texOffs(10, 156).addBox(-20.0F, -4.2941F, -2.0F, 40.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-40.0F, -98.3934F, -56.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition bone59_r1 = bone57.addOrReplaceChild("bone59_r1", CubeListBuilder.create().texOffs(0, 140).addBox(-20.5F, -99.0F, -55.0F, 41.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 107.7311F, 28.7446F, -0.2618F, 0.0F, 0.0F));

        PartDefinition bone58_r2 = bone57.addOrReplaceChild("bone58_r2", CubeListBuilder.create().texOffs(0, 166).addBox(-20.5F, -99.0F, -71.0F, 41.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 78.7433F, 79.4391F, 0.2618F, 0.0F, 0.0F));

        PartDefinition bone59 = bone56.addOrReplaceChild("bone59", CubeListBuilder.create().texOffs(236, 59).addBox(-7.0F, -20.0F, -1.0F, 14.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(268, 62).addBox(-5.5F, -18.5F, -1.7F, 11.0F, 19.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -67.2128F, -60.2462F, 0.0437F, 0.0F, 0.0F));

        PartDefinition bone60 = bone56.addOrReplaceChild("bone60", CubeListBuilder.create().texOffs(236, 83).addBox(-7.0F, -20.0F, -1.0F, 14.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(268, 83).addBox(-5.5F, -18.5F, -0.25F, 11.0F, 19.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -67.2563F, -51.751F, -0.0437F, 0.0F, 0.0F));

        PartDefinition bone61 = bone56.addOrReplaceChild("bone61", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bone63 = bone61.addOrReplaceChild("bone63", CubeListBuilder.create(), PartPose.offsetAndRotation(38.0F, -31.75F, -56.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition bone62 = bone56.addOrReplaceChild("bone62", CubeListBuilder.create().texOffs(278, 18).addBox(-40.25F, -8.0F, -58.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(246, 18).addBox(-40.25F, -27.0F, -58.0F, 4.0F, 19.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(274, 42).addBox(-42.0F, -24.0F, -57.0F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(236, 41).addBox(-45.0F, -31.0F, -58.5F, 14.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(274, 46).addBox(31.0F, -24.0F, -57.0F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(262, 18).addBox(36.25F, -27.0F, -58.0F, 4.0F, 19.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(278, 30).addBox(36.25F, -8.0F, -58.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(236, 50).addBox(31.0F, -31.0F, -58.5F, 14.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bone64 = bone62.addOrReplaceChild("bone64", CubeListBuilder.create().texOffs(245, 2).addBox(-8.9962F, -0.5872F, -3.0F, 17.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-38.0F, -31.75F, -56.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition bone66 = bone62.addOrReplaceChild("bone66", CubeListBuilder.create().texOffs(245, 10).addBox(-8.0038F, -0.5872F, -3.0F, 17.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(38.0F, -31.75F, -56.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition bone58 = bone56.addOrReplaceChild("bone58", CubeListBuilder.create().texOffs(186, 156).addBox(-20.0F, -4.2941F, -2.0F, 40.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(40.0F, -98.3934F, -56.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition bone59_r2 = bone58.addOrReplaceChild("bone59_r2", CubeListBuilder.create().texOffs(220, 140).addBox(-20.5F, -99.0F, -55.0F, 41.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 107.7311F, 28.7446F, -0.2618F, 0.0F, 0.0F));

        PartDefinition bone58_r3 = bone58.addOrReplaceChild("bone58_r3", CubeListBuilder.create().texOffs(220, 166).addBox(-20.5F, -99.0F, -71.0F, 41.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 78.7433F, 79.4391F, 0.2618F, 0.0F, 0.0F));

        PartDefinition pillar = partdefinition.addOrReplaceChild("pillar", CubeListBuilder.create().texOffs(0, 0).addBox(48.0F, -48.0F, 16.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(64, 21).addBox(47.5F, -48.5F, 15.5F, 17.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(47.5F, -33.5F, 15.5F, 17.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone2 = pillar.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(48, 0).addBox(36.9617F, -42.2246F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(40.9617F, -38.2246F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(44.9617F, -34.2246F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 14.8F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone3 = pillar.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(48, 0).addBox(37.0311F, 38.1562F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(33.0311F, 42.1562F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(29.0311F, 46.1562F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 14.8F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone4 = pillar.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(48, 0).addBox(36.9964F, -42.1904F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(40.9964F, -38.1904F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(44.9964F, -34.1904F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 33.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone5 = pillar.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(48, 0).addBox(36.9964F, 38.1904F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(32.9964F, 42.1904F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(28.9964F, 46.1904F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 33.2F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone10 = pillar.addOrReplaceChild("bone10", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 24.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition bone6 = bone10.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(48, 0).addBox(-11.7243F, -11.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-7.7243F, -7.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-3.7243F, -3.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -9.2F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone7 = bone10.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(48, 0).addBox(7.7243F, -11.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(3.7243F, -7.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-0.2757F, -3.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -9.2F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone8 = bone10.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(48, 0).addBox(7.7243F, 7.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(11.7243F, 11.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(15.7243F, 15.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 9.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone9 = bone10.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(48, 0).addBox(-11.7243F, 7.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-15.7243F, 11.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-19.7243F, 15.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 9.2F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition pillar2 = partdefinition.addOrReplaceChild("pillar2", CubeListBuilder.create().texOffs(0, 0).addBox(48.0F, -48.0F, -32.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(64, 21).addBox(47.5F, -48.5F, -32.5F, 17.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(47.5F, -33.5F, -32.5F, 17.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone11 = pillar2.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(48, 0).addBox(36.9617F, -42.2246F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(40.9617F, -38.2246F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(44.9617F, -34.2246F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -14.8F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone12 = pillar2.addOrReplaceChild("bone12", CubeListBuilder.create().texOffs(48, 0).addBox(37.0311F, 38.1562F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(33.0311F, 42.1562F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(29.0311F, 46.1562F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -14.8F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone13 = pillar2.addOrReplaceChild("bone13", CubeListBuilder.create().texOffs(48, 0).addBox(36.9964F, -42.1904F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(40.9964F, -38.1904F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(44.9964F, -34.1904F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -33.2F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone14 = pillar2.addOrReplaceChild("bone14", CubeListBuilder.create().texOffs(48, 0).addBox(36.9964F, 38.1904F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(32.9964F, 42.1904F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(28.9964F, 46.1904F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -33.2F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone15 = pillar2.addOrReplaceChild("bone15", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -24.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition bone16 = bone15.addOrReplaceChild("bone16", CubeListBuilder.create().texOffs(48, 0).addBox(-11.7243F, -11.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-7.7243F, -7.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-3.7243F, -3.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 9.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone17 = bone15.addOrReplaceChild("bone17", CubeListBuilder.create().texOffs(48, 0).addBox(7.7243F, -11.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(3.7243F, -7.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-0.2757F, -3.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 9.2F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone18 = bone15.addOrReplaceChild("bone18", CubeListBuilder.create().texOffs(48, 0).addBox(7.7243F, 7.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(11.7243F, 11.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(15.7243F, 15.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -9.2F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone19 = bone15.addOrReplaceChild("bone19", CubeListBuilder.create().texOffs(48, 0).addBox(-11.7243F, 7.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-15.7243F, 11.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-19.7243F, 15.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -9.2F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition pillar3 = partdefinition.addOrReplaceChild("pillar3", CubeListBuilder.create().texOffs(0, 0).addBox(-64.0F, -48.0F, 16.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(64, 21).addBox(-64.5F, -48.5F, 15.5F, 17.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(-64.5F, -33.5F, 15.5F, 17.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone20 = pillar3.addOrReplaceChild("bone20", CubeListBuilder.create().texOffs(48, 0).addBox(-40.9617F, -42.2246F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-44.9617F, -38.2246F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-48.9617F, -34.2246F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 14.8F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone21 = pillar3.addOrReplaceChild("bone21", CubeListBuilder.create().texOffs(48, 0).addBox(-41.0311F, 38.1562F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-37.0311F, 42.1562F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-33.0311F, 46.1562F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 14.8F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone22 = pillar3.addOrReplaceChild("bone22", CubeListBuilder.create().texOffs(48, 0).addBox(-40.9964F, -42.1904F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-44.9964F, -38.1904F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-48.9964F, -34.1904F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 33.2F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone23 = pillar3.addOrReplaceChild("bone23", CubeListBuilder.create().texOffs(48, 0).addBox(-40.9964F, 38.1904F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-36.9964F, 42.1904F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-32.9964F, 46.1904F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 33.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone24 = pillar3.addOrReplaceChild("bone24", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 24.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition bone25 = bone24.addOrReplaceChild("bone25", CubeListBuilder.create().texOffs(48, 0).addBox(7.7243F, -11.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(3.7243F, -7.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-0.2757F, -3.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -9.2F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone26 = bone24.addOrReplaceChild("bone26", CubeListBuilder.create().texOffs(48, 0).addBox(-11.7243F, -11.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-7.7243F, -7.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-3.7243F, -3.5766F, 54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -9.2F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone27 = bone24.addOrReplaceChild("bone27", CubeListBuilder.create().texOffs(48, 0).addBox(-11.7243F, 7.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-15.7243F, 11.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-19.7243F, 15.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 9.2F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone28 = bone24.addOrReplaceChild("bone28", CubeListBuilder.create().texOffs(48, 0).addBox(7.7243F, 7.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(11.7243F, 11.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(15.7243F, 15.5766F, 54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 9.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition pillar4 = partdefinition.addOrReplaceChild("pillar4", CubeListBuilder.create().texOffs(0, 0).addBox(-64.0F, -48.0F, -32.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(64, 21).addBox(-64.5F, -48.5F, -32.5F, 17.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(-64.5F, -33.5F, -32.5F, 17.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone29 = pillar4.addOrReplaceChild("bone29", CubeListBuilder.create().texOffs(48, 0).addBox(-40.9617F, -42.2246F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-44.9617F, -38.2246F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-48.9617F, -34.2246F, -0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -14.8F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone30 = pillar4.addOrReplaceChild("bone30", CubeListBuilder.create().texOffs(48, 0).addBox(-41.0311F, 38.1562F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-37.0311F, 42.1562F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-33.0311F, 46.1562F, -0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -14.8F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone31 = pillar4.addOrReplaceChild("bone31", CubeListBuilder.create().texOffs(48, 0).addBox(-40.9964F, -42.1904F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-44.9964F, -38.1904F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-48.9964F, -34.1904F, 0.0955F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -33.2F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone32 = pillar4.addOrReplaceChild("bone32", CubeListBuilder.create().texOffs(48, 0).addBox(-40.9964F, 38.1904F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-36.9964F, 42.1904F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-32.9964F, 46.1904F, 0.3045F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -33.2F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone33 = pillar4.addOrReplaceChild("bone33", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -24.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition bone34 = bone33.addOrReplaceChild("bone34", CubeListBuilder.create().texOffs(48, 0).addBox(7.7243F, -11.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(3.7243F, -7.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-0.2757F, -3.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 9.2F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone35 = bone33.addOrReplaceChild("bone35", CubeListBuilder.create().texOffs(48, 0).addBox(-11.7243F, -11.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-7.7243F, -7.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-3.7243F, -3.5766F, -54.5114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 9.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone36 = bone33.addOrReplaceChild("bone36", CubeListBuilder.create().texOffs(48, 0).addBox(-11.7243F, 7.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-15.7243F, 11.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-19.7243F, 15.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -9.2F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone37 = bone33.addOrReplaceChild("bone37", CubeListBuilder.create().texOffs(48, 0).addBox(7.7243F, 7.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(11.7243F, 11.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(15.7243F, 15.5766F, -54.1114F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -9.2F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition pillar5 = partdefinition.addOrReplaceChild("pillar5", CubeListBuilder.create().texOffs(0, 0).addBox(-32.0F, -48.0F, 48.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(64, 21).addBox(-32.5F, -48.5F, 47.5F, 17.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(-32.5F, -33.5F, 47.5F, 17.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone38 = pillar5.addOrReplaceChild("bone38", CubeListBuilder.create().texOffs(48, 0).addBox(-13.1213F, -24.731F, 31.1903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-17.1213F, -20.731F, 31.1903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-21.1213F, -16.731F, 31.1903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 14.8F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone39 = pillar5.addOrReplaceChild("bone39", CubeListBuilder.create().texOffs(48, 0).addBox(-24.3042F, 9.7179F, 31.2799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-20.3042F, 13.7179F, 31.2799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-16.3042F, 17.7179F, 31.2799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 14.8F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone40 = pillar5.addOrReplaceChild("bone40", CubeListBuilder.create().texOffs(48, 0).addBox(-24.2695F, -13.7521F, 30.8799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-28.2695F, -9.7521F, 30.8799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-32.2695F, -5.7521F, 30.8799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 33.2F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone41 = pillar5.addOrReplaceChild("bone41", CubeListBuilder.create().texOffs(48, 0).addBox(-13.156F, 20.6968F, 30.7903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-9.156F, 24.6968F, 30.7903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-5.156F, 28.6968F, 30.7903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 33.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone42 = pillar5.addOrReplaceChild("bone42", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 24.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition bone43 = bone42.addOrReplaceChild("bone43", CubeListBuilder.create().texOffs(48, 0).addBox(24.4512F, 16.8617F, 23.536F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(20.4512F, 20.8617F, 23.536F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(16.4512F, 24.8617F, 23.536F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -9.2F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone44 = bone42.addOrReplaceChild("bone44", CubeListBuilder.create().texOffs(48, 0).addBox(16.1161F, -29.0702F, 23.4166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(20.1161F, -25.0702F, 23.4166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(24.1161F, -21.0702F, 23.4166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -9.2F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone45 = bone42.addOrReplaceChild("bone45", CubeListBuilder.create().texOffs(48, 0).addBox(16.1161F, 25.0702F, 23.0166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(12.1161F, 29.0702F, 23.0166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(8.1161F, 33.0702F, 23.0166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 9.2F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone46 = bone42.addOrReplaceChild("bone46", CubeListBuilder.create().texOffs(48, 0).addBox(24.4512F, -20.8617F, 23.136F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(28.4512F, -16.8617F, 23.136F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(32.4512F, -12.8617F, 23.136F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 9.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition pillar6 = partdefinition.addOrReplaceChild("pillar6", CubeListBuilder.create().texOffs(0, 0).addBox(16.0F, -48.0F, 48.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(64, 21).addBox(15.5F, -48.5F, 47.5F, 17.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(15.5F, -33.5F, 47.5F, 17.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone47 = pillar6.addOrReplaceChild("bone47", CubeListBuilder.create().texOffs(48, 0).addBox(9.1213F, -24.731F, 31.1903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(13.1213F, -20.731F, 31.1903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(17.1213F, -16.731F, 31.1903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 14.8F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone48 = pillar6.addOrReplaceChild("bone48", CubeListBuilder.create().texOffs(48, 0).addBox(20.3042F, 9.7179F, 31.2799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(16.3042F, 13.7179F, 31.2799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(12.3042F, 17.7179F, 31.2799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 14.8F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone49 = pillar6.addOrReplaceChild("bone49", CubeListBuilder.create().texOffs(48, 0).addBox(20.2695F, -13.7521F, 30.8799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(24.2695F, -9.7521F, 30.8799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(28.2695F, -5.7521F, 30.8799F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 33.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone50 = pillar6.addOrReplaceChild("bone50", CubeListBuilder.create().texOffs(48, 0).addBox(9.156F, 20.6968F, 30.7903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(5.156F, 24.6968F, 30.7903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(1.156F, 28.6968F, 30.7903F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 33.2F, 0.1745F, 0.1745F, -0.7854F));

        PartDefinition bone51 = pillar6.addOrReplaceChild("bone51", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 24.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition bone52 = bone51.addOrReplaceChild("bone52", CubeListBuilder.create().texOffs(48, 0).addBox(-28.4512F, 16.8617F, 23.536F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-24.4512F, 20.8617F, 23.536F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-20.4512F, 24.8617F, 23.536F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, -9.2F, -0.1745F, 0.1745F, 0.7854F));

        PartDefinition bone53 = bone51.addOrReplaceChild("bone53", CubeListBuilder.create().texOffs(48, 0).addBox(-20.1161F, -29.0702F, 23.4166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-24.1161F, -25.0702F, 23.4166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-28.1161F, -21.0702F, 23.4166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, -9.2F, -0.1745F, -0.1745F, -0.7854F));

        PartDefinition bone54 = bone51.addOrReplaceChild("bone54", CubeListBuilder.create().texOffs(48, 0).addBox(-20.1161F, 25.0702F, 23.0166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-16.1161F, 29.0702F, 23.0166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-12.1161F, 33.0702F, 23.0166F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, -27.5F, 9.2F, 0.1745F, -0.1745F, 0.7854F));

        PartDefinition bone55 = bone51.addOrReplaceChild("bone55", CubeListBuilder.create().texOffs(48, 0).addBox(-28.4512F, -20.8617F, 23.136F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-32.4512F, -16.8617F, 23.136F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-36.4512F, -12.8617F, 23.136F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.25F, -27.5F, 9.2F, 0.1745F, 0.1745F, -0.7854F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        bone56.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pillar.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pillar2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pillar3.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pillar4.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pillar5.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pillar6.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}