package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.mvp.event.ExceptionEvent;
import edu.ualberta.med.biobank.mvp.event.model.study.StudyChangedEvent;
import edu.ualberta.med.biobank.mvp.event.presenter.study.StudyViewPresenterShowEvent;
import edu.ualberta.med.biobank.mvp.exception.InitPresenterException;
import edu.ualberta.med.biobank.mvp.presenter.impl.StudyEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.presenter.validation.validator.NotEmptyValidator;
import edu.ualberta.med.biobank.mvp.presenter.validation.validator.NotNullValidator;
import edu.ualberta.med.biobank.mvp.user.ui.ListField;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;
import edu.ualberta.med.biobank.mvp.view.IView;

public class StudyEntryPresenter extends AbstractEntryFormPresenter<View> {
    private final Dispatcher dispatcher;
    private final ActivityStatusComboPresenter activityStatusComboPresenter;
    private Integer studyId;

    public interface View extends IEntryFormView {
        void setActivityStatusComboView(IView view);

        ValueField<String> getName();

        ValueField<String> getNameShort();

        ListField<Contact> getContacts();

        ListField<SourceSpecimen> getSourceSpecimens();

        ListField<AliquotedSpecimen> getAliquotedSpecimens();

        // TODO: add study event attributes
    }

    @Inject
    public StudyEntryPresenter(View view, EventBus eventBus,
        Dispatcher dispatcher,
        ActivityStatusComboPresenter activityStatusComboPresenter) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
        this.activityStatusComboPresenter = activityStatusComboPresenter;

        // so this view can create the other views if create() is called
        view.setActivityStatusComboView(activityStatusComboPresenter.getView());
    }

    @Override
    public void onBind() {
        super.onBind();

        activityStatusComboPresenter.bind();

        state.add(activityStatusComboPresenter);

        validation.validate(view.getName())
            .using(new NotEmptyValidator("name"));
        validation.validate(view.getNameShort())
            .using(new NotEmptyValidator("nameShort"));

        validation.validate(
            activityStatusComboPresenter.getView().getActivityStatus())
            .using(new NotNullValidator("activityStatus"));
    }

    @Override
    protected void onUnbind() {
        super.onUnbind();

        activityStatusComboPresenter.unbind();
    }

    @Override
    protected void doSave() {
        StudySaveAction saveStudy = new StudySaveAction();
        saveStudy.setId(studyId);

        // TODO: set site ids?
        saveStudy.setSiteIds(new HashSet<Integer>());

        saveStudy.setName(view.getName().getValue());
        saveStudy.setNameShort(view.getNameShort().getValue());
        saveStudy.setActivityStatusId(getActivityStatusId());
        saveStudy.setContactIds(getContactIds());
        saveStudy.setSourceSpcIds(getSourceSpecimenIds());
        saveStudy.setAliquotSpcIds(getAliquotedSepcimenIds());
        saveStudy.setStudyEventAttrIds(getStudyEventAttrIds());

        // TODO: this happens asynchronously now, how to inform GUI?
        dispatcher.asyncExec(saveStudy, new ActionCallback<IdResult>() {
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new ExceptionEvent(caught));
            }

            @Override
            public void onSuccess(IdResult result) {
                Integer studyId = result.getId();

                // clear dirty state (so form can close without prompt to save)
                getState().checkpoint();

                eventBus.fireEvent(new StudyChangedEvent(studyId));
                eventBus.fireEvent(new StudyViewPresenterShowEvent(studyId));
                close();
            }
        });
    }

    public View createStudy() throws InitPresenterException {
        return load(new StudyCreate());
    }

    public View editStudy(Integer studyId) throws InitPresenterException {
        return load(new StudyEdit(studyId));
    }

    private void editStudy(StudyInfo studyInfo) throws InitPresenterException {
        Study study = studyInfo.getStudy();
        view.getName().setValue(study.getName());
        view.getNameShort().setValue(study.getNameShort());

        activityStatusComboPresenter.setActivityStatus(study
            .getActivityStatus());

        view.getAliquotedSpecimens().setElements(studyInfo.getAliquotedSpcs());
        view.getSourceSpecimens().setElements(studyInfo.getSourceSpcs());
        view.getContacts().setElements(getContacts(studyInfo.getClinicInfos()));
    }

    private Integer getActivityStatusId() {
        return activityStatusComboPresenter.getActivityStatusId();
    }

    private List<Contact> getContacts(List<ClinicInfo> clinicInfos) {
        List<Contact> contacts = new ArrayList<Contact>();

        for (ClinicInfo clinicInfo : clinicInfos) {
            contacts.addAll(clinicInfo.getClinic().getContactCollection());
        }

        return contacts;
    }

    private Set<Integer> getContactIds() {
        Set<Integer> ids = new HashSet<Integer>();
        for (Contact c : view.getContacts().asUnmodifiableList()) {
            ids.add(c.getId());
        }
        return ids;
    }

    private Set<Integer> getSourceSpecimenIds() {
        Set<Integer> ids = new HashSet<Integer>();
        for (SourceSpecimen ss : view.getSourceSpecimens().asUnmodifiableList()) {
            ids.add(ss.getId());
        }
        return ids;
    }

    private Set<Integer> getAliquotedSepcimenIds() {
        Set<Integer> ids = new HashSet<Integer>();
        for (AliquotedSpecimen as : view.getAliquotedSpecimens()
            .asUnmodifiableList()) {
            ids.add(as.getId());
        }
        return ids;
    }

    private Set<Integer> getStudyEventAttrIds() {
        Set<Integer> ids = new HashSet<Integer>();
        // TODO: this
        return ids;
    }

    private class StudyEdit implements Loadable {
        private final Integer newStudyId;

        public StudyEdit(Integer newStudyId) {
            this.newStudyId = newStudyId;
        }

        @Override
        public void run() throws Exception {
            studyId = newStudyId;

            StudyInfo studyInfo =
                dispatcher.exec(new StudyGetInfoAction(studyId));

            editStudy(studyInfo);
        }
    }

    private class StudyCreate implements Loadable {
        @Override
        public void run() throws Exception {
            studyId = null;

            StudyInfo studyInfo =
                new StudyInfo(new Study(), 0l, 0l, new ArrayList<ClinicInfo>(),
                    new ArrayList<SourceSpecimen>(),
                    new ArrayList<AliquotedSpecimen>(),
                    new ArrayList<StudyEventAttr>());

            editStudy(studyInfo);
        }
    }
}
