package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;

public class PaletteBarCodeValidator extends AbstractValidator {

	private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9]{6}$");

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
		Matcher m = PATTERN.matcher(v);
		if (m.matches()) {
			controlDecoration.hide();
			return Status.OK_STATUS;
		}
		controlDecoration.show();
		return ValidationStatus.error(message);
	}

}
