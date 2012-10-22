package edu.ualberta.med.biobank.action;

import java.io.Serializable;

public abstract class ReadPageAction<R extends ReadPageResult<E>, E extends Serializable>
    implements Action<R> {
    private static final long serialVersionUID = 1L;

    private final Integer firstResult;
    private final Integer maxResults;

    protected ReadPageAction(Integer firstResult, Integer maxResults) {
        this.firstResult = firstResult;
        this.maxResults = maxResults;
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public Integer getMaxResults() {
        return maxResults;
    }
}
