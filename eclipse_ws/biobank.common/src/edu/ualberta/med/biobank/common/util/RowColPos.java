package edu.ualberta.med.biobank.common.util;

import java.io.Serializable;

public class RowColPos implements Comparable<RowColPos>, Serializable {

    private static final long serialVersionUID = 1L;

    public static Integer PALLET_96_ROW_MAX = 8;

    public static Integer PALLET_96_COL_MAX = 12;

    public Integer row;
    public Integer col;

    public RowColPos() {

    }

    public RowColPos(Integer row, Integer col) {
        super();
        if (row == null)
            throw new RuntimeException("row cannot be null");
        if (col == null)
            throw new RuntimeException("col cannot be null");
        this.row = row;
        this.col = col;
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
        String hash = ""; //$NON-NLS-1$
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
        return "(" + row + "," + col + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public int compareTo(RowColPos pos) {
        if (row.equals(pos.row)) {
            return col - pos.col;
        }
        return row - pos.row;
    }

}
