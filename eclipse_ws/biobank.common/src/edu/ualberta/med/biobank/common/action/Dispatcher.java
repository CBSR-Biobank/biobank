package edu.ualberta.med.biobank.common.action;

import java.io.Serializable;

public interface Dispatcher {
    public <T extends Serializable> T exec(Action<T> action);

    /**
     * 
     * @param action
     * @param cb
     * @return {@code true} if successful, otherwise {@code false}.
     */
    public <T extends Serializable> boolean exec(Action<T> action,
        ActionCallback<T> cb);
}
