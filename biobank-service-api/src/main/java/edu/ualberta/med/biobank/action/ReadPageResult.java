package edu.ualberta.med.biobank.action;

import java.io.Serializable;
import java.util.List;

public abstract class ReadPageResult<R extends Serializable>
    implements ActionResult {
    private static final long serialVersionUID = 1L;

    private List<R> results;
    private Integer firstResult;
    private Integer maxResults;
    private Integer totalResults;

    public List<R> getResults() {
        return results;
    }

    public void setResults(List<R> results) {
        this.results = results;
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }
}
