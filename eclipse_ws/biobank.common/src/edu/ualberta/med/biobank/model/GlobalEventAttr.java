package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;

public class GlobalEventAttr extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String label;
    private EventAttrType eventAttrType;

    @NotEmpty
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public EventAttrType getEventAttrType() {
        return eventAttrType;
    }

    public void setEventAttrType(EventAttrType eventAttrType) {
        this.eventAttrType = eventAttrType;
    }
}
