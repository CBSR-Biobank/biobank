package edu.ualberta.med.biobank.model.listener;

import java.util.Date;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;

import edu.ualberta.med.biobank.model.HasInsertTime;

public class InsertTimeListener
    implements PreInsertEventListener {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof HasInsertTime) {
            ((HasInsertTime) entity).setInsertTime(new Date());
        }
        return false;
    }
}
