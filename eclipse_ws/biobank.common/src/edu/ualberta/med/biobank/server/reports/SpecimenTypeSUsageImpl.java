package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenTypeSUsageImpl extends AbstractReport {

    private final static String USED_SPECIMEN_TYPES_QUERY = "SELECT alqs.specimenType.nameShort," //$NON-NLS-1$
        + "     alqs.study.nameShort" //$NON-NLS-1$
        + (" FROM " + AliquotedSpecimen.class.getName() + " alqs ") //$NON-NLS-1$ //$NON-NLS-2$
        + " ORDER BY alqs.specimenType.nameShort, alqs.study.nameShort"; //$NON-NLS-1$

    private final static String NOT_USED_QUERY = "SELECT st.nameShort " //$NON-NLS-1$
        + (" FROM " + SpecimenType.class.getName() + " st ") //$NON-NLS-1$ //$NON-NLS-2$
        + " WHERE st not in (SELECT ss.specimenType " //$NON-NLS-1$
        + ("    FROM " + AliquotedSpecimen.class.getName() + " ss") + ")" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "ORDER BY st.nameShort"; //$NON-NLS-1$

    public SpecimenTypeSUsageImpl(BiobankReport report) {
        super("", report); //$NON-NLS-1$
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService)
        throws ApplicationException {
        List<Object> results = new ArrayList<Object>();
        List<Object> parameters = report.getParams();

        HQLCriteria c1 = new HQLCriteria(USED_SPECIMEN_TYPES_QUERY, parameters);
        results.addAll(appService.query(c1));

        HQLCriteria c2 = new HQLCriteria(NOT_USED_QUERY, parameters);
        results.addAll(specialPostProcess(appService.query(c2)));

        return results;
    }

    protected List<Object> specialPostProcess(List<Object> results) {
        List<Object> expandedResults = new ArrayList<Object>();
        for (Object ob : results) {
            expandedResults
                .add(new Object[] {
                    ob,
                    Messages
                        .getString(
                            "SpecimenTypeSUsageImpl.unused.label", report.getLocale()) }); //$NON-NLS-1$
        }
        return expandedResults;
    }
}
