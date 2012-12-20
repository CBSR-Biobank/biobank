package edu.ualberta.med.biobank.reports;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.omg.CORBA.portable.ApplicationException;

public interface QueryCommand extends Serializable {

    public List<Object> start(Session s, BiobankApplicationService appService)
        throws ApplicationException;

}
