package com.littlepawcraft;

abstract class Task {
    public abstract String id();
    public abstract void run();
    public abstract int duration();
    public abstract int resources();
    public int cost() {
        return this.duration() * this.resources();
    };

    /**
     * Just a convenient method to create a task with one-liner
     */
    static Task of(String id, int duration, int resource, Runnable execution) {
        return new Task() {
            @Override
            public void run() {
                execution.run();
            }

            @Override
            public int duration() {
                return duration;
            }

            @Override
            public int resources() {
                return resource;
            }

            @Override
            public String id() {
                return id;
            }
        };
    }

    @Override
    public String toString() {
        return this.id();
    }
}
