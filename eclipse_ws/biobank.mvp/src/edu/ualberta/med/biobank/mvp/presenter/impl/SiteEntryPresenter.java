package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SaveSiteAction;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteChangedEvent;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.util.ObjectCloner;
import edu.ualberta.med.biobank.mvp.view.BaseView;
import edu.ualberta.med.biobank.mvp.view.FormView;

public class SiteEntryPresenter extends BaseEntryPresenter<View> {
    private final Dispatcher dispatcher;
    private final AddressEntryPresenter addressEntryPresenter;
    private final ActivityStatusComboPresenter aStatusComboPresenter;
    private SiteInfo siteInfo;

    public interface View extends FormView {
        // TODO: have general validation errors
        void setGeneralErrors(Collection<Object> errors);

        void setAddressEntryView(BaseView view);

        void setActivityStatusComboView(BaseView view);

        HasValue<String> getName();

        HasValue<String> getNameShort();

        HasValue<String> getComment();

        HasValue<List<StudyInfo>> getStudies();
    }

    @Inject
    public SiteEntryPresenter(View view, EventBus eventBus,
        Dispatcher dispatcher, AddressEntryPresenter addressEntryPresenter,
        ActivityStatusComboPresenter aStatusComboPresenter) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
        this.addressEntryPresenter = addressEntryPresenter;
        this.aStatusComboPresenter = aStatusComboPresenter;

        // Doesn't _NEED_ to be done here, can be done later, then the
        // SiteEntryPresenter.View can create these sub-views when they're set.
        view.setAddressEntryView(addressEntryPresenter.getView());
        view.setActivityStatusComboView(aStatusComboPresenter.getView());
    }

    @Override
    public void onBind() {
        // TODO: listen to Display properties for validation purposes.
        // registerHandler(view.getName().addValueChangeHandler(
        // new ValueChangeHandler<String>() {
        // @Override
        // public void onValueChange(ValueChangeEvent<String> event) {
        // // display.getName().set
        // }
        // }));

        addressEntryPresenter.bind();
        aStatusComboPresenter.bind();
    }

    @Override
    protected void onUnbind() {
        addressEntryPresenter.unbind();
        aStatusComboPresenter.unbind();
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

        Integer aStatusId = aStatusComboPresenter.getSelectedValue().getId();
        saveSite.setActivityStatusId(aStatusId);

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
        aStatusComboPresenter.setSelectedValue(siteInfo.site
            .getActivityStatus());
    }
}
