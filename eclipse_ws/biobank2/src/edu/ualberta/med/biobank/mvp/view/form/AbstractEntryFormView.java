package edu.ualberta.med.biobank.mvp.view.form;

import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.value.ValueTarget;

import edu.ualberta.med.biobank.mvp.user.ui.IButton;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;
import edu.ualberta.med.biobank.mvp.view.item.ButtonItem;

public abstract class AbstractEntryFormView extends AbstractFormView implements
    IEntryFormView {
    protected final ButtonItem save = new ButtonItem();
    private final DirtyValueTarget dirtyValueTarget = new DirtyValueTarget();
    private ValidationResult validationResult;

    @Override
    public IButton getSave() {
        return save;
    }

    @Override
    public ValueTarget<Boolean> getDirty() {
        return dirtyValueTarget;
    }

    private class DirtyValueTarget implements ValueTarget<Boolean> {
        @Override
        public void setValue(Boolean value) {
            if (editor != null) {
                // TODO: what to do if it's null?
                editor.setDirty(value);
            }
        }
    }

    @Override
    public void setValidationResult(ValidationResult result) {
        validationResult = result;
        // BaseForm baseForm = getBaseForm();
        // System.out.println(new Date());
        // for (ValidationMessage message : result.getMessages()) {
        // baseForm.setMessage(message.getMessage());
        // }
    }
}
