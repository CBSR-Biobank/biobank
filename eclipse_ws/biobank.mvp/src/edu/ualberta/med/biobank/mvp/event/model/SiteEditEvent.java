package edu.ualberta.med.biobank.mvp.event.model;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} needs to be editted.
 * 
 * @author jferland
 * 
 */
public class SiteEditEvent extends GwtEvent<SiteEditHandler> {
    private final Integer siteId;

    /**
     * Handler type.
     */
    private static Type<SiteEditHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SiteEditHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SiteEditHandler>();
        }
        return TYPE;
    }

    public SiteEditEvent(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    @Override
    public Type<SiteEditHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SiteEditHandler handler) {
        handler.onSiteEdit(this);
    }
}
