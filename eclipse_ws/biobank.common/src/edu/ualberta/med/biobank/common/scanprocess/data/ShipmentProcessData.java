package edu.ualberta.med.biobank.common.scanprocess.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.ItemState;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ItemWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.server.scanprocess.DispatchCreateProcess;
import edu.ualberta.med.biobank.server.scanprocess.ServerProcess;
import edu.ualberta.med.biobank.server.scanprocess.ShipmentReceiveProcess;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ShipmentProcessData extends ProcessWithPallet {

    private static final long serialVersionUID = 1L;

    // used for dispatch sending only
    private Integer senderId;

    private Map<Integer, ItemState> currentDispatchSpecimenIds;
    private boolean creation;
    private boolean errorIfAlreadyAdded;

    public ShipmentProcessData(ContainerWrapper pallet, RequestWrapper request,
        boolean creation, boolean errorIfAlreadyAdded) {
        super(pallet);
        init(request.getRequestSpecimenCollection(false), creation,
            errorIfAlreadyAdded, null);
    }

    public ShipmentProcessData(ContainerWrapper pallet,
        DispatchWrapper dispatch, boolean creation, boolean errorIfAlreadyAdded) {
        super(pallet);
        init(dispatch.getDispatchSpecimenCollection(false), creation,
            errorIfAlreadyAdded, dispatch.getSenderCenter());
    }

    private void init(List<? extends ItemWrapper> items, boolean creation,
        boolean errorIfAlreadyAdded, CenterWrapper<?> sender) {
        if (items != null) {
            currentDispatchSpecimenIds = new HashMap<Integer, ItemState>();
            for (ItemWrapper iw : items) {
                currentDispatchSpecimenIds.put(iw.getSpecimen().getId(),
                    iw.getSpecimenState());
            }
            senderId = sender == null ? null : sender.getId();
        }
        this.creation = creation;
        this.errorIfAlreadyAdded = errorIfAlreadyAdded;
    }

    @Override
    public ServerProcess getProcessInstance(
        WritableApplicationService appService, User user) {
        if (isCreation())
            return new DispatchCreateProcess(appService, this, user);
        return new ShipmentReceiveProcess(appService, this, user);
    }

    public Map<Integer, ItemState> getCurrentDispatchSpecimenIds() {
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
