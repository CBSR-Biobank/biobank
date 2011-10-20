package edu.ualberta.med.biobank.mvp.binding;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;

import edu.ualberta.med.biobank.mvp.user.ui.HasModelValue;

public class Binder {
    public <T> void bind(HasValue<T> view, HasModelValue<T> model) {
        // bind the view to model so changes go from one to the other
        // if view implements ValidationView then setValidationResult on event
    }

    public void enable(HasEnabled object, HasValue<Boolean> condition) {
        // watch the condition
    }

    public void disable(HasEnabled object, HasValue<Boolean> condition) {
    }

    public void show(HasVisibility object, HasValue<Boolean> condition) {
    }

    public void hide(HasVisibility object, HasValue<Boolean> condition) {
    }

    public void unbind() {

    }
}
