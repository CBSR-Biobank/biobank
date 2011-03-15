package edu.ualberta.med.biobank.server.interceptor;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;

import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.server.BiobankThreadVariable;
import edu.ualberta.med.biobank.server.LocalInfo;
import edu.ualberta.med.biobank.server.LocalInfo.ActionType;
import edu.ualberta.med.biobank.server.LocalInfo.CenterInfo;
import edu.ualberta.med.biobank.server.applicationservice.BiobankSecurityUtil;

/**
 * 
 * This class intercepts all the related events when the client application
 * performs the persistence such as save, update and delete. stages.
 * 
 * See use in application-config.xml
 * 
 */
public class SessionInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;

    /**
     * This method gets called before an object is saved.
     */
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
        String[] propertyNames, org.hibernate.type.Type[] types)
        throws CallbackException {
        if (entity instanceof Center) {
            addCenterInfo((Integer) id, state, propertyNames,
                entity.getClass(), ActionType.INSERT);
        }
        return false;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state,
        String[] propertyNames, org.hibernate.type.Type[] types) {
        if (entity instanceof Center) {
            addCenterInfo((Integer) id, state, propertyNames,
                entity.getClass(), ActionType.DELETE);
        }
    }

    private void addCenterInfo(Integer id, Object[] state,
        String[] propertyNames, Class<?> centerClass, ActionType actionType) {
        LocalInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new LocalInfo();
        String nameShort = getNameShort(state, propertyNames);
        userInfo.addNewCenterInfo(id, nameShort, centerClass, actionType);
        BiobankThreadVariable.set(userInfo);
    }

    private String getNameShort(Object[] state, String[] propertyNames) {
        int i = 0;
        String nameShort = null;
        while (nameShort == null && i < propertyNames.length) {
            if (CenterPeer.NAME_SHORT.getName().equals(propertyNames[i]))
                nameShort = (String) state[i];
            i++;
        }
        return nameShort;
    }

    /**
     * Really write logs registered in the buffer.
     */
    @Override
    public void afterTransactionCompletion(Transaction tx) {
        LocalInfo info = BiobankThreadVariable.get();
        if (info.hasCenterInfos() && tx.wasCommitted() && !tx.wasRolledBack()) {
            Set<Entry<Integer, CenterInfo>> entries = info
                .getCenterInfosEntrySet();
            if (entries != null)
                for (Entry<Integer, CenterInfo> entry : entries) {
                    if (entry.getValue().type == ActionType.INSERT)
                        BiobankSecurityUtil.newCenterSecurity(entry.getKey(),
                            entry.getValue().nameShort,
                            entry.getValue().centerClass);
                    else if (entry.getValue().type == ActionType.DELETE)
                        BiobankSecurityUtil.deleteCenterSecurity(
                            entry.getKey(), entry.getValue().nameShort,
                            entry.getValue().centerClass);
                }
        }
        info.clearCenterInfos();
        // set back the local thread variable
        BiobankThreadVariable.set(info);
    }

}
