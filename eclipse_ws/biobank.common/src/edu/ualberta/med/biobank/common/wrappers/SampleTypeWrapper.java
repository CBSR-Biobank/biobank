package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleTypeWrapper extends ModelWrapper<SampleType> {

    public SampleTypeWrapper(WritableApplicationService appService,
        SampleType wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void firePropertyChanges(SampleType oldWrappedObject,
        SampleType newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<SampleType> getWrappedClass() {
        return SampleType.class;
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<SampleType> getSampleTypeNotInPalletsOrBoxes(
        WritableApplicationService appService, Site site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "select st from "
                + SampleType.class.getName()
                + " as st inner join st.containerTypeCollection as ct where (st.site = ? or st.site = null)"
                + " and ct.name not like '%pallet%' and ct.name not like '%box%'",
            Arrays.asList(new Object[] { site }));
        return appService.query(criteria);
    }

}
