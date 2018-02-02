package edu.ualberta.med.biobank.common.action.batchoperation.specimen.position;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Tr;

/**
 * Errors that can be encountered while processing a specimen position batch operation.
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
public class PositionBatchOpActionErrors {

    public static final long serialVersionUID = 1L;

    public static final Bundle bundle = new CommonBundle();

    public static final LString CSV_SPECIMEN_INVENTORY_INVALID_ERROR =
        bundle.tr("specimen inventory ID is invalid").format();

    public static final LString CSV_SPECIMEN_HAS_NO_POSITION_ERROR =
        bundle.tr("specimen does not currently have a position, but current pallet label is specified")
        .format();

    public static final Tr CSV_SPECIMEN_PALLET_LABEL_INVALID_ERROR =
        bundle.tr("current pallet label is invalid: {0}");

}