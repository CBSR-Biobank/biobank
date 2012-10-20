package edu.ualberta.med.biobank.action.scanprocess.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.model.center.Container;
import edu.ualberta.med.biobank.model.type.ItemState;

public class ShipmentProcessInfo extends AbstractProcessPalletInfo {

    private static final long serialVersionUID = 1L;

    // used for dispatch sending only
    private Integer senderId;

    private Map<Integer, ItemState> currentDispatchSpecimenIds;
    private boolean errorIfAlreadyAdded;

    public ShipmentProcessInfo(Container pallet, RequestWrapper request,
        boolean errorIfAlreadyAdded) {
        super(pallet == null ? null : pallet.getId());
        init(request.getRequestSpecimenCollection(false),
            errorIfAlreadyAdded, null);
    }

    public ShipmentProcessInfo(Container pallet,
        DispatchWrapper dispatch, boolean errorIfAlreadyAdded) {
        super(pallet == null ? null : pallet.getId());
        init(dispatch.getDispatchSpecimenCollection(false),
            errorIfAlreadyAdded, dispatch.getSenderCenter());
    }

    private void init(List<? extends ItemWrapper> items,
        boolean errorIfAlreadyAdded, CenterWrapper<?> sender) {
        if (items != null) {
            currentDispatchSpecimenIds = new HashMap<Integer, ItemState>();
            for (ItemWrapper iw : items) {
                currentDispatchSpecimenIds.put(iw.getSpecimen().getId(),
                    iw.getSpecimenState());
            }
            senderId = sender == null ? null : sender.getId();
        }
        this.errorIfAlreadyAdded = errorIfAlreadyAdded;
    }

    public Map<Integer, ItemState> getCurrentDispatchSpecimenIds() {
        return currentDispatchSpecimenIds;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public boolean isErrorIfAlreadyAdded() {
        return errorIfAlreadyAdded;
    }

}
