package net.nekoyuni.SimpleEnemyMod.event.common;

import com.tacz.guns.api.event.common.GunShootEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.entity.client.NpcMuzzleFlashRenderer;
import net.nekoyuni.SimpleEnemyMod.entity.unit.AbstractUnit;

@EventBusSubscriber(modid = SimpleEnemyMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TaczGunShootEventListener {

    @SubscribeEvent
    public static void onGunShoot(GunShootEvent event) {
        if (event.getLogicalSide() != LogicalSide.CLIENT) {
            return;
        }
        if (!(event.getShooter() instanceof AbstractUnit)) {
            return;
        }
        NpcMuzzleFlashRenderer.onNpcShoot(event.getShooter().getId());
    }
}
