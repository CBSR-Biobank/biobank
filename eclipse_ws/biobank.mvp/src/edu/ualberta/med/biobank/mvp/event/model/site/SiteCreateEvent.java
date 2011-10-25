package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} needs to be created.
 * 
 * @author jferland
 * 
 */
public class SiteCreateEvent extends GwtEvent<SiteCreateHandler> {
    /**
     * Handler type.
     */
    private static Type<SiteCreateHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SiteCreateHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SiteCreateHandler>();
        }
        return TYPE;
    }

    public SiteCreateEvent() {
    }

    @Override
    public Type<SiteCreateHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SiteCreateHandler handler) {
        handler.onSiteCreate(this);
    }
}
