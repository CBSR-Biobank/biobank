package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
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

    public Response start(Session s) {
        this.s = s;
        List<Object> obs = new ArrayList<Object>();
        try {
            obs = (qc.start(s, appService));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(obs);
    }
}
