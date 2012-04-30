package edu.ualberta.med.biobank.mvp.view.form;

import com.pietschy.gwt.pectin.client.form.validation.EmptyValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.Severity;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.message.ValidationMessage;
import com.pietschy.gwt.pectin.client.value.ValueTarget;

import edu.ualberta.med.biobank.mvp.user.ui.HasButton;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;
import edu.ualberta.med.biobank.mvp.view.form.ToolBarButtonManager.ButtonType;
import edu.ualberta.med.biobank.mvp.view.widget.DelegatingButton;

public abstract class AbstractEntryFormView extends AbstractFormView implements
    IEntryFormView {
    protected final DelegatingButton save = new DelegatingButton();
    private final DirtyValueTarget dirtyValueTarget = new DirtyValueTarget();
    private ValidationResult validationResult = EmptyValidationResult.INSTANCE;
    private boolean dirty = false;

    @Override
    public HasButton getSave() {
        return save;
    }

    @Override
    public ValueTarget<Boolean> getDirty() {
        return dirtyValueTarget;
    }

    private class DirtyValueTarget implements ValueTarget<Boolean> {
        @Override
        public void setValue(Boolean value) {
            dirty = value;
            updateDirty();
        }
    }

    @Override
    public void setValidationResult(ValidationResult result) {
        validationResult = result;
        updateFormMessage();
    }

    @Override
    public void onCreate(BaseForm form) {
        initActions(form);
        updateFormMessage();
        updateDirty();
    }

    public abstract String getOkMessage();

    private void initActions(BaseForm form) {
        HasButton saveButton = form.getToolbar().get(ButtonType.SAVE);
        save.setDelegate(saveButton);
    }

    private void updateFormMessage() {
        BaseForm form = getForm();
        if (form != null) {
            if (validationResult.contains(Severity.ERROR)) {
                for (ValidationMessage message : validationResult
                    .getMessages(Severity.ERROR)) {
                    form.setErrorMessage(message.getMessage());
                    break;
                }
            } else {
                form.setMessage(getOkMessage());
            }
        }
    }

    private void updateDirty() {
        if (editor != null) {
            editor.setDirty(dirty);
        }
    }
}
