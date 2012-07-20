package edu.ualberta.med.biobank.model.event;

import edu.ualberta.med.biobank.model.event.GlobalEvent.GlobalEventType;

public class GlobalEvent extends Event<GlobalEventType> {
    private static final long serialVersionUID = 1L;

    @Override
    public GlobalEventType getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(GlobalEventType eventType) {
        this.eventType = eventType;
    }

    public enum GlobalEventType implements EventType {
        ;

        private final Integer id;

        private GlobalEventType(Integer id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }
}
