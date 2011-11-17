package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.form.FieldModel;
import com.pietschy.gwt.pectin.client.form.ListFieldModel;
import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;
import com.pietschy.gwt.pectin.client.form.validation.validator.NotEmptyValidator;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.mvp.event.ExceptionEvent;
import edu.ualberta.med.biobank.mvp.model.AbstractModel;
import edu.ualberta.med.biobank.mvp.presenter.impl.StudyEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;
import edu.ualberta.med.biobank.mvp.view.IView;

public class StudyEntryPresenter extends AbstractEntryFormPresenter<View> {
    private final Dispatcher dispatcher;
    private final ActivityStatusComboPresenter activityStatusComboPresenter;
    private final Model model;
    private Integer studyId;

    public interface View extends IEntryFormView, ValidationDisplay {
        void setActivityStatusComboView(IView view);

        HasValue<String> getName();

        HasValue<String> getNameShort();

        HasValue<Collection<ClinicInfo>> getClinics();
    }

    @Inject
    public StudyEntryPresenter(View view, EventBus eventBus,
        Dispatcher dispatcher,
        ActivityStatusComboPresenter activityStatusComboPresenter) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
        this.activityStatusComboPresenter = activityStatusComboPresenter;

        this.model = new Model();

        // so this view can create the other views if create() is called
        view.setActivityStatusComboView(activityStatusComboPresenter.getView());
    }

    @Override
    public void onBind() {
        super.onBind();

        activityStatusComboPresenter.bind();
        binder.bind(model.studyId).to(view.getIdentifier());
        binder.bind(model.name).to(view.getName());
        binder.bind(model.nameShort).to(view.getNameShort());
        binder.bind(model.clinics).to(view.getClinics());
        binder.bind(model.activityStatus).to(
            activityStatusComboPresenter.getActivityStatus());
        binder.bind(model.dirty()).to(view.getDirty());

        model.bind();
        model.bindValidationTo(view);
        binder.enable(view.getSave()).when(model.validAndDirty());
    }

    @Override
    protected void onUnbind() {
        model.unbind();
        activityStatusComboPresenter.unbind();
    }

    @Override
    public void doReload() {
        if (studyId != null) {
            editStudy(studyId);
        } else {
            createStudy();
        }
    }

    @Override
    protected void doSave() {
        // TODO Auto-generated method stub
    }

    public void createStudy() {
        StudyInfo studyInfo = new StudyInfo();
        editStudy(studyInfo);
    }

    public boolean editStudy(Integer studyId) {
        this.studyId = studyId;

        StudyGetInfoAction studyGetInfoAction = new StudyGetInfoAction(studyId);

        boolean success = dispatcher.exec(studyGetInfoAction,
            new ActionCallback<StudyInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    eventBus.fireEvent(new ExceptionEvent(caught));
                    close();
                }

                @Override
                public void onSuccess(StudyInfo studyInfo) {
                    editStudy(studyInfo);
                }
            });

        return success;
    }

    private void editStudy(StudyInfo studyInfo) {
        model.setValue(studyInfo);
    }

    /**
     * The {@link Model} holds the data that the {@link View} needs and supplies
     * validation.
     * 
     * @author jferland
     * 
     */
    public static class Model extends AbstractModel<StudyInfo> {

        final FieldModel<Integer> studyId;
        final FieldModel<String> name;
        final FieldModel<String> nameShort;
        final FieldModel<ActivityStatus> activityStatus;
        final ListFieldModel<ClinicInfo> clinics;

        @SuppressWarnings("unchecked")
        private Model() {
            super(StudyInfo.class);

            // TODO: have a provider(x.class) method that creates and returns a
            // provider? (while adding the dirty listener, etc.) Keep a refernce
            // to the provider to allow getters and setters to be called on it.

            studyId = fieldOfType(Integer.class)
                .boundTo(provider, "study.id");
            name = fieldOfType(String.class)
                .boundTo(provider, "study.name");
            nameShort = fieldOfType(String.class)
                .boundTo(provider, "study.nameShort");
            activityStatus = fieldOfType(ActivityStatus.class)
                .boundTo(provider, "study.activityStatus");

            clinics = listOfType(ClinicInfo.class)
                .boundTo(provider, "clinicInfos");

            ValidationPlugin.validateField(name)
                .using(new NotEmptyValidator("Name is required"));
            ValidationPlugin.validateField(nameShort)
                .using(new NotEmptyValidator("Name Short is required"));
        }

        Integer getActivityStatusId() {
            ActivityStatus activityStatus = this.activityStatus.getValue();
            return activityStatus != null ? activityStatus.getId() : null;
        }

        Set<Integer> getContactIds() {
            Set<Integer> contactIds = new HashSet<Integer>();
            for (ClinicInfo clinicInfo : clinics) {
                for (Contact c : clinicInfo.getContacts()) {
                    contactIds.add(c.getId());
                }
            }
            return contactIds;
        }

        @Override
        public void onBind() {
            // do nothing
        }

        @Override
        public void onUnbind() {
            // do nothing
        }
    }
}
