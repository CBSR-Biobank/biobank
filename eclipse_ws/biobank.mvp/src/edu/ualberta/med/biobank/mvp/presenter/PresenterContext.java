package edu.ualberta.med.biobank.mvp.presenter;

import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.Dispatcher;

public interface PresenterContext {
    Dispatcher getDispatcher();

    EventBus getEventBus();

    void inject(Object object);
}
