package edu.ualberta.med.biobank.widgets.report;

import java.util.Collection;

import edu.ualberta.med.biobank.model.ReportFilterValue;

/**
 * Different widgets need to provide a way to get and set their values.
 * 
 * @author jferland
 * 
 */
public interface ValueAccessor {
    public Collection<ReportFilterValue> getValues();

    public void setValues(Collection<ReportFilterValue> values);
}
