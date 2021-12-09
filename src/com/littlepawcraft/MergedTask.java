package com.littlepawcraft;

class MergedTask extends Task {

    private final Task originalTask;
    MergedTask(Task originalTask) {
        this.originalTask = originalTask;
    }

    @Override
    public void run() {
        originalTask.run();
    }

    @Override
    public int duration() {
        return originalTask.duration();
    }

    @Override
    public int resources() {
        return originalTask.resources()/3;
    }

    @Override
    public String id() {
        return "MERGED(" + originalTask.id() + ")";
    }
}
