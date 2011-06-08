package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BiobankText;

public class MatchingTextValidator extends AbstractValidator {
    private BiobankText text;

    public MatchingTextValidator(String message, BiobankText text) {
        super(message);
        this.text = text;
    }

    @Override
    public IStatus validate(Object value) {
        if (value != null && value.equals(text.getText())) {
            hideDecoration();
            return Status.OK_STATUS;
        }

        showDecoration();

        return ValidationStatus.error(errorMessage);
    }

    /**
     * The <code>confirmationText</code> needs to listen to the
     * <code>originalText<code> so that whenever the original text changes,
     * the confirmation text can re-validate to see if it matches. Calling
     * this method will setup the listener relationship.
     * 
     * @param originalText
     * @param confirmationText
     */
    public static void addListener(BiobankText originalText,
        final BiobankText confirmationText) {
        originalText.getTextBox().addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                String originalText = confirmationText.getText();
                confirmationText.setText(originalText + "+1");
                confirmationText.setText(originalText);
            }
        });
    }
}
