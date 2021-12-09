package com.littlepawcraft.improved;


import java.util.*;
import java.util.stream.Collectors;

public class CommonSubsequenceFinder {
    private <T> Cell[][] buildCube(List<T> l1, List<T> l2, String axisOrder) {
        Cell[][] cube = new Cell[l1.size() + 1][l2.size() + 1];

        for (int i = 0; i <= l1.size() ; i++) {
            for (int j = 0; j <= l2.size() ; j++) {
                if (i == 0 || j == 0) {
                    cube[i][j] = new Cell(0,i , j, -1, -1, false, null, null);
                    continue;
                }

                T t1 = l1.get(i - 1);
                T t2 = l2.get(j - 1);

                if (t1.equals(t2)) {
                    cube[i][j] = new Cell(cube[i - 1][j - 1].value() + 1, i, j, i - 1, j - 1,  true, t1, t2);
                } else {
                    Cell prevCellX = cube[i - 1][j];
                    Cell prevCellY = cube[i][j - 1];

                    Cell ref;
                    if (axisOrder.equals("XY")) {
                        ref = prevCellX;
                    } else if (axisOrder.equals("YX")) {
                        ref = prevCellY;
                    } else {
                        throw new RuntimeException("Invalid axisOrder, should be XY or YX");
                    }

                    if (prevCellX.value() > ref.value()) {
                        ref = prevCellX;
                    }
                    if (prevCellY.value() > ref.value()) {
                        ref = prevCellY;
                    }

                    cube[i][j] = new Cell(ref.value(), i, j, ref.x(), ref.y(), false, t1, t2);
                }
            }
        }

        return cube;
    }

    public Set<List<Cell>> backtract(Cell[][] cube, int sizeX, int sizeY) {
        Set<List<Cell>> allSubsequences = new HashSet<>();
        for (int i = 0; i <= sizeX; i++) {
            for (int j = 0; j <= sizeY; j++) {

                Cell maybeFirstCommonCell = cube[i][j];
                if (maybeFirstCommonCell.isCommon()) {
                    List<Cell> subsequence = new ArrayList<>();
                    subsequence.add(maybeFirstCommonCell);

                    int x = maybeFirstCommonCell.refX();
                    int y = maybeFirstCommonCell.refY();

                    while (x >= 0 && y >= 0) {
                        Cell cell = cube[x][y];
                        if (cell.isCommon()) {
                            subsequence.add(cell);
                        }

                        x = cell.refX();
                        y = cell.refY();
                    }

                    Collections.reverse(subsequence);
                    allSubsequences.add(subsequence);
                }
            }
        }
        return allSubsequences;
    }

    public <T> Set<List<T>> findAllIn2Lists(List<T> l1, List<T> l2) {
        Cell[][] cubeXY = buildCube(l1, l2, "XY");
        Cell[][] cubeYX = buildCube(l1, l2, "YX");
//        System.out.println(Arrays.deepToString(cubeYX));
//        System.exit(0);
        Set<List<Cell>> allSubsequences = new HashSet<>();
        allSubsequences.addAll(backtract(cubeXY, l1.size(), l2.size()));
        allSubsequences.addAll(backtract(cubeYX, l1.size(), l2.size()));
        return allSubsequences
                .stream()
                .map(seq -> seq.stream().map(cell -> (T) cell.t1()).toList())
                .collect(Collectors.toSet());
    }

    /**
     * @param list must be a non-repeating list
     */
    public <T> boolean isASubsequenceOf(List<T> list, List<T> subSequence) {
        return list.stream().filter(subSequence::contains).toList().equals(subSequence);
    }

    public <T> Set<List<T>> findAllInNList(List<T> ... listArray) {
        if (listArray.length <= 1) {
            return new HashSet<>();
        }

        List<List<T>> lists = Arrays.asList(listArray);

        List<T> firstList = lists.get(0);
        List<T> secondList = lists.get(1);

        Set<List<T>> commonSubsequences = findAllIn2Lists(firstList, secondList);

        if (commonSubsequences.isEmpty()) {
            return commonSubsequences;
        }

        if (lists.size() == 2) {
            return commonSubsequences;
        }

        System.out.println(commonSubsequences);

        Set<List<T>> commonSubsequencesEx = new HashSet<>();
        List<List<T>> theRest = lists.subList(2, lists.size());
        for (List<T> subSeq : commonSubsequences) {
            List<List<T>> l = new ArrayList<>();
            l.add(subSeq);
            l.addAll(theRest);
            commonSubsequencesEx.addAll(findAllInNList(l.toArray(new List[l.size()])));
        }

        return commonSubsequencesEx;
    }
}
