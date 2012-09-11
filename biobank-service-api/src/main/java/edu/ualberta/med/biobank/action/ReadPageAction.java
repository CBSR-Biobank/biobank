package edu.ualberta.med.biobank.action;

import java.io.Serializable;

public abstract class ReadPageAction<R extends ReadPageResult<E>, E extends Serializable>
    implements Action<R> {
    private static final long serialVersionUID = 1L;
}
