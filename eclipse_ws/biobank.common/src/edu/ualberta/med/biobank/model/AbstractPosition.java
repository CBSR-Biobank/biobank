package edu.ualberta.med.biobank.model;

import org.hibernate.validator.Min;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

public abstract class AbstractPosition extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String positionString;
    // TODO: move container here and make a unique key on (container, row, col)
    private Integer row;
    private Integer col;

    @NotNull
    @Min(value = 0)
    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    @NotNull
    @Min(value = 0)
    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    @NotEmpty
    public String getPositionString() {
        return positionString;
    }

    public void setPositionString(String positionString) {
        this.positionString = positionString;
    }
}