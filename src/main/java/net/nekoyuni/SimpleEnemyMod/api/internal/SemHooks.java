package net.nekoyuni.SimpleEnemyMod.api.internal;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.nekoyuni.SimpleEnemyMod.api.event.DynamicEventFireEvent;
import net.nekoyuni.SimpleEnemyMod.api.event.GarrisonSpawnEvent;
import net.nekoyuni.SimpleEnemyMod.api.event.UnitGoalsSetupEvent;
import net.nekoyuni.SimpleEnemyMod.api.event.UnitLoadoutsLoadingEvent;
import net.nekoyuni.SimpleEnemyMod.api.event.UnitOrderEvent;
import net.nekoyuni.SimpleEnemyMod.api.event.UnitOwnerChangedEvent;
import net.nekoyuni.SimpleEnemyMod.api.event.UnitTargetEvent;
import net.nekoyuni.SimpleEnemyMod.entity.ai.orders.OrderType;
import net.nekoyuni.SimpleEnemyMod.entity.unit.AbstractUnit;
import net.nekoyuni.SimpleEnemyMod.entity.unit.PmcUnitEntity;
import net.nekoyuni.SimpleEnemyMod.procedural.events.system.DynamicEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Not API. One-line dispatch calls placed in SEM's own classes so upstream code stays recognisable
 * (and upstream merges stay clean). Addons use the events and registries in {@code api} instead.
 */
public final class SemHooks {

    private SemHooks() {
    }

    /** @return true if a listener rejected the order (SEM must not apply it). */
    public static boolean onOrderIssued(LivingEntity unit, ServerPlayer sender, OrderType order, Vec3 targetPos,
                                        int formationIndex, int targetEntityId) {
        UnitOrderEvent event = NeoForge.EVENT_BUS.post(
                new UnitOrderEvent(unit, sender, order, targetPos, formationIndex, targetEntityId));

        if (event.isCanceled() && event.getFailureMessage() != null) {
            sender.displayClientMessage(event.getFailureMessage(), true);
        }
        return event.isCanceled();
    }

    public static TriState preSetTarget(AbstractUnit unit, @Nullable LivingEntity target) {
        if (target == null) {
            return TriState.DEFAULT;
        }
        return NeoForge.EVENT_BUS.post(new UnitTargetEvent.Pre(unit, target)).getResult();
    }

    public static void postSetTarget(AbstractUnit unit, @Nullable LivingEntity oldTarget, @Nullable LivingEntity newTarget) {
        if (oldTarget != newTarget) {
            NeoForge.EVENT_BUS.post(new UnitTargetEvent.Post(unit, oldTarget, newTarget));
        }
    }

    public static void onGoalsSetup(AbstractUnit unit, GoalSelector goalSelector, GoalSelector targetSelector) {
        NeoForge.EVENT_BUS.post(new UnitGoalsSetupEvent(unit, unit.getRole(), goalSelector, targetSelector));
    }

    public static void onOwnerChanged(PmcUnitEntity unit, @Nullable UUID oldOwner, @Nullable UUID newOwner) {
        if (unit.level().isClientSide || !unit.isAddedToLevel() || Objects.equals(oldOwner, newOwner)) {
            return;
        }
        NeoForge.EVENT_BUS.post(new UnitOwnerChangedEvent(unit, oldOwner, newOwner));
    }

    /** @return true if a listener cancelled the event (SEM must not execute it). */
    public static boolean preDynamicEvent(ServerLevel level, ServerPlayer player, DynamicEvent event, BlockPos pos) {
        return NeoForge.EVENT_BUS.post(new DynamicEventFireEvent.Pre(level, player, event, pos)).isCanceled();
    }

    public static void postDynamicEvent(ServerLevel level, ServerPlayer player, DynamicEvent event, BlockPos pos, boolean success) {
        NeoForge.EVENT_BUS.post(new DynamicEventFireEvent.Post(level, player, event, pos, success));
    }

    /** @return true if a listener cancelled this guard spawn. */
    public static boolean onGarrisonSpawn(ServerLevel level, BlockPos basePos, boolean isRu) {
        return NeoForge.EVENT_BUS.post(new GarrisonSpawnEvent(level, basePos, isRu)).isCanceled();
    }

    public static void onLoadoutsLoading(Map<ResourceLocation, JsonElement> files) {
        NeoForge.EVENT_BUS.post(new UnitLoadoutsLoadingEvent(files));
    }
}
