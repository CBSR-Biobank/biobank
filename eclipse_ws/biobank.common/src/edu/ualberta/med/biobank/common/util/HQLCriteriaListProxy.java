package edu.ualberta.med.biobank.common.util;

import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.Serializable;
import java.util.List;

public class HQLCriteriaListProxy<E> extends AbstractBiobankListProxy<E>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    protected HQLCriteria criteria;

    public HQLCriteriaListProxy(ApplicationService appService,
        HQLCriteria criteria) {
        super(appService);
        this.criteria = criteria;
    }

    @Override
    public List<Object> getChunk(Integer firstRow) throws ApplicationException {
        return appService.query(criteria, firstRow, Site.class.getName());
    }
}