package edu.ualberta.med.biobank.auditor;

import java.util.Map;
import java.util.WeakHashMap;

import org.hibernate.cfg.Configuration;
import org.hibernate.envers.configuration.AuditConfiguration;

public class CustomAuditConfiguration extends AuditConfiguration {

    public CustomAuditConfiguration(Configuration cfg) {
        super(cfg);
    }

    private static Map<Configuration, CustomAuditConfiguration> cfgs =
        new WeakHashMap<Configuration, CustomAuditConfiguration>();

    public synchronized static CustomAuditConfiguration getFor(Configuration cfg) {
        CustomAuditConfiguration verCfg = cfgs.get(cfg);

        if (verCfg == null) {
            verCfg = new CustomAuditConfiguration(cfg);
            cfgs.put(cfg, verCfg);

            cfg.buildMappings();
        }

        return verCfg;
    }
}
