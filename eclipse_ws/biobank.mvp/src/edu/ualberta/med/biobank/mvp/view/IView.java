package edu.ualberta.med.biobank.mvp.view;


// TODO: make an ICreatableView, IView, and IShowHideableView?
public interface IView {
    /**
     * Creates the view.
     * <p>
     * With Eclipse RCP, {@link IView}-s cannot extend {@link Composite} and
     * implement the view because the {@link Composite#setParent()} method does
     * not necessarily work on all operating systems. So, instead, {@link IView}
     * -s must implement this interface and contain an inner class that is
     * created via the {@link IView#create(org.eclipse.swt.widgets.Composite)}
     * method.
     * <p>
     * Event IEditorPart uses {@link IEditorPart#createPartControl(Composite)}
     * to create the inner part of the editor, instead of taking a Composite as
     * an argument.
     * 
     * @param parent
     */
    void create(org.eclipse.swt.widgets.Composite parent);
}
