package edu.ualberta.med.biobank.common.scanprocess.data;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.server.scanprocess.ServerProcess;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public interface ProcessData extends Serializable {

    public ServerProcess getProcessInstance(
        WritableApplicationService appService, User user);

}
