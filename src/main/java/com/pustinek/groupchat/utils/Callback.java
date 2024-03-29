package com.pustinek.groupchat.utils;

import com.pustinek.groupchat.Main;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Callback<T> {
    private Main plugin;

    public Callback(Main plugin) {
        this.plugin = plugin;
    }

    public void onResult(T result) {
    }

    public void onError(Throwable throwable) {
    }

    public final void callSyncResult(final T result) {
        new BukkitRunnable() {
            @Override
            public void run() {
                onResult(result);
            }
        }.runTask(plugin);
    }

    public final void callSyncError(final Throwable throwable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                onError(throwable);
            }
        }.runTask(plugin);
    }

}
