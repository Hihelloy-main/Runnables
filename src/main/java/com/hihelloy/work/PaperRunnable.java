package com.hihelloy.work;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PaperRunnable implements Runnable {

    private static final Map<Integer, ScheduledTask> foliaTasks = new ConcurrentHashMap<>();
    private static final AtomicInteger foliaIdGen = new AtomicInteger(1);

    private int foliaTaskId = -1;
    private int bukkitTaskId = -1;

    private ScheduledTask task;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public abstract void run();

    public ScheduledTask getScheduledTask() {
        return task;
    }

    public int getTaskId() {
        if (isFolia()) {
            return foliaTaskId;
        } else {
            return bukkitTaskId;
        }
    }

    public int getBukkitTaskId() {
        return bukkitTaskId;
    }

    public int getFoliaTaskId() {
        return foliaTaskId;
    }

    public boolean isRunning() {
        return running.get();
    }


    public void cancelTask(int taskId) {
        if (taskId <= 0) return;

        ScheduledTask f = foliaTasks.remove(taskId);
        if (f != null) {
            try { f.cancel(); } catch (Throwable ignored) {}
            return;
        }

        try { Bukkit.getScheduler().cancelTask(taskId); } catch (Throwable ignored) {}
    }


    public void cancel() {

        if (task != null) {
            try { task.cancel(); } catch (Throwable ignored) {}
            task = null;
        }

        if (foliaTaskId != -1) {
            foliaTasks.remove(foliaTaskId);
            foliaTaskId = -1;
        }

        if (bukkitTaskId != -1) {
            try { Bukkit.getScheduler().cancelTask(bukkitTaskId); } catch (Throwable ignored) {}
            bukkitTaskId = -1;
        }

        running.set(false);
    }


    public static boolean isFolia() {
        return classExists("io.papermc.paper.threadedregions.RegionizedServer");
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void ensureNotRunning() {
        if (running.get()) throw new IllegalStateException("This PaperRunnable is already running!");
    }

    private void assignFoliaTask(ScheduledTask t) {
        this.task = t;
        this.foliaTaskId = foliaIdGen.getAndIncrement();
        foliaTasks.put(this.foliaTaskId, t);
    }


    public PaperRunnable runTask(Plugin plugin) {
        ensureNotRunning();
        Objects.requireNonNull(plugin, "plugin");

        if (isFolia()) {
            GlobalRegionScheduler global = Bukkit.getGlobalRegionScheduler();
            assignFoliaTask(global.run(plugin, scheduled -> run()));
        } else {
            BukkitScheduler sched = Bukkit.getScheduler();
            bukkitTaskId = sched.scheduleSyncDelayedTask(plugin, this);
        }

        running.set(true);
        return this;
    }

    public PaperRunnable runTaskLater(Plugin plugin, long delayTicks) {
        ensureNotRunning();
        Objects.requireNonNull(plugin, "plugin");

        if (isFolia()) {
            GlobalRegionScheduler global = Bukkit.getGlobalRegionScheduler();
            assignFoliaTask(global.runDelayed(plugin, scheduled -> run(), delayTicks));
        } else {
            bukkitTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, delayTicks);
        }

        running.set(true);
        return this;
    }

    public PaperRunnable runTaskTimer(Plugin plugin, long delayTicks, long periodTicks) {
        ensureNotRunning();
        Objects.requireNonNull(plugin, "plugin");

        if (isFolia()) {
            GlobalRegionScheduler global = Bukkit.getGlobalRegionScheduler();
            assignFoliaTask(global.runAtFixedRate(plugin, scheduled -> run(), delayTicks, periodTicks));
        } else {
            bukkitTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delayTicks, periodTicks);
        }

        running.set(true);
        return this;
    }


    public PaperRunnable runAtLocation(Plugin plugin, Location loc) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(loc);

        if (!isFolia()) throw new IllegalStateException("runAtLocation requires Folia");

        RegionScheduler region = Bukkit.getRegionScheduler();
        assignFoliaTask(region.run(plugin, loc, scheduled -> run()));
        running.set(true);
        return this;
    }

    public PaperRunnable runAtLocationLater(Plugin plugin, Location loc, long delayTicks) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(loc);

        if (!isFolia()) throw new IllegalStateException("runAtLocationLater requires Folia");

        RegionScheduler region = Bukkit.getRegionScheduler();
        assignFoliaTask(region.runDelayed(plugin, loc, scheduled -> run(), delayTicks));
        running.set(true);
        return this;
    }

    public PaperRunnable runAtLocationTimer(Plugin plugin, Location loc, long delayTicks, long periodTicks) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(loc);

        if (!isFolia()) throw new IllegalStateException("runAtLocationTimer requires Folia");

        RegionScheduler region = Bukkit.getRegionScheduler();
        assignFoliaTask(region.runAtFixedRate(plugin, loc, scheduled -> run(), delayTicks, periodTicks));
        running.set(true);
        return this;
    }


    public PaperRunnable runAtEntity(Plugin plugin, Entity entity) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(entity);

        if (!isFolia()) throw new IllegalStateException("runAtEntity requires Folia");

        EntityScheduler es = entity.getScheduler();
        assignFoliaTask(es.run(plugin, scheduled -> run(), this));
        running.set(true);
        return this;
    }

    public PaperRunnable runAtEntity(Plugin plugin, Entity entity, Runnable retired) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(retired);

        if (!isFolia()) throw new IllegalStateException("runAtEntity requires Folia");

        EntityScheduler es = entity.getScheduler();
        assignFoliaTask(es.run(plugin, scheduled -> run(), retired));
        running.set(true);
        return this;
    }

    public PaperRunnable runAtEntityLater(Plugin plugin, Entity entity, long delayTicks) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(entity);

        if (!isFolia()) throw new IllegalStateException("runAtEntityLater requires Folia");

        EntityScheduler es = entity.getScheduler();
        assignFoliaTask(es.runDelayed(plugin, scheduled -> run(), this, delayTicks));
        running.set(true);
        return this;
    }

    public PaperRunnable runAtEntityLater(Plugin plugin, Entity entity, long delayTicks, Runnable retired) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(retired);

        if (!isFolia()) throw new IllegalStateException("runAtEntityLater requires Folia");

        EntityScheduler es = entity.getScheduler();
        assignFoliaTask(es.runDelayed(plugin, scheduled -> run(), retired, delayTicks));
        running.set(true);
        return this;
    }

    public PaperRunnable runAtEntityTimer(Plugin plugin, Entity entity, long delayTicks, long periodTicks) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(entity);

        if (!isFolia()) throw new IllegalStateException("runAtEntityTimer requires Folia");

        EntityScheduler es = entity.getScheduler();
        assignFoliaTask(es.runAtFixedRate(plugin, scheduled -> run(), this, delayTicks, periodTicks));
        running.set(true);
        return this;
    }

    public PaperRunnable runAtEntityTimer(Plugin plugin, Entity entity, long delayTicks, long periodTicks, Runnable retired) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(retired);

        if (!isFolia()) throw new IllegalStateException("runAtEntityTimer requires Folia");

        EntityScheduler es = entity.getScheduler();
        assignFoliaTask(es.runAtFixedRate(plugin, scheduled -> run(), retired, delayTicks, periodTicks));
        running.set(true);
        return this;
    }


    public PaperRunnable runAsync(Plugin plugin) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);

        if (isFolia()) {
            AsyncScheduler async = Bukkit.getAsyncScheduler();
            assignFoliaTask(async.runNow(plugin, scheduled -> run()));
        } else {
            BukkitRunnable br = new BukkitRunnable() {
                @Override
                public void run() { PaperRunnable.this.run(); }
            };
            bukkitTaskId = br.runTaskAsynchronously(plugin).getTaskId();
        }

        running.set(true);
        return this;
    }

    public PaperRunnable runAsyncLater(Plugin plugin, long delayTicks) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);

        if (isFolia()) {
            AsyncScheduler async = Bukkit.getAsyncScheduler();
            long delayMs = delayTicks * 50L;
            assignFoliaTask(async.runDelayed(plugin, scheduled -> run(), delayMs, TimeUnit.MILLISECONDS));
        } else {
            BukkitRunnable br = new BukkitRunnable() {
                @Override
                public void run() { PaperRunnable.this.run(); }
            };
            bukkitTaskId = br.runTaskLaterAsynchronously(plugin, delayTicks).getTaskId();
        }

        running.set(true);
        return this;
    }

    public PaperRunnable runAsyncTimer(Plugin plugin, long delayTicks, long periodTicks) {
        ensureNotRunning();
        Objects.requireNonNull(plugin);

        if (isFolia()) {
            AsyncScheduler async = Bukkit.getAsyncScheduler();
            long delayMs = delayTicks * 50L;
            long periodMs = periodTicks * 50L;
            assignFoliaTask(async.runAtFixedRate(plugin, scheduled -> run(), delayMs, periodMs, TimeUnit.MILLISECONDS));
        } else {
            BukkitRunnable br = new BukkitRunnable() {
                @Override
                public void run() { PaperRunnable.this.run(); }
            };
            bukkitTaskId = br.runTaskTimerAsynchronously(plugin, delayTicks, periodTicks).getTaskId();
        }

        running.set(true);
        return this;
    }
}
