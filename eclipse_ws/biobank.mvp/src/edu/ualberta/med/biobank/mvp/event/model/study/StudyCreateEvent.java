package edu.ualberta.med.biobank.mvp.event.model.study;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Study;

/**
 * Event fired whenever a {@link Study} needs to be created.
 * 
 */
public class StudyCreateEvent extends GwtEvent<StudyCreateHandler> {
    private final Integer studyId;

    /**
     * Handler type.
     */
    private static Type<StudyCreateHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<StudyCreateHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<StudyCreateHandler>();
        }
        return TYPE;
    }

    public StudyCreateEvent(Integer studyId) {
        this.studyId = studyId;
    }

    public Integer getStudyId() {
        return studyId;
    }

    @Override
    public Type<StudyCreateHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StudyCreateHandler handler) {
        handler.onStudyCreate(this);
    }
}
