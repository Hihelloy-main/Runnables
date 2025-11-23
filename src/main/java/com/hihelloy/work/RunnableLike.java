package com.hihelloy.work;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.hihelloy.work.PaperRunnable;

/**
 * RunnableLike: A unified interface for anything "Runnable-like":
 * - Supports PaperRunnable (Folia + async + region + entity)
 * - Supports BukkitRunnable (sync + async)
 * - Supports task cancellation
 */
@FunctionalInterface
public interface RunnableLike {


    void runTask(Plugin plugin);


    default void runTaskLater(Plugin plugin, long delayTicks) {
        throw new UnsupportedOperationException("runTaskLater not implemented");
    }

    default void runTaskTimer(Plugin plugin, long delayTicks, long periodTicks) {
        throw new UnsupportedOperationException("runTaskTimer not implemented");
    }


    default void runTaskAsync(Plugin plugin) {
        throw new UnsupportedOperationException("runTaskAsync not implemented");
    }

    default void runTaskAsyncLater(Plugin plugin, long delayTicks) {
        throw new UnsupportedOperationException("runTaskAsyncLater not implemented");
    }

    default void runTaskAsyncTimer(Plugin plugin, long delayTicks, long periodTicks) {
        throw new UnsupportedOperationException("runTaskAsyncTimer not implemented");
    }

    default void runAtLocation(Plugin plugin, Location loc) {
        throw new UnsupportedOperationException("runAtLocation not implemented");
    }

    default void runAtLocationLater(Plugin plugin, Location loc, long delayTicks) {
        throw new UnsupportedOperationException("runAtLocationLater not implemented");
    }

    default void runAtLocationTimer(Plugin plugin, Location loc, long delayTicks, long periodTicks) {
        throw new UnsupportedOperationException("runAtLocationTimer not implemented");
    }


    default void runAtEntity(Plugin plugin, Entity entity) {
        throw new UnsupportedOperationException("runAtEntity not implemented");
    }

    default void runAtEntity(Plugin plugin, Entity entity, Runnable retired) {
        throw new UnsupportedOperationException("runAtEntity with retired not implemented");
    }

    default void runAtEntityLater(Plugin plugin, Entity entity, long delayTicks) {
        throw new UnsupportedOperationException("runAtEntityLater not implemented");
    }

    default void runAtEntityLater(Plugin plugin, Entity entity, long delayTicks, Runnable retired) {
        throw new UnsupportedOperationException("runAtEntityLater with retired not implemented");
    }

    default void runAtEntityTimer(Plugin plugin, Entity entity, long delayTicks, long periodTicks) {
        throw new UnsupportedOperationException("runAtEntityTimer not implemented");
    }

    default void runAtEntityTimer(Plugin plugin, Entity entity, long delayTicks, long periodTicks, Runnable retired) {
        throw new UnsupportedOperationException("runAtEntityTimer with retired not implemented");
    }


    default void cancelTask() {
        throw new UnsupportedOperationException("cancelTask not implemented");
    }

    default int getTaskId() {
        throw new UnsupportedOperationException("getTaskId not implemented");
    }


    static RunnableLike of(PaperRunnable pr) {
        return new RunnableLike() {
            @Override
            public void runTask(Plugin plugin) { pr.runTask(plugin); }

            @Override
            public void runTaskLater(Plugin plugin, long delayTicks) { pr.runTaskLater(plugin, delayTicks); }

            @Override
            public void runTaskTimer(Plugin plugin, long delayTicks, long periodTicks) { pr.runTaskTimer(plugin, delayTicks, periodTicks); }

            @Override
            public void runTaskAsync(Plugin plugin) { pr.runAsync(plugin); }

            @Override
            public void runTaskAsyncLater(Plugin plugin, long delayTicks) { pr.runAsyncLater(plugin, delayTicks); }

            @Override
            public void runTaskAsyncTimer(Plugin plugin, long delayTicks, long periodTicks) { pr.runAsyncTimer(plugin, delayTicks, periodTicks); }

            @Override
            public void runAtLocation(Plugin plugin, Location loc) { pr.runAtLocation(plugin, loc); }

            @Override
            public void runAtLocationLater(Plugin plugin, Location loc, long delayTicks) { pr.runAtLocationLater(plugin, loc, delayTicks); }

            @Override
            public void runAtLocationTimer(Plugin plugin, Location loc, long delayTicks, long periodTicks) { pr.runAtLocationTimer(plugin, loc, delayTicks, periodTicks); }

            @Override
            public void runAtEntity(Plugin plugin, Entity entity) { pr.runAtEntity(plugin, entity); }

            @Override
            public void runAtEntity(Plugin plugin, Entity entity, Runnable retired) { pr.runAtEntity(plugin, entity, retired); }

            @Override
            public void runAtEntityLater(Plugin plugin, Entity entity, long delayTicks) { pr.runAtEntityLater(plugin, entity, delayTicks); }

            @Override
            public void runAtEntityLater(Plugin plugin, Entity entity, long delayTicks, Runnable retired) { pr.runAtEntityLater(plugin, entity, delayTicks, retired); }

            @Override
            public void runAtEntityTimer(Plugin plugin, Entity entity, long delayTicks, long periodTicks) { pr.runAtEntityTimer(plugin, entity, delayTicks, periodTicks); }

            @Override
            public void runAtEntityTimer(Plugin plugin, Entity entity, long delayTicks, long periodTicks, Runnable retired) { pr.runAtEntityTimer(plugin, entity, delayTicks, periodTicks, retired); }

            @Override
            public void cancelTask() { pr.cancel(); }

            @Override
            public int getTaskId() {
                return pr.getTaskId();
            }
        };
    }

    static RunnableLike of(BukkitRunnable br) {
        return new RunnableLike() {
            @Override
            public void runTask(Plugin plugin) { br.runTask(plugin); }

            @Override
            public void runTaskLater(Plugin plugin, long delayTicks) { br.runTaskLater(plugin, delayTicks); }

            @Override
            public void runTaskTimer(Plugin plugin, long delayTicks, long periodTicks) { br.runTaskTimer(plugin, delayTicks, periodTicks); }

            @Override
            public void runTaskAsync(Plugin plugin) { br.runTaskAsynchronously(plugin); }

            @Override
            public void runTaskAsyncLater(Plugin plugin, long delayTicks) { br.runTaskLaterAsynchronously(plugin, delayTicks); }

            @Override
            public void runTaskAsyncTimer(Plugin plugin, long delayTicks, long periodTicks) { br.runTaskTimerAsynchronously(plugin, delayTicks, periodTicks); }

            @Override
            public void cancelTask() { br.cancel(); }

            @Override
            public int getTaskId() {
                return br.getTaskId();
            }
        };
    }
}
