package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

// TODO: test name uniqueness? Rethink design regarding localization?
@Entity
@Table(name = "SHIPPING_METHOD")
@Unique(properties = "name", groups = PrePersist.class)
@NotUsed(by = ShipmentInfo.class, property = "shippingMethod", groups = PreDelete.class)
public class ShippingMethod extends AbstractBiobankModel
    implements HasName {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Shipping Method",
        "Shipping Methods");

    public static class PropertyName {
        public static final LString NAME =
            HasName.PropertyName.NAME;
    }

    private String name;

    @Override
    @NotEmpty(message = "{edu.ualberta.med.biobank.model.ShippingMethod.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
