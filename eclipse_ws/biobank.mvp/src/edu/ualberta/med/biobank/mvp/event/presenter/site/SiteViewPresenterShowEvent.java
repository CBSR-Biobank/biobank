package edu.ualberta.med.biobank.mvp.event.presenter.site;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} is created or updated.
 * 
 * @author jferland
 * 
 */
public class SiteViewPresenterShowEvent extends
    GwtEvent<SiteViewPresenterShowHandler> {
    private final Integer siteId;

    /**
     * Handler type.
     */
    private static Type<SiteViewPresenterShowHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SiteViewPresenterShowHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SiteViewPresenterShowHandler>();
        }
        return TYPE;
    }

    public SiteViewPresenterShowEvent(Integer id) {
        this.siteId = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    @Override
    public Type<SiteViewPresenterShowHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SiteViewPresenterShowHandler handler) {
        handler.onSiteViewPresenterShow(this);
    }

}
