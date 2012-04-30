package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

import com.pietschy.gwt.pectin.client.form.validation.Severity;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;
import com.pietschy.gwt.pectin.client.form.validation.message.ValidationMessage;

public class AbstractValidationField extends AbstractControlField
    implements ValidationDisplay {
    private static final FieldDecoration ERROR_FIELD_DECORATION =
        FieldDecorationRegistry.getDefault()
            .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
    private ValidationResult validationResult;
    private ControlDecoration controlDecoration;

    public void setValidationControl(Control control) {
        disposeOldControlDecoration();
        controlDecoration = createControlDecoration(control);
        updateControlDecoration();
    }

    @Override
    public void setValidationResult(ValidationResult result) {
        validationResult = result;
        updateControlDecoration();
    }

    private void disposeOldControlDecoration() {
        if (controlDecoration != null) {
            controlDecoration.dispose();
        }
    }

    private void updateControlDecoration() {
        if (controlDecoration != null) {
            if (validationResult != null
                && validationResult.contains(Severity.ERROR)) {
                controlDecoration.setDescriptionText(getDescriptionText());
                controlDecoration.show();
            } else {
                controlDecoration.hide();
            }
        }
    }

    private String getDescriptionText() {
        ValidationMessage message = validationResult
            .getMessages(Severity.ERROR).iterator().next();
        return message.getMessage();
    }

    private ControlDecoration createControlDecoration(Control control) {
        ControlDecoration controlDecoration = new ControlDecoration(control,
            SWT.RIGHT | SWT.TOP);
        controlDecoration.setImage(ERROR_FIELD_DECORATION.getImage());
        return controlDecoration;
    }
}
