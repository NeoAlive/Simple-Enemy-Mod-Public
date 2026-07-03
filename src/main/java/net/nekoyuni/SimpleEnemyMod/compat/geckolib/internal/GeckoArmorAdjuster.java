package net.nekoyuni.SimpleEnemyMod.compat.geckolib.internal;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GeckoArmorAdjuster {

    private static final float SCALE_HEAD = 1.0F;
    private static final float SCALE_BODY = 1.0F;
    private static final float SCALE_ARMS = 1.04F;
    private static final float SCALE_LEGS = 1.08F;
    private static final float SCALE_BOOTS = 1.0F;

    private static final float OFF_HEAD_X = 0.0F;
    private static final float OFF_HEAD_Y = 6.00F;
    private static final float OFF_HEAD_Z = -1.75F;

    private static final float OFF_BODY_X = 0.0F;
    private static final float OFF_BODY_Y = 5.90F;
    private static final float OFF_BODY_Z = -1.85F;

    private static final float OFF_ARM_RIGHT_X = -0.20F;
    private static final float OFF_ARM_RIGHT_Y = 5.90F;
    private static final float OFF_ARM_RIGHT_Z = -1.65F;

    private static final float OFF_ARM_LEFT_X = 0.20F;
    private static final float OFF_ARM_LEFT_Y = 5.90F;
    private static final float OFF_ARM_LEFT_Z = -1.65F;

    private static final float OFF_LEG_RIGHT_X = 0.10F;
    private static final float OFF_LEG_RIGHT_Y = 5.98F;
    private static final float OFF_LEG_RIGHT_Z = -0.5F;

    private static final float OFF_LEG_LEFT_X = -0.10F;
    private static final float OFF_LEG_LEFT_Y = 5.98F;
    private static final float OFF_LEG_LEFT_Z = -0.5F;

    private static final float OFF_BOOT_RIGHT_X = 0.25F;
    private static final float OFF_BOOT_RIGHT_Y = 7.0F;
    private static final float OFF_BOOT_RIGHT_Z = -1.2F;

    private static final float OFF_BOOT_LEFT_X = -0.25F;
    private static final float OFF_BOOT_LEFT_Y = 7.0F;
    private static final float OFF_BOOT_LEFT_Z = -1.2F;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void applyAdjustments(GeoArmorRenderer<?> renderer, HumanoidModel<?> baseModel, EquipmentSlot slot) {
        if (renderer == null || baseModel == null) return;

        GeoModel model = renderer.getGeoModel();

        GeoBone geoHead = renderer.getHeadBone(model);
        GeoBone geoBody = renderer.getBodyBone(model);
        GeoBone geoRightArm = renderer.getRightArmBone(model);
        GeoBone geoLeftArm = renderer.getLeftArmBone(model);
        GeoBone geoRightLeg = renderer.getRightLegBone(model);
        GeoBone geoLeftLeg = renderer.getLeftLegBone(model);
        GeoBone geoRightBoot = renderer.getRightBootBone(model);
        GeoBone geoLeftBoot = renderer.getLeftBootBone(model);

        ModelPart vanHead = baseModel.head;
        ModelPart vanBody = baseModel.body;
        ModelPart vanRightArm = baseModel.rightArm;
        ModelPart vanLeftArm = baseModel.leftArm;
        ModelPart vanRightLeg = baseModel.rightLeg;
        ModelPart vanLeftLeg = baseModel.leftLeg;

        switch (slot) {
            case HEAD -> {
                applyScale(geoHead, SCALE_HEAD);
                applyOffset(vanHead, OFF_HEAD_X, OFF_HEAD_Y, OFF_HEAD_Z);
            }
            case CHEST -> {
                applyScale(geoBody, SCALE_BODY);
                applyOffset(vanBody, OFF_BODY_X, OFF_BODY_Y, OFF_BODY_Z);

                applyScale(geoRightArm, SCALE_ARMS);
                applyOffset(vanRightArm, OFF_ARM_RIGHT_X, OFF_ARM_RIGHT_Y, OFF_ARM_RIGHT_Z);

                applyScale(geoLeftArm, SCALE_ARMS);
                applyOffset(vanLeftArm, OFF_ARM_LEFT_X, OFF_ARM_LEFT_Y, OFF_ARM_LEFT_Z);
            }
            case LEGS -> {
                applyScale(geoBody, SCALE_BODY);
                applyOffset(vanBody, OFF_BODY_X, OFF_BODY_Y, OFF_BODY_Z);

                applyScale(geoRightLeg, SCALE_LEGS);
                applyOffset(vanRightLeg, OFF_LEG_RIGHT_X, OFF_LEG_RIGHT_Y, OFF_LEG_RIGHT_Z);

                applyScale(geoLeftLeg, SCALE_LEGS);
                applyOffset(vanLeftLeg, OFF_LEG_LEFT_X, OFF_LEG_LEFT_Y, OFF_LEG_LEFT_Z);
            }
            case FEET -> {
                applyScale(geoRightBoot, SCALE_BOOTS);
                applyOffset(vanRightLeg, OFF_BOOT_RIGHT_X, OFF_BOOT_RIGHT_Y, OFF_BOOT_RIGHT_Z);

                applyScale(geoLeftBoot, SCALE_BOOTS);
                applyOffset(vanLeftLeg, OFF_BOOT_LEFT_X, OFF_BOOT_LEFT_Y, OFF_BOOT_LEFT_Z);
            }
        }
    }

    private static void applyScale(GeoBone bone, float scale) {
        if (bone != null) {
            bone.setScaleX(scale);
            bone.setScaleY(scale);
            bone.setScaleZ(scale);
        }
    }

    private static void applyOffset(ModelPart part, float x, float y, float z) {
        if (part != null) {
            part.x += x;
            part.y += y;
            part.z += z;
        }
    }
}
