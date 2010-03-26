package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.proxy.ListProxy;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleTypeSUsage extends QueryObject {

    public static String NAME = "Sample Type Usage by Study";

    private static String query1 = "select ss.sampleType.nameShort, "
        + "ss.study.nameShort from "
        + SampleStorage.class.getName()
        + " ss "
        + "where ss.study.site = {0,number,#} OR {0,number,#}=-9999 ORDER BY ss.sampleType.nameShort";
    private static String query2 = "select st.nameShort from "
        + SampleType.class.getName()
        + " st where st not in (select ss.sampleType from "
        + SampleStorage.class.getName()
        + " ss) and st.site = {0,number,#} OR {0,number,#}=-9999";

    public SampleTypeSUsage(@SuppressWarnings("unused") String op,
        Integer siteId) {
        super(
            "Lists sample types, and the associated studies permitting them as valid sample storage.",
            "", new String[] { "Sample Type", "Study" });
        query1 = MessageFormat.format(query1, siteId);
        query2 = MessageFormat.format(query2, siteId);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        List<Object> results = new ArrayList<Object>();
        HQLCriteria c1 = new HQLCriteria(query1);
        c1.setParameters(params);
        results = ((ListProxy) appService.query(c1)).getListChunk();
        HQLCriteria c2 = new HQLCriteria(query2);
        c2.setParameters(params);
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
