package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;

public class PostalCode implements IValidator {
	
	private final String message;
	
	private final ControlDecoration controlDecoration;
	
	private static final Pattern pattern = Pattern.compile(
			"^[abceghjklmnprstvxyABCEGHJKLMNPRSTVXY]\\d[a-zA-Z]-?\\d[a-zA-Z]\\d$");
	
	public PostalCode(String message, ControlDecoration controlDecoration) {
		super();
		this.message = message;
		this.controlDecoration = controlDecoration;
	}

	public IStatus validate(Object value) {
		if (! (value instanceof String)) {
			throw new RuntimeException(
			"Not supposed to be called for non-strings.");
		}
		
		String v = (String) value;

		if (v.length() == 0) {
			controlDecoration.hide();
			return Status.OK_STATUS;
		}

		Matcher m = pattern.matcher(v);		
		if (m.matches()) {
			controlDecoration.hide();
			return Status.OK_STATUS;
		}
		
		controlDecoration.show();
		return ValidationStatus.error(message);
	}
}
