package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} is created or updated.
 * 
 * @author jferland
 * 
 */
public class SiteChangedEvent extends GwtEvent<SiteChangedHandler> {
    private final Integer id;

    /**
     * Handler type.
     */
    private static Type<SiteChangedHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SiteChangedHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SiteChangedHandler>();
        }
        return TYPE;
    }

    public SiteChangedEvent(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return id;
    }

    @Override
    public Type<SiteChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SiteChangedHandler handler) {
        handler.onSiteChanged(this);
    }
}
