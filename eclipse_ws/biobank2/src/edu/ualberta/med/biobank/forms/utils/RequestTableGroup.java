package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.treeview.RequestAliquotAdapter;
import edu.ualberta.med.biobank.treeview.admin.RequestContainerAdapter;

public enum RequestTableGroup {
    PROCESSED("Processed") {
        @Override
        public List<RequestContainerAdapter> getChildren(RequestWrapper request) {
            return getAdapterTree(request
                .getProcessedRequestAliquotCollection());
        }

        @Override
        public List<RequestAliquotWrapper> getAliquotChildren(
            RequestWrapper request) {
            return request.getProcessedRequestAliquotCollection();
        }
    },
    NON_PROCESSED("Non Processed") {
        @Override
        public List<RequestContainerAdapter> getChildren(RequestWrapper request) {
            return getAdapterTree(request
                .getNonProcessedRequestAliquotCollection());
        }

        @Override
        public List<RequestAliquotWrapper> getAliquotChildren(
            RequestWrapper request) {
            return request.getNonProcessedRequestAliquotCollection();
        }
    },
    UNAVAILABLE("Unavailable") {
        @Override
        public List<RequestContainerAdapter> getChildren(RequestWrapper request) {
            return getAdapterTree(getAliquotChildren(request));
        }

        @Override
        public List<RequestAliquotWrapper> getAliquotChildren(
            RequestWrapper request) {
            return request.getUnavailableRequestAliquotCollection();
        }

    };

    private String label;

    private RequestTableGroup(String label) {
        this.label = label;
    }

    public abstract List<RequestAliquotWrapper> getAliquotChildren(
        RequestWrapper request);

    @Override
    public String toString() {
        return label;
    }

    public String getTitle(RequestWrapper request) {
        return label + " (" + getAliquotChildren(request).size() + ")";
    }

    public abstract List<RequestContainerAdapter> getChildren(
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

    public List<RequestContainerAdapter> getAdapterTree(
        List<RequestAliquotWrapper> aliquots) {
        HashSet<ContainerWrapper> containers = new HashSet<ContainerWrapper>();
        // get all the containers to display
        for (RequestAliquotWrapper a : aliquots) {
            containers.add(a.getAliquot().getParent());
            containers.addAll(getParents(a.getAliquot().getParent()));
        }
        // create adapters
        HashMap<ContainerWrapper, RequestContainerAdapter> adapters = new HashMap<ContainerWrapper, RequestContainerAdapter>();
        for (ContainerWrapper c : containers)
            adapters.put(c, new RequestContainerAdapter(null, c));
        // set up relationships
        for (ContainerWrapper c : containers) {
            RequestContainerAdapter child = adapters.get(c);
            if (containers.contains(c.getParent())) {
                RequestContainerAdapter parent = adapters.get(c.getParent());
                parent.addChild(child);
            }
            for (RequestAliquotWrapper raw : aliquots)
                if (raw.getAliquot().getParent().equals(c))
                    child.addChild(new RequestAliquotAdapter(null, raw));
        }
        // get the top containers to start with
        List<RequestContainerAdapter> tops = new ArrayList<RequestContainerAdapter>();
        for (RequestContainerAdapter ca : adapters.values())
            if (ca.getParent() == null)
                tops.add(ca);
        return tops;
    }

    private List<ContainerWrapper> getParents(ContainerWrapper c) {
        if (c.getParent() == null)
            return new ArrayList<ContainerWrapper>();
        else {
            List<ContainerWrapper> parents = getParents(c.getParent());
            parents.add(c);
            return parents;
        }
    }

}
