package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;

public enum DispatchTableGroup {
    ADDED("Added") {
        @Override
        public List<DispatchShipmentAliquotWrapper> getChildren(
            DispatchShipmentWrapper shipment) {
            return shipment.getNonProcessedDispatchShipmentAliquotCollection();
        }
    },
    NON_PROCESSED("Non Processed") {
        @Override
        public List<DispatchShipmentAliquotWrapper> getChildren(
            DispatchShipmentWrapper shipment) {
            return shipment.getNonProcessedDispatchShipmentAliquotCollection();
        }
    },
    RECEIVED("Received") {
        @Override
        public List<DispatchShipmentAliquotWrapper> getChildren(
            DispatchShipmentWrapper shipment) {
            return shipment.getReceivedDispatchShipmentAliquots();
        }
    },
    EXTRA("Extra") {
        @Override
        public List<DispatchShipmentAliquotWrapper> getChildren(
            DispatchShipmentWrapper shipment) {
            return shipment.getExtraDispatchShipmentAliquots();
        }
    },
    MISSING("Missing") {
        @Override
        public List<DispatchShipmentAliquotWrapper> getChildren(
            DispatchShipmentWrapper shipment) {
            return shipment.getMissingDispatchShipmentAliquots();
        }
    };

    private String label;

    private DispatchTableGroup(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getTitle(DispatchShipmentWrapper ship) {
        return label + " (" + getChildren(ship).size() + ")";
    }

    public abstract List<DispatchShipmentAliquotWrapper> getChildren(
        DispatchShipmentWrapper shipment);

    public static Object findParent(DispatchShipmentAliquotWrapper dsa) {
        for (DispatchTableGroup tg : values()) {
            if (tg.getChildren(dsa.getShipment()).contains(dsa)) {
                return tg;
            }
        }
        return null;
    }

    public static List<DispatchTableGroup> getGroupsForShipment(
        DispatchShipmentWrapper ship) {
        List<DispatchTableGroup> groups = new ArrayList<DispatchTableGroup>();
        if (ship.isInCreationState()) {
            groups.add(ADDED);
        } else {
            groups.add(NON_PROCESSED);
        }
        if (ship.hasBeenReceived()) {
            groups.add(RECEIVED);
            groups.add(EXTRA);
        }
        if (ship.hasBeenReceived() || ship.isInTransitState()) {
            groups.add(MISSING);
        }
        return groups;
    }
}
