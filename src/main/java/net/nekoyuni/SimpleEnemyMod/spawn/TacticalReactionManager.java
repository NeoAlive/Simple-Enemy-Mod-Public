package net.nekoyuni.SimpleEnemyMod.spawn;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber
public class TacticalReactionManager {

    private static final Queue<Runnable> pendingReactions = new ConcurrentLinkedQueue<>();
    private static final int MAX_REACTIONS_PER_TICK = 5;

    public static void queueReaction(Runnable reaction) {
        pendingReactions.add(reaction);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        processQueue();
    }

    private static void processQueue() {
        int processedCount = 0;

        while (!pendingReactions.isEmpty() && processedCount < MAX_REACTIONS_PER_TICK) {
            Runnable task = pendingReactions.poll();
            if (task != null) {
                try {
                    task.run();
                } catch (Exception ignored) {
                }
            }
            processedCount++;
        }
    }
}
