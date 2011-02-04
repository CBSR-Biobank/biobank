package edu.ualberta.med.biobank.server.applicationservice;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;

import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.BiobankThreadVariable;
import edu.ualberta.med.biobank.server.LocalInfo;
import edu.ualberta.med.biobank.server.LocalInfo.SiteInfo;
import edu.ualberta.med.biobank.server.LocalInfo.Type;

/**
 * 
 * This class intercepts all the related events when the client application
 * performs the persistence such as save, update and delete. stages.
 * 
 * See use in application-config.xml
 * 
 */
public class BiobankObjectStateInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;

    /**
     * This method gets called before an object is saved.
     */
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
        String[] propertyNames, org.hibernate.type.Type[] types)
        throws CallbackException {
        if (entity instanceof Site) {
            addSiteInfo((Integer) id, state, propertyNames, Type.INSERT);
        }
        return false;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state,
        String[] propertyNames, org.hibernate.type.Type[] types) {
        if (entity instanceof Site) {
            addSiteInfo((Integer) id, state, propertyNames, Type.DELETE);
        }
    }

    private void addSiteInfo(Integer id, Object[] state,
        String[] propertyNames, Type type) {
        LocalInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new LocalInfo();
        int i = 0;
        String nameShort = null;
        while (nameShort == null) {
            if (SitePeer.NAME_SHORT.getName().equals(propertyNames[i]))
                nameShort = (String) state[i];
            i++;
        }
        userInfo.addNewSiteInfo(id, nameShort, type);
        BiobankThreadVariable.set(userInfo);
    }

    /**
     * Really write logs registered in the buffer.
     */
    @Override
    public void afterTransactionCompletion(Transaction tx) {
        LocalInfo info = BiobankThreadVariable.get();
        if (info.hasSiteInfos() && tx.wasCommitted() && !tx.wasRolledBack()) {
            Set<Entry<Integer, SiteInfo>> entries = info
                .getSitesInfosEntrySet();
            if (entries != null)
                for (Entry<Integer, SiteInfo> entry : entries) {
                    if (entry.getValue().type == Type.INSERT)
                        BiobankSecurityUtil.newSiteSecurity(entry.getKey(),
                            entry.getValue().nameShort);
                    else if (entry.getValue().type == Type.DELETE)
                        BiobankSecurityUtil.deleteSiteSecurity(entry.getKey(),
                            entry.getValue().nameShort);
                }
        }
        info.clearSiteInfos();
        // set back the local thread variable
        BiobankThreadVariable.set(info);
    }

}
