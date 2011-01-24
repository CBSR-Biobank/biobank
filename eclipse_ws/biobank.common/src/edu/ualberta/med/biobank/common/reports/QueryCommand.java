package edu.ualberta.med.biobank.common.reports;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

public interface QueryCommand extends Serializable {

    public List<Object> start(Session s, BiobankApplicationService appService)
        throws ApplicationException;

}
