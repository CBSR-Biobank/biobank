package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.ExceptionEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteChangedEvent;
import edu.ualberta.med.biobank.mvp.event.presenter.site.SiteViewPresenterShowEvent;
import edu.ualberta.med.biobank.mvp.exception.InitPresenterException;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.presenter.validation.validator.NotEmpty;
import edu.ualberta.med.biobank.mvp.presenter.validation.validator.NotNull;
import edu.ualberta.med.biobank.mvp.user.ui.ListField;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;
import edu.ualberta.med.biobank.mvp.view.IView;

/**
 * 
 * @author jferland
 * 
 */
public class SiteEntryPresenter extends AbstractEntryFormPresenter<View> {
    private final Dispatcher dispatcher;
    private final AddressEntryPresenter addressEntryPresenter;
    private final ActivityStatusComboPresenter activityStatusComboPresenter;
    private Integer siteId;

    public interface View extends IEntryFormView, ValidationDisplay {
        void setActivityStatusComboView(IView view);

        void setAddressEditView(IView view);

        ValueField<String> getName();

        ValueField<String> getNameShort();

        ListField<StudyInfo> getStudies();
    }

    @Inject
    public SiteEntryPresenter(View view, EventBus eventBus,
        Dispatcher dispatcher, AddressEntryPresenter addressEntryPresenter,
        ActivityStatusComboPresenter activityStatusComboPresenter) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
        this.addressEntryPresenter = addressEntryPresenter;
        this.activityStatusComboPresenter = activityStatusComboPresenter;

        // so this view can create the other views if create() is called
        view.setAddressEditView(addressEntryPresenter.getView());
        view.setActivityStatusComboView(activityStatusComboPresenter.getView());
    }

    @Override
    public void onBind() {
        super.onBind();

        addressEntryPresenter.bind();

        state.add(addressEntryPresenter);
        state.add(activityStatusComboPresenter);

        validation.add(addressEntryPresenter);

        validation.validate(view.getName())
            .using(new NotEmpty("name"));
        validation.validate(view.getNameShort())
            .using(new NotEmpty("nameShort"));

        validation.validate(
            activityStatusComboPresenter.getView().getActivityStatus())
            .using(new NotNull("activityStatus"));
    }

    @Override
    protected void onUnbind() {
        super.onUnbind();

        activityStatusComboPresenter.unbind();
        addressEntryPresenter.unbind();
    }

    @Override
    public void doSave() {
        SiteSaveAction saveSite = new SiteSaveAction();
        saveSite.setId(siteId);
        saveSite.setName(view.getName().getValue());
        saveSite.setNameShort(view.getNameShort().getValue());
        // saveSite.setComment(view.getComment().getValue());
        saveSite.setAddress(addressEntryPresenter.getAddress());
        saveSite.setActivityStatusId(getActivityStatusId());
        saveSite.setStudyIds(getStudyIds());

        dispatcher.exec(saveSite, new ActionCallback<IdResult>() {
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new ExceptionEvent(caught));
            }

            @Override
            public void onSuccess(IdResult result) {
                Integer siteId = result.getId();

                // clear dirty state (so form can close without prompt to save)
                getState().checkpoint();

                eventBus.fireEvent(new SiteChangedEvent(siteId));
                eventBus.fireEvent(new SiteViewPresenterShowEvent(siteId));
                close();
            }
        });
    }

    public View createSite() throws InitPresenterException {
        return load(new SiteCreate());
    }

    public View editSite(Integer siteId) throws InitPresenterException {
        return load(new SiteEdit(siteId));
    }

    private void editSite(SiteInfo siteInfo) throws InitPresenterException {
        view.getIdentifier().setValue(siteId);
        view.getStudies().setElements(siteInfo.getStudyCollection());

        Site site = siteInfo.getSite();
        view.getName().setValue(site.getName());
        view.getNameShort().setValue(site.getNameShort());

        ActivityStatus activityStatus = site.getActivityStatus();
        activityStatusComboPresenter.setActivityStatus(activityStatus);

        Address address = site.getAddress();
        addressEntryPresenter.setAddress(address);
    }

    private Integer getActivityStatusId() {
        return activityStatusComboPresenter.getActivityStatusId();
    }

    private Set<Integer> getStudyIds() {
        Set<Integer> studyIds = new HashSet<Integer>();
        for (StudyInfo studyInfo : view.getStudies().asUnmodifiableList()) {
            studyIds.add(studyInfo.getStudy().getId());
        }
        return studyIds;
    }

    private class SiteEdit implements Loadable {
        private final Integer newSiteId;

        public SiteEdit(Integer newSiteId) {
            this.newSiteId = newSiteId;
        }

        @Override
        public void run() throws Exception {
            siteId = newSiteId;

            SiteGetInfoAction siteGetInfoAction = new SiteGetInfoAction(siteId);

            SiteInfo siteInfo = dispatcher.exec(siteGetInfoAction);

            editSite(siteInfo);
        }
    }

    private class SiteCreate implements Loadable {
        @Override
        public void run() throws Exception {
            siteId = null;

            SiteInfo siteInfo = new SiteInfo.Builder().build();

            editSite(siteInfo);
        }
    }
}
