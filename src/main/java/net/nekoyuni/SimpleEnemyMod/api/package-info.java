/**
 * Additive extension points for addons (e.g. tacz_sewv) of the Simple Enemy Mod NeoForge port.
 *
 * <p>Not part of upstream SEM. Every hook is inert when nothing is registered, so with no listeners
 * the mod behaves exactly like upstream. Events in {@link net.nekoyuni.SimpleEnemyMod.api.event}
 * are posted on {@code NeoForge.EVENT_BUS}; hot-path checks (faction relations, goal suppression)
 * use plain registries instead of events to avoid per-tick allocations.
 *
 * <p>{@code api.internal} is the dispatch glue called from SEM's own classes and is not API.
 */
package net.nekoyuni.SimpleEnemyMod.api;
