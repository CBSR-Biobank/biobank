package edu.ualberta.med.biobank.model.event;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public interface HasEventType<T extends Enum<T> & EventType> {
    @NotNull(message = Event.EVENT_TYPE_NOT_NULL_MESSAGE)
    @Column(name = Event.EVENT_TYPE_COLUMN_NAME)
    T getEventType();

    void setEventType(T eventType);
}
