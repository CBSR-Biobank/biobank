package edu.ualberta.med.biobank.common.action.csvimport;

import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException.ImportError;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

@SuppressWarnings("nls")
public abstract class CsvImportAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    protected static final int MAX_ERRORS_TO_REPORT = 50;

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
    protected Center getCenter(String name) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        Criteria c = context.getSession()
            .createCriteria(Center.class, "c")
            .add(Restrictions.eq("nameShort", name));

        return (Center) c.uniqueResult();
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

}
