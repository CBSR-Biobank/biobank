
package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

import java.lang.reflect.Constructor;
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

    public PvInfoPossible getPvInfoPossible(String label) throws Exception {
        PvInfoPossible pvInfoPossible = new PvInfoPossible();
        pvInfoPossible.setLabel(label);

        List<PvInfoPossible> list = appService.search(PvInfoPossible.class,
            pvInfoPossible);
        if (list.size() != 1) throw new Exception();
        return list.get(0);
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
