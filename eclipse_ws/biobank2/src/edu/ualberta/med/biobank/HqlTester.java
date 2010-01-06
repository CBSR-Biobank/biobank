package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.common.ServiceConnection;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.List;

public class HqlTester {

    private static WritableApplicationService appService;

    public static void main(String[] args) throws Exception {
        new HqlTester();
    }

    public HqlTester() throws Exception {
        appService = ServiceConnection.getAppService("http://"
            + System.getProperty("server", "localhost:8080") + "/biobank2",
            "testuser", "test");

        getShipmentsByWeek();
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
