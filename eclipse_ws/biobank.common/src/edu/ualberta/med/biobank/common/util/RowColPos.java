package edu.ualberta.med.biobank.common.util;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;

public class RowColPos implements Comparable<RowColPos>, Serializable {

    private static final long serialVersionUID = 1L;

    public static Integer PALLET_96_ROW_MAX = 8;

    public static Integer PALLET_96_COL_MAX = 12;

    private final Integer row;
    private final Integer col;

    public RowColPos(Integer row, Integer col) {
        this.row = row;
        this.col = col;

        if (row == null || col == null) {
            throw new IllegalArgumentException(
                "Neither the row nor column of a position can be null");
        }
    }

    // TODO: this should be a convenience method outside of this class.
    // RowColPos shouldn't know about AbstractPositionWrapper -JMF
    public RowColPos(AbstractPositionWrapper<?> pos) {
        this(pos.getRow(), pos.getCol());
    }

    public Integer getRow() {
        return row;
    }

    public Integer getCol() {
        return col;
    }

    public boolean equals(Integer row, Integer col) {
        return (this.row.equals(row) && this.col.equals(col));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RowColPos) {
            RowColPos pos = (RowColPos) o;
            if (row != null && col != null) {
                return row.equals(pos.row) && col.equals(pos.col);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        String hash = "";
        if (row != null) {
            hash += row.toString();
        }
        if (col != null) {
            hash += col.toString();
        }
        return hash.hashCode();
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }

    @Override
    public int compareTo(RowColPos pos) {
        if (row.equals(pos.row)) {
            return col - pos.col;
        }
        return row - pos.row;
    }
}
