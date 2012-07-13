package edu.ualberta.med.biobank.auditor;

import org.hibernate.cfg.Configuration;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.event.AuditEventListener;
import org.hibernate.envers.synchronization.AuditProcess;
import org.hibernate.envers.synchronization.AuditProcessManager;

/**
 * Make a custom {@link AuditEventListener} so that we can use a custom
 * {@link AuditConfiguration} so that we can customise the
 * {@link AuditProcessManager} and its {@link AuditProcess}-es.
 * 
 * @author Jonathan Ferland
 */
public class CustomAuditEventListener extends AuditEventListener {
    private static final long serialVersionUID = 1L;

    private AuditConfiguration verCfg;

    @Override
    public void initialize(Configuration cfg) {
        verCfg = AuditConfiguration.getFor(cfg);
    }

    @Override
    public AuditConfiguration getVerCfg() {
        return verCfg;
    }
}
