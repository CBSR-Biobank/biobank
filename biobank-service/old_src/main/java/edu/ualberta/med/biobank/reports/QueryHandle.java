package edu.ualberta.med.biobank.reports;

import java.io.Serializable;

import edu.ualberta.med.biobank.model.util.NotAProxy;

public class QueryHandle implements Serializable, NotAProxy {
    private static final long serialVersionUID = 1L;

    private Integer queryId;

    public QueryHandle(Integer id) {
        this.queryId = id;
    }

    public Integer getQueryId() {
        return queryId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((queryId == null) ? 0 : queryId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryHandle other = (QueryHandle) obj;
        if (queryId == null) {
            if (other.queryId != null)
                return false;
        } else if (!queryId.equals(other.queryId))
            return false;
        return true;
    }

}
