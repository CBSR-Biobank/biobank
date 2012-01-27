package edu.ualberta.med.biobank.model;

import org.hibernate.validator.Min;
import org.hibernate.validator.NotNull;

public class Capacity extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer rowCapacity;
    private Integer colCapacity;

    @NotNull
    @Min(value = 0)
    public Integer getRowCapacity() {
        return rowCapacity;
    }

    public void setRowCapacity(Integer rowCapacity) {
        this.rowCapacity = rowCapacity;
    }

    @NotNull
    @Min(value = 0)
    public Integer getColCapacity() {
        return colCapacity;
    }

    public void setColCapacity(Integer colCapacity) {
        this.colCapacity = colCapacity;
    }
}
