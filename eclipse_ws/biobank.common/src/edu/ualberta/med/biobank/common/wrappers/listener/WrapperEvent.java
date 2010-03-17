package edu.ualberta.med.biobank.common.wrappers.listener;

import java.util.EventObject;

public class WrapperEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    public static enum WrapperEventType {
        UPDATE, INSERT, DELETE;
    }

    private WrapperEventType type;

    public WrapperEvent(WrapperEventType type, Object source) {
        super(source);
        this.type = type;
    }

    public WrapperEventType getType() {
        return type;
    }
}
