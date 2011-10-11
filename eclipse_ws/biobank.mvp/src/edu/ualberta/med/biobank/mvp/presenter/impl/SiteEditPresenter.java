package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SaveSiteAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.model.SiteSavedEvent;
import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValue;

public class SiteEditPresenter {
    private Display display;
    private EventBus eventBus;
    private final Integer siteId;
    private Dispatcher dispatcher;
    private final SelectActivityStatusPresenter selectActivityStatusPresenter =
        new SelectActivityStatusPresenter();

    // data should go in constructor
    // display/ eventbus should be injected elsewhere
    public SiteEditPresenter(Site site) {
        this.siteId = site != null ? site.getId() : null;

        bindDisplay();
        init();
    }

    private void bindDisplay() {
        selectActivityStatusPresenter.bind(display, eventBus);

        display.getSave().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doSave();
            }
        });

        // TODO: listen to Display properties for validation purposes.
        display.getName().addValueChangeHandler(
            new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    // display.getName().set
                }
            });
    }

    private void init() {
        SiteInfo siteInfo = null;
        if (siteId != null) {
            GetSiteInfoAction action = new GetSiteInfoAction(siteId);
            // siteInfo = service.doAction(action);
        }
        else {
            siteInfo = new SiteInfo();
            siteInfo.site = new Site();
            siteInfo.site.setAddress(new Address());
            siteInfo.studies = new ArrayList<StudyInfo>();
        }

        display.getName().setValue(siteInfo.site.getName());
        display.getNameShort().setValue(siteInfo.site.getNameShort());
        display.getComment().setValue(siteInfo.site.getComment());
        display.getActivityStatus().setValue(siteInfo.site.getActivityStatus());
        display.getStudies().setValue(siteInfo.studies);

        Address address = siteInfo.site.getAddress();

        display.getStreet1().setValue(address.getStreet1());
        display.getStreet2().setValue(address.getStreet2());
        display.getCity().setValue(address.getCity());
        display.getProvince().setValue(address.getProvince());
        display.getPostalCode().setValue(address.getPostalCode());
        display.getPhoneNumber().setValue(address.getPhoneNumber());
        display.getFaxNumber().setValue(address.getFaxNumber());
        display.getCountry().setValue(address.getCountry());
    }

    public void doSave() {
        SaveSiteAction saveSite = new SaveSiteAction(siteId);
        saveSite.setName(display.getName().getValue());
        saveSite.setNameShort(display.getNameShort().getValue());
        saveSite.setComment(display.getComment().getValue());
        // updateSite.setAddress(display.getAddressEditDisplay().g)
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

                eventBus.fireEvent(new SiteSavedEvent(siteId));
            }
        });
    }

    public interface Display extends SelectActivityStatusPresenter.Display {
        HasClickHandlers getSave();

        // TODO: have general validation errors
        void setGeneralErrors(Collection<Object> errors);

        HasValue<String> getName();

        HasValue<String> getNameShort();

        HasValue<String> getComment();

        // TODO: (1) extract address into a separate display and presenter or
        // (2) have a center presenter and display this this site presenter and
        // display inherit from.
        HasValue<String> getStreet1();

        HasValue<String> getStreet2();

        HasValue<String> getCity();

        HasValue<String> getProvince();

        HasValue<String> getPostalCode();

        HasValue<String> getPhoneNumber();

        HasValue<String> getFaxNumber();

        HasValue<String> getCountry();

        // TODO: where to the options for ActivityStatus come from? See what GWT
        // does for combo boxes...?
        HasSelectedValue<ActivityStatus> getActivityStatus();

        HasValue<Collection<StudyInfo>> getStudies();
    }
}
