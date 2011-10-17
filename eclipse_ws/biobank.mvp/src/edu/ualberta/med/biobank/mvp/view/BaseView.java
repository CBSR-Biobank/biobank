package edu.ualberta.med.biobank.mvp.view;

import com.google.gwt.canvas.dom.client.Context2d.Composite;

public interface BaseView {
    /**
     * Creates the view.
     * <p>
     * With Eclipse RCP, {@link BaseView}-s cannot extend {@link Composite} and
     * implement the view because the {@link Composite#setParent()} method does
     * not necessarily work on all operating systems. So, instead,
     * {@link BaseView}-s must implement this interface and contain an inner
     * class that is created via the
     * {@link BaseView#create(org.eclipse.swt.widgets.Composite)} method.
     * <p>
     * Event IEditorPart uses {@link IEditorPart#createPartControl(Composite)}
     * to create the inner part of the editor, instead of taking a Composite as
     * an argument.
     * 
     * @param parent
     */
    void create(org.eclipse.swt.widgets.Composite parent);
}
