package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.collectionEvent.SaveCollectionEventAction;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.mvp.event.AlertEvent;
import edu.ualberta.med.biobank.mvp.presenter.SaveablePresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.CollectionEventEditPresenter.Display;
import edu.ualberta.med.biobank.mvp.view.CloseableView;
import edu.ualberta.med.biobank.mvp.view.ReloadableView;
import edu.ualberta.med.biobank.mvp.view.SaveableView;

public abstract class CollectionEventEditPresenter extends
    BaseEditPresenter<Display> implements SaveablePresenter<Display> {
    public interface Display extends CloseableView, ReloadableView,
        SaveableView {
        void setPatient(Patient patient);

        HasValue<Integer> getVisitNumber();
    }

    @Override
    protected void doSave() {
        SaveCollectionEventAction save = new SaveCollectionEventAction();
        // TODO: populate save action with appropriate data

        dispatcher.exec(save, new ActionCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO: replace with a better message.
                eventBus.fireEvent(new AlertEvent("FAIL!"));
            }

            @Override
            public void onSuccess(Integer result) {

            }
        });
    }

    @Override
    public void doInit() {
        CollectionEvent collectionEvent = getCollectionEvent();

        display.setPatient(collectionEvent.getPatient());
        display.getVisitNumber().setValue(collectionEvent.getVisitNumber());
    }

    @Override
    protected void onUnbind() {
        // TODO Auto-generated method stub

    }

    protected abstract CollectionEvent getCollectionEvent();

    public static class Update extends CollectionEventEditPresenter {
        public Update(Integer ceId) {
        }

        @Override
        protected CollectionEvent getCollectionEvent() {
            return null; // action to get CE
        }
    }

    public static class Create extends CollectionEventEditPresenter {
        public Create(Integer patientId) {
        }

        @Override
        protected CollectionEvent getCollectionEvent() {
            CollectionEvent tmp = new CollectionEvent();
            tmp.setVisitNumber(null); // action to get next visit number
            tmp.setPatient(null); // action to get patient;

            // TODO: work on action batch query?

            return tmp;
        }
    }
}
