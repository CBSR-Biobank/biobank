package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.proxy.ListProxy;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleTypeSUsageImpl extends AbstractReport {

    private final static String QUERY1 = "select ss.sampleType.nameShort, "
        + "ss.study.nameShort from " + SampleStorage.class.getName() + " ss "
        + "where ss.study.site = " + SITE_ID + " OR " + SITE_ID
        + "=-9999 ORDER BY ss.sampleType.nameShort";

    private final static String QUERY2 = "select st.nameShort from "
        + SampleType.class.getName()
        + " st where st not in (select ss.sampleType from "
        + SampleStorage.class.getName() + " ss) and st.site = " + SITE_ID
        + " OR " + SITE_ID + "=-9999";

    public SampleTypeSUsageImpl(List<Object> parameters,
        List<ReportOption> options) {
        super("", parameters, options);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> executeQuery(WritableApplicationService appService,
        String siteOperator, Integer siteId) throws ApplicationException {
        List<Object> results = new ArrayList<Object>();
        HQLCriteria c1 = new HQLCriteria(QUERY1.replaceAll(
            SITE_ID_SEARCH_STRING, siteId.toString()), parameters);
        results = ((ListProxy) appService.query(c1)).getListChunk();
        HQLCriteria c2 = new HQLCriteria(QUERY2.replaceAll(
            SITE_ID_SEARCH_STRING, siteId.toString()), parameters);
        results.addAll(specialPostProcess(appService.query(c2)));
        return results;
    }

    protected List<Object> specialPostProcess(List<Object> results) {
        List<Object> expandedResults = new ArrayList<Object>();
        for (Object ob : results) {
            expandedResults.add(new Object[] { ob, "Unused" });
        }
        return expandedResults;
    }
}
