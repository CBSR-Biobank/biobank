package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CAPACITY")
public class Capacity extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer rowCapacity;
    private Integer colCapacity;

    @Min(value = 0, message = "{edu.ualberta.med.biobank.model.Capacity.rowCapacity.Min}")
    @NotNull(message = "{edu.ualberta.med.biobank.model.Capacity.rowCapacity.NotNull}")
    @Column(name = "ROW_CAPACITY", nullable = false)
    public Integer getRowCapacity() {
        return this.rowCapacity;
    }

    public void setRowCapacity(Integer rowCapacity) {
        this.rowCapacity = rowCapacity;
    }

    @Min(value = 0, message = "{edu.ualberta.med.biobank.model.Capacity.rowCapacity.Min}")
    @NotNull(message = "{edu.ualberta.med.biobank.model.Capacity.rowCapacity.NotNull}")
    @Column(name = "COL_CAPACITY", nullable = false)
    public Integer getColCapacity() {
        return this.colCapacity;
    }

    public void setColCapacity(Integer colCapacity) {
        this.colCapacity = colCapacity;
    }
}
