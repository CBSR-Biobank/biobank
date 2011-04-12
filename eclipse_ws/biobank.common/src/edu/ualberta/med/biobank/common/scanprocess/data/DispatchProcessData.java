package edu.ualberta.med.biobank.common.scanprocess.data;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.server.scanprocess.DispatchCreateProcess;
import edu.ualberta.med.biobank.server.scanprocess.DispatchReceiveProcess;
import edu.ualberta.med.biobank.server.scanprocess.ServerProcess;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchProcessData extends ProcessWithPallet {

    private static final long serialVersionUID = 1L;

    private Integer senderId;
    private Map<Integer, DispatchSpecimenState> currentDispatchSpecimenIds;
    private boolean creation;
    private boolean errorIfAlreadyAdded;

    public DispatchProcessData(ContainerWrapper pallet,
        DispatchWrapper dispatch, boolean creation, boolean errorIfAlreadyAdded) {
        super(pallet);
        if (dispatch != null) {
            currentDispatchSpecimenIds = new HashMap<Integer, DispatchSpecimenState>();
            for (DispatchSpecimenWrapper dsw : dispatch
                .getDispatchSpecimenCollection(false)) {
                currentDispatchSpecimenIds.put(dsw.getSpecimen().getId(),
                    dsw.getDispatchSpecimenState());
            }
            senderId = dispatch.getSenderCenter() == null ? null : dispatch
                .getSenderCenter().getId();
        }
        this.creation = creation;
        this.errorIfAlreadyAdded = errorIfAlreadyAdded;
    }

    @Override
    public ServerProcess getProcessInstance(
        WritableApplicationService appService, User user) {
        if (isCreation())
            return new DispatchCreateProcess(appService, this, user);
        return new DispatchReceiveProcess(appService, this, user);
    }

    public Map<Integer, DispatchSpecimenState> getCurrentDispatchSpecimenIds() {
        return currentDispatchSpecimenIds;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public boolean isCreation() {
        return creation;
    }

    public boolean isErrorIfAlreadyAdded() {
        return errorIfAlreadyAdded;
    }

}
