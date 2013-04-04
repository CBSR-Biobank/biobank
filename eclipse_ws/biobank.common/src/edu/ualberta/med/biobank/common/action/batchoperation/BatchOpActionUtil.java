package edu.ualberta.med.biobank.common.action.batchoperation;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.FileMetaData;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

/**
 * Server side utility functions that load model objects from the database.
 * <p>
 * <strong>These methods should eventually be moved to DAO classes for the individual model
 * objects.</strong>
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class BatchOpActionUtil {

    private static final Bundle bundle = new CommonBundle();

    public static final LString CSV_FILE_ERROR =
        bundle.tr("CSV file not loaded").format();

    public static final String CSV_PARSE_ERROR = "Parse error at line {0}\n{1}";

    public static Patient getPatient(Session session, String pnumber) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        Patient patient = (Patient) session.createCriteria(Patient.class)
            .add(Restrictions.eq("pnumber", pnumber)).uniqueResult();
        return patient;
    }

    public static Specimen getSpecimen(Session session, String inventoryId) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        Criteria c = session.createCriteria(Specimen.class)
            .add(Restrictions.eq("inventoryId", inventoryId));

        return (Specimen) c.uniqueResult();
    }

    public static SpecimenType getSpecimenType(Session session,
        String name) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        Criteria c = session.createCriteria(SpecimenType.class)
            .add(Restrictions.eq("name", name));

        return (SpecimenType) c.uniqueResult();
    }

    public static Study getStudy(Session session, String nameShort) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }
        Criteria c = session.createCriteria(Study.class)
            .add(Restrictions.eq("nameShort", nameShort));

        return (Study) c.uniqueResult();
    }

    public static Center getCenter(Session session, String nameShort) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        Criteria c = session.createCriteria(Center.class)
            .add(Restrictions.eq("nameShort", nameShort));

        return (Center) c.uniqueResult();
    }

    public static Site getSite(Session session, String nameShort) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        Criteria c = session
            .createCriteria(Site.class, "s")
            .add(Restrictions.eq("nameShort", nameShort));

        return (Site) c.uniqueResult();
    }

    public static Container getContainer(Session session, String label) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        Criteria c = session
            .createCriteria(Container.class, "c")
            .add(Restrictions.eq("label", label));

        return (Container) c.uniqueResult();
    }

    public static OriginInfo getOriginInfo(Session session,
        String waybill) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        Criteria c = session
            .createCriteria(OriginInfo.class, "oi")
            .createAlias("oi.shipmentInfo", "si")
            .add(Restrictions.eq("si.waybill", waybill));

        return (OriginInfo) c.uniqueResult();
    }

    /*
     * 
     */
    public static ShippingMethod getShippingMethod(Session session, String name) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        return (ShippingMethod) session
            .createCriteria(ShippingMethod.class)
            .add(Restrictions.eq("name", name)).uniqueResult();
    }

    public static CollectionEvent getCollectionEvent(Session session,
        String patientNumber, Integer visitNumber) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        return (CollectionEvent) session
            .createCriteria(CollectionEvent.class, "ce")
            .createAlias("ce.patient", "pt")
            .add(Restrictions.eq("ce.visitNumber", visitNumber))
            .add(Restrictions.eq("pt.pnumber", patientNumber))
            .uniqueResult();
    }

    public static ProcessingEvent getProcessingEvent(Session session, String worksheetNumber) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }

        return (ProcessingEvent) session
            .createCriteria(ProcessingEvent.class)
            .add(Restrictions.eq("worksheet", worksheetNumber))
            .uniqueResult();
    }

    public static BatchOperation createBatchOperation(Session session, User user,
        final FileData fileData) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }
        if (user == null) {
            throw new NullPointerException("user is null");
        }
        if (fileData == null) {
            throw new NullPointerException("fileData is null");
        }
        BatchOperation batchOperation = new BatchOperation();

        batchOperation.setInput(fileData);
        batchOperation.setExecutedBy(user);
        batchOperation.setTimeExecuted(new Date());
        batchOperation.setInput(fileData);
        batchOperation.setTimeExecuted(new Date());

        session.saveOrUpdate(fileData);
        session.saveOrUpdate(batchOperation);
        return batchOperation;
    }

    public static FileMetaData getFileMetaData(Session session, Integer batchOpId) {
        if (session == null) {
            throw new NullPointerException("session is null");
        }
        if (batchOpId == null) {
            throw new NullPointerException("batchOpId is null");
        }
        FileMetaData metaData = (FileMetaData) session
            .createCriteria(BatchOperation.class, "batchOp")
            .createAlias("batchOp.input", "input")
            .setProjection(Projections.property("input.metaData"))
            .add(Restrictions.eq("id", batchOpId))
            .uniqueResult();
        return metaData;
    }
}
