package edu.ualberta.med.biobank.common.wrappers.actions;

import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.query.SDKQuery;

import org.hibernate.Session;

/**
 * Interface for queries to be given a {@code Session} instance and potentially
 * throw exception if a problem is detected.
 * 
 * @author jferland
 * 
 */
public interface BiobankSessionAction extends SDKQuery {
    public Object doAction(Session session) throws BiobankSessionException;
}