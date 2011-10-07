package edu.ualberta.med.biobank.mvp.presenter;

import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.mvp.view.View;

public interface Presenter<D extends View> {
    public void setDispatcher(Dispatcher dispatcher);

    /**
     * Called when the presenter is initialised. This should be called before
     * any other methods. Any event handlers and other setup should be done here
     * rather than in the constructor.
     * 
     * @param display
     * @param eventBus
     */
    public void bind(D display, EventBus eventBus);

    /**
     * Called after the presenter and display have been finished with for the
     * moment.
     */
    public void unbind();

    /**
     * Returns true if the presenter is currently in a 'bound' state. That is,
     * the {@link #bind()} method has completed and {@link #unbind()} has not
     * been called.
     * 
     * @return <code>true</code> if bound.
     */
    public boolean isBound();

    /**
     * Returns the {@link View} for the current presenter.
     * 
     * @return The display.
     */
    public D getDisplay();
}
