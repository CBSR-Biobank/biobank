package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;

public class EmailAddress implements IValidator {
	
	private final String message;
	
	private final ControlDecoration controlDecoration;
	
	private static final Pattern pattern = Pattern.compile(
			"^([^@\\s]+)@((?:[-a-z0-9]+.)+[a-z]{2,})$");
	
	public EmailAddress(String message, ControlDecoration controlDecoration) {
		super();
		this.message = message;
		this.controlDecoration = controlDecoration;
	}

	public IStatus validate(Object value) {
		if (! (value instanceof String)) {
			throw new RuntimeException(
			"Not supposed to be called for non-strings.");
		}

		if (((String) value).length() == 0) {
			controlDecoration.hide();
			return Status.OK_STATUS;
		}

		Matcher m = pattern.matcher((String) value);
		if (m.matches()) {
			controlDecoration.hide();
			return Status.OK_STATUS;
		}
		else {
			controlDecoration.show();
			return ValidationStatus.error(message);
		}
	}
}
