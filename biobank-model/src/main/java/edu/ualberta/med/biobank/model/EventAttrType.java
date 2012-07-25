package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;

// TODO: make enum?
@Entity
@Table(name = "EVENT_ATTR_TYPE")
public class EventAttrType extends AbstractBiobankModel
    implements HasName {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Event Attribute Type",
        "Event Attribute Types");

    private String name;

    @Override
    @NotEmpty(message = "{edu.ualberta.med.biobank.model.EventAttrType.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false, length = 50)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
