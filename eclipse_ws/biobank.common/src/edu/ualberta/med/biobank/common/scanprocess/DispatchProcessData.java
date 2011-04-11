package edu.ualberta.med.biobank.common.scanprocess;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.server.scanprocess.DispatchProcess;
import edu.ualberta.med.biobank.server.scanprocess.ServerProcess;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchProcessData implements ProcessData {

    private static final long serialVersionUID = 1L;

    private Integer palletId;
    private String palletLabel;
    private Integer containerTypeId;
    private Integer palletRowCapacity;
    private Integer palletColCapacity;
    private Map<RowColPos, Integer> expectedSpecimens;
    private Integer senderId;
    private Map<Integer, DispatchSpecimenState> currentDispatchSpecimenIds;
    private boolean creation;

    public DispatchProcessData(ContainerWrapper pallet,
        DispatchWrapper dispatch, boolean creation) {
        if (pallet != null) {
            palletId = pallet.getId();
            palletLabel = pallet.getLabel();
            ContainerTypeWrapper type = pallet.getContainerType();
            if (type != null) {
                containerTypeId = type.getId();
                palletRowCapacity = type.getRowCapacity();
                palletColCapacity = type.getColCapacity();
            }
            Map<RowColPos, SpecimenWrapper> expectedSpecimensClient = pallet
                .getSpecimens();
            expectedSpecimens = new HashMap<RowColPos, Integer>();
            for (Entry<RowColPos, SpecimenWrapper> entry : expectedSpecimensClient
                .entrySet()) {
                expectedSpecimens.put(entry.getKey(), entry.getValue().getId());
            }
        }
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
    }

    @Override
    public ServerProcess getProcessInstance(
        WritableApplicationService appService, User user) {
        return new DispatchProcess(appService, this, user);
    }

    public Integer getPalletId() {
        return palletId;
    }

    public Map<RowColPos, Integer> getExpectedSpecimens() {
        return expectedSpecimens;
    }

    public String getPalletLabel() {
        return palletLabel;
    }

    public Integer getContainerTypeId() {
        return containerTypeId;
    }

    public Integer getPalletRowCapacity() {
        return palletRowCapacity;
    }

    public Integer getPalletColCapacity() {
        return palletColCapacity;
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

}
