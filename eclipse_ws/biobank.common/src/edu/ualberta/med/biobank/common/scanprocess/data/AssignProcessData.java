package edu.ualberta.med.biobank.common.scanprocess.data;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.server.scanprocess.AssignProcess;
import edu.ualberta.med.biobank.server.scanprocess.ServerProcess;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AssignProcessData extends ProcessWithPallet {

    private static final long serialVersionUID = 1L;

    public AssignProcessData(ContainerWrapper pallet) {
        super(pallet);
    }

    @Override
    public ServerProcess getProcessInstance(
        WritableApplicationService appService, User user) {
        return new AssignProcess(appService, this, user);
    }

}
