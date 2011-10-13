package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.Collection;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SaveSiteAction;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.model.SiteChangeEvent;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.util.ObjectCloner;
import edu.ualberta.med.biobank.mvp.view.BaseView;
import edu.ualberta.med.biobank.mvp.view.EntryView;

// TODO: replace SiteEditPresenter with this class
public class SiteEntryPresenter extends BaseEntryPresenter<View> {
    private final AddressEntryPresenter addressEntryPresenter;
    private final ActivityStatusComboPresenter aStatusComboPresenter;
    private SiteInfo siteInfo;

    public interface View extends EntryView {
        // TODO: have general validation errors
        void setGeneralErrors(Collection<Object> errors);

        void setAddressEntryView(BaseView view);

        void setActivityStatusComboView(BaseView view);

        HasValue<String> getName();

        HasValue<String> getNameShort();

        HasValue<String> getComment();

        HasValue<Collection<StudyInfo>> getStudies();
    }

    public SiteEntryPresenter(AddressEntryPresenter addressEntryPresenter,
        ActivityStatusComboPresenter aStatusComboPresenter) {
        this.addressEntryPresenter = addressEntryPresenter;
        this.aStatusComboPresenter = aStatusComboPresenter;

        display.setAddressEntryView(addressEntryPresenter.getView());
        display.setActivityStatusComboView(aStatusComboPresenter.getView());
    }

    @Override
    public void onBind() {
        // TODO: listen to Display properties for validation purposes.
        registerHandler(display.getName().addValueChangeHandler(
            new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    // display.getName().set
                }
            }));

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
        saveSite.setComment(display.getComment().getValue());

        saveSite.setName(display.getName().getValue());
        saveSite.setNameShort(display.getNameShort().getValue());
        saveSite.setComment(display.getComment().getValue());

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

                eventBus.fireEvent(new SiteChangeEvent(siteId));

                // TODO: fire event to open a view form for this site
            }
        });
    }

    public void createSite() {
        siteInfo = new SiteInfo();
        siteInfo.site = new Site();
        siteInfo.site.setAddress(new Address());
        populateView();
    }

    public void editSite(SiteInfo siteInfo) {
        this.siteInfo = ObjectCloner.deepCopy(siteInfo);

        // TODO: another method with id to fetch data from database?
        populateView();
    }

    private void populateView() {
        display.getName().setValue(siteInfo.site.getName());
        display.getNameShort().setValue(siteInfo.site.getNameShort());
        display.getComment().setValue(siteInfo.site.getComment());
        display.getStudies().setValue(siteInfo.studies);

        addressEntryPresenter.editAddress(siteInfo.site.getAddress());
        aStatusComboPresenter.setSelectedValue(siteInfo.site
            .getActivityStatus());
    }
}
