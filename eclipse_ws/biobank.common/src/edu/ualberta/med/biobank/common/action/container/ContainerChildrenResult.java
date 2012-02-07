package edu.ualberta.med.biobank.common.action.container;

import java.util.ArrayList;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Container;

public class ContainerChildrenResult implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final ArrayList<Container> childContainers;

    public ContainerChildrenResult(ArrayList<Container> childContainers) {
        this.childContainers = childContainers;
    }

    public ArrayList<Container> getChildContainers() {
        return childContainers;
    }

}
