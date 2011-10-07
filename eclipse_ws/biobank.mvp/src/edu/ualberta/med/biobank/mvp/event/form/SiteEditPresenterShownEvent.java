package edu.ualberta.med.biobank.mvp.event.form;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} is created or updated.
 * 
 * @author jferland
 * 
 */
public class SiteEditPresenterShownEvent extends GwtEvent<SiteEditPresenterShownHandler> {
    private final Integer id;

    /**
     * Handler type.
     */
    private static Type<SiteEditPresenterShownHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SiteEditPresenterShownHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SiteEditPresenterShownHandler>();
        }
        return TYPE;
    }

    public SiteEditPresenterShownEvent(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return id;
    }

    @Override
    public Type<SiteEditPresenterShownHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SiteEditPresenterShownHandler handler) {
        handler.onSiteEditPresenterShown(this);
    }

}
