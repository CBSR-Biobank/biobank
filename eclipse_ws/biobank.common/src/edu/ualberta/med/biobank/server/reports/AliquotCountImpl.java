package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;

public class AliquotCountImpl extends AbstractReport {

    // private static final String QUERY =
    // "Select Alias.sampleType.name, count(*) from "
    // + Aliquot.class.getName()
    // +
    // " as Alias left join Alias.aliquotPosition as p where (p is null or p not in (from "
    // + AliquotPosition.class.getName()
    // +
    // " a where a.container.label like 'SS%')) and Alias.linkDate between ? and ? GROUP BY Alias.sampleType.name";

    public AliquotCountImpl(BiobankReport report) {
        // super(QUERY, report);
        super("", report); //$NON-NLS-1$
    }
}
