package edu.ualberta.med.biobank.common.reports;

import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.dao.Response;

public class QueryProcess {

    private Session s;
    private BiobankApplicationService appService;
    private QueryCommand qc;

    public QueryProcess(QueryCommand qc, BiobankApplicationService appService) {
        this.qc = qc;
        this.appService = appService;
    }

    public void stop() {
        if (s != null)
            s.cancelQuery();
    }

    public synchronized Response start(Session s) throws ApplicationException {
        if (this.s != null)
            throw new ApplicationException("cannot start a query twice"); //$NON-NLS-1$

        this.s = s;

        List<Object> obs = qc.start(s, appService);
        return new Response(obs);
    }
}
