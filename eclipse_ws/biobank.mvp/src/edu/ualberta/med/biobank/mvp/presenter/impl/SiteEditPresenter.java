package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SaveSiteAction;
import edu.ualberta.med.biobank.common.util.Holder;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.AlertEvent;
import edu.ualberta.med.biobank.mvp.event.model.SiteChangeEvent;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEditPresenter.Display;
import edu.ualberta.med.biobank.mvp.view.CloseableView;
import edu.ualberta.med.biobank.mvp.view.ReloadableView;
import edu.ualberta.med.biobank.mvp.view.SaveableView;

public abstract class SiteEditPresenter extends BaseEditPresenter<Display> {
    protected final Integer siteId;
    private final AddressEditPresenter addressEditPresenter;
    private final SelectActivityStatusPresenter selectActivityStatusPresenter;

    protected SiteEditPresenter(Integer siteId) {
        this.siteId = siteId;
        addressEditPresenter = new AddressEditPresenter();
        selectActivityStatusPresenter = new SelectActivityStatusPresenter();
    }

    @Override
    public void onBind() {
        addressEditPresenter.bind(display, eventBus);
        selectActivityStatusPresenter.bind(display, eventBus);

        // TODO: listen to Display properties for validation purposes.
        registerHandler(display.getName().addValueChangeHandler(
            new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    // display.getName().set
                }
            }));
    }

    @Override
    protected void onUnbind() {
        addressEditPresenter.unbind();
        selectActivityStatusPresenter.unbind();
    }

    @Override
    public void doInit() {
        SiteInfo siteInfo = getSiteInfo();

        display.getName().setValue(siteInfo.site.getName());
        display.getNameShort().setValue(siteInfo.site.getNameShort());
        display.getComment().setValue(siteInfo.site.getComment());
        display.getActivityStatus().setValue(siteInfo.site.getActivityStatus());
        display.getStudies().setValue(siteInfo.studies);

        addressEditPresenter.setAddress(siteInfo.site.getAddress());
    }

    @Override
    public void doSave() {
        SaveSiteAction saveSite = new SaveSiteAction(siteId);

        saveSite.setName(display.getName().getValue());
        saveSite.setNameShort(display.getNameShort().getValue());
        saveSite.setComment(display.getComment().getValue());

        Integer aStatusId = display.getActivityStatus().getValue().getId();
        saveSite.setActivityStatusId(aStatusId);

        saveSite.setAddress(addressEditPresenter.getAddress());
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
            }
        });
    }

    public interface Display extends CloseableView, ReloadableView,
        SaveableView, SelectActivityStatusPresenter.Display,
        AddressEditPresenter.Display {
        // TODO: have general validation errors
        void setGeneralErrors(Collection<Object> errors);

        HasValue<String> getName();

        HasValue<String> getNameShort();

        HasValue<String> getComment();

        HasValue<Collection<StudyInfo>> getStudies();
    }

    protected abstract SiteInfo getSiteInfo();

    public static class Update extends SiteEditPresenter {
        public Update(Site site) {
            super(site.getId());
        }

        @Override
        protected SiteInfo getSiteInfo() {
            final Holder<SiteInfo> siteInfoHolder = new Holder<SiteInfo>(null);
            GetSiteInfoAction getSiteInfo = new GetSiteInfoAction(siteId);
            dispatcher.exec(getSiteInfo, new ActionCallback<SiteInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    // TODO: better error message and show or log exception?
                    eventBus.fireEvent(new AlertEvent("FAIL!"));
                    display.close();
                    unbind();
                }

                @Override
                public void onSuccess(SiteInfo result) {
                    siteInfoHolder.setValue(result);
                }
            });
            return siteInfoHolder.getValue();
        }
    }

    public static class Create extends SiteEditPresenter {
        public Create() {
            super(null);
        }

        @Override
        protected SiteInfo getSiteInfo() {
            SiteInfo siteInfo = new SiteInfo();
            siteInfo.site = new Site();
            siteInfo.site.setAddress(new Address());
            siteInfo.studies = new ArrayList<StudyInfo>();
            return null;
        }
    }
}
