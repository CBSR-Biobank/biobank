package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.event.ExceptionEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteCreateEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteCreateHandler;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteEditEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteEditHandler;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteViewEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteViewHandler;
import edu.ualberta.med.biobank.mvp.event.model.study.StudyCreateEvent;
import edu.ualberta.med.biobank.mvp.event.model.study.StudyCreateHandler;
import edu.ualberta.med.biobank.mvp.event.model.study.StudyEditEvent;
import edu.ualberta.med.biobank.mvp.event.model.study.StudyEditHandler;
import edu.ualberta.med.biobank.mvp.exception.InitPresenterException;
import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter.View;
import edu.ualberta.med.biobank.mvp.view.IFormView;
import edu.ualberta.med.biobank.mvp.view.IView;

public class FormManagerPresenter extends AbstractPresenter<View> {
    private Provider<SiteEntryPresenter> siteEntryPresenterProvider;
    private Provider<SiteViewPresenter> siteViewPresenterProvider;
    private Provider<StudyEntryPresenter> studyEntryPresenterProvider;

    public interface View extends IView {
        void openForm(IFormView view);
    }

    @Inject
    public FormManagerPresenter(View view, EventBus eventBus,
        Provider<SiteEntryPresenter> siteEntryPresenterProvider,
        Provider<SiteViewPresenter> siteViewPresenterProvider,
        Provider<StudyEntryPresenter> studyEntryPresenterProvider) {
        super(view, eventBus);
        this.siteEntryPresenterProvider = siteEntryPresenterProvider;
        this.siteViewPresenterProvider = siteViewPresenterProvider;
        this.studyEntryPresenterProvider = studyEntryPresenterProvider;
    }

    @Override
    protected void onBind() {
        registerHandler(eventBus.addHandler(SiteEditEvent.getType(),
            new SiteEditHandler() {
                @Override
                public void onSiteEdit(SiteEditEvent event) {
                    doSiteEdit(event.getSiteId());
                }
            }));

        registerHandler(eventBus.addHandler(SiteCreateEvent.getType(),
            new SiteCreateHandler() {
                @Override
                public void onSiteCreate(SiteCreateEvent event) {
                    doSiteCreate();
                }
            }));

        registerHandler(eventBus.addHandler(SiteViewEvent.getType(),
            new SiteViewHandler() {
                @Override
                public void onSiteView(SiteViewEvent event) {
                    doSiteView(event.getSiteId());
                }
            }));

        registerHandler(eventBus.addHandler(StudyEditEvent.getType(),
            new StudyEditHandler() {
                @Override
                public void onStudyEdit(StudyEditEvent event) {
                    doStudyEdit(event.getStudyId());
                }
            }));

        registerHandler(eventBus.addHandler(StudyCreateEvent.getType(),
            new StudyCreateHandler() {
                @Override
                public void onStudyCreate(StudyCreateEvent event) {
                    doStudyCreate();
                }
            }));
    }

    @Override
    protected void onUnbind() {
    }

    private void doSiteEdit(Integer siteId) {
        SiteEntryPresenter presenter = siteEntryPresenterProvider.get();
        presenter.bind();

        try {
            view.openForm(presenter.editSite(siteId));
        } catch (InitPresenterException caught) {
            eventBus.fireEvent(new ExceptionEvent(caught));
        }
    }

    private void doSiteCreate() {
        SiteEntryPresenter presenter = siteEntryPresenterProvider.get();
        presenter.bind();

        try {
            view.openForm(presenter.createSite());
        } catch (InitPresenterException caught) {
            eventBus.fireEvent(new ExceptionEvent(caught));
        }
    }

    private void doSiteView(Integer siteId) {
        SiteViewPresenter presenter = siteViewPresenterProvider.get();
        presenter.bind();

        try {
            view.openForm(presenter.viewSite(siteId));
        } catch (InitPresenterException caught) {
            eventBus.fireEvent(new ExceptionEvent(caught));
        }
    }

    private void doStudyEdit(Integer studyId) {
        StudyEntryPresenter presenter = studyEntryPresenterProvider.get();
        presenter.bind();

        try {
            view.openForm(presenter.editStudy(studyId));
        } catch (InitPresenterException caught) {
            eventBus.fireEvent(new ExceptionEvent(caught));
        }
    }

    private void doStudyCreate() {
        StudyEntryPresenter presenter = studyEntryPresenterProvider.get();
        presenter.bind();

        try {
            view.openForm(presenter.createStudy());
        } catch (InitPresenterException caught) {
            eventBus.fireEvent(new ExceptionEvent(caught));
        }
    }
}
