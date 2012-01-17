package edu.ualberta.med.biobank.common.action.constraint;

import org.hibernate.Session;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Site;

public class PreUpdateHandler implements PreUpdateEventListener {
    private static final long serialVersionUID = 1L;

    public PreUpdateHandler() {
        reg(SitePeer.NAME, new NotEmptyValidator("Name is required."));
        reg(SitePeer.NAME_SHORT, new NotEmptyValidator(
            "Short name is required."));
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        String entityName = event.getPersister().getEntityName();
        Session session = event.getSession();

        if (Site.class.getName().equals(entityName)) {
            Site site = (Site) entity;
            // site.getName().
        }

        return false;
    }

    private void reg(Property<?, ?> property, IConstraintValidator validator) {

    }
}
