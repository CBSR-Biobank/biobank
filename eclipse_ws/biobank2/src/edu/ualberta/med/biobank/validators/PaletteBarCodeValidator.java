package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;

public class PaletteBarCodeValidator extends AbstractValidator {

	private static final Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

	public PaletteBarCodeValidator(String message,
			ControlDecoration controlDecoration) {
		super(message, controlDecoration);
	}

	@Override
	public IStatus validate(Object value) {
		if (!(value instanceof String)) {
			throw new RuntimeException(
				"Not supposed to be called for non-strings.");
		}

		String v = (String) value;
		if (v.length() == 6) {
			Matcher m = pattern.matcher(v);
			if (m.matches()) {
				controlDecoration.hide();
				return Status.OK_STATUS;
			}
		}
		controlDecoration.show();
		return ValidationStatus.error(message);
	}

}
