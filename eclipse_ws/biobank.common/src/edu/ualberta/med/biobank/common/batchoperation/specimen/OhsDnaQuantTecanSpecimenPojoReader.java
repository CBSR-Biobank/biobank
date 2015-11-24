package edu.ualberta.med.biobank.common.batchoperation.specimen;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.exception.SuperCSVReflectionException;
import org.supercsv.io.ICsvBeanReader;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.OhsTecanSpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.common.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.common.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.util.InventoryIdUtil;
import edu.ualberta.med.biobank.model.Center;

/**
 * Reads an OHS DNA Quantitation TECAN CSV file containing specimen information and returns the file
 * as a list of SpecimenBatchOpInputPojo.
 *
 * @author Brian Allen
 *
 */
public class OhsDnaQuantTecanSpecimenPojoReader implements
    IBatchOpPojoReader<SpecimenBatchOpInputPojo> {

    private static final I18n i18n = I18nFactory.getI18n(OhsDnaQuantTecanSpecimenPojoReader.class);

    @SuppressWarnings("nls")
    public static final String CSV_CONTAINER_BARCODE_MISSING_ERROR =
        i18n.tr("container barcode missing");

    @SuppressWarnings("nls")
    public static final String CSV_SAMPLE_ALIAS_MISSING_ERROR =
        i18n.tr("sample alias missing");

    @SuppressWarnings("nls")
    public static final String CSV_CONTAINER_POSITION_MISSING_ERROR =
        i18n.tr("container position missing");

    @SuppressWarnings("nls")
    public static final String CSV_VOLUME_MISSING_ERROR =
        i18n.tr("volume missing");

    @SuppressWarnings("nls")
    public static final String CSV_USERDEF_VALUE1_MISSING_ERROR =
        i18n.tr("userdef value1 missing");

    @SuppressWarnings("nls")
    public static final String CSV_USERDEF_VALUE2_MISSING_ERROR =
        i18n.tr("userdef value2 missing");

    @SuppressWarnings("nls")
    public static final String CSV_CONCENTRATION_ABS_MISSING_ERROR =
        i18n.tr("concentration abs missing");

    @SuppressWarnings("nls")
    public static final String CSV_CONCENTRATION_FLUOR_MISSING_ERROR =
        i18n.tr("concentration fluor missing");

    @SuppressWarnings("nls")
    public static final String CSV_BLANK_RATIO_260_OVER_230_MISSING_ERROR =
        i18n.tr("blank ratio 260/230 missing");

    @SuppressWarnings("nls")
    public static final String CSV_BLANK_RATIO_260_OVER_280_MISSING_ERROR =
        i18n.tr("blank ratio 260/280 missing");

    @SuppressWarnings("nls")
    public static final String CSV_CONTAINER_BARCODE_POSITION_FORMAT_ERROR =
        i18n.tr("container barcode or position not microplate format");

    @SuppressWarnings("nls")
    public static final String CSV_SPECIMEN_VOLUME_ERROR =
        i18n.tr("specimen volume has too many decimal places");

    @SuppressWarnings("nls")
    public static final String CSV_SPECIMEN_VOLUME_NEGATIVE_ERROR =
        i18n.tr("specimen volume negative");

    @SuppressWarnings("nls")
    public static final String CSV_TIME_STAMP_PARSE_ERROR =
        i18n.tr("timestamp format incorrect");

    @SuppressWarnings("nls")
    public static final String CSV_MULTIPLE_CONTAINER_BARCODES_ERROR =
        i18n.tr("multiple container barcodes in same csv file");

    @SuppressWarnings("nls")
    public static final String CSV_MULTIPLE_TECHNICIANS_ERROR =
        i18n.tr("multiple technicians in same csv file");

    @SuppressWarnings("nls")
    public static final String CSV_CONCENTRATION_ABS_ERROR =
        i18n.tr("specimen absorption concentration has too many decimal places");

    @SuppressWarnings("nls")
    public static final String CSV_CONCENTRATION_ABS_NEGATIVE_ERROR =
        i18n.tr("concentration abs negative");

    @SuppressWarnings("nls")
    public static final String CSV_CONCENTRATION_FLUOR_ERROR =
        i18n.tr("specimen fluorescence concentration has too many decimal places");

    @SuppressWarnings("nls")
    public static final String CSV_CONCENTRATION_FLUOR_NEGATIVE_ERROR =
        i18n.tr("concentration fluor negative");

    @SuppressWarnings("nls")
    public static final String CSV_BLANK_RATIO_260_OVER_230_ERROR =
        i18n.tr("OD 260/230 has too many decimal places");

    @SuppressWarnings("nls")
    public static final String CSV_BLANK_RATIO_260_OVER_230_NEGATIVE_ERROR =
        i18n.tr("blank ratio 260/230 negative");

    @SuppressWarnings("nls")
    public static final String CSV_BLANK_RATIO_260_OVER_280_ERROR =
        i18n.tr("OD 260/280 has too many decimal places");

    @SuppressWarnings("nls")
    public static final String CSV_BLANK_RATIO_260_OVER_280_NEGATIVE_ERROR =
        i18n.tr("blank ratio 260/280 negative");

    @SuppressWarnings("nls")
    private static final String CSV_FIRST_HEADER = "containerbarcode";

    @SuppressWarnings("nls")
    private static final String TYPE_DNA_QUANT = "DNA Quantified";

    @SuppressWarnings("nls")
    private static final SimpleDateFormat TIME_STAMP_FORMAT =
        new SimpleDateFormat("yyyyMMdd_HHmmss");

    private Set<SpecimenBatchOpInputPojo> aliquotSpecimens;
    private Set<SpecimenBatchOpInputPojo> sourceSpecimens;
    private String containerBarcode;
    private Date timestamp;
    private String technicianId;

    public static class TecanCsvRowPojo implements IBatchOpInputPojo {
        private static final long serialVersionUID = 1L;

        int lineNumber;
        String containerBarcode;
        String sampleAlias;
        String dontCare2;
        String containerPosition;
        String volume;
        String dontCare5;
        String dontCare6;
        String userdefValue1;
        String userdefValue2;
        String concentrationAbs;
        String concentrationFluor;
        String dontCare11;
        String dontCare12;
        String dontCare13;
        String blankRatio260Over230;
        String blankRatio260Over280;

        @Override
        public int getLineNumber() {
            return lineNumber;
        }

        @Override
        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getContainerBarcode() {
            return containerBarcode;
        }

        public void setContainerBarcode(String containerBarcode) {
            this.containerBarcode = containerBarcode;
        }

        public String getSampleAlias() {
            return sampleAlias;
        }

        public void setSampleAlias(String sampleAlias) {
            this.sampleAlias = sampleAlias;
        }

        public String getDontCare2() {
            return dontCare2;
        }

        public void setDontCare2(String dontCare2) {
            this.dontCare2 = dontCare2;
        }

        public String getContainerPosition() {
            return containerPosition;
        }

        public void setContainerPosition(String containerPosition) {
            this.containerPosition = containerPosition;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getDontCare5() {
            return dontCare5;
        }

        public void setDontCare5(String dontCare5) {
            this.dontCare5 = dontCare5;
        }

        public String getDontCare6() {
            return dontCare6;
        }

        public void setDontCare6(String dontCare6) {
            this.dontCare6 = dontCare6;
        }

        public String getUserdefValue1() {
            return userdefValue1;
        }

        public void setUserdefValue1(String userdefValue1) {
            this.userdefValue1 = userdefValue1;
        }

        public String getUserdefValue2() {
            return userdefValue2;
        }

        public void setUserdefValue2(String userdefValue2) {
            this.userdefValue2 = userdefValue2;
        }

        public String getConcentrationAbs() {
            return concentrationAbs;
        }

        public void setConcentrationAbs(String concentrationAbs) {
            this.concentrationAbs = concentrationAbs;
        }

        public String getConcentrationFluor() {
            return concentrationFluor;
        }

        public void setConcentrationFluor(String concentrationFluor) {
            this.concentrationFluor = concentrationFluor;
        }

        public String getDontCare11() {
            return dontCare11;
        }

        public void setDontCare11(String dontCare11) {
            this.dontCare11 = dontCare11;
        }

        public String getDontCare12() {
            return dontCare12;
        }

        public void setDontCare12(String dontCare12) {
            this.dontCare12 = dontCare12;
        }

        public String getDontCare13() {
            return dontCare13;
        }

        public void setDontCare13(String dontCare13) {
            this.dontCare13 = dontCare13;
        }

        public String getBlankRatio260Over230() {
            return blankRatio260Over230;
        }

        public void setBlankRatio260Over230(String blankRatio260Over230) {
            this.blankRatio260Over230 = blankRatio260Over230;
        }

        public String getBlankRatio260Over280() {
            return blankRatio260Over280;
        }

        public void setBlankRatio260Over280(String blankRatio260Over280) {
            this.blankRatio260Over280 = blankRatio260Over280;
        }
    }

    @SuppressWarnings("nls")
    private static final String[] NAME_MAPPINGS = new String[] {
        "containerBarcode",
        "sampleAlias",
        "dontCare2",
        "containerPosition",
        "volume",
        "dontCare5",
        "dontCare6",
        "userdefValue1",
        "userdefValue2",
        "concentrationAbs",
        "concentrationFluor",
        "dontCare11",
        "dontCare12",
        "dontCare13",
        "blankRatio260Over230",
        "blankRatio260Over280"
    };

    private final Center workingCenter;

    private final String filename;

    private final ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    public OhsDnaQuantTecanSpecimenPojoReader(Center workingCenter, String filename) {
        this.workingCenter = workingCenter;
        this.filename = filename;
    }

    // cell processors have to be recreated every time the file is read
    @SuppressWarnings("nls")
    public CellProcessor[] getCellProcessors() {

        Map<String, CellProcessor> aMap =
            new LinkedHashMap<String, CellProcessor>();

        aMap.put("containerBarcode", null);
        aMap.put("sampleAlias", null);
        aMap.put("dontCare2", null);
        aMap.put("containerPosition", null);
        aMap.put("volume", null);
        aMap.put("dontCare5", null);
        aMap.put("dontCare6", null);
        aMap.put("userdefValue1", null);
        aMap.put("userdefValue2", null);
        aMap.put("concentrationAbs", null);
        aMap.put("concentrationFluor", null);
        aMap.put("dontCare11", null);
        aMap.put("dontCare12", null);
        aMap.put("dontCare13", null);
        aMap.put("blankRatio260Over230", null);
        aMap.put("blankRatio260Over280", null);

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

    @SuppressWarnings("nls")
    public static boolean isHeaderValid(String[] csvHeaders) {
        if (csvHeaders == null) {
            throw new NullPointerException("csvHeaders is null");
        }
        return csvHeaders[0].equals(CSV_FIRST_HEADER)
            && csvHeaders.length == NAME_MAPPINGS.length;
    }

    @SuppressWarnings("nls")
    @Override
    public Set<SpecimenBatchOpInputPojo> readPojos(ICsvBeanReader reader)
        throws ClientBatchOpErrorsException, IOException {

        aliquotSpecimens = new LinkedHashSet<SpecimenBatchOpInputPojo>(0);
        sourceSpecimens = new LinkedHashSet<SpecimenBatchOpInputPojo>(0);
        containerBarcode = null;
        timestamp = new Date();
        technicianId = null;

        if (reader == null) {
            throw new IllegalStateException("CSV reader is null");
        }

        CellProcessor[] cellProcessors = getCellProcessors();

        TecanCsvRowPojo csvPojo;

        try {
            while ((csvPojo =
                reader.read(TecanCsvRowPojo.class,
                    NAME_MAPPINGS, cellProcessors)) != null) {

                csvPojo.lineNumber = reader.getLineNumber();

                SpecimenBatchOpInputPojo batchOpPojo =
                    convertToSpecimenBatchOpInputPojo(reader.getLineNumber(),
                        csvPojo);

                if (batchOpPojo == null) {
                    // pojo could not be converted, ignore this row
                    continue;
                }

                // container barcode
                if (containerBarcode == null) {
                    containerBarcode = csvPojo.containerBarcode;
                }
                else if (!containerBarcode.equals(csvPojo.containerBarcode)) {
                    getErrorList().addError(reader.getLineNumber(),
                        CSV_MULTIPLE_CONTAINER_BARCODES_ERROR);
                }

                // technician ID for processing event
                if (technicianId == null) {
                    technicianId = csvPojo.userdefValue2;
                }
                else if (!technicianId.equals(csvPojo.userdefValue2)) {
                    getErrorList().addError(reader.getLineNumber(),
                        CSV_MULTIPLE_TECHNICIANS_ERROR);
                }

                // deal with source specimens

                boolean sourceSpecimenAlreadyEncountered = false;
                for (SpecimenBatchOpInputPojo sPojo : sourceSpecimens) {
                    if (sPojo.getInventoryId().equals(csvPojo.sampleAlias)) {
                        sourceSpecimenAlreadyEncountered = true;
                        break;
                    }
                }
                if (!sourceSpecimenAlreadyEncountered) {
                    SpecimenBatchOpInputPojo sourcePojo =
                        new SpecimenBatchOpInputPojo();
                    sourcePojo.setInventoryId(csvPojo.sampleAlias);
                    sourceSpecimens.add(sourcePojo);
                }

                if (batchOpPojo.getCreatedAt().before(timestamp)) {
                    timestamp = batchOpPojo.getCreatedAt();
                }

                aliquotSpecimens.add(batchOpPojo);
            }

            if (aliquotSpecimens.size() == 0) {
                throw new IllegalStateException(
                    "no aliquots to be imported");
            }

            return aliquotSpecimens;
        } catch (SuperCSVReflectionException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (SuperCSVException e) {
            throw new ClientBatchOpErrorsException(e);
        }
    }

    @SuppressWarnings("nls")
    private SpecimenBatchOpInputPojo convertToSpecimenBatchOpInputPojo(
        int linenumber, TecanCsvRowPojo csvPojo) {
        SpecimenBatchOpInputPojo batchOpPojo = new SpecimenBatchOpInputPojo();
        batchOpPojo.setLineNumber(csvPojo.lineNumber);

        // check for missing column values
        if (csvPojo.containerBarcode == null || csvPojo.containerBarcode.isEmpty()) {
            getErrorList()
                .addError(linenumber, CSV_CONTAINER_BARCODE_MISSING_ERROR);
            return null;
        }
        if (csvPojo.sampleAlias == null || csvPojo.sampleAlias.isEmpty()) {
            getErrorList().addError(linenumber, CSV_SAMPLE_ALIAS_MISSING_ERROR);
            return null;
        }
        if (csvPojo.containerPosition == null || csvPojo.containerPosition.isEmpty()) {
            getErrorList().addError(linenumber, CSV_CONTAINER_POSITION_MISSING_ERROR);
            return null;
        }
        if (csvPojo.volume == null || csvPojo.volume.isEmpty()) {
            getErrorList()
                .addError(linenumber, CSV_VOLUME_MISSING_ERROR);
            return null;
        }
        if (csvPojo.userdefValue1 == null || csvPojo.userdefValue1.isEmpty()) {
            getErrorList()
                .addError(linenumber, CSV_USERDEF_VALUE1_MISSING_ERROR);
            return null;
        }
        if (csvPojo.userdefValue2 == null || csvPojo.userdefValue2.isEmpty()) {
            getErrorList()
                .addError(linenumber, CSV_USERDEF_VALUE2_MISSING_ERROR);
            return null;
        }
        if (csvPojo.concentrationAbs == null || csvPojo.concentrationAbs.isEmpty()) {
            getErrorList()
                .addError(linenumber, CSV_CONCENTRATION_ABS_MISSING_ERROR);
            return null;
        }
        if (csvPojo.concentrationFluor == null || csvPojo.concentrationFluor.isEmpty()) {
            getErrorList()
                .addError(linenumber, CSV_CONCENTRATION_FLUOR_MISSING_ERROR);
            return null;
        }
        if (csvPojo.blankRatio260Over230 == null || csvPojo.blankRatio260Over230.isEmpty()) {
            getErrorList()
                .addError(linenumber, CSV_BLANK_RATIO_260_OVER_230_MISSING_ERROR);
            return null;
        }
        if (csvPojo.blankRatio260Over280 == null || csvPojo.blankRatio260Over280.isEmpty()) {
            getErrorList()
                .addError(linenumber, CSV_BLANK_RATIO_260_OVER_280_MISSING_ERROR);
            return null;
        }

        // process other columns

        // deal with specimen type
        batchOpPojo.setSpecimenType(TYPE_DNA_QUANT);

        // deal with inventory ID
        String position = csvPojo.containerPosition;
        if (csvPojo.containerPosition.matches("\\D0\\d"))
            position =
                csvPojo.containerPosition.substring(0, 1).concat(csvPojo.containerPosition.substring(2, 3));
        batchOpPojo.setInventoryId(InventoryIdUtil.formatMicroplatePosition(csvPojo.containerBarcode, position));
        if (batchOpPojo.getInventoryId() == null) {
            getErrorList()
                .addError(linenumber, CSV_CONTAINER_BARCODE_POSITION_FORMAT_ERROR);
            return null;
        }

        // deal with source ID
        batchOpPojo.setParentInventoryId(csvPojo.sampleAlias);

        // deal with aliquot volume
        if (csvPojo.volume.lastIndexOf('.') != -1
            &&
            csvPojo.volume.length()
                - csvPojo.volume.lastIndexOf('.') > 7) {
            getErrorList().addError(linenumber, CSV_SPECIMEN_VOLUME_ERROR);
            return null;
        }
        batchOpPojo.setVolume(new BigDecimal(csvPojo.volume)
            .divide(new BigDecimal(1000)));
        if (batchOpPojo.getVolume().signum() == -1) {
            getErrorList()
                .addError(linenumber, CSV_SPECIMEN_VOLUME_NEGATIVE_ERROR);
            return null;
        }

        // deal with timestamp
        String timeStamp = csvPojo.userdefValue1;
        try {
            TIME_STAMP_FORMAT.setLenient(false);
            batchOpPojo.setCreatedAt(TIME_STAMP_FORMAT.parse(timeStamp));
        } catch (ParseException pe) {
            getErrorList().addError(linenumber, CSV_TIME_STAMP_PARSE_ERROR);
            return null;
        }

        // deal with absorption concentration
        if (csvPojo.concentrationAbs.lastIndexOf('.') != -1
            &&
            csvPojo.concentrationAbs.length()
                - csvPojo.concentrationAbs.lastIndexOf('.') > 7) {
            getErrorList().addError(linenumber, CSV_CONCENTRATION_ABS_ERROR);
            return null;
        }
        batchOpPojo.setConcentrationAbs(new BigDecimal(csvPojo.concentrationAbs));
        if (batchOpPojo.getConcentrationAbs().signum() == -1) {
            getErrorList()
                .addError(linenumber, CSV_CONCENTRATION_ABS_NEGATIVE_ERROR);
            return null;
        }

        // deal with fluorescence concentration
        if (csvPojo.concentrationFluor.lastIndexOf('.') != -1
            &&
            csvPojo.concentrationFluor.length()
                - csvPojo.concentrationFluor.lastIndexOf('.') > 7) {
            getErrorList().addError(linenumber, CSV_CONCENTRATION_FLUOR_ERROR);
            return null;
        }
        batchOpPojo.setConcentrationFluor(new BigDecimal(csvPojo.concentrationFluor));
        if (batchOpPojo.getConcentrationFluor().signum() == -1) {
            getErrorList()
                .addError(linenumber, CSV_CONCENTRATION_FLUOR_NEGATIVE_ERROR);
            return null;
        }

        // deal with optical density OD 260/230
        if (csvPojo.blankRatio260Over230.lastIndexOf('.') != -1
            &&
            csvPojo.blankRatio260Over230.length()
                - csvPojo.blankRatio260Over230.lastIndexOf('.') > 7) {
            getErrorList().addError(linenumber, CSV_BLANK_RATIO_260_OVER_230_ERROR);
            return null;
        }
        batchOpPojo.setOd260Over230(new BigDecimal(csvPojo.blankRatio260Over230));
        if (batchOpPojo.getOd260Over230().signum() == -1) {
            getErrorList()
                .addError(linenumber, CSV_BLANK_RATIO_260_OVER_230_NEGATIVE_ERROR);
            return null;
        }

        // deal with optical density OD 260/280
        if (csvPojo.blankRatio260Over280.lastIndexOf('.') != -1
            &&
            csvPojo.blankRatio260Over280.length()
                - csvPojo.blankRatio260Over280.lastIndexOf('.') > 7) {
            getErrorList().addError(linenumber, CSV_BLANK_RATIO_260_OVER_280_ERROR);
            return null;
        }
        batchOpPojo.setOd260Over280(new BigDecimal(csvPojo.blankRatio260Over280));
        if (batchOpPojo.getOd260Over280().signum() == -1) {
            getErrorList()
                .addError(linenumber, CSV_BLANK_RATIO_260_OVER_280_NEGATIVE_ERROR);
            return null;
        }

        // deal with aliquot yield
        batchOpPojo.setAliquotYield(batchOpPojo.getConcentrationFluor().multiply(batchOpPojo.getVolume()));

        // deal with plate errors
        batchOpPojo.setPlateErrors(null);

        // deal with sample errors
        batchOpPojo.setSamplEerrors(null);

        return batchOpPojo;
    }

    @Override
    public Action<IdResult> getAction() throws NoSuchAlgorithmException,
        IOException {
        return new OhsTecanSpecimenBatchOpAction(workingCenter, aliquotSpecimens,
            new File(filename), sourceSpecimens,
            new File(filename).getName(),
            timestamp,
            technicianId,
            true);
    }

}
