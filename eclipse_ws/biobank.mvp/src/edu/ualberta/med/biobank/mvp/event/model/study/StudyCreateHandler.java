package edu.ualberta.med.biobank.mvp.event.model.study;

import com.google.gwt.event.shared.EventHandler;

public interface StudyCreateHandler extends EventHandler {
    public void onStudyCreate(StudyCreateEvent event);
}
