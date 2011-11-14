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
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.mvp.event.ExceptionEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteChangedEvent;
import edu.ualberta.med.biobank.mvp.event.presenter.site.SiteViewPresenterShowEvent;
import edu.ualberta.med.biobank.mvp.model.AbstractModel;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter.View;
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
    private final Model model;

    public interface View extends IEntryFormView, ValidationDisplay {
        void setActivityStatusComboView(IView view);

        void setAddressEditView(IView view);

        HasValue<String> getName();

        HasValue<String> getNameShort();

        HasValue<Collection<StudyInfo>> getStudies();
    }

    @Inject
    public SiteEntryPresenter(View view, EventBus eventBus,
        Dispatcher dispatcher, AddressEntryPresenter addressEntryPresenter,
        ActivityStatusComboPresenter activityStatusComboPresenter) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
        this.addressEntryPresenter = addressEntryPresenter;
        this.activityStatusComboPresenter = activityStatusComboPresenter;

        this.model = new Model(addressEntryPresenter.getModel());

        // so this view can create the other views if create() is called
        view.setAddressEditView(addressEntryPresenter.getView());
        view.setActivityStatusComboView(activityStatusComboPresenter.getView());
    }

    @Override
    public void onBind() {
        super.onBind();

        addressEntryPresenter.bind(); // still necessary to bind view to model
        activityStatusComboPresenter.bind();

        binder.bind(model.siteId).to(view.getIdentifier());
        binder.bind(model.name).to(view.getName());
        binder.bind(model.nameShort).to(view.getNameShort());
        binder.bind(model.studies).to(view.getStudies());
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
        addressEntryPresenter.unbind();
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
        if (!model.validAndDirty().getValue()) return;

        SiteSaveAction saveSite = new SiteSaveAction();
        saveSite.setId(model.siteId.getValue());
        saveSite.setName(model.name.getValue());
        saveSite.setNameShort(model.nameShort.getValue());
        // saveSite.setComment(model.comment.getValue());
        saveSite.setAddress(model.address.getValue());
        saveSite.setActivityStatusId(model.getActivityStatusId());
        saveSite.setStudyIds(model.getStudyIds());

        dispatcher.exec(saveSite, new ActionCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new ExceptionEvent(caught));
            }

            @Override
            public void onSuccess(Integer siteId) {
                // clear dirty state (so form can close without prompt to save)
                model.checkpoint();

                eventBus.fireEvent(new SiteChangedEvent(siteId));
                eventBus.fireEvent(new SiteViewPresenterShowEvent(siteId));
                close();
            }
        });
    }

    public void createSite() {
        SiteInfo siteInfo = new SiteInfo.Builder().build();
        editSite(siteInfo);
    }

    public boolean editSite(Integer siteId) {
        SiteGetInfoAction siteGetInfoAction = new SiteGetInfoAction(siteId);

        boolean success = dispatcher.exec(siteGetInfoAction,
            new ActionCallback<SiteInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    eventBus.fireEvent(new ExceptionEvent(caught));
                    close();
                }

                @Override
                public void onSuccess(SiteInfo siteInfo) {
                    editSite(siteInfo);
                }
            });

        return success;
    }

    private void editSite(SiteInfo siteInfo) {
        model.setValue(siteInfo);
    }

    /**
     * The {@link Model} holds the data that the {@link View} needs and supplies
     * validation.
     * 
     * @author jferland
     * 
     */
    public static class Model extends AbstractModel<SiteInfo> {
        private final AbstractModel<Address> addressModel;

        final FieldModel<Integer> siteId;
        final FieldModel<String> name;
        final FieldModel<String> nameShort;
        final FieldModel<ActivityStatus> activityStatus;
        final FieldModel<Address> address;
        final ListFieldModel<StudyInfo> studies;

        @SuppressWarnings("unchecked")
        private Model(AbstractModel<Address> addressModel) {
            super(SiteInfo.class);

            this.addressModel = addressModel;

            // TODO: have a provider(x.class) method that creates and returns a
            // provider? (while adding the dirty listener, etc.)

            siteId = fieldOfType(Integer.class)
                .boundTo(provider, "site.id");
            name = fieldOfType(String.class)
                .boundTo(provider, "site.name");
            nameShort = fieldOfType(String.class)
                .boundTo(provider, "site.nameShort");
            activityStatus = fieldOfType(ActivityStatus.class)
                .boundTo(provider, "site.activityStatus");
            address = fieldOfType(Address.class)
                .boundTo(provider, "site.address");
            studies = listOfType(StudyInfo.class)
                .boundTo(provider, "studyCollection");

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

        @Override
        public void onBind() {
            bind(address, addressModel);
        }

        @Override
        public void onUnbind() {
        }
    }
}
