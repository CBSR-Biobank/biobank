package edu.ualberta.med.biobank.model.envers;

/**
 * Allows for having multiple revisions per transaction.
 * 
 * @author Jonathan Ferland
 * @see https://community.jboss.org/thread/208112?tstart=0
 */
public interface AuditWriter {
    /**
     * Flushes the session data, the audit data, and then clears the current
     * revision entity. This can be used to create multiple revisions in the
     * same transaction.
     */
    public void flush();
}
