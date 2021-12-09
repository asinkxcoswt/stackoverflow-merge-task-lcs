package com.littlepawcraft;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof Task t) {
            return id().equals(t.id());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }
}
