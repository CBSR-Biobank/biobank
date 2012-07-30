package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

// TODO: test name uniqueness? Rethink design regarding localization?
@Audited
@Entity
@Table(name = "SHIPPING_METHOD")
@Unique(properties = "name", groups = PrePersist.class)
@NotUsed(by = ShipmentInfo.class, property = "shippingMethod", groups = PreDelete.class)
public class ShippingMethod extends AbstractModel
    implements HasName {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Shipping Method",
        "Shipping Methods");

    private String name;
    private Boolean waybillRequired = Boolean.TRUE;

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

    @NotNull(message = "{ShippingMethod.waybillRequired.NotEmpty}")
    @Column(name = "WAYBILL_REQUIRED")
    public Boolean isWaybillRequired() {
        return waybillRequired;
    }

    public void setWaybillRequired(Boolean waybillRequired) {
        this.waybillRequired = waybillRequired;
    }

}
