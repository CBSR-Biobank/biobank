package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.List;

import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.form.FieldModel;
import com.pietschy.gwt.pectin.client.form.ListFieldModel;
import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
import com.pietschy.gwt.pectin.client.form.validation.binding.ValidationBinder;
import com.pietschy.gwt.pectin.client.form.validation.validator.NotEmptyValidator;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SaveSiteAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteChangedEvent;
import edu.ualberta.med.biobank.mvp.model.BaseModel;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.util.ObjectCloner;
import edu.ualberta.med.biobank.mvp.view.BaseView;
import edu.ualberta.med.biobank.mvp.view.FormView;

public class SiteEntryPresenter extends BaseEntryPresenter<View> {
    private final Dispatcher dispatcher;
    private final AddressEditPresenter addressEditPresenter;
    private final ActivityStatusComboPresenter activityStatusComboPresenter;
    private final ValidationBinder validationBinder = new ValidationBinder();
    private SiteInfo siteInfo;
    private final SiteInfoModel model = new SiteInfoModel();

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
        Dispatcher dispatcher,
        AddressEditPresenter addressEntryPresenter,
        ActivityStatusComboPresenter activityStatusComboPresenter) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
        this.addressEditPresenter = addressEntryPresenter;
        this.activityStatusComboPresenter = activityStatusComboPresenter;

        // so this view can create the other views if create() is called
        view.setAddressEntryView(addressEntryPresenter.getView());
        view.setActivityStatusComboView(activityStatusComboPresenter.getView());
    }

    @Override
    public void onBind() {
        addressEditPresenter.bind();
        activityStatusComboPresenter.bind();

        binder.bind(model.name).to(view.getName());
        // binder.bind(model.address).to

        binder.enable(view.getSave()).when(model.dirty());
        // binder.bind(addressEditPresenter.getModel().get).

        // TODO: need to have some sort of:
        // (1) aggregated binding system
        // (2) immediate validation system
        // validationBinder.bindValidationOf(addressEditPresenter.getModel())
        // .to(null);
    }

    @Override
    protected void onUnbind() {
        validationBinder.dispose();

        activityStatusComboPresenter.unbind();
        addressEditPresenter.unbind();
    }

    @Override
    public void doReload() {
    }

    @Override
    public void doSave() {
        SaveSiteAction saveSite = new SaveSiteAction(null);
        saveSite.setComment(model.comment.getValue());

        saveSite.setName(view.getName().getValue());
        saveSite.setNameShort(view.getNameShort().getValue());
        saveSite.setComment(view.getComment().getValue());

        ActivityStatus activityStatus =
            activityStatusComboPresenter.getSelectedValue();
        saveSite.setActivityStatusId(activityStatus.getId());

        saveSite.setAddress(addressEditPresenter.getAddress());

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
        model.setValue(siteInfo);
        return view;
    }

    private static class SiteInfoModel extends BaseModel<SiteInfo> {
        protected final FieldModel<String> name;
        protected final FieldModel<String> nameShort;
        protected final FieldModel<String> comment;
        protected final FieldModel<ActivityStatus> activityStatus;
        protected final FieldModel<Address> address;
        protected final ListFieldModel<StudyInfo> studies;

        public SiteInfoModel() {
            super(SiteInfo.class);
            name = addField(String.class, "site.name");
            nameShort = addField(String.class, "site.nameShort");
            comment = addField(String.class, "site.comment");

            address = addField(Address.class, "site.address");

            activityStatus = fieldOfType(ActivityStatus.class)
                .boundTo(provider, "site.activityStatus");

            studies = listOfType(StudyInfo.class).boundTo(provider, "studies");

            ValidationPlugin.validateField(name).using(
                new NotEmptyValidator("Surname is required"));
        }
    }
}
