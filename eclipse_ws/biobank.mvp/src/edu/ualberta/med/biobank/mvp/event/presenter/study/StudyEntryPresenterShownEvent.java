package edu.ualberta.med.biobank.mvp.event.presenter.study;

import com.google.gwt.event.shared.GwtEvent;

public class StudyEntryPresenterShownEvent extends
    GwtEvent<StudyEntryPresenterShownHandler> {
    private final Integer id;

    /**
     * Handler type.
     */
    private static Type<StudyEntryPresenterShownHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<StudyEntryPresenterShownHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<StudyEntryPresenterShownHandler>();
        }
        return TYPE;
    }

    public StudyEntryPresenterShownEvent(Integer id) {
        this.id = id;
    }

    public Integer getStudyId() {
        return id;
    }

    @Override
    public Type<StudyEntryPresenterShownHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StudyEntryPresenterShownHandler handler) {
        handler.onStudyEntryPresenterShown(this);
    }

}
