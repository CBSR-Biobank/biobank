package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.OrderAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.OrderWrapper;

public enum OrderTableGroup {
    PROCESSED("Processed") {
        @Override
        public List<OrderAliquotWrapper> getChildren(OrderWrapper order) {
            return order.getProcessedOrderAliquotCollection();
        }
    },
    NON_PROCESSED("Non Processed") {
        @Override
        public List<OrderAliquotWrapper> getChildren(OrderWrapper order) {
            return order.getNonProcessedOrderAliquotCollection();
        }
    };

    private String label;

    private OrderTableGroup(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getTitle(OrderWrapper order) {
        return label + " (" + getChildren(order).size() + ")";
    }

    public abstract List<OrderAliquotWrapper> getChildren(OrderWrapper shipment);

    public static Object findParent(AliquotWrapper dsa) {
        for (OrderTableGroup tg : values()) {
            if (tg.getChildren(null).contains(dsa)) {
                return tg;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static List<OrderTableGroup> getGroupsForShipment(OrderWrapper ship) {
        List<OrderTableGroup> groups = new ArrayList<OrderTableGroup>();
        groups.add(PROCESSED);
        groups.add(NON_PROCESSED);
        return groups;
    }
}
