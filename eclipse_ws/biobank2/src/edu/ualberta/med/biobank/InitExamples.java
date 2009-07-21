package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * After re-generation : init some storagetype and storage containers for one
 * site
 */
public class InitExamples {

    private static final int MAX_CLINICS = 2;

    private static WritableApplicationService appService;

    private Site site;
    private Study study;
    private Clinic[] clinics;

    private ContainerType paletteType;
    private ContainerType hotel19Type;
    private ContainerType hotel13Type;
    private ContainerType freezerType;

    private Patient patient;

    private PatientVisit patientVisit;

    private ContainerType binType;

    private ContainerType drawerType;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        InitExamples init = new InitExamples();
        // appService = (WritableApplicationService)
        // ApplicationServiceProvider
        // .getApplicationServiceFromUrl("http://aicml-med.cs.ualberta.ca:8080/biobank2");

        appService = (WritableApplicationService) ApplicationServiceProvider
            .getApplicationServiceFromUrl("http://aicml-med:8080/biobank2",
                "testuser", "test");

        init.deleteAll(Container.class);
        init.deleteAll(ContainerType.class);
        init.deleteAll(Patient.class);
        init.deleteAll(PatientVisit.class);
        init.deleteAll(Study.class);
        init.deleteAll(Clinic.class);
        init.deleteAll(Site.class);

        init.insertSite();

        init.insertClinicsInSite();
        init.insertStudyInSite();
        init.insertPatientInStudy();
        init.insertPatientVisitInPatient();
        init.insertSampleInPatientVisit();

        init.insertContainerTypesInSite();

        init.insertContainers();

        System.out.println("Init done.");
    }

    public InitExamples() {
        clinics = new Clinic[MAX_CLINICS];
    }

    private void insertSite() throws ApplicationException {
        site = new Site();
        site.setName("Site Edmonton Test");
        Address address = new Address();
        address.setCity("Edmonton");
        site.setAddress(address);
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            site));
        site = (Site) res.getObjectResult();
    }

    private void insertClinicsInSite() throws ApplicationException {
        for (int i = 0; i < 2; ++i) {
            Clinic clinic = new Clinic();
            clinic.setName("Clinic " + i);
            clinic.setSite(site);

            Address address = new Address();
            address.setCity("Edmonton");
            clinic.setAddress(address);

            SDKQueryResult res = appService
                .executeQuery(new InsertExampleQuery(clinic));
            clinics[i] = (Clinic) res.getObjectResult();
        }
    }

    private void insertStudyInSite() throws ApplicationException {
        study = new Study();
        study.setName("Study Test");
        study.setNameShort("ST");
        study.setSite(site);
        study.setClinicCollection(site.getClinicCollection());
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            study));
        study = (Study) res.getObjectResult();
    }

    private void insertSampleInPatientVisit() throws ApplicationException {
        Sample sample = new Sample();
        sample.setInventoryId("123");
        sample.setPatientVisit(patientVisit);
        sample.setProcessDate(new Date());
        sample.setSampleType(getSampleType());
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            sample));
        sample = (Sample) res.getObjectResult();
    }

    private SampleType getSampleType() throws ApplicationException {
        return (SampleType) appService.search(SampleType.class,
            new SampleType()).get(0);
    }

    private void insertPatientVisitInPatient() throws ApplicationException {
        patientVisit = new PatientVisit();

        patientVisit.setClinic(clinics[0]);

        SimpleDateFormat df = new SimpleDateFormat(BioBankPlugin.DATE_FORMAT);
        try {
            patientVisit.setDateDrawn(df.parse("2009-01-01 00:00"));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        patientVisit.setPatient(patient);
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            patientVisit));
        patientVisit = (PatientVisit) res.getObjectResult();
    }

    private void insertPatientInStudy() throws ApplicationException {
        patient = new Patient();
        patient.setNumber("1111");
        patient.setStudy(study);
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            patient));
        patient = (Patient) res.getObjectResult();
    }

    private void deleteAll(Class<?> classType) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        List<?> list = appService.search(classType, instance);
        for (Object o : list) {
            appService.executeQuery(new DeleteExampleQuery(o));
        }
    }

    private void insertContainerTypesInSite() throws ApplicationException {
        paletteType = insertContainerTypeInSite("Palette", 8, 12, null);
        hotel13Type = insertContainerTypeInSite("Hotel-13", 13, 1, Arrays
            .asList(new ContainerType[] { paletteType }));
        hotel19Type = insertContainerTypeInSite("Hotel-19", 19, 1, Arrays
            .asList(new ContainerType[] { paletteType }));
        freezerType = insertContainerTypeInSite("Freezer", 5, 6, Arrays
            .asList(new ContainerType[] { hotel13Type, hotel19Type }));
        binType = insertContainerTypeInSite("Bin", 4, 26, null);
        drawerType = insertContainerTypeInSite("Drawer", 6, 6, Arrays
            .asList(new ContainerType[] { binType }));
        insertContainerTypeInSite("Cabinet", 4, 1, Arrays
            .asList(new ContainerType[] { drawerType }));
    }

    private ContainerType insertContainerTypeInSite(String name, int dim1,
        int dim2, List<ContainerType> children) throws ApplicationException {
        ContainerType ct = new ContainerType();
        ct.setName(name);
        ct.setSite(site);
        Capacity capacity = new Capacity();
        capacity.setDimensionOneCapacity(dim1);
        capacity.setDimensionTwoCapacity(dim2);
        ct.setCapacity(capacity);
        ct.setDimensionOneLabel("dim1");
        ct.setDimensionTwoLabel("dim2");
        if (children != null) {
            ct.setChildContainerTypeCollection(new HashSet<ContainerType>(
                children));
        }
        SDKQueryResult res = appService
            .executeQuery(new InsertExampleQuery(ct));
        return (ContainerType) res.getObjectResult();
    }

    private Container insertContainer(String name, ContainerType ct,
        Container parent, int pos1, int pos2) throws ApplicationException {
        Container sc = new Container();
        sc.setName(name);
        sc.setBarcode(name);
        sc.setSite(site);
        sc.setContainerType(ct);
        ContainerPosition cp = new ContainerPosition();
        cp.setContainer(sc);
        if (parent != null) {
            cp.setParentContainer(parent);
            cp.setPositionDimensionOne(pos1);
            cp.setPositionDimensionTwo(pos2);
        }
        sc.setPosition(cp);
        SDKQueryResult res = appService
            .executeQuery(new InsertExampleQuery(sc));
        return (Container) res.getObjectResult();
    }

    private void insertContainers() throws ApplicationException {
        Container freezer = insertContainer("Freezer1", freezerType, null, 0, 0);
        Container hotel1 = insertContainer("Hotelaa", hotel19Type, freezer, 1,
            1);
        insertContainer("Palette1", paletteType, hotel1, 1, 1);
        insertContainer("Palette2", paletteType, hotel1, 3, 1);
        Container hotel2 = insertContainer("Hotelbb", hotel13Type, freezer, 2,
            2);
        insertContainer("Palette3", paletteType, hotel2, 1, 1);
        insertContainer("Palette4", paletteType, hotel2, 5, 1);
    }
}
