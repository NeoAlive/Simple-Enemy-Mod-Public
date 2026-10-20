package net.nekoyuni.SimpleEnemyMod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nekoyuni.SimpleEnemyMod.entity.ai.roles.utils.UnitRole;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RoleSpawnEggItem extends SpawnEggItem {

    private final UnitRole role;

    public RoleSpawnEggItem(DeferredHolder<EntityType<?>, ? extends EntityType<? extends Mob>> typeIn,
                            int primaryColorIn, int secondaryColorIn, Properties builder, UnitRole role) {
        super(typeIn.get(), primaryColorIn, secondaryColorIn, builder);
        this.role = role;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();

    if (level.isClientSide) {
        return InteractionResult.SUCCESS;
    }
    if (!(level instanceof ServerLevel serverLevel)) {
        return InteractionResult.SUCCESS;
    }

    ItemStack itemstack = context.getItemInHand();
    BlockPos blockpos = context.getClickedPos();
    Direction direction = context.getClickedFace();
    BlockState blockstate = level.getBlockState(blockpos);

    BlockPos finalSpawnPos = blockstate.getCollisionShape(level, blockpos).isEmpty()
            ? blockpos
            : blockpos.relative(direction);

    // Upstream passes the role through EntityTag so readAdditionalSaveData sets it and rebuilds the
    // role goals before the unit joins the level; ENTITY_DATA is the 1.21 equivalent.
    CustomData.update(DataComponents.ENTITY_DATA, itemstack, tag -> tag.putString("UnitRole", this.role.name()));

    EntityType<?> entitytype = this.getType(itemstack);

    Entity entity = entitytype.spawn(
            serverLevel,
            itemstack,
            context.getPlayer(),
            finalSpawnPos,
            MobSpawnType.SPAWN_EGG,
            true,
            !blockpos.equals(finalSpawnPos) && direction == Direction.UP
    );

    itemstack.remove(DataComponents.ENTITY_DATA);

    if (entity == null) {
        return InteractionResult.PASS;
    }

    if (entity instanceof Mob mob) {
        mob.setYHeadRot(mob.getYRot());
        mob.setYBodyRot(mob.getYRot());
    }

    level.playSound(null, finalSpawnPos, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 0.7F, 0.9F);
    itemstack.shrink(1);

    return InteractionResult.CONSUME;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId());
    }
}
