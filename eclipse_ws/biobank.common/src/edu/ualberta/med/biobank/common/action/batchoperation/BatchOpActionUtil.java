package edu.ualberta.med.biobank.common.action.batchoperation;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

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

    public static final LString CSV_HEADER_ERROR =
        bundle.tr("invalid headers in CSV file").format();

    public static final String CSV_PARSE_ERROR =
        "Parse error at line {0}\n{1}";

    public static Patient getPatient(ActionContext context, String pnumber) {
        if (context == null) {
            throw new NullPointerException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession().createCriteria(Patient.class)
            .add(Restrictions.eq("pnumber", pnumber));

        return (Patient) c.uniqueResult();
    }

    /*
     * Generates an action exception if specimen with inventory ID does not exist.
     */
    public static Specimen getSpecimen(ActionContext context, String inventoryId) {
        if (context == null) {
            throw new NullPointerException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession().createCriteria(Specimen.class)
            .add(Restrictions.eq("inventoryId", inventoryId));

        return (Specimen) c.uniqueResult();
    }

    /*
     * Generates an action exception if specimen type does not exist.
     */
    public static SpecimenType getSpecimenType(ActionContext context,
        String name) {
        if (context == null) {
            throw new NullPointerException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession().createCriteria(SpecimenType.class)
            .add(Restrictions.eq("name", name));

        return (SpecimenType) c.uniqueResult();
    }

    /*
     * Generates an action exception if centre with name does not exist.
     */
    public static Study getStudy(ActionContext context, String nameShort) {
        if (context == null) {
            throw new NullPointerException(
                "should only be called once the context is initialized");
        }
        Criteria c = context.getSession().createCriteria(Study.class)
            .add(Restrictions.eq("nameShort", nameShort));

        return (Study) c.uniqueResult();
    }

    /*
     * Generates an action exception if centre with name does not exist.
     */
    public static Center getCenter(ActionContext context, String nameShort) {
        if (context == null) {
            throw new NullPointerException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession().createCriteria(Center.class)
            .add(Restrictions.eq("nameShort", nameShort));

        return (Center) c.uniqueResult();
    }

    /*
     * Generates an action exception if centre with name does not exist.
     */
    public static Site getSite(ActionContext context, String nameShort) {
        if (context == null) {
            throw new NullPointerException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(Site.class, "s")
            .add(Restrictions.eq("nameShort", nameShort));

        return (Site) c.uniqueResult();
    }

    /*
     * 
     */
    public static Container getContainer(ActionContext context, String label) {
        if (context == null) {
            throw new NullPointerException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(Container.class, "c")
            .add(Restrictions.eq("label", label));

        return (Container) c.uniqueResult();
    }

    public static OriginInfo getOriginInfo(ActionContext context,
        String waybill) {
        if (context == null) {
            throw new NullPointerException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(OriginInfo.class, "oi")
            .createAlias("oi.shipmentInfo", "si")
            .add(Restrictions.eq("si.waybill", waybill));

        return (OriginInfo) c.uniqueResult();
    }

    /*
     * 
     */
    public static ShippingMethod getShippingMethod(ActionContext context,
        String name) {
        if (context == null) {
            throw new NullPointerException(
                "should only be called once the context is initialized");
        }

        return (ShippingMethod) context.getSession()
            .createCriteria(ShippingMethod.class)
            .add(Restrictions.eq("name", name)).uniqueResult();
    }

    public static CollectionEvent getCollectionEvent(ActionContext context,
        String patientNumber, Integer visitNumber) {

        return (CollectionEvent) context.getSession()
            .createCriteria(CollectionEvent.class, "ce")
            .createAlias("ce.patient", "pt")
            .add(Restrictions.eq("ce.visitNumber", visitNumber))
            .add(Restrictions.eq("pt.pnumber", patientNumber))
            .uniqueResult();
    }

    public static ProcessingEvent getProcessingEvent(ActionContext context,
        String worksheetNumber) {

        return (ProcessingEvent) context.getSession()
            .createCriteria(ProcessingEvent.class)
            .add(Restrictions.eq("worksheet", worksheetNumber))
            .uniqueResult();
    }
}
