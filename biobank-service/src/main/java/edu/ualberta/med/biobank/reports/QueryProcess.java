package edu.ualberta.med.biobank.reports;

import java.util.List;

import javax.xml.ws.Response;

import org.hibernate.Session;
import org.omg.CORBA.portable.ApplicationException;

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
