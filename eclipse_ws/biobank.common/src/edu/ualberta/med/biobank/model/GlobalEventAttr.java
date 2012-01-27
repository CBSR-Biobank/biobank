package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "GLOBAL_EVENT_ATTR")
public class GlobalEventAttr extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String label;
    private EventAttrType eventAttrType;

    // TODO: unique
    @NotEmpty
    @Column(name = "LABEL", unique = true, nullable = false, length = 50)
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ATTR_TYPE_ID", nullable = false)
    public EventAttrType getEventAttrType() {
        return this.eventAttrType;
    }

    public void setEventAttrType(EventAttrType eventAttrType) {
        this.eventAttrType = eventAttrType;
    }
}
