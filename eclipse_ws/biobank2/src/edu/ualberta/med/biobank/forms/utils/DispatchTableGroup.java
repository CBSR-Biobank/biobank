package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;

public enum DispatchTableGroup {
    ADDED("Added") {
        @Override
        public List<DispatchSpecimenWrapper> getChildren(
            DispatchWrapper shipment) {
            return shipment.getNonProcessedDispatchSpecimenCollection();
        }
    },
    NON_PROCESSED("Non Processed") {
        @Override
        public List<DispatchSpecimenWrapper> getChildren(
            DispatchWrapper shipment) {
            return shipment.getNonProcessedDispatchSpecimenCollection();
        }
    },
    RECEIVED("Received") {
        @Override
        public List<DispatchSpecimenWrapper> getChildren(
            DispatchWrapper shipment) {
            return shipment.getReceivedDispatchSpecimens();
        }
    },
    EXTRA("Extra") {
        @Override
        public List<DispatchSpecimenWrapper> getChildren(
            DispatchWrapper shipment) {
            return shipment.getExtraDispatchSpecimens();
        }
    },
    MISSING("Missing") {
        @Override
        public List<DispatchSpecimenWrapper> getChildren(
            DispatchWrapper shipment) {
            return shipment.getMissingDispatchSpecimens();
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

    public String getTitle(DispatchWrapper ship) {
        return label + " (" + getChildren(ship).size() + ")";
    }

    public abstract List<DispatchSpecimenWrapper> getChildren(
        DispatchWrapper shipment);

    public static Object findParent(DispatchSpecimenWrapper dsa) {
        for (DispatchTableGroup tg : values()) {
            if (tg.getChildren(dsa.getDispatch()).contains(dsa)) {
                return tg;
            }
        }
        return null;
    }

    public static List<DispatchTableGroup> getGroupsForShipment(
        DispatchWrapper ship) {
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
