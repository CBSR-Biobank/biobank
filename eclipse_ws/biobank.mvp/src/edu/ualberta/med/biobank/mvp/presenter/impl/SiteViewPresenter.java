package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.Collection;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.info.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.ExceptionEvent;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteViewPresenter.View;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.IViewFormView;

public class SiteViewPresenter extends AbstractViewFormPresenter<View> {
    private final Dispatcher dispatcher;
    private Integer siteId;

    public interface View extends IViewFormView {
        ValueField<String> getName();

        ValueField<String> getNameShort();

        ValueField<Long> getStudyCount();

        ValueField<Long> getContainerTypeCount();

        ValueField<Long> getTopContainerCount();

        ValueField<Long> getPatientCount();

        ValueField<Long> getCollectionEventCount();

        ValueField<Long> getAliquotedSpecimenCount();

        ValueField<ActivityStatus> getActivityStatus();

        ValueField<Address> getAddress();

        ValueField<Collection<Comment>> getCommentCollection();

        ValueField<Collection<StudyInfo>> getStudyCollection();

        ValueField<Collection<ContainerTypeInfo>> getContainerTypeCollection();

        ValueField<Collection<Container>> getTopContainerCollection();
    }

    @Inject
    public SiteViewPresenter(View view, EventBus eventBus, Dispatcher dispatcher) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
    }

    @Override
    public void doReload() {
        viewSite(siteId);
    }

    @Override
    protected void onUnbind() {
    }

    public boolean viewSite(Integer siteId) {
        this.siteId = siteId;

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
                    viewSite(siteInfo);
                }
            });

        return success;
    }

    private void viewSite(SiteInfo siteInfo) {
        Site site = siteInfo.getSite();

        view.getName().setValue(site.getName());
        view.getNameShort().setValue(site.getNameShort());
        view.getActivityStatus().setValue(site.getActivityStatus());
        view.getAddress().setValue(site.getAddress());

        view.getStudyCount()
            .setValue(siteInfo.getStudyCount());
        view.getContainerTypeCount()
            .setValue(siteInfo.getContainerTypeCount());
        view.getTopContainerCount()
            .setValue(siteInfo.getTopContainerCount());
        view.getPatientCount()
            .setValue(siteInfo.getPatientCount());
        view.getAliquotedSpecimenCount()
            .setValue(siteInfo.getAliquotedSpecimenCount());
        view.getCollectionEventCount()
            .setValue(siteInfo.getCollectionEventCount());

        // TODO: set comment collection
        // view.getCommentCollection()

        view.getStudyCollection()
            .setValue(siteInfo.getStudyCollection());
        view.getContainerTypeCollection()
            .setValue(siteInfo.getContainerTypeCollection());
        view.getTopContainerCollection()
            .setValue(siteInfo.getTopContainerCollection());
    }
}
