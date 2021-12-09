package com.littlepawcraft;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

class ParallelTask extends Task {
    private final String id;
    private final Task[][] taskSequences;
    ParallelTask(String id, Task[] ... taskSequences) {
        this.id = id;
        this.taskSequences = taskSequences;
    }

    @Override
    public void run() {
        Arrays.stream(this.taskSequences).parallel().forEach(taskSeq -> {
            Arrays.stream(taskSeq).forEach(Task::run);
        });
    }

    @Override
    public int duration() {
        IntStream eachSequenceTotalDuration = Arrays.stream(this.taskSequences).mapToInt(
                taskSeq -> Arrays.stream(taskSeq).mapToInt(Task::duration).sum()
        );

        return eachSequenceTotalDuration.max().orElse(0);
    }

    @Override
    public int resources() {
        IntStream eachSequenceTotalResources = Arrays.stream(this.taskSequences).mapToInt(
                taskSeq -> Arrays.stream(taskSeq).mapToInt(Task::resources).sum()
        );

        return eachSequenceTotalResources.sum();
    }

    @Override
    public String id() {
        return this.id;
    }

    public void report(boolean showSummary) {
        if (showSummary) {
            System.out.println("------------------------");
            System.out.println("TASK " + this.id() + " (COST " + this.cost() + ", DURATION " + this.duration() + ", RESOURCES " + this.resources() + ")");
            System.out.println("------------------------");
        }

        int l = Arrays.stream(this.taskSequences).mapToInt(taskSeq -> taskSeq.length).max().orElse(0);
        for (int j = 0; j < l; j ++) {
            for (int i = 0; i < this.taskSequences.length; i++) {
                if (this.taskSequences[i].length - 1 < j) {
                    System.out.print("  ");
                    continue;
                }
                Task task = this.taskSequences[i][j];
                if (task instanceof ParallelTask p) {
                    p.report(false);
                } else if (task instanceof MergedTask m) {
                    System.out.println(task.id());
                } else {
                    System.out.print(task.id() + " ");
                }
            }
            System.out.println("");
        }
    }
}
