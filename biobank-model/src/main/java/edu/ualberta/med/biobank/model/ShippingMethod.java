package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.center.Shipment;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

// TODO: consider making these per-center or eliminating the class. 
@Audited
@Entity
@Table(name = "SHIPPING_METHOD")
@Unique(properties = "name", groups = PrePersist.class)
@NotUsed(by = Shipment.class, property = "data.shippingMethod", groups = PreDelete.class)
public class ShippingMethod
    extends VersionedLongIdModel
    implements HasName {
    private static final long serialVersionUID = 1L;

    private String name;
    private Boolean waybillRequired;

    @Override
    @NotEmpty(message = "{ShippingMethod.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @NotNull(message = "{ShippingMethod.waybillRequired.NotNull}")
    @Column(name = "IS_WAYBILL_REQUIRED")
    public Boolean isWaybillRequired() {
        return waybillRequired;
    }

    public void setWaybillRequired(Boolean waybillRequired) {
        this.waybillRequired = waybillRequired;
    }
}
