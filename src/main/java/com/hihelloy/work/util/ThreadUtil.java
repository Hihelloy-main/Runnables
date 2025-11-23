package com.hihelloy.work.util;


import com.hihelloy.work.PaperRunnable;
import com.hihelloy.work.Runnables;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for ensuring that a task is run on the correct thread.
 * Ensures compatibility between Folia and non-Folia servers. */
public class ThreadUtil {

    /**
     * Runs a task on the same thread as an entity. On Spigot, this is the main
     * thread. On Folia, this is the thread that the entity is on.<br><br>
     *
     * <b>NOTE:</b> On Folia, this will run the task <i>next</i> tick as it
     * is impossible to immediately run a task on the same thread as an entity.
     * This is because the task has to find what region the entity is in and
     * wait for the next tick in that thread to begin.
     * @param entity The entity to run the task on.
     * @param runnable The task to run.
     */
    public static void ensureEntity(Entity entity, Runnable runnable) {
        if (PaperRunnable.isFolia()) {
            if (Bukkit.isOwnedByCurrentRegion(entity) || Bukkit.isStopping()) {
                runnable.run();
                return;
            }
            entity.getScheduler().execute(Runnables.plugin, runnable, null, 1L);
        } else {
            if (Bukkit.isPrimaryThread()) {
                runnable.run();
                return;
            }
            Bukkit.getScheduler().runTask(Runnables.plugin, runnable);
        }
    }

    /**
     * Runs a task on the same thread as an entity after a delay. On Spigot, this is the main
     * thread. On Folia, this is the thread that the entity is on.
     * @param entity The entity to run the task on.
     * @param runnable The task to run.
     * @param delay The delay in ticks before running the task.
     */
    public static void ensureEntityDelay(Entity entity, Runnable runnable, long delay) {
        delay = Math.max(1, delay);
        if (PaperRunnable.isFolia()) {
            entity.getScheduler().execute(Runnables.plugin, runnable, null, delay);
        } else {
            Bukkit.getScheduler().runTaskLater(Runnables.plugin, runnable, delay);
        }
    }

    /**
     * Runs a task on the same thread as an entity after a delay and repeats it until cancelled.
     * On Spigot, this is the main thread. On Folia, this is the thread that the entity is on.
     * @param entity The entity to run the task on.
     * @param runnable The task to run.
     * @param delay The delay in ticks before running the task.
     * @param repeat The delay in ticks between each repeat of the task.
     */
    public static Object ensureEntityTimer(Entity entity, Runnable runnable, long delay, long repeat) {
        delay = Math.max(1, delay);
        if (PaperRunnable.isFolia()) {
            return entity.getScheduler().runAtFixedRate(Runnables.plugin, (task) -> runnable.run(), null, delay, repeat);
        } else {
            return Bukkit.getScheduler().runTaskTimer(Runnables.plugin, runnable, delay, repeat);
        }
    }

    /**
     * Runs a task on the same thread as a location. On Spigot, this is the main
     * thread. On Folia, this is the thread that the location is in.
     * @param location The location to run the task on.
     * @param runnable The task to run.
     */
    public static void ensureLocation(Location location, Runnable runnable) {
        if (PaperRunnable.isFolia()) {
            if (Bukkit.isOwnedByCurrentRegion(location) || Bukkit.isStopping()) {
                runnable.run();
                return;
            }
            RegionScheduler scheduler = Bukkit.getRegionScheduler();
            scheduler.execute(Runnables.plugin, location, runnable);
        } else {
            if (Bukkit.isPrimaryThread()) {
                runnable.run();
                return;
            }
            Bukkit.getScheduler().runTask(Runnables.plugin, runnable);
        }
    }

    /**
     * Runs a task on the same thread as a location after a delay. On Spigot, this is the main
     * thread. On Folia, this is the thread that the location is in.
     * @param location The location to run the task on.
     * @param runnable The task to run.
     * @param delay The delay in ticks before running the task.
     */
    public static void ensureLocationDelay(@NotNull Location location, Runnable runnable, long delay) {
        delay = Math.max(1, delay);
        if (PaperRunnable.isFolia()) {
            RegionScheduler scheduler = Bukkit.getRegionScheduler();
            scheduler.runDelayed(Runnables.plugin, location, (task) -> runnable.run(), delay);
        } else {
            Bukkit.getScheduler().runTaskLater(Runnables.plugin, runnable, delay);
        }
    }

    /**
     * Runs a task on the same thread as a location after a delay and repeats it until cancelled.
     * On Spigot, this is the main thread. On Folia, this is the thread that the location is in.
     * @param location The location to run the task on.
     * @param runnable The task to run.
     * @param delay The delay in ticks before running the task.
     * @param repeat The delay in ticks between each repeat of the task.
     * @return The task object that can be used to cancel the task. Is a
     * {@link io.papermc.paper.threadedregions.scheduler.ScheduledTask} on Folia and a
     * {@link org.bukkit.scheduler.BukkitTask} on Spigot.
     */
    public static Object ensureLocationTimer(Location location, Runnable runnable, long delay, long repeat) {
        delay = Math.max(1, delay);
        if (PaperRunnable.isFolia()) {
            RegionScheduler scheduler = Bukkit.getRegionScheduler();
            return scheduler.runAtFixedRate(Runnables.plugin, location, (task) -> runnable.run(), delay, repeat);
        } else {
            return Bukkit.getScheduler().runTaskLater(Runnables.plugin, runnable, delay);
        }
    }

    /**
     * Runs a task asynchronously.
     * @param runnable The task to run.
     */
    public static void runAsync(Runnable runnable) {
        if (PaperRunnable.isFolia()) {
            if (Bukkit.isStopping()) {
                runnable.run();
                return;
            }
            Bukkit.getAsyncScheduler().runNow(Runnables.plugin, (task) -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Runnables.plugin, runnable);
        }
    }

    /**
     * Runs a task asynchronously after a delay.
     * @param runnable The task to run.
     * @param delay The delay in ticks before running the task.
     */
    public static void runAsyncLater(Runnable runnable, long delay) {
        delay = Math.max(1, delay);
        if (PaperRunnable.isFolia()) {
            Bukkit.getAsyncScheduler().runDelayed(Runnables.plugin, (task) -> runnable.run(), delay * 50, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getScheduler().runTaskLater(Runnables.plugin, runnable, delay);
        }
    }

    /**
     * Runs a task asynchronously after a delay and repeats it until cancelled.
     * @param runnable The task to run.
     * @param delay The delay in ticks before running the task.
     * @param repeat The delay in ticks between each repeat of the task.
     */
    public static Object runAsyncTimer(Runnable runnable, long delay, long repeat) {
        delay = Math.max(1, delay);
        if (PaperRunnable.isFolia()) {
            return Bukkit.getAsyncScheduler().runAtFixedRate(Runnables.plugin, (task) -> runnable.run(), delay * 50, repeat * 50, TimeUnit.MILLISECONDS);
        } else {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(Runnables.plugin, runnable, delay, repeat);
        }
    }

    /**
     * Runs a task synchronously. On Spigot, this is on the main thread. On Folia,
     * this is on the global region thread.
     * @param runnable The task to run.
     */
    public static void runSync(Runnable runnable) {
        if (PaperRunnable.isFolia()) {
            if (Bukkit.isStopping()) {
                runnable.run();
                return;
            }
            Bukkit.getGlobalRegionScheduler().run(Runnables.plugin, (task) -> runnable.run());
        } else {
            Bukkit.getScheduler().runTask(Runnables.plugin, runnable);
        }
    }

    /**
     * Runs a task synchronously after a delay. On Spigot, this is on the main thread.
     * On Folia, this is on the global region thread.
     * @param runnable The task to run.
     * @param delay The delay in ticks before running the task.
     * @return The task object that can be used to cancel the task. Is a
     * {@link io.papermc.paper.threadedregions.scheduler.ScheduledTask} on Folia and a
     * {@link org.bukkit.scheduler.BukkitTask} on Spigot.
     */
    public static Object runSyncLater(Runnable runnable, long delay) {
        delay = Math.max(1, delay);
        if (PaperRunnable.isFolia()) {
            return Bukkit.getGlobalRegionScheduler().runDelayed(Runnables.plugin, (task) -> runnable.run(), delay);
        } else {
            return  Bukkit.getScheduler().runTaskLater(Runnables.plugin, runnable, delay);
        }
    }

    /**
     * Runs a task synchronously after a delay and repeats it until cancelled.
     * On Spigot, this is on the main thread. On Folia, this is on the global region thread.
     * @param runnable The task to run.
     * @param delay The delay in ticks before running the task.
     * @param repeat The delay in ticks between each repeat of the task.
     */
    public static Object runSyncTimer(Runnable runnable, long delay, long repeat) {
        delay = Math.max(1, delay);
        if (PaperRunnable.isFolia()) {
            return Bukkit.getGlobalRegionScheduler().runAtFixedRate(Runnables.plugin, (task) -> runnable.run(), delay, repeat);
        } else {
            return Bukkit.getScheduler().runTaskTimer(Runnables.plugin, runnable, delay, repeat);
        }
    }

    /**
     * Cancels a task that was created with {@link #ensureLocationTimer(Location, Runnable, long, long)}
     * or {@link #ensureEntityTimer(Entity, Runnable, long, long)}.
     * @param task The task to cancel. This is the object returned from
     * {@link #ensureLocationTimer(Location, Runnable, long, long)} or
     *             {@link #ensureEntityTimer(Entity, Runnable, long, long)}.
     * @return True if the task was cancelled successfully, false otherwise.
     */
    public static boolean cancelTimerTask(Object task) {
        if (task == null) return false;

        if (PaperRunnable.isFolia()) {
            if (task instanceof io.papermc.paper.threadedregions.scheduler.ScheduledTask) {
                ((io.papermc.paper.threadedregions.scheduler.ScheduledTask) task).cancel();
                return true;
            }
        } else {
            if (task instanceof org.bukkit.scheduler.BukkitTask) {
                ((org.bukkit.scheduler.BukkitTask) task).cancel();
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a task is cancelled.
     * @return True if the task is cancelled, false otherwise.
     */
    public static boolean isTaskCancelled(Object task) {
        if (PaperRunnable.isFolia()) {
            if (task instanceof io.papermc.paper.threadedregions.scheduler.ScheduledTask) {
                return ((io.papermc.paper.threadedregions.scheduler.ScheduledTask) task).isCancelled();
            }
        } else {
            if (task instanceof org.bukkit.scheduler.BukkitTask) {
                return ((org.bukkit.scheduler.BukkitTask) task).isCancelled();
            }
        }
        return false;
    }
}