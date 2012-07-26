package edu.ualberta.med.biobank.model.listener;

import java.util.Date;

import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

import edu.ualberta.med.biobank.model.HasTimeUpdated;

public class TimeUpdatedListener
    implements PreUpdateEventListener {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof HasTimeUpdated) {
            ((HasTimeUpdated) entity).setTimeUpdated(new Date());
        }
        return false;
    }
}
