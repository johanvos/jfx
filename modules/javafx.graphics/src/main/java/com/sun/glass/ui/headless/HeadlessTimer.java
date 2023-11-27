package com.sun.glass.ui.headless;

import com.sun.glass.ui.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class HeadlessTimer extends Timer {

    private static ScheduledThreadPoolExecutor scheduler;
    private ScheduledFuture<?> task;

    HeadlessTimer(final Runnable runnable) {
        super(runnable);
    }

    @Override protected long _start(final Runnable runnable, int period) {
        if (scheduler == null) {
            scheduler = new ScheduledThreadPoolExecutor(1, target -> {
                Thread thread = new Thread(target, "Headless Timer");
                thread.setDaemon(true);
                return thread;
            });
        }

        task = scheduler.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MILLISECONDS);
        return 1; // need something non-zero to denote success.

    }

    @Override protected long _start(Runnable runnable) {
        throw new RuntimeException("vsync timer not supported");
    }   

    @Override protected void _stop(long timer) {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
    }   

    @Override protected void _pause(long timer) {}
    @Override protected void _resume(long timer) {}
}

