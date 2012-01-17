package edu.ualberta.med.biobank.common.action.site;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.model.Site;

public class SitePreDeleteChecks {

    private final Site site;

    public SitePreDeleteChecks(Site site) {
        this.site = site;
    }

    public void run(ActionContext context) {
        new CollectionIsEmptyCheck<Site>(
            Site.class, site, SitePeer.CONTAINER_TYPE_COLLECTION,
            site.getNameShort(), null).run(context);

        new CollectionIsEmptyCheck<Site>(
            Site.class, site, SitePeer.CONTAINER_COLLECTION,
            site.getNameShort(), null).run(context);

        new CollectionIsEmptyCheck<Site>(
            Site.class, site, SitePeer.PROCESSING_EVENT_COLLECTION,
            site.getNameShort(), null).run(context);
    }
}
