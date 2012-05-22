package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.validator.constraint.model.InBounds;

@Audited
@MappedSuperclass
@InBounds
public abstract class AbstractPosition extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Position",
        "Positions");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString ROW = bundle.trc(
            "model",
            "Row").format();
        public static final LString COLUMN = bundle.trc(
            "model",
            "Column").format();
    }

    private Integer row;
    private Integer col;

    @Min(value = 0, message = "{edu.ualberta.med.biobank.model.AbstractPosition.row.Min}")
    @NotNull(message = "{edu.ualberta.med.biobank.model.AbstractPosition.row.NotNull}")
    @Column(name = "ROW", nullable = false)
    public Integer getRow() {
        return this.row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    @Min(value = 0, message = "{edu.ualberta.med.biobank.model.AbstractPosition.col.Min}")
    @NotNull(message = "{edu.ualberta.med.biobank.model.AbstractPosition.col.NotNull}")
    @Column(name = "COL", nullable = false)
    public Integer getCol() {
        return this.col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    @Transient
    public RowColPos getPosition() {
        return new RowColPos(getRow(), getCol());
    }

    // AbstractPosition _SHOULD_ have a parentContainer property so it has more
    // meaning, but this method is a band-aid until ParentContainer is pulled
    // into this class.
    @Transient
    public abstract Container getHoldingContainer();
}
