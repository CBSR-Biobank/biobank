package edu.ualberta.med.biobank.common.action.csvimport;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenInfo;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * This action takes a CSV file as input and import the specimens contained in
 * the file.
 * 
 * @author loyola
 * 
 */
public class SpecimenCsvImportAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString CSV_PARSE_ERROR =
        bundle.tr("Parse error at line {0}\n{1}").format();

    @SuppressWarnings("nls")
    public static final LString CSV_FILE_ERROR =
        bundle.tr("CVS file not loaded").format();

    @SuppressWarnings("nls")
    public static final LString CSV_UNCOMPRESS_ERROR =
        bundle.tr("CVS file could not be uncompressed").format();

    @SuppressWarnings("nls")
    public static final LString CSV_PATIENT_ERROR =
        bundle.tr("CVS patient does not exist").format();

    @SuppressWarnings("nls")
    public static final LString CSV_PARENT_SPECIMEN_ERROR =
        bundle.tr("CVS parent specimen does not exist").format();

    @SuppressWarnings("nls")
    public static final LString CSV_CURRENT_CENTER_ERROR =
        bundle.tr("CVS current center does not exist").format();

    @SuppressWarnings("nls")
    public static final LString CSV_SPECIMEN_TYPE_ERROR =
        bundle.tr("CVS specimen type does not exist").format();

    @SuppressWarnings("nls")
    public static final LString CSV_CONTAINER_LABEL_ERROR =
        bundle.tr("CVS container does not exist").format();

    @SuppressWarnings("nls")
    public static final LString CSV_SPECIMEN_LABEL_ERROR =
        bundle.tr("CVS specimen position is invalid").format();

    @SuppressWarnings("nls")
    private static final CellProcessor[] PROCESSORS = new CellProcessor[] {
        new Unique(),
        null,
        null,
        new ParseDate("yyyy-MM-dd HH:mm"),
        null,
        null,
        new ParseInt(),
        null,
        null,
        new ParseBool(),
        null,
        null,
        new Optional(),
        new Optional()
    };

    private CompressedReference<ArrayList<SpecimenCsvInfo>> compressedList =
        null;

    private ActionContext context = null;

    public SpecimenCsvImportAction(String filename) throws IOException {
        setCsvFile(filename);
    }

    @SuppressWarnings("nls")
    private void setCsvFile(String filename) throws IOException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        final String[] header = new String[] {
            "inventoryId",
            "parentInventoryID",
            "specimenType",
            "createdAt",
            "studyName",
            "patientNumber",
            "visitNumber",
            "currentCenter",
            "originCenter",
            "sourceSpecimen",
            "worksheet",
            "rootContainerType",
            "palletLabel",
            "palletPosition"
        };

        try {
            ArrayList<SpecimenCsvInfo> specimenCsvInfos =
                new ArrayList<SpecimenCsvInfo>(0);

            SpecimenCsvInfo specimenCsvInfo;
            reader.getCSVHeader(true);
            while ((specimenCsvInfo =
                reader.read(SpecimenCsvInfo.class, header, PROCESSORS)) != null) {

                if ((specimenCsvInfo.getPalletLabel() != null)
                    && (specimenCsvInfo.getPalletPosition() == null)) {
                    // if pallet label defined then position has to be defined
                    // System.out.println("line: " + reader.getLineNumber());
                    throw new ActionException(CSV_PARSE_ERROR);
                }

                if ((specimenCsvInfo.getPalletLabel() == null)
                    && (specimenCsvInfo.getPalletPosition() != null)) {
                    // if pallet position defined then label has to be defined
                    // System.out.println("line: " + reader.getLineNumber());
                    throw new ActionException(CSV_PARSE_ERROR);
                }

                specimenCsvInfos.add(specimenCsvInfo);
            }

            System.out.println("rows read: " + specimenCsvInfos.size());

            compressedList =
                new CompressedReference<ArrayList<SpecimenCsvInfo>>(
                    specimenCsvInfos);

        } catch (SuperCSVException e) {
            // System.out.println("message: " + e.getMessage());
            // System.out.println("context: " + e.getCsvContext());
            // TODO: what exception should be thrown here
            throw new ActionException(CSV_PARSE_ERROR);
            // TODO: add parameters reader.getLineNumber() and
            // e.getCsvContext())) to this exception
        } finally {
            reader.close();
        }
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.LEGACY_IMPORT_CSV.isAllowed(context.getUser());
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        if (compressedList == null) {
            throw new ActionException(CSV_FILE_ERROR);
        }

        this.context = context;
        boolean result = false;

        ArrayList<SpecimenCsvInfo> specimenCsvInfos = compressedList.get();
        for (SpecimenCsvInfo csvInfo : specimenCsvInfos) {
            if (csvInfo.getParentInventoryID().isEmpty()) {
                addSourceSpecimen(csvInfo);
            } else {
                addAliquotedSpecimen(csvInfo);
            }
        }

        result = true;
        return new BooleanResult(result);
    }

    @SuppressWarnings("nls")
    private void addSourceSpecimen(SpecimenCsvInfo csvInfo) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        Patient p = loadPatient(csvInfo.getPatientNumber());

        CollectionEvent cevent = null;
        Integer id = null;

        // find the collection event
        // if the visit number not found, a new collection event is created
        for (CollectionEvent ce : p.getCollectionEvents()) {
            if (ce.getVisitNumber().equals(csvInfo.getVisitNumber())) {
                cevent = ce;
                id = ce.getId();
                break;
            }
        }

        SaveCEventSpecimenInfo ceventSpecimenInfo;
        Set<SaveCEventSpecimenInfo> ceventSpecimenInfos =
            new HashSet<SaveCEventSpecimenInfo>();

        if (cevent != null) {
            for (Specimen specimen : cevent.getOriginalSpecimens()) {
                ceventSpecimenInfo = new SaveCEventSpecimenInfo();
                ceventSpecimenInfo.id = specimen.getId();
                ceventSpecimenInfo.inventoryId = specimen.getInventoryId();
                ceventSpecimenInfo.createdAt = specimen.getCreatedAt();
                ceventSpecimenInfo.activityStatus =
                    specimen.getActivityStatus();
                ceventSpecimenInfo.specimenTypeId =
                    specimen.getSpecimenType().getId();
                ceventSpecimenInfo.centerId =
                    specimen.getOriginInfo().getCenter().getId();
                ceventSpecimenInfos.add(ceventSpecimenInfo);
            }
        }

        ceventSpecimenInfo = new SaveCEventSpecimenInfo();
        ceventSpecimenInfo.inventoryId = csvInfo.getInventoryId();
        ceventSpecimenInfo.createdAt = csvInfo.getCreatedAt();
        ceventSpecimenInfo.activityStatus = ActivityStatus.ACTIVE;
        ceventSpecimenInfo.specimenTypeId =
            loadSpecimenType(csvInfo.getSpecimenType()).getId();
        ceventSpecimenInfo.centerId =
            loadCenter(csvInfo.getOriginCenter()).getId();
        ceventSpecimenInfos.add(ceventSpecimenInfo);

        CollectionEventSaveAction ceventSaveAction =
            new CollectionEventSaveAction(id, p.getId(),
                csvInfo.getVisitNumber(), ActivityStatus.ACTIVE, null,
                ceventSpecimenInfos, null);
        ceventSaveAction.run(context);
    }

    @SuppressWarnings("nls")
    private void addAliquotedSpecimen(SpecimenCsvInfo csvInfo) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        // make sure patient exists
        Patient p = loadPatient(csvInfo.getPatientNumber());
        Specimen parentSpecimen = loadSpecimen(csvInfo.getParentInventoryID());
        Center currentCenter = loadCenter(csvInfo.getCurrentCenter());
        SpecimenType specimenType = loadSpecimenType(csvInfo.getSpecimenType());

        AliquotedSpecimenInfo specimenInfo = new AliquotedSpecimenInfo();
        specimenInfo.inventoryId = csvInfo.getInventoryId();
        specimenInfo.typeId = specimenType.getId();
        specimenInfo.activityStatus = ActivityStatus.ACTIVE;
        specimenInfo.parentSpecimenId = parentSpecimen.getId();

        String palletLabel = csvInfo.getPalletLabel();
        String palletPosition = csvInfo.getPalletPosition();

        if ((palletLabel != null) && (palletPosition != null)) {
            Container container = loadContainer(csvInfo.getPalletLabel());
            specimenInfo.containerId = container.getId();
            try {
                container.getChildByLabel(csvInfo.getPalletPosition());
            } catch (Exception e) {
                throw new ActionException(CSV_SPECIMEN_LABEL_ERROR);
            }
        } else {
            throw new IllegalStateException(
                "both pallet label and position should be defined");
        }

        // link the specimen to the patient
        SpecimenLinkSaveAction specimenLinkSaveAction =
            new SpecimenLinkSaveAction(currentCenter.getId(),
                p.getStudy().getId(), Arrays.asList(specimenInfo));
        specimenLinkSaveAction.run(context);
    }

    @SuppressWarnings("nls")
    private Patient getPatient(String pnumber) {
        Criteria c = context.getSession()
            .createCriteria(Patient.class, "p")
            .createAlias("p.study", "s", Criteria.LEFT_JOIN)
            .add(Restrictions.eq("pnumber", pnumber));

        return (Patient) c.uniqueResult();
    }

    /*
     * Generates an action exception if patient does not exist.
     */
    private Patient loadPatient(String pnumber) {
        // make sure patient exists
        Patient p = getPatient(pnumber);
        if (p == null) {
            throw new ActionException(CSV_PATIENT_ERROR);
        }
        return p;
    }

    /*
     * Generates an action exception if specimen type does not exist.
     */
    @SuppressWarnings("nls")
    private SpecimenType loadSpecimenType(String name) {
        Criteria c = context.getSession()
            .createCriteria(SpecimenType.class, "st")
            .add(Restrictions.eq("name", name));

        SpecimenType specimenType = (SpecimenType) c.uniqueResult();
        if (specimenType == null) {
            throw new ActionException(CSV_SPECIMEN_TYPE_ERROR);
        }
        return specimenType;
    }

    /*
     * Generates an action exception if centre with name does not exist.
     */
    @SuppressWarnings("nls")
    private Center loadCenter(String name) {
        Criteria c = context.getSession()
            .createCriteria(Center.class, "c")
            .add(Restrictions.eq("pnumber", name));

        Center center = (Center) c.uniqueResult();
        if (center == null) {
            throw new ActionException(CSV_CURRENT_CENTER_ERROR);
        }

        return center;
    }

    /*
     * Generates an action exception if specimen with inventory ID does not
     * exist.
     */
    @SuppressWarnings("nls")
    private Specimen loadSpecimen(String inventoryId) {
        Criteria c = context.getSession()
            .createCriteria(Specimen.class, "s")
            .add(Restrictions.eq("inventoryId",
                inventoryId));

        Specimen specimen = (Specimen) c.uniqueResult();
        if (specimen == null) {
            throw new ActionException(CSV_PARENT_SPECIMEN_ERROR);
        }

        return specimen;
    }

    /*
     * Generates an action exception if container label does not exist.
     */
    @SuppressWarnings("nls")
    private Container loadContainer(String label) {
        Criteria c = context.getSession()
            .createCriteria(Container.class, "c")
            .add(Restrictions.eq("label", label));

        Container container = (Container) c.uniqueResult();
        if (container == null) {
            throw new ActionException(CSV_CONTAINER_LABEL_ERROR);
        }
        return container;
    }

}
