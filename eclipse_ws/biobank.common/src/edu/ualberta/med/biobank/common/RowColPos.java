package edu.ualberta.med.biobank.common;

public class RowColPos implements Comparable<RowColPos> {
    public Integer row;
    public Integer col;

    public RowColPos() {

    }

    public RowColPos(Integer row, Integer col) {
        super();
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
        if (row == pos.row) {
            return col - pos.col;
        }
        return row - pos.row;
    }

}
