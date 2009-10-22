package edu.ualberta.med.biobank.common;

public class RowColPos {
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
        return ((this.row == row) && (this.col == col));
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }

}
