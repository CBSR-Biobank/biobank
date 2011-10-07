package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.mvp.presenter.SaveablePresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.CollectionEventEditPresenter.Display;
import edu.ualberta.med.biobank.mvp.view.SaveableView;

public abstract class CollectionEventEditPresenter extends
    BaseEditPresenter<Display> implements SaveablePresenter<Display> {
    public interface Display extends SaveableView {
        void setPatient(Patient patient);

        HasValue<Integer> getVisitNumber();
    }

    @Override
    protected void doSave() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doInit() {

        // display.setPatient();
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
            tmp.setPatient(null); // action to get patient;
            return tmp;
        }
    }
}
