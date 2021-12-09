package com.littlepawcraft;

import com.littlepawcraft.improved.CommonSubsequenceFinder;

import java.util.*;
import java.util.function.Function;

public class Main {

    static record Cell(int value, int x, int y, int z, int refX, int refY, int refZ, boolean isCommon, Task taskX, Task taskY, Task taskZ) {
        @Override
        public String toString() {
            if (taskX == null || taskY == null || taskZ == null) {
                return "Cell("+x+","+y+","+z+")";
            }
            String task = taskX.id().equals(taskY.id()) && taskX.id().equals(taskZ.id()) ? taskX.id() : taskX.id() + taskY.id() + taskZ.id();
            return "Cell(" +
                    "xyz" + x +
                    "" + y +
                    "" + z +
                    ",ref" + refX +
                    "" + refY +
                    "" + refZ +
                    ",common=" + isCommon  +
                    ",tasks=" + task +
                    ')';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return x == cell.x && y == cell.y && z == cell.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }

    static List<Cell> backtrack(Cell[][][] cube, int scanAxisLength, Function<Integer, Cell> scanAxis) {

        List<Cell> commonCellList = new ArrayList<>();

        int scanIndex = scanAxisLength;

        Cell firstCommonCell = null;

        do {
            Cell cell = scanAxis.apply(scanIndex);
            if (cell.isCommon) {
                firstCommonCell = cell;
            }
            scanIndex--;
        } while (scanIndex >= 0 && firstCommonCell == null);

        if (firstCommonCell == null) {
            return commonCellList; // empty list
        }

        commonCellList.add(firstCommonCell);

        int i = firstCommonCell.refX;
        int j = firstCommonCell.refY;
        int k = firstCommonCell.refZ;
        while (i >= 0 && j >= 0 && k >= 0) {

            Cell cell = cube[i][j][k];
            if (cell.isCommon) {
                commonCellList.add(cell);
            }

            i = cell.refX;
            j = cell.refY;
            k = cell.refZ;
        }

        Collections.reverse(commonCellList);
        return commonCellList;

    }

    static Set<List<Cell>> findAllCommonSubsequences(List<Task> case1, List<Task> case2, List<Task> case3, String axisOrder) {
        Cell[][][] cube = new Cell[case1.size() + 1][case2.size() + 1][case3.size() + 1];

        for (int i = 0; i <= case1.size() ; i++) {
            for (int j = 0; j <= case2.size() ; j++) {
                for (int k = 0; k <= case3.size() ; k++) {
                    if (i == 0 || j == 0 || k == 0) {
                        cube[i][j][k] = new Cell(0,i , j, k, -1, -1, -1, false, null, null, null);
                        continue;
                    }

                    Task t1 = case1.get(i - 1);
                    Task t2 = case2.get(j - 1);
                    Task t3 = case3.get(k - 1);

                    if (t1.id().equals(t2.id()) && t1.id().equals(t3.id())) {
                        cube[i][j][k] = new Cell(cube[i - 1][j - 1][k - 1].value + 1, i, j, k, i - 1, j - 1, k - 1, true, t1, t2, t3);
                    } else {
                        Cell prevCellX = cube[i - 1][j][k];
                        Cell prevCellY = cube[i][j - 1][k];
                        Cell prevCellZ = cube[i][j][k - 1];

                        Map<String, Cell> refCandidateMap = new HashMap<>();

                        int maxValue = prevCellZ.value;
                        refCandidateMap.put("Z", prevCellZ);

                        if (prevCellY.value == maxValue) {
                            refCandidateMap.put("Y", prevCellY);

                        } else if (prevCellY.value > maxValue) {
                            maxValue = prevCellY.value;
                            refCandidateMap = new HashMap<>();
                            refCandidateMap.put("Y", prevCellY);
                        }

                        if (prevCellX.value == maxValue) {
                            refCandidateMap.put("X", prevCellX);

                        } else if (prevCellX.value > maxValue) {
                            refCandidateMap = new HashMap<>();
                            refCandidateMap.put("X", prevCellX);
                        }

                        Cell ref = null;
                        for (String preferAxios : axisOrder.split("")) {
                            ref = refCandidateMap.get(preferAxios);
                            if (ref != null) {
                                break;
                            }
                        }

                        if (ref == null) {
                            throw new RuntimeException("Invalid axisOrder config: " + axisOrder + " , the valid value should be such as XYZ");
                        }

                        cube[i][j][k] = new Cell(ref.value, i, j, k, ref.x, ref.y, ref.z, false, t1, t2, t3);
                    }
                }
            }
        }

//        System.out.println(Arrays.deepToString(cube));

        // start backtrack
        Set<List<Cell>> allCommonSubsequenceList = new HashSet<>();

        // Scan from x,y side
        for (int i = 0; i <= case1.size() ; i++) {
            for (int j = 0; j <= case2.size() ; j++) {
                final int x = i;
                final int y = j;
                List<Cell> commonCellList = backtrack(cube, case3.size(), (z) -> cube[x][y][z]);
                if (!commonCellList.isEmpty()) {
                    allCommonSubsequenceList.add(commonCellList);
                }
            }
        }

        // Scan from x,z side
        for (int i = 0; i <= case1.size() ; i++) {
            for (int k = 0; k <= case3.size() ; k++) {
                final int x = i;
                final int z = k;
                List<Cell> commonCellList = backtrack(cube, case2.size(), (y) -> cube[x][y][z]);
                if (!commonCellList.isEmpty()) {
                    allCommonSubsequenceList.add(commonCellList);
                }
            }
        }

        // Scan from y,z side
        for (int j = 0; j <= case2.size() ; j++) {
            for (int k = 0; k <= case3.size() ; k++) {
                final int y = j;
                final int z = k;
                List<Cell> commonCellList = backtrack(cube, case1.size(), (x) -> cube[x][y][z]);
                if (!commonCellList.isEmpty()) {
                    allCommonSubsequenceList.add(commonCellList);
                }
            }
        }

        return allCommonSubsequenceList;
    }

    static ParallelTask merge(List<Cell> commonSubsequence, List<Task> case1, List<Task> case2, List<Task> case3) {

        // pad cell index to prevent confusion, because the original cells will have indices = taskList's index - 1 due to LCS algorithm
        List<Cell> commonSubsequencePadded = commonSubsequence.stream()
                .map(cell -> new Cell(cell.value, cell.x - 1, cell.y - 1, cell.z -1, 0, 0, 0, true, cell.taskX, cell.taskY, cell.taskZ))
                .toList();
//        System.out.println(commonSubsequencePadded);

        Cell prevCell = null;
        int taskNameIndex = 1;
        StringBuilder finalResultTaskName = new StringBuilder("MERGED_TASK(");
        List<Task> merged = new ArrayList<>();
        for (Cell cell : commonSubsequencePadded) {
            int startL1 = prevCell == null ? 0 : prevCell.x + 1;
            int startL2 = prevCell == null ? 0 : prevCell.y + 1;
            int startL3 = prevCell == null ? 0 : prevCell.z + 1;
            Task[] l1 = cell.x == startL1 ? new Task[0] : case1.subList(startL1, cell.x).toArray(new Task[0]);
            Task[] l2 = cell.y == startL2 ? new Task[0] : case2.subList(startL2, cell.y).toArray(new Task[0]);
            Task[] l3 = cell.z == startL3 ? new Task[0] : case3.subList(startL3, cell.z).toArray(new Task[0]);
            Task p = new ParallelTask("p" + taskNameIndex, l1, l2, l3);
            Task m = new MergedTask(cell.taskX);
            merged.add(p);
            merged.add(m);

            prevCell = cell;
            taskNameIndex++;
            finalResultTaskName.append(cell.taskX.id());
        }

        // if there are some remaining tasks after the last merge point
        if (prevCell != null && (prevCell.x + 1 < case1.size() || prevCell.y + 1 < case2.size() || prevCell.z + 1 < case3.size())) {
            Task[] l1 = prevCell.x + 1 < case1.size() ? case1.subList(prevCell.x + 1, case1.size()).toArray(new Task[0]) : new Task[0];
            Task[] l2 = prevCell.y + 1 < case2.size() ? case2.subList(prevCell.y + 1, case2.size()).toArray(new Task[0]) : new Task[0];
            Task[] l3 = prevCell.z + 1 < case3.size() ? case3.subList(prevCell.z + 1, case3.size()).toArray(new Task[0]) : new Task[0];
            Task p = new ParallelTask("p" + taskNameIndex, l1, l2, l3);
            merged.add(p);
        }

        return new ParallelTask(finalResultTaskName.append(")").toString(), merged.toArray(new Task[0]));
    }

    public static void main(String[] args) {
        Task A = Task.of("A",1, 1, () -> System.out.println("I'm task A"));
        Task B = Task.of("B",1, 1, () -> System.out.println("I'm task B"));
        Task C = Task.of("C",1, 1, () -> System.out.println("I'm task C"));
        Task D = Task.of("D",1, 1, () -> System.out.println("I'm task D"));
        Task E = Task.of("E",1, 1, () -> System.out.println("I'm task E"));
        Task U = Task.of("U",1, 1, () -> System.out.println("I'm task U"));
        Task W = Task.of("W",1, 1, () -> System.out.println("I'm task W"));
        Task X = Task.of("X",1, 1, () -> System.out.println("I'm task X"));
        Task Y = Task.of("Y",1, 1, () -> System.out.println("I'm task Y"));
        Task Z = Task.of("Z",1, 1, () -> System.out.println("I'm task Z"));

        Task[] case1 = new Task[] {A,B,C,D,E};
        Task[] case2 = new Task[] {W,B,A,U,C,E};
        Task[] case3 = new Task[] {B,X,Y,Z,E,A,C};

//        new ParallelTask("INITIAL_SOLUTION", case1, case2, case3).report(true);
//
//        Task p1 = new ParallelTask(
//                "p1",
//                new Task[] {A},
//                new Task[] {W}
//        );
//        Task m1 = new MergedTask(B);
//        Task p2 = new ParallelTask(
//                "p2",
//                new Task[] {C, D},
//                new Task[] {A, U, C},
//                new Task[] {X, Y, Z}
//        );
//        Task m2 = new MergedTask(E);
//        Task p3 = new ParallelTask(
//                "p3",
//                new Task[] {A, C}
//        );
//
//        new ParallelTask("MANUAL_SOLUTION", new Task[] {p1, m1, p2, m2, p3}).report(true);

//        merge(List.of(
//                new Cell(0, 1, 3, 6, 0, 0, 0, true, A, A, A),
//                new Cell(0, 3, 5, 7, 0, 0, 0, true, C, C, C)), List.of(case1), List.of(case2), List.of(case3))
//                .report(true);
//
//        Set<List<Cell>> allCommonSubsequenceList = new HashSet<>();
//        allCommonSubsequenceList.addAll(findAllCommonSubsequences(List.of(case1), List.of(case2), List.of(case3), "XYZ"));
//        allCommonSubsequenceList.addAll(findAllCommonSubsequences(List.of(case1), List.of(case2), List.of(case3), "XZY"));
//        allCommonSubsequenceList.addAll(findAllCommonSubsequences(List.of(case1), List.of(case2), List.of(case3), "YXZ"));
//        allCommonSubsequenceList.addAll(findAllCommonSubsequences(List.of(case1), List.of(case2), List.of(case3), "YZX"));
//        allCommonSubsequenceList.addAll(findAllCommonSubsequences(List.of(case1), List.of(case2), List.of(case3), "ZXY"));
//        allCommonSubsequenceList.addAll(findAllCommonSubsequences(List.of(case1), List.of(case2), List.of(case3), "ZYX"));
//        System.out.println(allCommonSubsequenceList);
//
//        List<ParallelTask> allPossibleMergeList = allCommonSubsequenceList.stream().map(commonSubsequence -> merge(commonSubsequence, List.of(case1), List.of(case2), List.of(case3))).toList();
//        allPossibleMergeList.forEach(t -> t.report(true));

        // improved version to find all common subsequences in N list

        Set<List<Task>> allCommonSubsequences = new CommonSubsequenceFinder().findAllInNList(List.of(case1), List.of(case2), List.of(case3));
        System.out.println(allCommonSubsequences);
    }
}
