package edu.ualberta.med.biobank.auditor;

import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.configuration.AuditEntitiesConfiguration;
import org.hibernate.event.Initializable;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import edu.ualberta.med.biobank.model.Revision;

/**
 * This class does things I would like to do by extending the Envers code, but
 * that is too difficult right now, or involves a big copy and paste.
 * <p>
 * Look for updates on the audit tables and ensure that the end revision number
 * is greater than the revision number (this check <em>should</em> happen in
 * {@link org.hibernate.envers.strategy.ValidityAuditStrategy}, but doesn't).
 * 
 * @author Jonathan Ferland
 */
// TODO: write a test case to check whether this does its job.
public class ValidEndRevisionListener
    implements PreUpdateEventListener, Initializable {
    private static final long serialVersionUID = 1L;

    private AuditConfiguration verCfg;

    @Override
    public void initialize(Configuration cfg) {
        verCfg = AuditConfiguration.getFor(cfg);
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        String entityName = event.getPersister().getEntityName();
        if (isEntityNameRevisionData(entityName)) {
            Object entity = event.getEntity();

            @SuppressWarnings("unchecked")
            Map<String, Object> revisionData = (Map<String, Object>) entity;
            checkEndRevision(revisionData);
        }
        return false;
    }

    private boolean isEntityNameRevisionData(String entityName) {
        return verCfg.getEntCfg()
            .getEntityNameForVersionsEntityName(entityName) != null;
    }

    @SuppressWarnings("nls")
    private void checkEndRevision(Map<String, Object> revisionData) {
        AuditEntitiesConfiguration auditEntCfg = verCfg.getAuditEntCfg();
        String originalIdPropName = auditEntCfg.getOriginalIdPropName();
        String revFieldName = auditEntCfg.getRevisionFieldName();
        String revEndFieldName = auditEntCfg.getRevisionEndFieldName();

        @SuppressWarnings("unchecked")
        Map<String, Object> originalId =
            (Map<String, Object>) revisionData.get(originalIdPropName);
        Revision revision = (Revision) originalId.get(revFieldName);
        Revision endRevision = (Revision) revisionData.get(revEndFieldName);

        if (endRevision != null && revision.getId() >= endRevision.getId()) {
            throw new RuntimeException("The end revision number/id must be" +
                " greater than the revision number/id of an audit entry," +
                " otherwise revision numbers are not strictly increasing for" +
                " modifications on the same entity.");
        }
    }
}
