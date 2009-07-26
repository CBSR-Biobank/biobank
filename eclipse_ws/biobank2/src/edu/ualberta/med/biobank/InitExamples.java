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
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

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
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new InitExamples();
    }

    public InitExamples() throws Exception {
        clinics = new Clinic[MAX_CLINICS];

        // appService = (WritableApplicationService)
        // ApplicationServiceProvider
        // .getApplicationServiceFromUrl("http://aicml-med.cs.ualberta.ca:8080/biobank2");

        appService = (WritableApplicationService) ApplicationServiceProvider
            .getApplicationServiceFromUrl("http://localhost:8080/biobank2",
                "testuser", "test");

        deleteAll(Container.class);
        deleteAll(ContainerType.class);
        deleteAll(PatientVisit.class);
        deleteAll(Patient.class);
        deleteAll(Study.class);
        deleteAll(Clinic.class);
        deleteAll(Site.class);

        insertSite();

        insertClinicsInSite();
        insertStudyInSite();
        insertPatientInStudy();
        insertPatientVisitInPatient();
        insertSampleInPatientVisit();

        insertContainerTypesInSite();

        insertContainers();
        insertSampleStorage();

        System.out.println("Init done.");
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
        study.setName("Blood Borne Pathogens");
        study.setNameShort("BBP");
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
        paletteType = insertContainerTypeInSite("Palette", "Row", "Column", 8,
            12, null);
        hotel13Type = insertContainerTypeInSite("Hotel-13", "Row", "", 13, 1,
            Arrays.asList(new ContainerType[] { paletteType }));
        hotel19Type = insertContainerTypeInSite("Hotel-19", "Row", "", 19, 1,
            Arrays.asList(new ContainerType[] { paletteType }));
        freezerType = insertContainerTypeInSite("Freezer", "Row", "Column", 5,
            6, Arrays.asList(new ContainerType[] { hotel13Type, hotel19Type }));
        binType = insertContainerTypeInSite("Bin", "Row", "", 120, 1, null);
        drawerType = insertContainerTypeInSite("Drawer", "Row", "", 36, 1,
            Arrays.asList(new ContainerType[] { binType }));
        insertContainerTypeInSite("Cabinet", "Row", "", 4, 1, Arrays
            .asList(new ContainerType[] { drawerType }));
    }

    private ContainerType insertContainerTypeInSite(String name,
        String dim1label, String dim2label, int dim1, int dim2,
        List<ContainerType> children) throws ApplicationException {
        ContainerType ct = new ContainerType();
        ct.setName(name);
        ct.setSite(site);
        Capacity capacity = new Capacity();
        capacity.setDimensionOneCapacity(dim1);
        capacity.setDimensionTwoCapacity(dim2);
        ct.setCapacity(capacity);
        ct.setDimensionOneLabel(dim1label);
        ct.setDimensionTwoLabel(dim2label);
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
        sc.setPositionCode(name);
        sc.setProductBarcode(name);
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

    private void insertSampleStorage() throws Exception {
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Study as study "
                + "where study.nameShort=?");
        c.setParameters(Arrays.asList(new Object[] { "BBP" }));
        List<Study> studies = appService.query(c);
        if (studies.size() != 1) {
            throw new Exception("BBP study not found");
        }

        Study bbpStudy = studies.get(0);

        c = new HQLCriteria("from edu.ualberta.med.biobank.model.SampleType");
        List<SampleType> results = appService.query(c);
        if (results.size() == 0) {
            throw new Exception("not sample types in database");
        }

        SampleStorage ss;

        for (SampleType type : results) {
            if (type.getName().equals("DNA (Blood)")) {
                ss = new SampleStorage();
                ss.setQuantity(2);
                ss.setVolume(0.4);
            } else if (type.getName().equals("Plasma (Na Heparin) - DAD")) {
                ss = new SampleStorage();
                ss.setQuantity(15);
                ss.setVolume(0.2);
                ss.setSampleType(type);
            } else if (type.getName().equals("Duodenum")) {
                ss = new SampleStorage();
                ss.setQuantity(10);
                ss.setVolume(0.6);
                ss.setSampleType(type);
            } else {
                continue;
            }

            ss.setSampleType(type);
            ss.setStudy(bbpStudy);

            appService.executeQuery(new InsertExampleQuery(ss));
        }
    }

}
