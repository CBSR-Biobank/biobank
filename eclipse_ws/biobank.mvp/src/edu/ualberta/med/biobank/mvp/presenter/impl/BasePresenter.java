package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import edu.ualberta.med.biobank.mvp.presenter.Presenter;
import edu.ualberta.med.biobank.mvp.view.BaseView;

public abstract class BasePresenter<V extends BaseView> implements Presenter<V> {
    private final List<HandlerRegistration> handlerRegistrations =
        new ArrayList<HandlerRegistration>();
    protected final V view;
    protected final EventBus eventBus;
    private boolean bound = false;

    protected BasePresenter(V view, EventBus eventBus) {
        this.view = view;
        this.eventBus = eventBus;

        // would call bind here, but don't call overridden methods from the
        // constructor as the derived object may not have completed
        // initialisation.
    }

    @Override
    public void bind() {
        if (!bound) {
            onBind();
            bound = true;
        }
    }

    @Override
    public void unbind() {
        if (bound) {
            bound = false;

            for (HandlerRegistration reg : handlerRegistrations) {
                reg.removeHandler();
            }
            handlerRegistrations.clear();

            onUnbind();
        }
    }

    /**
     * Checks if the presenter has been bound. Will be set to false after a call
     * to {@link #unbind()}.
     * 
     * @return The current bound status.
     */
    @Override
    public boolean isBound() {
        return bound;
    }

    /**
     * Returns the view for the presenter.
     * 
     * @return The view.
     */
    @Override
    public V getView() {
        return view;
    }

    /**
     * Any {@link HandlerRegistration}s added will be removed when
     * {@link #unbind()} is called. This provides a handy way to track event
     * handler registrations when binding and unbinding.
     * 
     * @param handlerRegistration
     *            The registration.
     */
    protected void registerHandler(HandlerRegistration handlerRegistration) {
        handlerRegistrations.add(handlerRegistration);
    }

    /**
     * This method is called when binding the presenter. Any additional bindings
     * should be done here.
     */
    protected abstract void onBind();

    /**
     * This method is called when unbinding the presenter. Any handler
     * registrations recorded with {@link #registerHandler(HandlerRegistration)}
     * will have already been removed at this point.
     */
    protected abstract void onUnbind();
}
