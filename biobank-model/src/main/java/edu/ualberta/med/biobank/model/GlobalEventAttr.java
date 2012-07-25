package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "GLOBAL_EVENT_ATTR")
@Unique(properties = "label", groups = PrePersist.class)
public class GlobalEventAttr extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Global Event Attribute",
        "Global Event Attributes");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString LABEL = bundle.trc(
            "model",
            "Label").format();
    }

    private String label;
    private EventAttrType eventAttrType;

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.GlobalEventAttr.label.NotEmpty}")
    @Column(name = "LABEL", unique = true, nullable = false, length = 50)
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @NotAudited
    @NotNull(message = "{edu.ualberta.med.biobank.model.GlobalEventAttr.eventAttrType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ATTR_TYPE_ID", nullable = false)
    public EventAttrType getEventAttrType() {
        return this.eventAttrType;
    }

    public void setEventAttrType(EventAttrType eventAttrType) {
        this.eventAttrType = eventAttrType;
    }
}
