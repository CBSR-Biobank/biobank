package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Immutable;

import edu.ualberta.med.biobank.model.util.ProxyUtil;

@Immutable
@Entity
@Table(name = "BATCH_OPERATION_SPECIMEN")
public class BatchOperationSpecimen
    implements Serializable {
    private static final long serialVersionUID = 1L;

    private BatchOperation batch;
    private Specimen specimen;

    @NotNull(message = "{edu.ualberta.med.biobank.model.BatchOperationSpecimen.batch.NotNull}")
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BATCH_OPERATION_ID", nullable = false)
    public BatchOperation getBatch() {
        return batch;
    }

    public void setBatch(BatchOperation batch) {
        this.batch = batch;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.BatchOperationSpecimen.specimen.NotNull}")
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "none")
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 37)
            .append(batch)
            .append(specimen)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!ProxyUtil.isClassEqual(this, obj)) return false;
        BatchOperationSpecimen rhs = (BatchOperationSpecimen) obj;
        return new EqualsBuilder()
            .append(batch, rhs.batch)
            .append(specimen, rhs.specimen)
            .isEquals();
    }
}
