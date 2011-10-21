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
import com.pietschy.gwt.pectin.client.form.validation.binding.ValidationBinder;
import com.pietschy.gwt.pectin.client.form.validation.validator.NotEmptyValidator;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SaveSiteAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.AlertEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteChangedEvent;
import edu.ualberta.med.biobank.mvp.event.presenter.site.ShowSiteViewPresenterEvent;
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
    private final Model model = new Model();

    public interface View extends FormView {
        void setAddressEntryView(BaseView view);

        void setActivityStatusComboView(BaseView view);

        HasValue<String> getName();

        HasValue<String> getNameShort();

        HasValue<String> getComment();

        HasValue<Collection<StudyInfo>> getStudies();
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
        super.onBind();

        addressEditPresenter.bind();
        activityStatusComboPresenter.bind();

        binder.bind(model.name).to(view.getName());
        binder.bind(model.nameShort).to(view.getNameShort());
        binder.bind(model.comment).to(view.getComment());
        // binder.bind(model.studies).to(view.getStudies());

        // TODO: proppa?
        // binder.bind(model.address)
        // .to(addressEditPresenter.getAddress());
        binder.bind(model.activityStatus)
            .to(activityStatusComboPresenter.getActivityStatus());

        // TODO: need to have some sort of:
        // (1) aggregated binding system
        // (2) immediate validation system
        // validationBinder.bindValidationOf(addressEditPresenter.getModel())
        // .to(null);

        binder.enable(view.getSave()).when(model.dirty());
    }

    @Override
    protected void onUnbind() {
        validationBinder.dispose();

        activityStatusComboPresenter.unbind();
        addressEditPresenter.unbind();
    }

    @Override
    public void doReload() {
        // TODO: this resets the form. To reload it from the database, something
        // different must be done (e.g. setting a Command that is re-run on
        // reload).
        model.revert();
    }

    @Override
    public void doSave() {
        SaveSiteAction saveSite = new SaveSiteAction();
        saveSite.setId(model.siteId.getValue());
        saveSite.setName(model.name.getValue());
        saveSite.setNameShort(model.nameShort.getValue());
        saveSite.setComment(model.comment.getValue());
        saveSite.setAddress(model.address.getValue());
        saveSite.setActivityStatusId(model.getActivityStatusId());
        saveSite.setStudyIds(model.getStudyIds());

        dispatcher.exec(saveSite, new ActionCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO: better error message and show or log exception?
                eventBus.fireEvent(new AlertEvent(caught.getLocalizedMessage()));
            }

            @Override
            public void onSuccess(Integer siteId) {
                eventBus.fireEvent(new SiteChangedEvent(siteId));
                eventBus.fireEvent(new ShowSiteViewPresenterEvent(siteId));
                close();
            }
        });
    }

    public View createSite() {
        SiteInfo siteInfo = new SiteInfo();
        siteInfo.setSite(new Site());
        siteInfo.getSite().setAddress(new Address());
        return editSite(siteInfo);
    }

    public View editSite(Integer siteId) {
        GetSiteInfoAction getSiteInfo = new GetSiteInfoAction(siteId);
        dispatcher.exec(getSiteInfo, new ActionCallback<SiteInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO: better error message and show or log exception?
                eventBus.fireEvent(new AlertEvent(caught.getLocalizedMessage()));
                close();
            }

            @Override
            public void onSuccess(SiteInfo siteInfo) {
                editSite(siteInfo);
            }
        });

        return view;
    }

    public View editSite(SiteInfo siteInfo) {
        // get our own (deep) copy of the data
        SiteInfo clone = ObjectCloner.deepCopy(siteInfo);
        model.setValue(clone);
        return view;
    }

    // TODO: expose Model for testing purposes?
    private static class Model extends BaseModel<SiteInfo> {
        final FieldModel<Integer> siteId;
        final FieldModel<String> name;
        final FieldModel<String> nameShort;
        final FieldModel<String> comment;
        final FieldModel<Address> address;
        final FieldModel<ActivityStatus> activityStatus;
        final ListFieldModel<StudyInfo> studies;

        @SuppressWarnings("unchecked")
        public Model() {
            super(SiteInfo.class);

            // TODO: consider using bindgen to generate a binding class via a
            // @Binding annotation so that the pays for binding fields to
            // providers is checked at compile time (don't use strings).

            siteId = fieldOfType(Integer.class)
                .boundTo(provider, "site.id");
            name = fieldOfType(String.class)
                .boundTo(provider, "site.name");
            nameShort = fieldOfType(String.class)
                .boundTo(provider, "site.nameShort");
            comment = fieldOfType(String.class)
                .boundTo(provider, "site.comment");
            address = fieldOfType(Address.class)
                .boundTo(provider, "site.address");
            activityStatus = fieldOfType(ActivityStatus.class)
                .boundTo(provider, "site.activityStatus");
            studies = listOfType(StudyInfo.class)
                .boundTo(provider, "studies");

            ValidationPlugin.validateField(name)
                .using(new NotEmptyValidator("Name is required"));
            ValidationPlugin.validateField(nameShort)
                .using(new NotEmptyValidator("Name Short is required"));
        }

        Integer getActivityStatusId() {
            ActivityStatus activityStatus = this.activityStatus.getValue();
            return activityStatus != null ? activityStatus.getId() : null;
        }

        Set<Integer> getStudyIds() {
            Set<Integer> studyIds = new HashSet<Integer>();
            for (StudyInfo studyInfo : studies) {
                studyIds.add(studyInfo.getStudy().getId());
            }
            return studyIds;
        }
    }
}
