package edu.ualberta.med.biobank.mvp.event.model;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} is created or updated.
 * 
 * @author jferland
 * 
 */
public class SiteChangeEvent extends GwtEvent<SiteChangeHandler> {
    private final Integer id;

    /**
     * Handler type.
     */
    private static Type<SiteChangeHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SiteChangeHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SiteChangeHandler>();
        }
        return TYPE;
    }

    public SiteChangeEvent(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return id;
    }

    @Override
    public Type<SiteChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SiteChangeHandler handler) {
        handler.onSiteChange(this);
    }
}
