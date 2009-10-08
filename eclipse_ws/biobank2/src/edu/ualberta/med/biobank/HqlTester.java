package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.Arrays;
import java.util.List;

public class HqlTester {

    private static WritableApplicationService appService;

    public static void main(String[] args) throws Exception {
        new HqlTester();
    }

    public HqlTester() throws Exception {
        appService = (WritableApplicationService) ApplicationServiceProvider
            .getApplicationServiceFromUrl("http://localhost:8080/biobank2",
                "testuser", "test");

        getBrokenQuery();

        getTopContainers();
    }

    @SuppressWarnings("unused")
    private void getPatientIds() throws Exception {
        HQLCriteria c = new HQLCriteria("select patients.id from "
            + Patient.class.getName()
            + " inner join patients.study as study where study.nameShort=?",
            Arrays.asList(new Object[] { "BBP" }));

        List<Integer> results = appService.query(c);
        for (Integer id : results) {
            System.out.println("getPatientIds: id: " + id);
        }
    }

    @SuppressWarnings("unused")
    private void geTopContainerTypes() throws Exception {
        HQLCriteria c = new HQLCriteria("from " + ContainerType.class.getName()
            + " as cttop where cttop.id not in (select child.id"
            + " from edu.ualberta.med.biobank.model.ContainerType as ct"
            + " left join ct.childContainerTypeCollection as child "
            + " where child.id!=null)");

        List<ContainerType> results = appService.query(c);
        for (ContainerType ct : results) {
            System.out.println("geTopContainerTypes: " + ct.getName());
        }
    }

    private void getBrokenQuery() throws Exception {
        HQLCriteria c = new HQLCriteria(
            "select containerType from edu.ualberta.med.biobank.model.ContainerType as containerType left join containerType.sampleTypeCollection as sampleTypeAlias  where sampleTypeAlias.name like '%blood%'");
        List<Object> results = appService.query(c);
        for (Object ct : results) {
            System.out.println(ct.toString());
        }
    }

    @SuppressWarnings("unused")
    private void geContainerLike() throws Exception {
        HQLCriteria c = new HQLCriteria("from " + Container.class.getName()
            + " where label like ?", Arrays.asList(new Object[] { "01%" }));

        List<Container> results = appService.query(c);
        for (Container container : results) {
            System.out.println("geContainerLike: " + container.getLabel());
        }
    }

    private void getTopContainers() throws Exception {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id = ? and position is null", Arrays
            .asList(new Object[] { new Integer(1) }));
        List<Container> containers = appService.query(criteria);
        for (Container container : containers) {
            System.out.println("getTopContainers: " + container.getLabel());
        }
    }

}
