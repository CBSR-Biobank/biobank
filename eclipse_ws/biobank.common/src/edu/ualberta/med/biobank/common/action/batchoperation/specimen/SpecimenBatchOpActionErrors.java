package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Tr;

/**
 * Errors that can be encountered while processing a specimen batch operation.
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
public class SpecimenBatchOpActionErrors {

    public static final long serialVersionUID = 1L;

    public static final Bundle bundle = new CommonBundle();

    public static final LString SPC_ALREADY_EXISTS_ERROR =
        bundle.tr("specimen already exists").format();

    public static final Tr CSV_CEVENT_ERROR =
        bundle.tr("collection event for patient {0} with visit number \"{1}\" does not exist");

    public static final LString CSV_PARENT_SPC_ERROR =
        bundle.tr("specimen declared a source specimen but parent inventory ID present").format();

    public static final Tr CSV_PARENT_SPC_INV_ID_ERROR =
        bundle.tr("parent inventory id does not exist: {0}");

    public static final LString CSV_ALIQ_SPC_PATIENT_CEVENT_MISSING_ERROR =
        bundle.tr("when parent inventory id is not specified, "
            + "patient number, and visit number are required").format();

    public static final LString CSV_PALLET_POS_INFO_INALID_ERROR =
        bundle.tr("position information invalid, missing all of these: product barcode, label, and position").format();

    public static final LString CSV_PALLET_POS_ERROR =
        bundle.tr("pallet position defined but not product barcode or label").format();

    public static final LString CSV_PROD_BARCODE_NO_POS_ERROR =
        bundle.tr("pallet product barcode defined but not position").format();

    public static final LString CSV_PALLET_LABEL_NO_CTYPE_ERROR =
        bundle.tr("pallet label defined but not container type").format();

    public static final Tr CSV_WAYBILL_ERROR =
        bundle.tr("waybill \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_TYPE_ERROR =
        bundle.tr("specimen type with name \"{0}\" does not exist");

    public static final Tr CSV_CONTAINER_LABEL_ERROR =
        bundle.tr("container with label \"{0}\" does not exist");

    public static final Tr CSV_CONTAINER_LABEL_ROOT_CONTAINER_TYPE_ERROR =
        bundle.tr("container with label \"{0}\" and root container type \"{1}\" does not exist");

    public static final Tr CSV_CONTAINER_BARCODE_ERROR =
        bundle.tr("container with product barcode \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_LABEL_ERROR =
        bundle.tr("specimen position \"{0}\" in container with label \"{1}\" is invalid");

    public static final Tr CSV_SPECIMEN_BARCODE_ERROR =
        bundle.tr("specimen position \"{0}\" in container with product barcode \"{1}\" is invalid");

    public static final Tr CSV_PATIENT_NUMBER_INVALID_ERROR =
        bundle.tr("patient number is invalid");

    public static final Tr CSV_PATIENT_MATCH_ERROR =
        bundle.tr("patient number \"{0}\" does not match "
            + "the patient on the source specimen \"{1}\"");

    public static final Tr CSV_CEVENT_MATCH_ERROR =
        bundle.tr("collection event with visit number \"{0}\" "
            + "does match the source specimen's collection event");

    public static final Tr CSV_STUDY_SOURCE_SPC_TYPE_ERROR =
        bundle.tr("specimen type \"{0}\" is invalid for parent specimens in study \"{1}\"");

    public static final Tr CSV_STUDY_ALIQUOTED_SPC_TYPE_ERROR =
        bundle.tr("specimen type \"{0}\" is invalid for child specimens in study \"{1}\"");

    public static final Tr CSV_ORIGIN_CENTER_SHORT_NAME_ERROR =
        bundle.tr("invalid origin center short name: {0}");

    public static final Tr CSV_CURRENT_CENTER_SHORT_NAME_ERROR =
        bundle.tr("invalid current center short name: {0}");

    public static final Tr CSV_CONTAINER_SPC_TYPE_ERROR =
        bundle.tr("specimen type \"{0}\" cannot be stored in this container");

    public static final Tr CSV_CONTAINER_POS_OCCUPIED_ERROR =
        bundle.tr("specimen position \"{0}\" in container with barcode \"{1}\" is occupied");

    public static final Tr CSV_LABEL_POS_OCCUPIED_ERROR =
        bundle.tr("specimen position \"{0}\" in container with label \"{1}\" is occupied");

    public static final LString CSV_PATIENT_NUMBER_MISMATCH_ERROR =
        bundle.tr("parent specimen does not belong to patient").format();

    public static final LString CSV_PARENT_SPECIMEN_INVENTORY_ID_REQUIRED_ERROR =
        bundle.tr("parent specimen inventory ID is requried").format();

    public static final LString CSV_PATIENT_NUMBER_REQUIRED_ERROR =
        bundle.tr("patient number is requried").format();

    private SpecimenBatchOpActionErrors() {
        // should never be created since this class is just a placeholder for constants.
    }

}
