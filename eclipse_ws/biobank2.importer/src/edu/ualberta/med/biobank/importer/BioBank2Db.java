
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

public class BioBank2Db {

    private static WritableApplicationService appService = null;

    private static BioBank2Db instance = null;

    private BioBank2Db() {}

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
        if (list.size() != 1) throw new Exception();
        return list.get(0);
    }

    public Clinic getClinic(String name) throws Exception {
        Clinic clinic = new Clinic();
        clinic.setName(name);

        List<Clinic> list = appService.search(Clinic.class, clinic);
        if (list.size() != 1) throw new Exception();
        return list.get(0);
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

    public StorageContainer getStorageContainer(String name) throws Exception {
        StorageContainer sc = new StorageContainer();
        sc.setName(name);

        List<StorageContainer> list = appService.search(StorageContainer.class,
            sc);
        if (list.size() != 1) throw new Exception();
        return list.get(0);
    }

    public StorageContainer getStorageContainerContents(
        StorageContainer container, int dim1pos, int dim2pos) throws Exception {
        Collection<ContainerPosition> positions = container.getOccupiedPositions();
        if ((positions == null)
            || (container.getOccupiedPositions().size() == 0)) {
            throw new Exception("Container " + container.getName()
                + " has no sub containers");
        }
        for (ContainerPosition position : positions) {
            if ((position.getPositionDimensionOne() == dim1pos)
                && (position.getPositionDimensionTwo() == dim2pos)) return position.getParentContainer();
        }
        throw new Exception("Container " + container.getName()
            + " has no sub container at position" + dim1pos + "," + dim2pos);
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
