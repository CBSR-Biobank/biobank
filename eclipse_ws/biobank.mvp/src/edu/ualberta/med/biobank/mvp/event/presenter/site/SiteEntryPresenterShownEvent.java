package edu.ualberta.med.biobank.mvp.event.presenter.site;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} is created or updated.
 * 
 * @author jferland
 * 
 */
public class SiteEntryPresenterShownEvent extends GwtEvent<SiteEntryPresenterShownHandler> {
    private final Integer id;

    /**
     * Handler type.
     */
    private static Type<SiteEntryPresenterShownHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SiteEntryPresenterShownHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SiteEntryPresenterShownHandler>();
        }
        return TYPE;
    }

    public SiteEntryPresenterShownEvent(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return id;
    }

    @Override
    public Type<SiteEntryPresenterShownHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SiteEntryPresenterShownHandler handler) {
        handler.onSiteEntryPresenterShown(this);
    }

}
