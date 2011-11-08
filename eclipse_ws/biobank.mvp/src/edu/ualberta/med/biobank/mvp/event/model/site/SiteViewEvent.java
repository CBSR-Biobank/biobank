package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} needs to be Viewed.
 * 
 * @author jferland
 * 
 */
public class SiteViewEvent extends GwtEvent<SiteViewHandler> {
    private final Integer siteId;

    /**
     * Handler type.
     */
    private static Type<SiteViewHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SiteViewHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SiteViewHandler>();
        }
        return TYPE;
    }

    public SiteViewEvent(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    @Override
    public Type<SiteViewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SiteViewHandler handler) {
        handler.onSiteView(this);
    }
}
