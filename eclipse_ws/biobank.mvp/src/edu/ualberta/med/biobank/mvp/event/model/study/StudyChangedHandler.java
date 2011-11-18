package edu.ualberta.med.biobank.mvp.event.model.study;

import com.google.gwt.event.shared.EventHandler;

public interface StudyChangedHandler extends EventHandler {
    public void onStudyChanged(StudyChangedEvent event);
}
