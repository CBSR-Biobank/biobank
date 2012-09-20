package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
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
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;

/**
 * Reads a CSV file containing specimen information and returns the file as a
 * list of SpecimenBatchOpInputPojo.
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class SpecimenBatchOpPojoReader implements
    IBatchOpPojoReader<SpecimenBatchOpInputPojo> {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenBatchOpPojoReader.class);

    public static final String CSV_PARENT_SPC_ERROR =
        i18n.tr("specimen declared a source specimen but parent " +
            "inventory ID present");

    public static final String CSV_SPC_PATIENT_ERROR =
        i18n.tr("parent specimen and child specimen "
            + "do not have the same patient number");

    public static final String CSV_SRC_SPC_PATIENT_CEVENT_MISSING_ERROR =
        i18n.tr("both patient number and visit number must be specified");

    public static final String CSV_ALIQ_SPC_PATIENT_CEVENT_MISSING_ERROR =
        i18n.tr("one of the following must be specified: parent inventory id "
            + "or patient number and visit number");

    public static final String CSV_PALLET_POS_ERROR =
        i18n.tr("pallet position defined but not product barcode or label");

    public static final String CSV_PROD_BARCODE_NO_POS_ERROR =
        i18n.tr("pallet product barcode defined but not position");

    public static final String CSV_PALLET_LABEL_NO_POS_ERROR =
        i18n.tr("pallet label defined but not position");

    public static final String CSV_PALLET_LABEL_NO_CTYPE_ERROR =
        i18n.tr("pallet label defined but not position");

    public static final String CSV_SRC_SPECIMEN_WORKSHEET_ERROR =
        i18n.tr("worksheet should not be specified for a source specimen");

    private static final String CSV_FIRST_HEADER = "Inventory ID";

    private static final String[] NAME_MAPPINGS;

    private static final CellProcessor[] CELL_PROCESSORS;

    static {
        Map<String, CellProcessor> aMap =
            new LinkedHashMap<String, CellProcessor>();

        aMap.put("inventoryId", new Unique());
        aMap.put("parentInventoryId", new Optional());
        aMap.put("volume", new ParseBigDecimal());
        aMap.put("specimenType", new StrNotNullOrEmpty());
        aMap.put("createdAt", new ParseDate("yyyy-MM-dd HH:mm"));
        aMap.put("patientNumber", new StrNotNullOrEmpty());
        aMap.put("visitNumber", new Optional(new ParseInt()));
        aMap.put("waybill", new Optional());
        aMap.put("sourceSpecimen", new ParseBool());
        aMap.put("worksheet", new Optional(new Unique()));
        aMap.put("palletProductBarcode", new Optional());
        aMap.put("rootContainerType", new Optional());
        aMap.put("palletLabel", new Optional());
        aMap.put("palletPosition", new Optional());
        aMap.put("comment", new Optional());

        NAME_MAPPINGS = aMap.keySet().toArray(new String[0]);
        CELL_PROCESSORS = aMap.values().toArray(new CellProcessor[0]);
    }

    private ICsvBeanReader reader;

    private final ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    private final List<SpecimenBatchOpInputPojo> csvInfos =
        new ArrayList<SpecimenBatchOpInputPojo>(0);

    public static boolean isHeaderValid(String[] csvHeaders) {
        return csvHeaders[0].equals(CSV_FIRST_HEADER)
            && (csvHeaders.length == NAME_MAPPINGS.length);
    }

    @Override
    public List<SpecimenBatchOpInputPojo> getPojos()
        throws ClientBatchOpErrorsException {
        final Map<String, SpecimenBatchOpInputPojo> parentSpcMap =
            new HashMap<String, SpecimenBatchOpInputPojo>();

        SpecimenBatchOpInputPojo csvPojo;

        try {
            while ((csvPojo =
                reader.read(SpecimenBatchOpInputPojo.class,
                    NAME_MAPPINGS, CELL_PROCESSORS)) != null) {

                if (csvPojo.getSourceSpecimen()) {
                    if (csvPojo.getParentInventoryId().isEmpty()) {
                        getErrorList().addError(reader.getLineNumber(),
                            CSV_PARENT_SPC_ERROR);
                    } else if (csvPojo.hasPatientAndCollectionEvent()) {
                        // does not have patient number
                        getErrorList().addError(reader.getLineNumber(),
                            CSV_SRC_SPC_PATIENT_CEVENT_MISSING_ERROR);
                    }

                    if (!csvPojo.getWorksheet().isEmpty()) {
                        getErrorList().addError(reader.getLineNumber(),
                            CSV_SRC_SPECIMEN_WORKSHEET_ERROR);
                    }

                    parentSpcMap.put(csvPojo.getInventoryId(), csvPojo);
                } else {
                    // this is an aliquoted specimen

                    if (csvPojo.getParentInventoryId().isEmpty()) {
                        // check that parent and child specimens have the same
                        // patient number
                        SpecimenBatchOpInputPojo parentCsvInfo =
                            parentSpcMap.get(csvPojo.getParentInventoryId());

                        if ((parentCsvInfo != null)
                            && !csvPojo.getPatientNumber().equals(
                                parentCsvInfo.getPatientNumber())) {
                            getErrorList().addError(reader.getLineNumber(),
                                CSV_SPC_PATIENT_ERROR);
                        }
                    } else if (!csvPojo.hasPatientAndCollectionEvent()) {
                        // no parent inventory id and does not have patient
                        // number and visit number
                        getErrorList().addError(reader.getLineNumber(),
                            CSV_ALIQ_SPC_PATIENT_CEVENT_MISSING_ERROR);
                    }
                }

                // check if only position defined and no label and no product
                // barcode
                if ((csvPojo.getPalletProductBarcode() == null)
                    && (csvPojo.getPalletLabel() == null)
                    && (csvPojo.getPalletPosition() != null)) {
                    getErrorList().addError(reader.getLineNumber(),
                        CSV_PALLET_POS_ERROR);
                }

                //
                if ((csvPojo.getPalletProductBarcode() != null)
                    && (csvPojo.getPalletPosition() == null)) {
                    getErrorList().addError(reader.getLineNumber(),
                        CSV_PROD_BARCODE_NO_POS_ERROR);
                }

                if ((csvPojo.getPalletLabel() != null)
                    && (csvPojo.getPalletPosition() == null)) {
                    getErrorList().addError(reader.getLineNumber(),
                        CSV_PALLET_POS_ERROR);
                }

                if ((csvPojo.getPalletLabel() != null)
                    && (csvPojo.getRootContainerType() == null)) {
                    getErrorList().addError(reader.getLineNumber(),
                        CSV_PALLET_LABEL_NO_CTYPE_ERROR);
                }

                csvPojo.setLineNumber(reader.getLineNumber());
                csvInfos.add(csvPojo);
            }

            return csvInfos;
        } catch (SuperCSVReflectionException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (SuperCSVException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (IOException e) {
            throw new ClientBatchOpErrorsException(e);
        }
    }

    @Override
    public void setReader(ICsvBeanReader reader) {
        this.reader = reader;
    }

    @Override
    public ClientBatchOpInputErrorList getErrorList() {
        return errorList;
    }

    @Override
    public void preExecution() {
        // does nothing
    }

    @Override
    public void postExecution() {
        // does nothing
    }
}
