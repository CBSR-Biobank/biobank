package edu.ualberta.med.biobank.mvp.event.presenter.study;

import com.google.gwt.event.shared.GwtEvent;

public class StudyViewPresenterShowEvent extends
    GwtEvent<StudyViewPresenterShowHandler> {
    private final Integer siteId;

    /**
     * Handler type.
     */
    private static Type<StudyViewPresenterShowHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<StudyViewPresenterShowHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<StudyViewPresenterShowHandler>();
        }
        return TYPE;
    }

    public StudyViewPresenterShowEvent(Integer id) {
        this.siteId = id;
    }

    public Integer getStudyId() {
        return siteId;
    }

    @Override
    public Type<StudyViewPresenterShowHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StudyViewPresenterShowHandler handler) {
        handler.onStudyViewPresenterShow(this);
    }

}
