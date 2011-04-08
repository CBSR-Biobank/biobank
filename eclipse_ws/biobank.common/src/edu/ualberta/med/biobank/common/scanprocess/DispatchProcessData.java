package edu.ualberta.med.biobank.common.scanprocess;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.server.scanprocess.ServerProcess;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchProcessData implements ProcessData {

    private static final long serialVersionUID = 1L;

    @Override
    public ServerProcess getProcessInstance(
        WritableApplicationService appService, User user) {
        return null;
    }
}
