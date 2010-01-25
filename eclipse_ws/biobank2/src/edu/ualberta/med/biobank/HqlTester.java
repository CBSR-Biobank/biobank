package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.common.ServiceConnection;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.List;

public class HqlTester {

    private static WritableApplicationService appService;

    public static void main(String[] args) throws Exception {
        new HqlTester();
    }

    public HqlTester() throws Exception {
        appService = ServiceConnection.getAppService("https://"
            + System.getProperty("server", "localhost:8443") + "/biobank2",
            "testuser", "test");
        test();
        // getShipmentsByWeek();
    }

    private void test() throws ApplicationException {
        HQLCriteria c = new HQLCriteria(
            "select pv.patient.id from edu.ualberta.med.biobank.model.PatientVisit pv inner join (select id from edu.ualberta.med.biobank.model.Patient) p on p.id=pv.patient.id");

        List<Object> results = appService.query(c);
        for (Object o : results) {
            // System.out.println(((Object[]) o)[0] + " " + ((Object[]) o)[1]);
            System.out.println(o);
        }
    }

    private void getShipmentsByWeek() throws Exception {
        HQLCriteria c = new HQLCriteria(
            "select week(s.dateReceived) as weeknr, count(*)"
                + " from edu.ualberta.med.biobank.model.Shipment as s"
                + " group by weeknr");

        List<Object> results = appService.query(c);
        for (Object o : results) {
            System.out.println(o);
        }
    }

}
