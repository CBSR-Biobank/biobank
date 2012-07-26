package edu.ualberta.med.biobank.model.listener;

import java.util.Date;

import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

import edu.ualberta.med.biobank.model.HasLastUpdateTime;

public class LastUpdateTimeListener
    implements PreUpdateEventListener {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof HasLastUpdateTime) {
            ((HasLastUpdateTime) entity).setLastUpdateTime(new Date());
        }
        return false;
    }
}
