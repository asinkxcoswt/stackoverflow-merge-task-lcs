package com.littlepawcraft.improved;

import java.util.Objects;

public record Cell(int value, int x, int y, int refX, int refY, boolean isCommon, Object t1, Object t2) {
    @Override
    public String toString() {
        if (t1 == null || t2 == null) {
            return "Cell("+x+","+y+")";
        }
        String objectRef = t1.equals(t2) ? t1.toString() : t1 + t2.toString();
        return "Cell(" +
                "xyz=" + x +
                "" + y +
                ",ref=" + refX +
                "" + refY +
                ",common=" + isCommon  +
                ",obj=" + objectRef +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
