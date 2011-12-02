package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.mvp.presenter.impl.SpecimenLinkPresenter.View;
import edu.ualberta.med.biobank.mvp.user.ui.HasButton;
import edu.ualberta.med.biobank.mvp.user.ui.SelectedValueField;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;

public class SpecimenLinkPresenter extends AbstractEntryFormPresenter<View> {

    public interface View extends IEntryFormView {
        ValueField<String> getPatientNumber();

        SelectedValueField<ProcessingEvent> getProcessingEvent();

        ValueField<Boolean> getProcessingEventIsRecent();

        SelectedValueField<CollectionEvent> getCollectionEvent();

        // ValueField<Mode> getMode();
        //
        // void setSingleModeView(IView view);
        //
        // void setMultipleModeView(IView view);

        HasButton getConfirm();
    }

    // public enum Mode {
    // SINGLE,
    // MULTIPLE;
    // }

    @Inject
    public SpecimenLinkPresenter(View view, EventBus eventBus) {
        super(view, eventBus);

        // Conditions.v

        // binder.enable(view.getProcessingEvent()).when();

        // binder.show(view.getCollectionEvent()).when()
    }

    @Override
    protected void onBind() {
    }

    @Override
    protected void onUnbind() {
    }

    @Override
    protected void doSave() {
        // TODO Auto-generated method stub

    }
}
