package edu.ualberta.med.biobank.model.event;

import javax.persistence.Embeddable;

@Embeddable
public class EventObject {
    private String label;
    private Integer objectId;

    public EventObject() {
    }
}
