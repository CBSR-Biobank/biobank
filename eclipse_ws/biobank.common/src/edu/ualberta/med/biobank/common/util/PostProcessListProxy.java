package edu.ualberta.med.biobank.common.util;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PostProcessListProxy extends HQLCriteriaListProxy {

    private static final long serialVersionUID = 1L;

    private AbstractRowPostProcess pp;

    public PostProcessListProxy(ApplicationService appService,
        HQLCriteria criteria, AbstractRowPostProcess pp) {
        super(appService, criteria);
        this.pp = pp;
    }

    @Override
    public Object getRowObject(Object object) {
        if (pp == null) {
            return object;
        }
        return pp.rowPostProcess(object);
    }

}
