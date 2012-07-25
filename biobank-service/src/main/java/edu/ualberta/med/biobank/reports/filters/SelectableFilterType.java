package edu.ualberta.med.biobank.reports.filters;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.Map;

public interface SelectableFilterType {
    /**
     * Return a <code>Map</code> of options to select from, where the key of the
     * <code>Map</code> is a possible <code>ReportFilterValue</code> value, and
     * the <code>Map</code> value is the <code>String</code> to display to a
     * user.
     * 
     * @param appService
     * @return
     */
    public Map<String, String> getOptions(WritableApplicationService appService);
}
