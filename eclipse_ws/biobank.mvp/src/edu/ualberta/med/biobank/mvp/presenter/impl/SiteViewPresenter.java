package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.Collection;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.info.SiteContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.exception.InitPresenterException;
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

        ValueField<Collection<StudyCountInfo>> getStudyCollection();

        ValueField<Collection<SiteContainerTypeInfo>> getContainerTypeCollection();

        ValueField<Collection<Container>> getTopContainerCollection();
    }

    @Inject
    public SiteViewPresenter(View view, EventBus eventBus, Dispatcher dispatcher) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
    }

    @Override
    protected void onUnbind() {
    }

    public View viewSite(Integer siteId) throws InitPresenterException {
        return load(new SiteView(siteId));
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
            .setValue(siteInfo.getSpecimenCount());
        view.getCollectionEventCount()
            .setValue(siteInfo.getProcessingEventCount());

        // TODO: set comment collection
        // view.getCommentCollection()

        view.getStudyCollection()
            .setValue(siteInfo.getStudyCollection());
        view.getContainerTypeCollection()
            .setValue(siteInfo.getContainerTypeCollection());
        view.getTopContainerCollection()
            .setValue(siteInfo.getTopContainerCollection());
    }

    private class SiteView implements Loadable {
        private final Integer newSiteId;

        public SiteView(Integer newSiteId) {
            this.newSiteId = newSiteId;
        }

        @Override
        public void run() throws Exception {
            siteId = newSiteId;

            SiteInfo siteInfo = dispatcher.exec(new SiteGetInfoAction(siteId));

            viewSite(siteInfo);
        }
    }
}
