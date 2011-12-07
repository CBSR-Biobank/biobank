package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultCollector;
import com.pietschy.gwt.pectin.client.form.validation.Validator;
import com.pietschy.gwt.pectin.client.form.validation.message.ErrorMessage;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetListAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetListResult;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.mvp.ApplicationContext;
import edu.ualberta.med.biobank.mvp.action.StaleSafeDispatcher;
import edu.ualberta.med.biobank.mvp.event.ExceptionEvent;
import edu.ualberta.med.biobank.mvp.event.impl.DelayedValueChangeHandler.Delayed500MsValueChangeHandler;
import edu.ualberta.med.biobank.mvp.presenter.impl.SpecimenLinkPresenter.View;
import edu.ualberta.med.biobank.mvp.user.ui.HasButton;
import edu.ualberta.med.biobank.mvp.user.ui.SelectedValueField;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;

public class SpecimenLinkPresenter extends AbstractEntryFormPresenter<View> {
    private final PNumberMonitor pNumberMonitor = new PNumberMonitor();
    private final PEventMonitor pEventMonitor = new PEventMonitor();
    private final IsRecentMonitor isRecentMonitor = new IsRecentMonitor();
    private final CEventMonitor cEventMonitor = new CEventMonitor();
    private final StaleSafeDispatcher dispatcher;
    private final ApplicationContext appContext;

    private boolean patientExists = false;
    private List<ProcessingEvent> pEventOptions = Collections.emptyList();

    public interface View extends IEntryFormView {
        ValueField<String> getPatientNumber();

        SelectedValueField<ProcessingEvent> getProcessingEvent();

        ValueField<Boolean> isRecentProcessingEvent();

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
    public SpecimenLinkPresenter(View view, EventBus eventBus,
        StaleSafeDispatcher dispatcher,
        ApplicationContext appContext) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
        this.appContext = appContext;
    }

    @Override
    protected void onBind() {
        view.getPatientNumber().addValueChangeHandler(pNumberMonitor);
        view.getProcessingEvent().addValueChangeHandler(pEventMonitor);
        view.isRecentProcessingEvent().addValueChangeHandler(isRecentMonitor);
        view.getCollectionEvent().addValueChangeHandler(cEventMonitor);

        // Conditions.v

        // binder.enable(view.getProcessingEvent()).when();

        // binder.show(view.getCollectionEvent()).when()

        validation.validate(view.getPatientNumber())
            .using(new PNumberValidator());
    }

    @Override
    protected void onUnbind() {
    }

    @Override
    protected void doSave() {
        // TODO Auto-generated method stub
    }

    private class PNumberMonitor extends Delayed500MsValueChangeHandler<String> {
        @Override
        public void onDelayedValueChange(ValueChangeEvent<String> event) {
            String pNumber = event.getValue();
            Integer centerId = appContext.getWorkingCenterId();

            dispatcher.asyncExec(
                new ProcessingEventGetListAction(pNumber, centerId),
                new ActionCallback<ProcessingEventGetListResult>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        pEventOptions = Collections.emptyList();
                        patientExists = false;

                        update();

                        eventBus.fireEvent(new ExceptionEvent(caught));
                    }

                    @Override
                    public void onSuccess(ProcessingEventGetListResult result) {
                        pEventOptions = result.getProcessingEvents();
                        patientExists = result.isPatientExists();

                        update();
                    }
                });
        }

        private void update() {
            validation.getValueValidation(view.getPatientNumber()).validate();
            updatePEventOptions();
        }
    }

    private void updatePEventOptions() {
        List<ProcessingEvent> options = new ArrayList<ProcessingEvent>();
        options.addAll(pEventOptions);

        if (view.isRecentProcessingEvent().getValue()) {
            PredicateUtil.filterOut(pEventOptions, new PEventOver7DaysOld());
        }

        view.getProcessingEvent().setOptions(options);
    }

    private class IsRecentMonitor implements ValueChangeHandler<Boolean> {
        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            updatePEventOptions();
        }
    }

    private class PEventMonitor implements ValueChangeHandler<ProcessingEvent> {
        @Override
        public void onValueChange(ValueChangeEvent<ProcessingEvent> event) {
            // TODO: run async thing to update collection visit options
        }
    }

    private class CEventMonitor implements ValueChangeHandler<CollectionEvent> {
        @Override
        public void onValueChange(ValueChangeEvent<CollectionEvent> event) {
            // TODO: update source-specimen list on all views.
        }
    }

    private class PNumberValidator implements Validator<String> {
        @Override
        public void validate(String value, ValidationResultCollector results) {
            if (value == null || value.isEmpty()) {
                // TODO: better validation.
            } else if (!patientExists) {
                results.add(
                    new ErrorMessage("Patient Number '" + value
                        + "' does not exist"));
            } else if (pEventOptions.isEmpty()) {

            }
        }
    }

    private static class PEventOver7DaysOld implements
        Predicate<ProcessingEvent> {
        private final Date midnight7DaysAgo;

        public PEventOver7DaysOld() {
            Calendar cal = Calendar.getInstance();

            cal.set(Calendar.AM_PM, Calendar.AM);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            cal.add(Calendar.DAY_OF_MONTH, -7);

            midnight7DaysAgo = cal.getTime();
        }

        @Override
        public boolean evaluate(ProcessingEvent pEvent) {
            return pEvent.getCreatedAt().before(midnight7DaysAgo);
        }
    };
}
