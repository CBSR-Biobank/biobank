package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;

public class TelephoneNumber implements IValidator {
	
	private final String message;
	
	private final ControlDecoration controlDecoration;
	
	public TelephoneNumber(String message, ControlDecoration controlDecoration) {
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
		int len = v.length();
		int numDigits = 0;

		if (len == 0) {
			controlDecoration.hide();
			return Status.OK_STATUS;
		}
		
		for (int i = 0; i < len; ++i) {
			if (Character.isDigit(v.charAt(i))) {
				++numDigits;
			}
		}
		
		if (numDigits >= 10) {
			controlDecoration.hide();
			return Status.OK_STATUS;
		}
		
		controlDecoration.show();
		return ValidationStatus.error(message);
	}
}
