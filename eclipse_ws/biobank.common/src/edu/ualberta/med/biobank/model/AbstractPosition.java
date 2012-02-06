package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class AbstractPosition extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer row;
    private Integer col;
    private String positionString;

    @Min(value = 0, message = "edu.ualberta.med.biobank.model.AbstractPosition.row.Min")
    @NotNull(message = "edu.ualberta.med.biobank.model.AbstractPosition.row.NotNull")
    @Column(name = "ROW", nullable = false)
    public Integer getRow() {
        return this.row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    @Min(value = 0, message = "edu.ualberta.med.biobank.model.AbstractPosition.col.Min")
    @NotNull(message = "edu.ualberta.med.biobank.model.AbstractPosition.col.NotNull")
    @Column(name = "COL", nullable = false)
    public Integer getCol() {
        return this.col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    // TODO: make position string abstract and use the container's type and labeling scheme?
    @NotNull(message = "edu.ualberta.med.biobank.model.AbstractPosition.positionString.NotNull")
    @Column(name = "POSITION_STRING", length = 255, nullable = false)
    public String getPositionString() {
        return this.positionString;
    }

    public void setPositionString(String positionString) {
        this.positionString = positionString;
    }
}
