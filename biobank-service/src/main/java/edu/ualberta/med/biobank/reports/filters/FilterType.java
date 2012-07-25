package edu.ualberta.med.biobank.reports.filters;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;

import edu.ualberta.med.biobank.model.ReportFilterValue;

public interface FilterType {
    /**
     * This <code>FilterType</code> should add the necessary restrictions to the
     * given <code>Criteria</code> object for the given
     * <code>FilterOperator</code> and corresponding
     * <code>ReportFilterValue</code>-s on the given
     * <code>String aliasedProperty</code>.
     * 
     * @param criteria
     * @param aliasedProperty
     * @param op
     * @param value
     */
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values);

    /**
     * Return a <code>Collection</code> of all the valid
     * <code>FilterOperator</code>-s that can be used with this
     * <code>FilterType</code> class.
     * 
     * @return
     */
    public Collection<FilterOperator> getOperators();
}