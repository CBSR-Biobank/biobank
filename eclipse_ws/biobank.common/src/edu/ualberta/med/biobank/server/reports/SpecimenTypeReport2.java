package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenTypeReport2 extends AbstractReport {

    private final static String USED_SPECIMEN_TYPES_QUERY = "SELECT alqs.specimenType.nameShort," 
        + "     alqs.study.nameShort" 
        + (" FROM " + AliquotedSpecimen.class.getName() + " alqs ")  
        + " ORDER BY alqs.specimenType.nameShort, alqs.study.nameShort"; 

    private final static String NOT_USED_QUERY = "SELECT st.nameShort " 
        + (" FROM " + SpecimenType.class.getName() + " st ")  
        + " WHERE st not in (SELECT ss.specimenType " 
        + ("    FROM " + AliquotedSpecimen.class.getName() + " ss") + ")"   
        + "ORDER BY st.nameShort"; 

    public SpecimenTypeReport2(BiobankReport report) {
        super("", report); 
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
                    "Unused" }); 
        }
        return expandedResults;
    }
}
