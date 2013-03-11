package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class ScannerBarcodeValidator extends AbstractValidator {

    private static final I18n i18n = I18nFactory.getI18n(ScannerBarcodeValidator.class);

    private RowColPos validPlateDimensions = null;

    public ScannerBarcodeValidator(String message) {
        super(message);
    }

    /**
     * When assigned, the dimensions of the plate are also validated.
     * 
     * @param dimensions
     */
    public void setValidPlateDimensions(RowColPos dimensions) {
        validPlateDimensions = dimensions;
    }

    @SuppressWarnings("nls")
    @Override
    public IStatus validate(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        String barcode = (String) value;
        if (!barcode.isEmpty() && BiobankPlugin.getDefault().isValidPlateBarcode(barcode)) {
            if (validPlateDimensions != null) {
                RowColPos plateDimensions = BiobankPlugin.getDefault().getGridDimensions(barcode);

                if (!validPlateDimensions.equals(plateDimensions)) {
                    showDecoration();
                    return ValidationStatus.error(
                        i18n.tr("Invalid plate dimensions"));
                }
            }
            hideDecoration();
            return Status.OK_STATUS;
        }

        showDecoration();
        return ValidationStatus.error(errorMessage);
    }

}
