package net.nekoyuni.SimpleEnemyMod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.model.SlotModel;
import com.tacz.guns.client.resource.pojo.display.gun.MuzzleFlash;
import com.tacz.guns.client.resource.pojo.model.BonesItem;
import com.tacz.guns.resource.modifier.custom.SilenceModifier;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NpcMuzzleFlashRenderer {

    private static final long TIME_RANGE = 50L;
    private static final long CLEANUP_INTERVAL_MS = 250L;
    private static final SlotModel FLASH_MODEL = new SlotModel(true);
    private static final Map<Integer, FlashData> FLASH_MAP = new ConcurrentHashMap<>();
    private static volatile long lastCleanup = 0L;

    public static void onNpcShoot(int entityId) {
        float randomRot = (float) (Math.random() * 360.0);
        FLASH_MAP.put(entityId, new FlashData(System.currentTimeMillis(), randomRot));
    }

    public static void render(LivingEntity entity, ItemStack gunStack, PoseStack poseStack) {
        cleanExpiredEntries();

        int id = entity.getId();
        FlashData data = FLASH_MAP.get(id);
        if (data == null) {
            return;
        }

        long elapsed = System.currentTimeMillis() - data.timestamp;
        if (elapsed > TIME_RANGE) {
            FLASH_MAP.remove(id);
            return;
        }

        IGun iGun = IGun.getIGunOrNull(gunStack);
        if (iGun == null) {
            return;
        }

        ItemStack muzzleAttachment = iGun.getAttachment(entity.level().registryAccess(), gunStack, AttachmentType.MUZZLE);
        IAttachment iAttachment = IAttachment.getIAttachmentOrNull(muzzleAttachment);
        if (iAttachment != null) {
            ResourceLocation attachmentId = iAttachment.getAttachmentId(muzzleAttachment);
            boolean[] isSilenced = {false};
            TimelessAPI.getCommonAttachmentIndex(attachmentId).ifPresent(index -> {
                Map<String, JsonProperty<?>> modifier = index.getData().getModifier();
                if (modifier.containsKey(SilenceModifier.ID)
                        && modifier.get(SilenceModifier.ID).getValue() instanceof Pair<?, ?> pair
                        && (Boolean) pair.right()) {
                    isSilenced[0] = true;
                }
            });
            if (isSilenced[0]) {
                return;
            }
        }

        TimelessAPI.getGunDisplay(gunStack).ifPresent(display -> {
            MuzzleFlash muzzleFlash = display.getMuzzleFlash();
            if (muzzleFlash == null) {
                return;
            }

            BedrockGunModel gunModel = display.getGunModel();
            List<?> muzzlePath = gunModel.getMuzzleFlashPosPath();
            if (muzzlePath == null || muzzlePath.isEmpty()) {
                return;
            }

            BonesItem muzzleBone = gunModel.getIndexBones().get("muzzle_flash");
            if (muzzleBone == null) {
                return;
            }

            List<Float> pivot = muzzleBone.getPivot();
            float pivotX = pivot.get(0);
            float pivotZ = pivot.get(2);

            BonesItem current = muzzleBone;
            BonesItem parent;
            while (current.getParent() != null && (parent = gunModel.getIndexBones().get(current.getParent())) != null) {
                current = parent;
            }
            float rootY = current.getPivot().get(1);
            float rootZ = current.getPivot().get(2);

            String parentName = muzzleBone.getParent();
            BonesItem parentBone = parentName != null ? gunModel.getIndexBones().get(parentName) : null;
            float refZ = parentBone != null && parentBone.getPivot() != null ? parentBone.getPivot().get(2) : rootZ;

            float offsetY = 0.0f;
            float scale = 1.0f / 16.0f;
            float rx = pivotX * scale;
            float ry = 0.0f;
            float rzFromParent = (pivotZ - refZ) * scale;
            float rzFromRoot = (pivotZ - rootZ) * scale;
            float rz;

            if (Math.abs(rzFromParent) < 0.15f) {
                rz = rzFromRoot * 0.6f;
                offsetY = 0.07f;
            } else if (Math.abs(rzFromParent) > 1.0f) {
                float excess = Math.abs(rzFromParent) - 0.9f;
                rz = rzFromParent + excess * 0.5f;
                offsetY = 0.085f;
            } else {
                rz = rzFromParent;
            }

            if (rz > 0.0f) {
                rz = -Math.abs(rzFromRoot);
            }

            PoseStack freshPs = new PoseStack();
            freshPs.last().pose().mul(poseStack.last().pose());
            freshPs.last().normal().mul(poseStack.last().normal());
            freshPs.translate(rx, ry + offsetY, rz);

            data.normal = new Matrix3f(freshPs.last().normal());
            data.pose = new Matrix4f(freshPs.last().pose());
            doRender(muzzleFlash, data, elapsed);
        });
    }

    private static void doRender(MuzzleFlash muzzleFlash, FlashData data, long elapsed) {
        if (data.normal == null || data.pose == null) {
            return;
        }

        float scale = 0.5f * muzzleFlash.getScale();
        float halfRange = TIME_RANGE / 2.0f;
        if (elapsed < halfRange) {
            scale *= elapsed / halfRange;
        }

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack ps = new PoseStack();
        ps.last().normal().mul(data.normal);
        ps.last().pose().mul(data.pose);

        ps.pushPose();
        ps.scale(scale, scale, scale);
        ps.mulPose(Axis.ZP.rotationDegrees(data.randomRotation));
        ps.translate(0.0f, -1.0f, 0.0f);
        FLASH_MODEL.renderToBuffer(ps, bufferSource.getBuffer(RenderType.entityTranslucent(muzzleFlash.getTexture())),
                0xF000F0, 655360, -1);
        ps.popPose();

        ps.pushPose();
        ps.scale(scale / 2.0f, scale / 2.0f, scale / 2.0f);
        ps.mulPose(Axis.ZP.rotationDegrees(data.randomRotation));
        ps.translate(0.0, -0.9, 0.0);
        FLASH_MODEL.renderToBuffer(ps, bufferSource.getBuffer(RenderType.energySwirl(muzzleFlash.getTexture(), 1.0f, 1.0f)),
                0xF000F0, 655360, -1);
        ps.popPose();
    }

    private static void cleanExpiredEntries() {
        long now = System.currentTimeMillis();
        if (now - lastCleanup < CLEANUP_INTERVAL_MS) {
            return;
        }
        lastCleanup = now;

        Iterator<Map.Entry<Integer, FlashData>> it = FLASH_MAP.entrySet().iterator();
        while (it.hasNext()) {
            if (now - it.next().getValue().timestamp > TIME_RANGE) {
                it.remove();
            }
        }
    }

    private static class FlashData {
        final long timestamp;
        final float randomRotation;
        Matrix3f normal = null;
        Matrix4f pose = null;

        FlashData(long timestamp, float randomRotation) {
            this.timestamp = timestamp;
            this.randomRotation = randomRotation;
        }
    }
}
