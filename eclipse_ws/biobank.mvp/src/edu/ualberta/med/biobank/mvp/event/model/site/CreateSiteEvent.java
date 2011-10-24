package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} needs to be created.
 * 
 * @author jferland
 * 
 */
public class CreateSiteEvent extends GwtEvent<CreateSiteHandler> {
    /**
     * Handler type.
     */
    private static Type<CreateSiteHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<CreateSiteHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<CreateSiteHandler>();
        }
        return TYPE;
    }

    public CreateSiteEvent() {
    }

    @Override
    public Type<CreateSiteHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CreateSiteHandler handler) {
        handler.onCreateSite(this);
    }
}
