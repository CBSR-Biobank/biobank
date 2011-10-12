package edu.ualberta.med.biobank.mvp.presenter;

import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.mvp.view.BaseView;

// TODO: give Presenter-s a "Context" that they can use to bind a display, get the eventBus, whatever. Put it in the constructor? Because need to pass to inner presenters.
public interface Presenter<D extends BaseView> {
    public void setDispatcher(Dispatcher dispatcher);

    public void setDisplay(D display);

    public void setEventBus(EventBus eventBus);

    /**
     * Called after the presenter is initialised (i.e. the {@link Dispatcher},
     * display, and {@link EventBus} have been set). This should be called
     * before any other methods. Any event handlers and other setup should be
     * done here rather than in the constructor.
     */
    public void bind();

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
     * Returns the {@link BaseView} for the current presenter.
     * 
     * @return The view.
     */
    public D getView();
}
