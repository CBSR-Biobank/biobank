package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractView implements IView {
    private boolean created = false;

    @Override
    public void create(Composite parent) {
        if (!created) {
            onCreate(parent);
        } else {
            new IllegalStateException("cannot create a view twice"); //$NON-NLS-1$
        }
    }

    protected abstract void onCreate(Composite parent);
}
