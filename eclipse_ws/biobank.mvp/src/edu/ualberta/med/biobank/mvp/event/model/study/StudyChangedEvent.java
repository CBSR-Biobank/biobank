package edu.ualberta.med.biobank.mvp.event.model.study;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} is created or updated.
 * 
 */
public class StudyChangedEvent extends GwtEvent<StudyChangedHandler> {
    private final Integer id;

    /**
     * Handler type.
     */
    private static Type<StudyChangedHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<StudyChangedHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<StudyChangedHandler>();
        }
        return TYPE;
    }

    public StudyChangedEvent(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return id;
    }

    @Override
    public Type<StudyChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StudyChangedHandler handler) {
        handler.onStudyChanged(this);
    }
}
