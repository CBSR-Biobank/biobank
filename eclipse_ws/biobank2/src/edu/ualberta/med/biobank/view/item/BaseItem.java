package edu.ualberta.med.biobank.view.item;

import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.event.HasValue;

public abstract class BaseItem<T> implements HasValue<T> {
    private final Control control;

    public BaseItem(Control control) {
        this.control = control;
    }

    public Control getControl() {
        return control;
    }
}
