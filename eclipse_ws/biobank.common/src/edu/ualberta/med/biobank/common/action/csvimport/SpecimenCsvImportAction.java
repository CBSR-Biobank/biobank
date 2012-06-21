package edu.ualberta.med.biobank.common.action.csvimport;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenInfo;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
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

    public static class SpecimenCsvInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        String inventoryId;
        String parentInventoryID;
        String specimenType;
        Date createdAt;
        private String studyName;
        String patientNumber;
        Integer visitNumber;
        String currentCenter;
        String originCenter;
        Boolean sourceSpecimen;
        String worksheet;
        String rootContainerType;
        String palletLabel;
        String palletPosition;

        public String getInventoryId() {
            return inventoryId;
        }

        public void setInventoryId(String inventoryId) {
            this.inventoryId = inventoryId;
        }

        public String getParentInventoryID() {
            return parentInventoryID;
        }

        public void setParentInventoryID(String parentInventoryID) {
            this.parentInventoryID = parentInventoryID;
        }

        public String getSpecimenType() {
            return specimenType;
        }

        public void setSpecimenType(String specimenType) {
            this.specimenType = specimenType;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createAt) {
            this.createdAt = createAt;
        }

        public String getPatientNumber() {
            return patientNumber;
        }

        public void setPatientNumber(String patientNumber) {
            this.patientNumber = patientNumber;
        }

        public Integer getVisitNumber() {
            return visitNumber;
        }

        public void setVisitNumber(Integer visitNumber) {
            this.visitNumber = visitNumber;
        }

        public String getCurrentCenter() {
            return currentCenter;
        }

        public void setCurrentCenter(String currentCenter) {
            this.currentCenter = currentCenter;
        }

        public String getOriginCenter() {
            return originCenter;
        }

        public void setOriginCenter(String originCenter) {
            this.originCenter = originCenter;
        }

        public Boolean getSourceSpecimen() {
            return sourceSpecimen;
        }

        public void setSourceSpecimen(Boolean sourceSpecimen) {
            this.sourceSpecimen = sourceSpecimen;
        }

        public String getWorksheet() {
            return worksheet;
        }

        public void setWorksheet(String worksheet) {
            this.worksheet = worksheet;
        }

        public String getRootContainerType() {
            return rootContainerType;
        }

        public void setRootContainerType(String rootContainerType) {
            this.rootContainerType = rootContainerType;
        }

        public String getPalletLabel() {
            return palletLabel;
        }

        public void setPalletLabel(String palletLabel) {
            this.palletLabel = palletLabel;
        }

        public String getPalletPosition() {
            return palletPosition;
        }

        public void setPalletPosition(String palletPosition) {
            this.palletPosition = palletPosition;
        }

        public String getStudyName() {
            return studyName;
        }

        public void setStudyName(String studyName) {
            this.studyName = studyName;
        }
    }

    @SuppressWarnings("nls")
    private static final CellProcessor[] PROCESSORS = new CellProcessor[] {
        new Unique(),
        null,
        null,
        new ParseDate("yyyy-MM-dd HH:mm"),
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

    public SpecimenCsvImportAction(String filename) throws IOException {
        setCsvFile(filename);
    }

    private ActionContext context = null;

    @SuppressWarnings("nls")
    private boolean setCsvFile(String filename) throws IOException {
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

        return (compressedList != null);
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.SPECIMEN_CSV_IMPORT.isAllowed(context.getUser());
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

            } else {
                addAliquotedSpecimen(csvInfo);
            }
        }

        result = true;
        return new BooleanResult(result);
    }

    @SuppressWarnings("nls")
    private void addAliquotedSpecimen(SpecimenCsvInfo csvInfo) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
        }

        // make sure patient exists
        Patient p =
            getPatient(csvInfo.getPatientNumber(), csvInfo.getStudyName());
        if (p == null) {
            throw new ActionException(CSV_PATIENT_ERROR);
        }

        Criteria c = context.getSession()
            .createCriteria(Specimen.class, "s")
            .add(Restrictions.eq("inventoryId",
                csvInfo.getParentInventoryID()));

        Specimen parentSpecimen = (Specimen) c.uniqueResult();
        if (parentSpecimen == null) {
            throw new ActionException(CSV_PARENT_SPECIMEN_ERROR);
        }

        c = context.getSession()
            .createCriteria(Center.class, "c")
            .add(Restrictions.eq("pnumber", csvInfo.getCurrentCenter()));

        Center currentCenter = (Center) c.uniqueResult();
        if (currentCenter == null) {
            throw new ActionException(CSV_CURRENT_CENTER_ERROR);
        }

        c = context.getSession()
            .createCriteria(SpecimenType.class, "st")
            .add(Restrictions.eq("name", csvInfo.getSpecimenType()));

        SpecimenType specimenType = (SpecimenType) c.uniqueResult();
        if (specimenType == null) {
            throw new ActionException(CSV_SPECIMEN_TYPE_ERROR);
        }

        AliquotedSpecimenInfo specimenInfo = new AliquotedSpecimenInfo();
        specimenInfo.inventoryId = csvInfo.getInventoryId();
        specimenInfo.typeId = specimenType.getId();
        specimenInfo.activityStatus = ActivityStatus.ACTIVE;
        specimenInfo.parentSpecimenId = parentSpecimen.getId();

        String palletLabel = csvInfo.getPalletLabel();
        String palletPosition = csvInfo.getPalletPosition();

        if ((palletLabel != null) && (palletPosition != null)) {
            c = context.getSession()
                .createCriteria(Container.class, "c")
                .add(Restrictions.eq("label", csvInfo.getPalletLabel()));

            Container container = (Container) c.uniqueResult();
            if (container == null) {
                throw new ActionException(CSV_CONTAINER_LABEL_ERROR);
            }
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
    private Patient getPatient(String pnumber, String studyNameShort) {
        Criteria c = context.getSession()
            .createCriteria(Patient.class, "p")
            .createAlias("p.study", "s", Criteria.LEFT_JOIN)
            .add(Restrictions.eq("pnumber", pnumber))
            .add(Restrictions.eq("s.nameShort", studyNameShort));

        return (Patient) c.uniqueResult();
    }

}
