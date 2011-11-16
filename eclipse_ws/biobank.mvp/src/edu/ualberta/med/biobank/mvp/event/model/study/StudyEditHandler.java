package edu.ualberta.med.biobank.mvp.event.model.study;

import com.google.gwt.event.shared.EventHandler;

public interface StudyEditHandler extends EventHandler {
    public void onStudyEdit(StudyEditEvent event);
}
