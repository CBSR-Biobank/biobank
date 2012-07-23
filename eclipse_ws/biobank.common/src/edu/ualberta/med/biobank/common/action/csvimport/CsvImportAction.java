package edu.ualberta.med.biobank.common.action.csvimport;

import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException.ImportError;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

/**
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public abstract class CsvImportAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    protected static final int MAX_ERRORS_TO_REPORT = 50;

    protected static final Bundle bundle = new CommonBundle();

    protected static final LString CSV_FILE_ERROR =
        bundle.tr("CVS file not loaded").format();

    public static final String CSV_PARSE_ERROR =
        "Parse error at line {0}\n{1}";

    protected final Set<ImportError> errors = new TreeSet<ImportError>();

    protected ActionContext context = null;

    protected void addError(int lineNumber, LString message)
        throws CsvImportException {
        ImportError importError = new ImportError(lineNumber, message);
        errors.add(importError);
        if (errors.size() > MAX_ERRORS_TO_REPORT) {
            throw new CsvImportException(errors);
        }
    }

    protected Patient getPatient(String pnumber) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(Patient.class, "p")
            .add(Restrictions.eq("pnumber", pnumber));

        return (Patient) c.uniqueResult();
    }

    /*
     * Generates an action exception if specimen with inventory ID does not
     * exist.
     */
    protected Specimen getSpecimen(String inventoryId) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(Specimen.class, "s")
            .add(Restrictions.eq("inventoryId",
                inventoryId));

        return (Specimen) c.uniqueResult();
    }

    /*
     * Generates an action exception if specimen type does not exist.
     */
    protected SpecimenType getSpecimenType(String name) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(SpecimenType.class, "st")
            .add(Restrictions.eq("name", name));

        return (SpecimenType) c.uniqueResult();
    }

    /*
     * Generates an action exception if centre with name does not exist.
     */
    protected Study getStudy(String nameShort) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }
        Criteria c = context.getSession()
            .createCriteria(Study.class, "st")
            .add(Restrictions.eq("nameShort", nameShort));

        return (Study) c.uniqueResult();
    }

    /*
     * Generates an action exception if centre with name does not exist.
     */
    protected Center getCenter(String nameShort) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(Center.class, "c")
            .add(Restrictions.eq("nameShort", nameShort));

        return (Center) c.uniqueResult();
    }

    /*
     * Generates an action exception if centre with name does not exist.
     */
    protected Site getSite(String nameShort) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(Site.class, "s")
            .add(Restrictions.eq("nameShort", nameShort));

        return (Site) c.uniqueResult();
    }

    /*
     * Generates an action exception if container label does not exist.
     */
    protected Container getContainer(String label) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(Container.class, "c")
            .add(Restrictions.eq("label", label));

        return (Container) c.uniqueResult();
    }

    /*
     * Generates an action exception if shippingMethod label does not exist.
     */
    protected ShippingMethod getShippingMethod(String name) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(ShippingMethod.class, "sm")
            .add(Restrictions.eq("name", name));

        return (ShippingMethod) c.uniqueResult();
    }

}
