package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.Optional;
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

import edu.ualberta.med.biobank.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.forms.DecodeImageForm;

public class LegacyImportSpecimenPojoReader {
    private static final I18n i18n = I18nFactory
        .getI18n(DecodeImageForm.class);

    public static final String CSV_PARENT_SPC_ERROR =
        i18n.tr("specimen is a source specimen but parent " +
            "inventory ID present");

    public static final String CSV_SPC_PATIENT_ERROR =
        i18n.tr("parent specimen and child specimen "
            + "do not have the same patient number");

    public static final String CSV_PALLET_POS_ERROR =
        i18n.tr("pallet position defined but not product barcode or label");

    public static final String CSV_PROD_BARCODE_NO_POS_ERROR =
        i18n.tr("pallet product barcode defined but not position");

    public static final String CSV_PALLET_LABEL_NO_POS_ERROR =
        i18n.tr("pallet label defined but not position");

    public static final String CSV_PALLET_LABEL_NO_CTYPE_ERROR =
        i18n.tr("pallet label defined but not position");

    private static final String CSV_FIRST_HEADER = "Inventory ID";

    private static final String[] NAME_MAPPINGS = new String[] {
        "inventoryId",
        "parentInventoryId",
        "volume",
        "specimenType",
        "createdAt",
        "patientNumber",
        "visitNumber",
        "waybill",
        "sourceSpecimen",
        "worksheet",
        "palletProductBarcode",
        "rootContainerType",
        "palletLabel",
        "palletPosition",
        "comment"
    };

    // @formatter:off
    private static final CellProcessor[] CELL_PROCESSORS =  new CellProcessor[] {
        new Unique(),                       // inventoryId,
        new Optional(),                     // parentInventoryID,
        new StrNotNullOrEmpty(),            // specimenType,
        new ParseDate("yyyy-MM-dd HH:mm"),  // createdAt,
        new StrNotNullOrEmpty(),            // patientNumber,
        new ParseInt(),                     // visitNumber,
        new Optional(),                     // waybill,
        new ParseBool(),                    // sourceSpecimen,
        new Optional(new Unique()),         // worksheet,
        new Optional(),                     // palletProductBarcode,
        new Optional(),                     // rootContainerType,
        new Optional(),                     // palletLabel,
        new Optional(),                     // palletPosition,
        new Optional()                      // comment
        };
    // @formatter:on

    private final ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    private final List<SpecimenBatchOpInputPojo> csvInfos =
        new ArrayList<SpecimenBatchOpInputPojo>(0);

    public static boolean isHeaderValid(String[] csvHeaders) {
        return csvHeaders[0].equals(CSV_FIRST_HEADER)
            && (csvHeaders.length == NAME_MAPPINGS.length);
    }

    public List<SpecimenBatchOpInputPojo> getBeans(ICsvBeanReader reader)
        throws SuperCSVReflectionException, BatchOpErrorsException,
        SuperCSVException, IOException {
        final Map<String, SpecimenBatchOpInputPojo> parentSpcMap =
            new HashMap<String, SpecimenBatchOpInputPojo>();

        SpecimenBatchOpInputPojo csvPojos;

        while ((csvPojos =
            reader.read(SpecimenBatchOpInputPojo.class,
                NAME_MAPPINGS, CELL_PROCESSORS)) != null) {

            if (csvPojos.getSourceSpecimen()) {
                if (csvPojos.hasParentInventoryId()) {
                    getErrorList().addError(reader.getLineNumber(),
                        CSV_PARENT_SPC_ERROR);
                }
                parentSpcMap.put(csvPojos.getInventoryId(), csvPojos);
            } else {
                if (csvPojos.hasParentInventoryId()) {
                    // check that parent and child specimens have the same
                    // patient number
                    SpecimenBatchOpInputPojo parentCsvInfo =
                        parentSpcMap.get(csvPojos.getParentInventoryId());

                    if ((parentCsvInfo != null)
                        && !csvPojos.getPatientNumber().equals(
                            parentCsvInfo.getPatientNumber())) {
                        getErrorList().addError(reader.getLineNumber(),
                            CSV_SPC_PATIENT_ERROR);
                    }
                }
            }

            // check if only position defined and no label and no product
            // barcode
            if ((csvPojos.getPalletProductBarcode() == null)
                && (csvPojos.getPalletLabel() == null)
                && (csvPojos.getPalletPosition() != null)) {
                getErrorList().addError(reader.getLineNumber(),
                    CSV_PALLET_POS_ERROR);
            }

            //
            if ((csvPojos.getPalletProductBarcode() != null)
                && (csvPojos.getPalletPosition() == null)) {
                getErrorList().addError(reader.getLineNumber(),
                    CSV_PROD_BARCODE_NO_POS_ERROR);
            }

            if ((csvPojos.getPalletLabel() != null)
                && (csvPojos.getPalletPosition() == null)) {
                getErrorList().addError(reader.getLineNumber(),
                    CSV_PALLET_POS_ERROR);
            }

            if ((csvPojos.getPalletLabel() != null)
                && (csvPojos.getRootContainerType() == null)) {
                getErrorList().addError(reader.getLineNumber(),
                    CSV_PALLET_LABEL_NO_CTYPE_ERROR);
            }

            csvPojos.setLineNumber(reader.getLineNumber());
            csvInfos.add(csvPojos);
        }

        return csvInfos;
    }

    public ClientBatchOpInputErrorList getErrorList() {
        return errorList;
    }
}
