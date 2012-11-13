package edu.ualberta.med.biobank.model.listener;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

import edu.ualberta.med.biobank.model.HasInsertedBy;
import edu.ualberta.med.biobank.model.HasTimeInserted;
import edu.ualberta.med.biobank.model.HasTimeUpdated;
import edu.ualberta.med.biobank.model.HasUpdatedBy;
import edu.ualberta.med.biobank.model.context.ExecutingUser;
import edu.ualberta.med.biobank.model.context.ExecutingUserImpl;

public class EventMetaDataListener
    implements PreInsertEventListener, PreUpdateEventListener {
    private static final long serialVersionUID = 1L;

    private final ExecutingUser executingUser = new ExecutingUserImpl();

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof HasTimeInserted) {
            ((HasTimeInserted) entity).setTimeInserted(System.currentTimeMillis());
        }
        if (entity instanceof HasInsertedBy) {
            ((HasInsertedBy) entity).setInsertedBy(executingUser.get());
        }
        onPrePersist(entity);
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        onPrePersist(entity);
        return false;
    }

    private void onPrePersist(Object entity) {
        if (entity instanceof HasTimeUpdated) {
            ((HasTimeUpdated) entity).setTimeUpdated(System.currentTimeMillis());
        }
        if (entity instanceof HasUpdatedBy) {
            ((HasUpdatedBy) entity).setUpdatedBy(executingUser.get());
        }
    }
}
