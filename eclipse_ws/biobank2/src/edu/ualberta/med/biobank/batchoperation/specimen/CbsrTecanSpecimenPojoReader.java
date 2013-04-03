package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.exception.SuperCSVReflectionException;
import org.supercsv.io.ICsvBeanReader;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.model.Center;

/**
 * Reads a TECAN CSV file containing specimen information and returns the file as a list of
 * SpecimenBatchOpInputPojo.
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class CbsrTecanSpecimenPojoReader implements
    IBatchOpPojoReader<SpecimenBatchOpInputPojo> {
    private static final I18n i18n = I18nFactory
        .getI18n(CbsrTecanSpecimenPojoReader.class);

    private static final String CSV_FIRST_HEADER = "Rack ID";

    public static final String CSV_PROCESSED_DATE_TIME_PARSE_ERROR =
        i18n.tr("invalid date and time format");

    private static final String INVALID_TUBE_1D_BC = "NoReadBC1";

    private static final String PROCESSED_DATE_TIME_PREFIX = "Processed_";

    private static final SimpleDateFormat PROCESSED_DATE_TIME_FORMAT =
        new SimpleDateFormat("ddMMyyy_HHmmss");

    public static class TecanCsvRowPojo implements IBatchOpInputPojo {
        private static final long serialVersionUID = 1L;

        int lineNumber;
        String rackId;
        String cavityId;
        String position;
        String sourceId;
        String concentration;
        String concentrationUnit;
        Integer volume;
        String tube1dBarcode;
        String processedDateTime;
        String scriptNameAndUser;
        String sampleType;
        String worksheet;
        String plateErrors;
        String sampleErrors;
        String sampelInstanceId;
        String sampleId;

        @Override
        public int getLineNumber() {
            return lineNumber;
        }

        @Override
        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getRackId() {
            return rackId;
        }

        public void setRackId(String rackId) {
            this.rackId = rackId;
        }

        public String getCavityId() {
            return cavityId;
        }

        public void setCavityId(String cavityId) {
            this.cavityId = cavityId;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public String getConcentration() {
            return concentration;
        }

        public void setConcentration(String concentration) {
            this.concentration = concentration;
        }

        public String getConcentrationUnit() {
            return concentrationUnit;
        }

        public void setConcentrationUnit(String concentrationUnit) {
            this.concentrationUnit = concentrationUnit;
        }

        public Integer getVolume() {
            return volume;
        }

        public void setVolume(Integer volume) {
            this.volume = volume;
        }

        public String getTube1dBarcode() {
            return tube1dBarcode;
        }

        public void setTube1dBarcode(String tube1dBarcode) {
            this.tube1dBarcode = tube1dBarcode;
        }

        public String getProcessedDateTime() {
            return processedDateTime;
        }

        public void setProcessedDateTime(String processedDateTime) {
            this.processedDateTime = processedDateTime;
        }

        public String getScriptNameAndUser() {
            return scriptNameAndUser;
        }

        public void setScriptNameAndUser(String scriptNameAndUser) {
            this.scriptNameAndUser = scriptNameAndUser;
        }

        public String getSampleType() {
            return sampleType;
        }

        public void setSampleType(String sampleType) {
            this.sampleType = sampleType;
        }

        public String getWorksheet() {
            return worksheet;
        }

        public void setWorksheet(String worksheet) {
            this.worksheet = worksheet;
        }

        public String getPlateErrors() {
            return plateErrors;
        }

        public void setPlateErrors(String plateErrors) {
            this.plateErrors = plateErrors;
        }

        public String getSampleErrors() {
            return sampleErrors;
        }

        public void setSampleErrors(String sampleErrors) {
            this.sampleErrors = sampleErrors;
        }

        public String getSampelInstanceId() {
            return sampelInstanceId;
        }

        public void setSampelInstanceId(String sampelInstanceId) {
            this.sampelInstanceId = sampelInstanceId;
        }

        public String getSampleId() {
            return sampleId;
        }

        public void setSampleId(String sampleId) {
            this.sampleId = sampleId;
        }
    }

    private static final String[] NAME_MAPPINGS = new String[] {
        "rackId",
        "cavityId",
        "position",
        "sourceId",
        "concentration",
        "concentrationUnit",
        "volume",
        "tube1dBarcode",
        "processedDateTime",
        "scriptNameAndUser",
        "sampleType",
        "worksheet",
        "plateErrors",
        "sampleErrors",
        "sampelInstanceId",
        "sampleId"
    };

    private final Center workingCenter;

    private final String filename;

    Set<SpecimenBatchOpInputPojo> convertedPojos =
        new HashSet<SpecimenBatchOpInputPojo>();

    private final ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    public CbsrTecanSpecimenPojoReader(Center workingCenter, String filename) {
        this.workingCenter = workingCenter;
        this.filename = filename;
    }

    // cell processors have to be recreated every time the file is read
    public CellProcessor[] getCellProcessors() {
        Map<String, CellProcessor> aMap =
            new LinkedHashMap<String, CellProcessor>();

        aMap.put("rackId", null);
        aMap.put("cavityId", new Unique());
        aMap.put("position", null);
        aMap.put("sourceId", null);
        aMap.put("concentration", null);
        aMap.put("concentrationUnit", null);
        aMap.put("volume", new ParseInt());
        aMap.put("tube1dBarcode", null);
        aMap.put("processedDateTime", null);
        aMap.put("scriptNameAndUser", null);
        aMap.put("sampleType", null);
        aMap.put("worksheet", null);
        aMap.put("plateErrors", null);
        aMap.put("sampleErrors", null);
        aMap.put("sampelInstanceId", null);
        aMap.put("sampleId", null);

        if (aMap.size() != NAME_MAPPINGS.length) {
            throw new IllegalStateException(
                "the number of name mappings do not match the cell processors");
        }

        return aMap.values().toArray(new CellProcessor[0]);
    }

    @Override
    public ClientBatchOpInputErrorList getErrorList() {
        return errorList;
    }

    public static boolean isHeaderValid(String[] csvHeaders) {
        if (csvHeaders == null) {
            throw new NullPointerException("csvHeaders is null");
        }
        return csvHeaders[0].equals(CSV_FIRST_HEADER)
            && (csvHeaders.length == NAME_MAPPINGS.length);
    }

    @Override
    public Set<SpecimenBatchOpInputPojo> readPojos(ICsvBeanReader reader)
        throws ClientBatchOpErrorsException, IOException {
        if (reader == null) {
            throw new NullPointerException("CSV reader is null");
        }

        CellProcessor[] cellProcessors = getCellProcessors();

        TecanCsvRowPojo csvPojo;

        try {
            while ((csvPojo =
                reader.read(TecanCsvRowPojo.class,
                    NAME_MAPPINGS, cellProcessors)) != null) {

                if (csvPojo.getSourceId().isEmpty()) {
                    // this row not processed by TECAN robot
                    continue;
                }

                csvPojo.setLineNumber(reader.getLineNumber());

                SpecimenBatchOpInputPojo batchOpPojo =
                    convertToSpecimenBatchOpInputPojo(csvPojo);

                if (batchOpPojo == null) {
                    // pojo could not be converted, ignore this row
                    continue;
                }

                convertedPojos.add(batchOpPojo);
            }
            return convertedPojos;
        } catch (SuperCSVReflectionException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (SuperCSVException e) {
            throw new ClientBatchOpErrorsException(e);
        }
    }

    private SpecimenBatchOpInputPojo convertToSpecimenBatchOpInputPojo(
        TecanCsvRowPojo csvPojo) {
        Date createdAt;
        String processedDateTime = csvPojo.getProcessedDateTime().replace(
            PROCESSED_DATE_TIME_PREFIX, "");
        try {
            createdAt = PROCESSED_DATE_TIME_FORMAT.parse(processedDateTime);
        } catch (ParseException e) {
            getErrorList().addError(csvPojo.getLineNumber(),
                CSV_PROCESSED_DATE_TIME_PARSE_ERROR);
            return null;
        }

        SpecimenBatchOpInputPojo batchOpPojo = new SpecimenBatchOpInputPojo();

        batchOpPojo.setLineNumber(csvPojo.getLineNumber());
        batchOpPojo.setInventoryId(csvPojo.getCavityId());
        batchOpPojo.setParentInventoryId(csvPojo.getSourceId());
        batchOpPojo.setSpecimenType(csvPojo.getSampleType());
        batchOpPojo.setVolume(new BigDecimal(csvPojo.getVolume())
            .divide(new BigDecimal(1000)));
        batchOpPojo.setPatientNumber(csvPojo.getTube1dBarcode());
        batchOpPojo.setCreatedAt(createdAt);
        batchOpPojo.setPlateErrors(csvPojo.getPlateErrors());
        batchOpPojo.setSamplEerrors(csvPojo.getSampleErrors());

        if (!csvPojo.getTube1dBarcode().equals(INVALID_TUBE_1D_BC)) {
            batchOpPojo.setPatientNumber(csvPojo.getTube1dBarcode());
        }

        return batchOpPojo;
    }

    @Override
    public Action<IdResult> getAction() throws NoSuchAlgorithmException,
        IOException {
        return new SpecimenBatchOpAction(workingCenter, convertedPojos,
            new File(filename));
    }
}
