package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;

import edu.ualberta.med.biobank.BioBankPlugin;

public class ScannerBarcodeValidator extends AbstractValidator {

	public ScannerBarcodeValidator(String message, ControlDecoration controlDecoration) {
        super(message, controlDecoration);
	}

	@Override
	public IStatus validate(Object value) {
		if (! (value instanceof String)) {
			throw new RuntimeException(
			"Not supposed to be called for non-strings.");
		}

		String barcode = (String) value; 
		if (barcode.length() != 0 && BioBankPlugin.getDefault().isValidPlateBarcode(barcode)) {
			controlDecoration.hide();
			return Status.OK_STATUS;
		}
		
		controlDecoration.show();
		return ValidationStatus.error(message);
	}

}
