package edu.ualberta.med.biobank.common.action;

import java.io.Serializable;

public interface Dispatcher {
    public <T extends Serializable> T exec(Action<T> action,
        ActionCallback<T> cb);
}
