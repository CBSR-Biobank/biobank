package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.RequestAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;

public enum RequestTableGroup {
    PROCESSED("Processed") {
        @Override
        public List<RequestAliquotWrapper> getChildren(RequestWrapper request) {
            return request.getProcessedRequestAliquotCollection();
        }
    },
    NON_PROCESSED("Non Processed") {
        @Override
        public List<RequestAliquotWrapper> getChildren(RequestWrapper request) {
            return request.getNonProcessedRequestAliquotCollection();
        }
    },
    UNAVAILABLE("Unavailable") {
        @Override
        public List<RequestAliquotWrapper> getChildren(RequestWrapper request) {
            return request.getUnavailableRequestAliquotCollection();
        }
    };

    private String label;

    private RequestTableGroup(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getTitle(RequestWrapper request) {
        return label + " (" + getChildren(request).size() + ")";
    }

    public abstract List<RequestAliquotWrapper> getChildren(
        RequestWrapper shipment);

    public static RequestTableGroup findParent(RequestAliquotWrapper dsa) {
        for (RequestTableGroup tg : values()) {
            if (tg.getChildren(dsa.getRequest()).contains(dsa)) {
                return tg;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static List<RequestTableGroup> getGroupsForShipment(
        RequestWrapper ship) {
        List<RequestTableGroup> groups = new ArrayList<RequestTableGroup>();
        groups.add(PROCESSED);
        groups.add(NON_PROCESSED);
        groups.add(UNAVAILABLE);
        return groups;
    }
}
