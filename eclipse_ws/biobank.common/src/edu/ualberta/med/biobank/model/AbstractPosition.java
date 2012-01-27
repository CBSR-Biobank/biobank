package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractPosition extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer row;
    private Integer col;
    private String positionString;

    @Column(name = "ROW", nullable = false)
    public Integer getRow() {
        return this.row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    @Column(name = "COL", nullable = false)
    public Integer getCol() {
        return this.col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    @Column(name = "POSITION_STRING", length = 255, nullable = false)
    public String getPositionString() {
        return this.positionString;
    }

    public void setPositionString(String positionString) {
        this.positionString = positionString;
    }
}
