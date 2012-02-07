package edu.ualberta.med.biobank.common.action.site;

import java.util.ArrayList;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Container;

public class SiteGetTopContainersResult implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final ArrayList<Container> topContainers;

    public SiteGetTopContainersResult(ArrayList<Container> topContainers) {
        this.topContainers = topContainers;
    }

    public ArrayList<Container> getTopContainers() {
        return topContainers;
    }
}
