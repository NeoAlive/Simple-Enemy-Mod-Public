package net.nekoyuni.SimpleEnemyMod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, SimpleEnemyMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_US_UNIT_HURT = registerSoundEvents("sound_us_unit_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_US_UNIT_DEATH = registerSoundEvents("sound_us_unit_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_US_UNIT_ALERT = registerSoundEvents("sound_us_unit_alert");

    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RU_UNIT_HURT = registerSoundEvents("sound_ru_unit_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RU_UNIT_DEATH = registerSoundEvents("sound_ru_unit_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RU_UNIT_ALERT = registerSoundEvents("sound_ru_unit_alert");

    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_BULLET_IMPACT = registerSoundEvents("sound_bullet_impact");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(SimpleEnemyMod.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
