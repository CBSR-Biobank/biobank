package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.event.ExceptionEvent;
import edu.ualberta.med.biobank.mvp.exception.InitPresenterException;
import edu.ualberta.med.biobank.mvp.presenter.IFormPresenter;
import edu.ualberta.med.biobank.mvp.view.IFormView;

public abstract class AbstractFormPresenter<V extends IFormView>
    extends AbstractCloseablePresenter<V>
    implements IFormPresenter<V> {
    private final ReloadClickHandler reloadClickHandler =
        new ReloadClickHandler();
    private Loadable reload;

    public AbstractFormPresenter(V view, EventBus eventBus) {
        super(view, eventBus);
    }

    @Override
    public void reload() throws InitPresenterException {
        if (reload != null) {
            load(reload);
        }
    }

    @Override
    protected void onBind() {
        super.onBind();

        registerHandler(view.getReload().addClickHandler(reloadClickHandler));
    }

    private class ReloadClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            try {
                reload();
            } catch (InitPresenterException e) {
                eventBus.fireEvent(new ExceptionEvent(e));
            }
        }
    }

    protected V load(Loadable loadable) throws InitPresenterException {
        this.reload = loadable;

        try {
            loadable.run();
        } catch (Exception exception) {
            close();
            throw new InitPresenterException(exception);
        }

        return view;
    }

    protected interface Loadable {
        void run() throws Exception;
    }
}
