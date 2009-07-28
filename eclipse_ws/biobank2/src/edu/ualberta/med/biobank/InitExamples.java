package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.Assert;

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

    private Collection<Patient> patients;

    private Collection<PatientVisit> patientVisits;

    private ContainerType binType;

    private ContainerType drawerType;

    private HashMap<String, ContainerLabelingScheme> numSchemeMap;

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new InitExamples();
    }

    public InitExamples() throws Exception {
        clinics = new Clinic[MAX_CLINICS];
        numSchemeMap = new HashMap<String, ContainerLabelingScheme>();

        // appService = (WritableApplicationService) ApplicationServiceProvider
        // .getApplicationServiceFromUrl(
        // "http://aicml-med.cs.ualberta.ca:8080/biobank2", "testuser",
        // "test");

        appService = (WritableApplicationService) ApplicationServiceProvider
            .getApplicationServiceFromUrl("http://localhost:8080/biobank2",
                "testuser", "test");

        // appService = (WritableApplicationService) ApplicationServiceProvider
        // .getApplicationServiceFromUrl("http://localhost:8080/biobank2",
        // "testuser", "test");

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
        for (PatientVisit patientVisit : patientVisits) {
            Sample sample = new Sample();
            sample.setInventoryId("123");
            sample.setPatientVisit(patientVisit);
            sample.setProcessDate(new Date());
            sample.setSampleType(getSampleType());
            SDKQueryResult res = appService
                .executeQuery(new InsertExampleQuery(sample));
            sample = (Sample) res.getObjectResult();
        }
    }

    private SampleType getSampleType() throws ApplicationException {
        return (SampleType) appService.search(SampleType.class,
            new SampleType()).get(0);
    }

    private void insertPatientVisitInPatient() throws ApplicationException {
        patientVisits = new ArrayList<PatientVisit>();
        for (Patient patient : patients) {
            PatientVisit patientVisit = new PatientVisit();
            patientVisit.setClinic(clinics[0]);
            SimpleDateFormat df = new SimpleDateFormat(
                BioBankPlugin.DATE_FORMAT);
            try {
                patientVisit.setDateDrawn(df.parse("2009-01-01 00:00"));
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            patientVisit.setPatient(patient);
            SDKQueryResult res = appService
                .executeQuery(new InsertExampleQuery(patientVisit));
            patientVisit = (PatientVisit) res.getObjectResult();
            patientVisits.add(patientVisit);
        }
    }

    private void insertPatientInStudy() throws ApplicationException {
        patients = new ArrayList<Patient>();
        for (int i = 0; i < 50; i++) {
            Patient patient = new Patient();
            patient.setNumber(Integer.toString(i));
            patient.setStudy(study);

            SDKQueryResult res = appService
                .executeQuery(new InsertExampleQuery(patient));
            patient = (Patient) res.getObjectResult();
            patients.add(patient);
        }
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
        List<ContainerLabelingScheme> numSchemes = appService.search(
            ContainerLabelingScheme.class, new ContainerLabelingScheme());
        Assert.isNotNull(numSchemes);
        for (ContainerLabelingScheme scheme : numSchemes) {
            numSchemeMap.put(scheme.getName(), scheme);
        }

        // Freezer Types
        paletteType = insertContainerTypeInSite("Palette", 8, 12, null,
            numSchemeMap.get("SBS Standard"));
        hotel13Type = insertContainerTypeInSite("Hotel-13", 13, 1, Arrays
            .asList(new ContainerType[] { paletteType }), numSchemeMap
            .get("CBSR 2 char alphabetic"));
        hotel19Type = insertContainerTypeInSite("Hotel-19", 19, 1, Arrays
            .asList(new ContainerType[] { paletteType }), numSchemeMap
            .get("CBSR 2 char alphabetic"));
        freezerType = insertContainerTypeInSite("Freezer", 3, 10, Arrays
            .asList(new ContainerType[] { hotel13Type, hotel19Type }),
            numSchemeMap.get("2 char numeric"));

        // Cabinet Types
        binType = insertContainerTypeInSite("Bin", 120, 1, null, numSchemeMap
            .get("2 char numeric"));
        drawerType = insertContainerTypeInSite("Drawer", 36, 1, Arrays
            .asList(new ContainerType[] { binType }), numSchemeMap
            .get("CBSR 2 char alphabetic"));
        insertContainerTypeInSite("Cabinet", 4, 1, Arrays
            .asList(new ContainerType[] { drawerType }), numSchemeMap
            .get("2 char numeric"));
    }

    private ContainerType insertContainerTypeInSite(String name, int dim1,
        int dim2, List<ContainerType> children,
        ContainerLabelingScheme childLabelingScheme)
        throws ApplicationException {
        ContainerType ct = new ContainerType();
        ct.setName(name);
        ct.setSite(site);
        if (childLabelingScheme != null) {
            ct.setChildLabelingScheme(childLabelingScheme);
        }
        Capacity capacity = new Capacity();
        capacity.setDimensionOneCapacity(dim1);
        capacity.setDimensionTwoCapacity(dim2);
        ct.setCapacity(capacity);
        if (children != null) {
            ct.setChildContainerTypeCollection(new HashSet<ContainerType>(
                children));
        }
        SDKQueryResult res = appService
            .executeQuery(new InsertExampleQuery(ct));
        return (ContainerType) res.getObjectResult();
    }

    private Container insertContainer(String label, ContainerType ct,
        Container parent) throws Exception {
        Container container = new Container();
        container.setLabel(label);
        container.setProductBarcode(label);
        container.setSite(site);
        container.setContainerType(ct);
        if (parent != null) {
            String labelingScheme = parent.getContainerType()
                .getChildLabelingScheme().getName();

            RowColPos rc;
            if (labelingScheme.equals("2 char numeric")) {
                rc = LabelingScheme.twoCharNumericToRowCol(parent
                    .getContainerType(), container.getLabel());
            } else if (labelingScheme.equals("CBSR 2 char alphabetic")) {
                rc = LabelingScheme.twoCharAlphaToRowCol(parent
                    .getContainerType(), container.getLabel());
            } else {
                throw new Exception("Invalid child labeling scheme: "
                    + labelingScheme);
            }

            ContainerPosition pos = new ContainerPosition();
            pos.setContainer(container);
            pos.setParentContainer(parent);
            pos.setPositionDimensionOne(rc.row);
            pos.setPositionDimensionTwo(rc.col);
            container.setPosition(pos);
        }
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            container));
        return (Container) res.getObjectResult();
    }

    private void insertContainers() throws Exception {
        Container freezer = insertContainer("01", freezerType, null);
        Container hotel1 = insertContainer("01AA", hotel19Type, freezer);
        insertContainer("01AA01", paletteType, hotel1);
        insertContainer("01AA03", paletteType, hotel1);
        Container hotel2 = insertContainer("01AE", hotel13Type, freezer);
        insertContainer("01AE01", paletteType, hotel2);
        insertContainer("01AE05", paletteType, hotel2);
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
