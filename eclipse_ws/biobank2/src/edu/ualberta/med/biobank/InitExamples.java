
package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
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
import java.util.HashSet;
import java.util.List;

/**
 * After re-generation : init some storagetype and storage containers for one
 * site
 */
public class InitExamples {

    private static WritableApplicationService appService;

    private Site site;
    private Study study;
    private Clinic [] clinics;

    private StorageType paletteType;
    private StorageType hotel19Type;
    private StorageType hotel13Type;
    private StorageType freezerType;

    private Patient patient;

    private PatientVisit patientVisit;

    private StorageType binType;

    private StorageType drawerType;

    /**
     * @param args
     */
    public static void main(String [] args) throws Exception {
        InitExamples init = new InitExamples();
        // appService = (WritableApplicationService) ApplicationServiceProvider
        // .getApplicationServiceFromUrl("http://aicml-med.cs.ualberta.ca:8080/biobank2");

        appService = (WritableApplicationService) ApplicationServiceProvider.getApplicationServiceFromUrl("http://localhost:8080/biobank2");

        init.deletedAll(Site.class);
        init.deletedAll(Clinic.class);
        init.deletedAll(Study.class);
        init.deletedAll(StorageType.class);
        init.deletedAll(StorageContainer.class);
        init.deletedAll(PatientVisit.class);
        init.deletedAll(Patient.class);

        init.insertSite();

        init.insertStudyInSite();
        init.insertClinicsInSite();
        init.insertPatientInStudy();
        init.insertPatientVisitInPatient();
        init.insertSampleInPatientVisit();

        init.insertStorageTypesInSite();

        init.insertStorageContainers();

        System.out.println("Init done.");
    }

    InitExamples() {
        clinics = new Clinic [2];
    }

    private void insertSampleInPatientVisit() throws ApplicationException {
        Sample sample = new Sample();
        sample.setInventoryId("123");
        sample.setPatientVisit(patientVisit);
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

        SimpleDateFormat df = new SimpleDateFormat(
            BioBankPlugin.DATE_TIME_FORMAT);
        try {
            patientVisit.setDateDrawn(df.parse("2009-01-01 00:00"));
        }
        catch (ParseException e1) {
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

    private void insertStudyInSite() throws ApplicationException {
        study = new Study();
        study.setName("Study Test");
        study.setNameShort("ST");
        study.setSite(site);
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            study));
        study = (Study) res.getObjectResult();
    }

    private void insertClinicsInSite() throws ApplicationException {
        int count = 1;
        for (Clinic clinic : clinics) {
            clinic.setName("Clinic " + count);
            clinic.setSite(site);
            SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
                clinic));
            clinic = (Clinic) res.getObjectResult();
            ++count;
        }
    }

    private void deletedAll(Class<?> classType) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        List<?> list = appService.search(classType, instance);
        for (Object o : list) {
            appService.executeQuery(new DeleteExampleQuery(o));
        }
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

    private void insertStorageTypesInSite() throws ApplicationException {
        paletteType = insertStorageTypeInSite("Palette", 8, 12, null);
        hotel13Type = insertStorageTypeInSite("Hotel-13", 13, 1,
            Arrays.asList(new StorageType [] { paletteType }));
        hotel19Type = insertStorageTypeInSite("Hotel-19", 19, 1,
            Arrays.asList(new StorageType [] { paletteType }));
        freezerType = insertStorageTypeInSite("Freezer", 5, 6,
            Arrays.asList(new StorageType [] { hotel13Type, hotel19Type }));
        binType = insertStorageTypeInSite("Bin", 4, 26, null);
        drawerType = insertStorageTypeInSite("Drawer", 6, 6,
            Arrays.asList(new StorageType [] { binType }));
        insertStorageTypeInSite("Cabinet", 4, 1,
            Arrays.asList(new StorageType [] { drawerType }));
    }

    private StorageType insertStorageTypeInSite(String name, int dim1,
        int dim2, List<StorageType> children) throws ApplicationException {
        StorageType st = new StorageType();
        st.setName(name);
        st.setSite(site);
        Capacity capacity = new Capacity();
        capacity.setDimensionOneCapacity(dim1);
        capacity.setDimensionTwoCapacity(dim2);
        st.setCapacity(capacity);
        st.setDimensionOneLabel("dim1");
        st.setDimensionTwoLabel("dim2");
        if (children != null) {
            st.setChildStorageTypeCollection(new HashSet<StorageType>(children));
        }
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(st));
        return (StorageType) res.getObjectResult();
    }

    private StorageContainer insertStorageContainer(String name,
        StorageType st, StorageContainer parent, int pos1, int pos2)
        throws ApplicationException {
        StorageContainer sc = new StorageContainer();
        sc.setName(name);
        sc.setBarcode(name);
        sc.setSite(site);
        sc.setStorageType(st);
        ContainerPosition cp = new ContainerPosition();
        cp.setOccupiedContainer(sc);
        if (parent != null) {
            cp.setParentContainer(parent);
            cp.setPositionDimensionOne(pos1);
            cp.setPositionDimensionTwo(pos2);
        }
        sc.setLocatedAtPosition(cp);
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(sc));
        return (StorageContainer) res.getObjectResult();
    }

    private void insertStorageContainers() throws ApplicationException {
        StorageContainer freezer = insertStorageContainer("Freezer1",
            freezerType, null, 0, 0);
        StorageContainer hotel1 = insertStorageContainer("Hotelaa",
            hotel19Type, freezer, 1, 1);
        insertStorageContainer("Palette1", paletteType, hotel1, 1, 1);
        insertStorageContainer("Palette2", paletteType, hotel1, 3, 1);
        StorageContainer hotel2 = insertStorageContainer("Hotelbb",
            hotel13Type, freezer, 2, 2);
        insertStorageContainer("Palette3", paletteType, hotel2, 1, 1);
        insertStorageContainer("Palette4", paletteType, hotel2, 5, 1);
    }
}
