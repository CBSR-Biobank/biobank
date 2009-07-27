
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class BioBank2Db {

    private static WritableApplicationService appService = null;

    public SimpleDateFormat bbpdbDateFmt;

    public SimpleDateFormat biobank2DateFmt;

    private static BioBank2Db instance = null;

    private BioBank2Db() {
        bbpdbDateFmt = new SimpleDateFormat(Importer.BBPDB_DATE_FMT);
        biobank2DateFmt = new SimpleDateFormat(Importer.BIOBANK2_DATE_FMT);
    }

    public static BioBank2Db getInstance() {
        if (instance != null) return instance;

        instance = new BioBank2Db();
        return instance;
    }

    public WritableApplicationService getAppService() {
        return appService;
    }

    public void setAppService(WritableApplicationService a) {
        appService = a;
    }

    public void deleteAll(Class<?> classType) throws Exception {
        System.out.println("deleting all " + classType.getName() + " instances");
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        List<?> list = appService.search(classType, instance);
        for (Object o : list) {
            appService.executeQuery(new DeleteExampleQuery(o));
        }
    }

    public Site createSite() throws ApplicationException {
        Site site = new Site();
        site.setName("CBR");
        Address address = new Address();
        site.setAddress(address);

        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            site));
        site = (Site) res.getObjectResult();

        return site;
    }

    public Study getStudy(String shortName) throws Exception {
        Study study = new Study();
        study.setNameShort(shortName);

        List<Study> list = appService.search(Study.class, study);
        if (list.size() != 1) {
            throw new Exception("study with short name" + shortName
                + " not found");
        }
        return list.get(0);
    }

    public Clinic getClinic(Study study, String clinicName) throws Exception {
        HQLCriteria c = new HQLCriteria("select clinics"
            + " from edu.ualberta.med.biobank.model.Study as study"
            + " inner join study.clinicCollection as clinics"
            + " where study.id=? and clinics.name=?");

        c.setParameters(Arrays.asList(new Object [] { study.getId(), clinicName }));

        List<Clinic> results = appService.query(c);
        if (results.size() == 0) return null;

        if (results.size() > 1) {
            throw new Exception("ERROR: multiple clinics with name "
                + clinicName + " found");
        }
        return results.get(0);
    }

    public Patient getPatient(String number) throws Exception {
        Patient patient = new Patient();
        patient.setNumber(number);

        List<Patient> list = appService.search(Patient.class, patient);
        if (list.size() != 1) throw new Exception();
        return list.get(0);
    }

    public PvInfoPossible getPvInfoPossible(String label) throws Exception {
        PvInfoPossible pvInfoPossible = new PvInfoPossible();
        pvInfoPossible.setLabel(label);

        List<PvInfoPossible> list = appService.search(PvInfoPossible.class,
            pvInfoPossible);
        if (list.size() != 1) throw new Exception();
        return list.get(0);
    }

    public Container getContainer(String name) throws Exception {
        Container sc = new Container();
        sc.setLabel(name);

        List<Container> list = appService.search(Container.class, sc);
        if (list.size() != 1) throw new Exception();
        return list.get(0);
    }

    public SampleType getSampleType(String nameShort) throws Exception {
        SampleType st = new SampleType();
        st.setNameShort(nameShort);

        List<SampleType> list = appService.search(SampleType.class, st);
        if (list.size() != 1) throw new Exception(
            "Sample type with short name \"" + nameShort + "\" not found");
        return list.get(0);
    }

    public Container getChildContainer(Container container, int dim1pos,
        int dim2pos) throws Exception {

        HQLCriteria c = new HQLCriteria("select occupied"
            + " from edu.ualberta.med.biobank.model.Container as sc"
            + " inner join sc.childPositionCollection as positions"
            + " inner join positions.container as occupied"
            + " where sc.id=? and positions.positionDimensionOne=? "
            + " and positions.positionDimensionTwo=?");

        c.setParameters(Arrays.asList(new Object [] {
            container.getId(), dim1pos, dim2pos }));

        List<Container> results = appService.query(c);
        if (results.size() != 1) {
            if (results.size() != 0) throw new Exception("Container "
                + container.getLabel() + " has no child containers");
            else
                throw new Exception("Container " + container.getLabel()
                    + " has no child container at position " + dim1pos + ","
                    + dim2pos);
        }
        return results.get(0);
    }

    public void containerCheckSampleTypeValid(Container container,
        SampleType sampleType) throws Exception {
        ContainerType stype = container.getContainerType();
        Collection<SampleType> st = stype.getSampleTypeCollection();
        if ((st == null) || (st.size() == 0)) {
            throw new Exception("Container " + container.getLabel()
                + " cannot hold any sample types: " + sampleType.getName());
        }
        HQLCriteria c = new HQLCriteria("select count(sampleTypes)"
            + " from edu.ualberta.med.biobank.model.ContainerType as stype"
            + " inner join stype.sampleTypeCollection as sampleTypes"
            + " where stype.id=? and sampleTypes.id=?");

        c.setParameters(Arrays.asList(new Object [] {
            stype.getId(), sampleType.getId() }));

        List<Long> results = appService.query(c);
        if (results.get(0) != 1) {
            throw new Exception("Sample type " + sampleType.getName()
                + " is not valid for container " + container.getLabel());
        }
    }

    public PatientVisit getPatientVisit(String studyNameShort, int patientNum,
        String dateDrawn) throws Exception {
        Date date = biobank2DateFmt.parse(dateDrawn);
        HQLCriteria c = new HQLCriteria("select visits"
            + " from edu.ualberta.med.biobank.model.Study as study"
            + " inner join study.patientCollection as patients"
            + " inner join patients.patientVisitCollection as visits"
            + " where study.nameShort=? and patients.number=?"
            + " and visits.dateDrawn=?");

        // System.out.println("getPatientVisit: studyNameShort/" +
        // studyNameShort
        // + " patientNum/" + patientNum + " dateDrawn/" + dateDrawn);

        c.setParameters(Arrays.asList(new Object [] {
            studyNameShort, String.format("%d", patientNum), date }));

        List<PatientVisit> results = appService.query(c);
        if (results.size() != 1) {
            if (results.size() == 0) {
                System.out.println("ERROR: found 0 patient visits for studyName/"
                    + studyNameShort
                    + " patientNum/"
                    + patientNum
                    + " dateDrawn/" + dateDrawn);
            }
            else {
                System.out.println("WARNING: found " + results.size()
                    + " patient visits for studyName/" + studyNameShort
                    + " patientNum/" + patientNum + " dateDrawn/" + dateDrawn);
                for (PatientVisit v : results) {
                    System.out.println("visit_id/" + v.getId() + " dateDrawn/"
                        + biobank2DateFmt.format(v.getDateDrawn()) + " pid/"
                        + v.getPatient().getId());
                }
            }
            return null;
        }
        return results.get(0);
    }

    public Object setObject(Object o) throws Exception {
        Integer id = null;
        SDKQuery qry;
        SDKQueryResult res;

        if (o instanceof Study) {
            id = ((Study) o).getId();
        }
        else if (o instanceof Clinic) {
            id = ((Clinic) o).getId();
        }
        else if (o instanceof Study) {
            id = ((Clinic) o).getId();
        }

        if (id == null) {
            qry = new InsertExampleQuery(o);
        }
        else {
            qry = new UpdateExampleQuery(o);
        }

        res = appService.executeQuery(qry);
        return res.getObjectResult();
    }
}
