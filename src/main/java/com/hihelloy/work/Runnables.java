package com.hihelloy.work;

import org.bukkit.plugin.java.JavaPlugin;

public class Runnables extends JavaPlugin {
    public static RunnableLike runnable;
    public static PaperRunnable paperRunnable;
    public static int taskid;

    @Override
    public void onEnable() {
        /*
            paperRunnable = new PaperRunnable() {
                @Override
                public void run() {
                    getLogger().info("Heya :)");
                }
            };
            runnable = RunnableLike.of(paperRunnable);
            runnable.runTaskTimer(this, 1L, 1L);
         */
        getLogger().info("Runnables plugin enabled");
    }

    @Override
    public void onDisable() {
        /*
        runnable.cancelTask();
         */
        getLogger().info("Runnables plugin disabled");
    }
}
