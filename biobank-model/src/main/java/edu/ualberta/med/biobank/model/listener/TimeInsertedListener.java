package edu.ualberta.med.biobank.model.listener;

import java.util.Date;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;

import edu.ualberta.med.biobank.model.HasTimeInserted;

public class TimeInsertedListener
    implements PreInsertEventListener {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof HasTimeInserted) {
            ((HasTimeInserted) entity).setTimeInserted(new Date());
        }
        return false;
    }
}
