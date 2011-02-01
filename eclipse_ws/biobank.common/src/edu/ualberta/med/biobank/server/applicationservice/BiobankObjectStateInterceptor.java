package edu.ualberta.med.biobank.server.applicationservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.logging.user.BiobankThreadVariable;
import edu.ualberta.med.biobank.server.logging.user.UserInfo;
import edu.ualberta.med.biobank.server.logging.user.UserInfo.SiteInfo;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.dao.ProtectionElementSearchCriteria;
import gov.nih.nci.security.dao.SearchCriteria;

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

    private static Logger log = Logger
        .getLogger(BiobankObjectStateInterceptor.class.getName());

    /**
     * This method gets called before an object is saved.
     */
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
        String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof Site) {
            addSiteInfo(
                (Integer) id,
                state,
                propertyNames,
                edu.ualberta.med.biobank.server.logging.user.UserInfo.Type.INSERT);
        }
        return false;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state,
        String[] propertyNames, Type[] types) {
        if (entity instanceof Site) {
            addSiteInfo(
                (Integer) id,
                state,
                propertyNames,
                edu.ualberta.med.biobank.server.logging.user.UserInfo.Type.DELETE);
        }
    }

    private void addSiteInfo(Integer id, Object[] state,
        String[] propertyNames,
        edu.ualberta.med.biobank.server.logging.user.UserInfo.Type type) {
        UserInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new UserInfo();
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
        UserInfo user = BiobankThreadVariable.get();
        if (user.hasSiteInfos() && tx.wasCommitted() && !tx.wasRolledBack()) {
            Set<Entry<Integer, SiteInfo>> entries = user
                .getSitesInfosEntrySet();
            if (entries != null)
                for (Entry<Integer, SiteInfo> entry : entries) {
                    if (entry.getValue().type == edu.ualberta.med.biobank.server.logging.user.UserInfo.Type.INSERT)
                        newSiteSecurity(entry.getKey(),
                            entry.getValue().nameShort);
                    else if (entry.getValue().type == edu.ualberta.med.biobank.server.logging.user.UserInfo.Type.DELETE)
                        deleteSiteSecurity(entry.getKey(),
                            entry.getValue().nameShort);
                }
        }
        user.clearSiteInfos();
        // set back the local thread variable
        BiobankThreadVariable.set(user);
    }

    private void newSiteSecurity(Integer siteId, String nameShort) {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankApplicationServiceImpl.APPLICATION_CONTEXT_NAME);
            Application currentApplication = upm
                .getApplication(BiobankApplicationServiceImpl.APPLICATION_CONTEXT_NAME);
            // Create protection element for the site
            ProtectionElement pe = new ProtectionElement();
            pe.setApplication(currentApplication);
            pe.setProtectionElementName(BiobankApplicationServiceImpl.SITE_CLASS_NAME
                + "/" + nameShort);
            pe.setProtectionElementDescription(nameShort);
            pe.setObjectId(BiobankApplicationServiceImpl.SITE_CLASS_NAME);
            pe.setAttribute("id");
            pe.setValue(siteId.toString());
            upm.createProtectionElement(pe);

            // Create a new protection group for this protection element only
            ProtectionGroup pg = new ProtectionGroup();
            pg.setApplication(currentApplication);
            pg.setProtectionGroupName(nameShort + " site");
            pg.setProtectionGroupDescription("Protection group for site "
                + nameShort + " (id=" + siteId + ")");
            pg.setProtectionElements(new HashSet<ProtectionElement>(Arrays
                .asList(pe)));
            // parent will be the "all sites" protection group
            ProtectionGroup allSitePg = upm
                .getProtectionGroupById(BiobankApplicationServiceImpl.ALL_SITES_PG_ID);
            pg.setParentProtectionGroup(allSitePg);
            upm.createProtectionGroup(pg);
        } catch (Exception e) {
            log.error("error adding new site security", e);
            throw new RuntimeException("Error adding new site " + siteId + ":"
                + nameShort + " security:" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteSiteSecurity(Integer siteId, String nameShort) {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankApplicationServiceImpl.APPLICATION_CONTEXT_NAME);
            ProtectionElement searchPE = new ProtectionElement();
            searchPE.setObjectId(Site.class.getName());
            searchPE.setAttribute("id");
            searchPE.setValue(siteId.toString());
            SearchCriteria sc = new ProtectionElementSearchCriteria(searchPE);
            List<ProtectionElement> peToDelete = upm.getObjects(sc);
            if (peToDelete == null || peToDelete.size() == 0) {
                return;
            }
            List<String> pgIdsToDelete = new ArrayList<String>();
            for (ProtectionElement pe : peToDelete) {
                Set<ProtectionGroup> pgs = upm.getProtectionGroups(pe
                    .getProtectionElementId().toString());
                for (ProtectionGroup pg : pgs) {
                    // remove the protection group only if it contains only
                    // this protection element and is not the main site
                    // admin group
                    String pgId = pg.getProtectionGroupId().toString();
                    if (!pgId
                        .equals(BiobankApplicationServiceImpl.ALL_SITES_PG_ID)
                        && upm.getProtectionElements(pgId).size() == 1) {
                        pgIdsToDelete.add(pgId);
                    }
                }
                upm.removeProtectionElement(pe.getProtectionElementId()
                    .toString());
            }
            for (String pgId : pgIdsToDelete) {
                upm.removeProtectionGroup(pgId);
            }
        } catch (Exception e) {
            log.error("error deleting site security", e);
            throw new RuntimeException("Error deleting site " + siteId + ":"
                + nameShort + " security: " + e.getMessage());
        }

    }
}
