package edu.ualberta.med.biobank.auditor;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.envers.revisioninfo.RevisionInfoGenerator;
import org.hibernate.envers.synchronization.AuditProcess;

public class CustomAuditProcess extends AuditProcess {
    public CustomAuditProcess(RevisionInfoGenerator revisionInfoGenerator,
        SessionImplementor session) {
        super(revisionInfoGenerator, session);
    }
}
