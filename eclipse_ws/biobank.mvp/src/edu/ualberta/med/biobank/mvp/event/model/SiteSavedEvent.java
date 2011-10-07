package edu.ualberta.med.biobank.mvp.event.model;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} is created or updated.
 * 
 * @author jferland
 * 
 */
public class SiteSavedEvent extends GwtEvent<SiteSavedHandler> {
    private final Integer id;

    /**
     * Handler type.
     */
    private static Type<SiteSavedHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SiteSavedHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SiteSavedHandler>();
        }
        return TYPE;
    }

    public SiteSavedEvent(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return id;
    }

    @Override
    public Type<SiteSavedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SiteSavedHandler handler) {
        handler.onSiteSaved(this);
    }

}
