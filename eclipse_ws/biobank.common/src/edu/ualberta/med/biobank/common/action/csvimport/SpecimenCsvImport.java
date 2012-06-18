package edu.ualberta.med.biobank.common.action.csvimport;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.PermissionEnum;

/**
 * This action takes a CSV file as input and import the specimens contained in
 * the file.
 * 
 * @author loyola
 * 
 */
public class SpecimenCsvImport implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString CSV_PARSE_ERROR =
        bundle.tr("Parse error at line {0}\n{1}").format();

    public static class SpecimenCsvInfo {
        String inventoryId;
        String parentInventoryID;
        String specimenType;
        Date createAt;
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

        public Date getCreateAt() {
            return createAt;
        }

        public void setCreateAt(Date createAt) {
            this.createAt = createAt;
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
    }

    private boolean actionValid = false;

    public boolean setCsvFile(String filename) throws IOException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        final CellProcessor[] processors = new CellProcessor[] {
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
            null,
            null
        };

        try {
            ArrayList<SpecimenCsvInfo> specimenCsvInfos =
                new ArrayList<SpecimenCsvInfo>(0);

            final String[] header = reader.getCSVHeader(true);
            SpecimenCsvInfo specimenCsvInfo;
            while ((specimenCsvInfo =
                reader.read(SpecimenCsvInfo.class, header, processors)) != null) {
                specimenCsvInfos.add(specimenCsvInfo);
            }

            // zip the info into the buffer

            actionValid = true;

        } catch (SuperCSVException e) {
            throw new ActionException(CSV_PARSE_ERROR);
            // TODO: add parameters reader.getLineNumber() and
            // e.getCsvContext())) to this exception
        } finally {
            reader.close();
        }

        return actionValid;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.SPECIMEN_CSV_IMPORT.isAllowed(context.getUser());
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        // TODO Auto-generated method stub
        return null;
    }

}
