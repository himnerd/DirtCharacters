package com.dirt.characters.scheduler;

public interface ScheduledTask {
    void cancel();

    boolean isCancelled();
}