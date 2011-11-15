package edu.ualberta.med.biobank.mvp.event.model.study;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Study;

/**
 * Event fired whenever a {@link Study} needs to be edited.
 * 
 */
public class StudyEditEvent extends GwtEvent<StudyEditHandler> {
    private final Integer studyId;

    /**
     * Handler type.
     */
    private static Type<StudyEditHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<StudyEditHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<StudyEditHandler>();
        }
        return TYPE;
    }

    public StudyEditEvent(Integer studyId) {
        this.studyId = studyId;
    }

    public Integer getStudyId() {
        return studyId;
    }

    @Override
    public Type<StudyEditHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StudyEditHandler handler) {
        handler.onStudyEdit(this);
    }
}
