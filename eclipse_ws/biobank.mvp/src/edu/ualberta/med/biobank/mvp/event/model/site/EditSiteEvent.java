package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} needs to be edited.
 * 
 * @author jferland
 * 
 */
public class EditSiteEvent extends GwtEvent<EditSiteHandler> {
    private final Integer siteId;

    /**
     * Handler type.
     */
    private static Type<EditSiteHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<EditSiteHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<EditSiteHandler>();
        }
        return TYPE;
    }

    public EditSiteEvent(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    @Override
    public Type<EditSiteHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(EditSiteHandler handler) {
        handler.onEditSite(this);
    }
}
