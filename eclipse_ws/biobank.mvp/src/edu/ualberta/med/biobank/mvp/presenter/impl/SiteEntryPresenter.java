package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.List;

import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SaveSiteAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteChangedEvent;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.util.ObjectCloner;
import edu.ualberta.med.biobank.mvp.validation.PresenterValidation;
import edu.ualberta.med.biobank.mvp.view.BaseView;
import edu.ualberta.med.biobank.mvp.view.FormView;

public class SiteEntryPresenter extends BaseEntryPresenter<View> {
    private final Dispatcher dispatcher;
    private final AddressEditPresenter addressEntryPresenter;
    private final ActivityStatusComboPresenter activityStatusComboPresenter;
    private final PresenterValidation validation = new PresenterValidation();
    private SiteInfo siteInfo;

    public interface View extends FormView {
        void setAddressEntryView(BaseView view);

        void setActivityStatusComboView(BaseView view);

        HasValue<String> getName();

        HasValue<String> getNameShort();

        HasValue<String> getComment();

        HasValue<List<StudyInfo>> getStudies();
    }

    @Inject
    public SiteEntryPresenter(View view, EventBus eventBus,
        Dispatcher dispatcher, AddressEditPresenter addressEntryPresenter,
        ActivityStatusComboPresenter activityStatusComboPresenter) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
        this.addressEntryPresenter = addressEntryPresenter;
        this.activityStatusComboPresenter = activityStatusComboPresenter;

        // so this view can create the other views if create() is called
        view.setAddressEntryView(addressEntryPresenter.getView());
        view.setActivityStatusComboView(activityStatusComboPresenter.getView());
    }

    @Override
    public void onBind() {
        addressEntryPresenter.bind();
        activityStatusComboPresenter.bind();

        // validation.validate(view.getName())
        // .using(new NotEmptyValidator("name"))
        // .when(something?);
        //
        // validation.validate(view.getName())
        // .using(new NotEmptyValidator("name"))
        // .when(something?);
        //
        // validation.validate(view.getNameShort())
        // .using(new NotEmptyValidator("nameShort"))
        // .when(something?);
        //
        // validation.validate(addressEntryPresenter.getValidation())
        // .when(something?);
        //
        // validation.validate();
        //
        // validation.addView(view);
    }

    @Override
    protected void onUnbind() {
        validation.unbind();
        activityStatusComboPresenter.unbind();
        addressEntryPresenter.unbind();
    }

    @Override
    public void doReload() {
    }

    @Override
    public void doSave() {
        SaveSiteAction saveSite = new SaveSiteAction(siteInfo.site.getId());
        saveSite.setComment(view.getComment().getValue());

        saveSite.setName(view.getName().getValue());
        saveSite.setNameShort(view.getNameShort().getValue());
        saveSite.setComment(view.getComment().getValue());

        ActivityStatus activityStatus =
            activityStatusComboPresenter.getSelectedValue();
        saveSite.setActivityStatusId(activityStatus.getId());

        saveSite.setAddress(addressEntryPresenter.getAddress());

        // TODO: get study ids
        // updateSite.setStudyIds(display.getStudyIds().getValue());

        dispatcher.exec(saveSite, new ActionCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
                // on failure:
                // log exception
                // have a listener to a DisplayExceptionEvent:
                // e.g. eventBus.fireEvent(new ExceptionEvent(???));
            }

            @Override
            public void onSuccess(Integer siteId) {
                // on success:

                // TODO: close this view
                // TODO: listen for SiteSavedEvent to (1) FormManager open view
                // form (2) TreeManager(s) update any trees that have this site.
                // But wait, probably shouldn't open the view form on any site
                // save event ... :-(

                eventBus.fireEvent(new SiteChangedEvent(siteId));

                // TODO: fire event to open a view form for this site
            }
        });
    }

    public View createSite() {
        siteInfo = new SiteInfo();
        siteInfo.site = new Site();
        siteInfo.site.setAddress(new Address());
        populateView();
        return view;
    }

    public View editSite(Integer siteId) {
        // final Holder<SiteInfo> siteInfoHolder = new Holder<SiteInfo>(null);
        // GetSiteInfoAction getSiteInfo = new GetSiteInfoAction(siteId);
        // dispatcher.exec(getSiteInfo, new ActionCallback<SiteInfo>() {
        // @Override
        // public void onFailure(Throwable caught) {
        // // TODO: better error message and show or log exception?
        // eventBus.fireEvent(new AlertEvent("FAIL!"));
        // display.close();
        // unbind();
        // }

        return view;
    }

    public View editSite(SiteInfo siteInfo) {
        this.siteInfo = ObjectCloner.deepCopy(siteInfo);

        // TODO: another method with id to fetch data from database?
        populateView();
        return view;
    }

    private void populateView() {
        view.getName().setValue(siteInfo.site.getName());
        view.getNameShort().setValue(siteInfo.site.getNameShort());
        view.getComment().setValue(siteInfo.site.getComment());
        view.getStudies().setValue(siteInfo.studies);

        addressEntryPresenter.editAddress(siteInfo.site.getAddress());
        activityStatusComboPresenter.setSelectedValue(siteInfo.site
            .getActivityStatus());
    }
}
