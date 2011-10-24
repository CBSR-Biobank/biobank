package edu.ualberta.med.biobank.mvp.event.presenter.site;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.model.Site;

/**
 * Event fired whenever a {@link Site} is created or updated.
 * 
 * @author jferland
 * 
 */
public class ShowSiteViewPresenterEvent extends
    GwtEvent<ShowSiteViewPresenterHandler> {
    private final Integer siteId;

    /**
     * Handler type.
     */
    private static Type<ShowSiteViewPresenterHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<ShowSiteViewPresenterHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ShowSiteViewPresenterHandler>();
        }
        return TYPE;
    }

    public ShowSiteViewPresenterEvent(Integer id) {
        this.siteId = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    @Override
    public Type<ShowSiteViewPresenterHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowSiteViewPresenterHandler handler) {
        handler.onShowSiteViewPresenter(this);
    }

}
