package edu.ualberta.med.biobank.mvp.event.presenter.study;

import com.google.gwt.event.shared.EventHandler;

public interface StudyEntryPresenterShownHandler extends EventHandler {
    public void onStudyEntryPresenterShown(StudyEntryPresenterShownEvent event);
}
