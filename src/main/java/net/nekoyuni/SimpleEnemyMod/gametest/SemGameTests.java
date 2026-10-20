package net.nekoyuni.SimpleEnemyMod.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.api.event.UnitGoalsSetupEvent;
import net.nekoyuni.SimpleEnemyMod.api.event.UnitOrderEvent;
import net.nekoyuni.SimpleEnemyMod.api.event.UnitTargetEvent;
import net.nekoyuni.SimpleEnemyMod.api.faction.SemFactionRelations;
import net.nekoyuni.SimpleEnemyMod.entity.ai.orders.OrderType;
import net.nekoyuni.SimpleEnemyMod.entity.unit.AbstractUnit;
import net.nekoyuni.SimpleEnemyMod.entity.unit.PmcUnitEntity;
import net.nekoyuni.SimpleEnemyMod.entity.unit.RUunitEntity;
import net.nekoyuni.SimpleEnemyMod.entity.unit.USunitEntity;
import net.nekoyuni.SimpleEnemyMod.entity.unit.util.UnitFactionTags;
import net.nekoyuni.SimpleEnemyMod.network.packets.PacketIssueOrder;
import net.nekoyuni.SimpleEnemyMod.registry.ModEntities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.items.IItemHandler;
import net.nekoyuni.SimpleEnemyMod.inventory.UnitInventoryHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.minecraft.network.chat.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dev-only GameTests (registered only when the simpleenemymod gametest namespace is enabled, i.e. in
 * the run configs) for the port's platform glue and the additive {@code api} hooks.
 * Run with {@code ./gradlew runGameTestServer}. Test listeners only react to entities carrying the
 * tags below, so they never affect real gameplay.
 */
@GameTestHolder(SimpleEnemyMod.MODID)
@PrefixGameTestTemplate(false)
public class SemGameTests {

    private static final String TEMPLATE = "gametest_empty";
    private static final String TAG_DENY = "sem_gametest_deny";
    private static final String TAG_ALLOW = "sem_gametest_allow";
    private static final String TAG_FRIEND = "sem_gametest_friend";
    private static final double REJECT_Y = -12345.0;

    private static final Set<Integer> GOAL_SETUP_SEEN = ConcurrentHashMap.newKeySet();

    static {
        NeoForge.EVENT_BUS.addListener((UnitGoalsSetupEvent e) -> GOAL_SETUP_SEEN.add(e.getUnit().getId()));

        NeoForge.EVENT_BUS.addListener((UnitTargetEvent.Pre e) -> {
            if (e.getTarget().getTags().contains(TAG_DENY)) e.setResult(TriState.FALSE);
            if (e.getTarget().getTags().contains(TAG_ALLOW)) e.setResult(TriState.TRUE);
        });

        NeoForge.EVENT_BUS.addListener((UnitOrderEvent e) -> {
            if (e.getTargetPos().y == REJECT_Y) e.reject(Component.literal("rejected by gametest"));
        });

        SemFactionRelations.registerNpcFriendlinessOverride((npc, other) ->
                other.getTags().contains(TAG_FRIEND) ? TriState.TRUE : TriState.DEFAULT);
    }

    // FakePlayer: GameTestHelper's mock player triggers TaCZ's login sync, which fails on the mock connection
    private static ServerPlayer fakePlayer(GameTestHelper helper) {
        return FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "sem_gametest"));
    }

    private static <T extends AbstractUnit> T spawnUnit(GameTestHelper helper, EntityType<T> type, BlockPos rel) {
        T unit = helper.spawn(type, rel);
        unit.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(unit.blockPosition()),
                MobSpawnType.MOB_SUMMONED, null);
        return unit;
    }

    @GameTest(template = TEMPLATE)
    public static void factionTagsAndCapability(GameTestHelper helper) {
        RUunitEntity ru = spawnUnit(helper, ModEntities.RUUNIT.get(), new BlockPos(0, 1, 0));
        USunitEntity us = spawnUnit(helper, ModEntities.USUNIT.get(), new BlockPos(1, 1, 0));
        PmcUnitEntity pmc = spawnUnit(helper, ModEntities.PMCUNIT.get(), new BlockPos(2, 1, 0));

        helper.assertTrue(UnitFactionTags.isRuUnit(ru) && UnitFactionTags.isUsUnit(us) && UnitFactionTags.isPmcUnit(pmc),
                "faction entity-type tags not applied");
        helper.assertTrue(pmc.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof UnitInventoryHandler,
                "PMC item handler capability is not its 18-slot inventory");
        IItemHandler ruHandler = ru.getCapability(Capabilities.ItemHandler.ENTITY, null);
        helper.assertTrue(ruHandler == null || ruHandler.getSlots() == 6,
                "RU unit should only expose NeoForge's equipment wrapper (hands + armour)");
        helper.assertTrue(pmc.getOffhandItem().isEmpty() == pmc.getInventory().getStackInSlot(1).isEmpty(),
                "PMC slot 1 does not mirror the offhand");
        helper.assertTrue(!UnitFactionTags.isFriendlyToNpcFaction(ru, us), "RU treats US as friendly");
        helper.assertTrue(GOAL_SETUP_SEEN.contains(ru.getId()) && GOAL_SETUP_SEEN.contains(pmc.getId()),
                "UnitGoalsSetupEvent not fired from setupRoleGoals");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void factionOverride(GameTestHelper helper) {
        RUunitEntity ru = spawnUnit(helper, ModEntities.RUUNIT.get(), new BlockPos(0, 1, 0));
        USunitEntity us = spawnUnit(helper, ModEntities.USUNIT.get(), new BlockPos(1, 1, 0));

        helper.assertTrue(!UnitFactionTags.isFriendlyToNpcFaction(ru, us), "baseline: US should be hostile to RU");
        us.addTag(TAG_FRIEND);
        helper.assertTrue(UnitFactionTags.isFriendlyToNpcFaction(ru, us), "faction override was not applied");

        ru.setTarget(us);
        helper.assertTrue(ru.getTarget() == null, "RU targeted a unit the override made friendly");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void targetEventVetoAndBypass(GameTestHelper helper) {
        RUunitEntity ru = spawnUnit(helper, ModEntities.RUUNIT.get(), new BlockPos(0, 1, 0));
        Zombie denied = helper.spawn(EntityType.ZOMBIE, new BlockPos(1, 1, 0));
        denied.addTag(TAG_DENY);
        Zombie normal = helper.spawn(EntityType.ZOMBIE, new BlockPos(2, 1, 0));

        ru.setTarget(denied);
        helper.assertTrue(ru.getTarget() == null, "UnitTargetEvent.Pre FALSE did not veto the target");
        ru.setTarget(normal);
        helper.assertTrue(ru.getTarget() == normal, "default path no longer sets a hostile target");

        PmcUnitEntity pmc = spawnUnit(helper, ModEntities.PMCUNIT.get(), new BlockPos(0, 1, 1));
        ServerPlayer player = fakePlayer(helper);
        pmc.setTarget(player);
        helper.assertTrue(pmc.getTarget() == null, "PMC targeted a player without a hook");
        player.addTag(TAG_ALLOW);
        pmc.setTarget(player);
        helper.assertTrue(pmc.getTarget() == player, "UnitTargetEvent.Pre TRUE did not bypass SEM's veto");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void orderEventCancel(GameTestHelper helper) {
        PmcUnitEntity pmc = spawnUnit(helper, ModEntities.PMCUNIT.get(), new BlockPos(0, 1, 0));
        ServerPlayer player = fakePlayer(helper);
        pmc.setOwner(player.getUUID());
        OrderType before = pmc.getOrder();

        PacketIssueOrder.handleOnServer(new PacketIssueOrder(pmc.getId(), OrderType.HOLD_POSITION,
                new Vec3(0, REJECT_Y, 0), 0, -1), player);
        helper.assertTrue(pmc.getOrder() == before, "cancelled UnitOrderEvent still applied the order");

        PacketIssueOrder.handleOnServer(new PacketIssueOrder(pmc.getId(), OrderType.HOLD_POSITION,
                pmc.position(), 0, -1), player);
        helper.assertTrue(pmc.getOrder() == OrderType.HOLD_POSITION, "order not applied on the default path");
        helper.succeed();
    }
}
