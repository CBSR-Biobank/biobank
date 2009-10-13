package edu.ualberta.med.biobank.common.wrappers;

/**
 * Simple class object representing a position on a 2 dimensional object.
 */
public class Position {

    public Integer row;

    public Integer col;

    public Position(Integer row, Integer col) {
        this.row = row;
        this.col = col;
    }

    public Position() {
    }
}
