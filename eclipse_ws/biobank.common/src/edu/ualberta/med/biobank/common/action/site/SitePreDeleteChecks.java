package edu.ualberta.med.biobank.common.action.site;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class SitePreDeleteChecks {

    private final Site site;

    public SitePreDeleteChecks(Site site) {
        this.site = site;
    }

    public void run(User user, Session session) {
        new CollectionIsEmptyCheck<Site>(
            Site.class, site, SitePeer.CONTAINER_TYPE_COLLECTION,
            site.getNameShort(), null).run(user, session);

        new CollectionIsEmptyCheck<Site>(
            Site.class, site, SitePeer.CONTAINER_COLLECTION,
            site.getNameShort(), null).run(user, session);

        new CollectionIsEmptyCheck<Site>(
            Site.class, site, SitePeer.PROCESSING_EVENT_COLLECTION,
            site.getNameShort(), null).run(user, session);
    }
}
