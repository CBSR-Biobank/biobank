package edu.ualberta.med.biobank.common.action.site;

import java.util.ArrayList;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.info.ContainerTypeInfo;

public class SiteGetContainerTypeInfoResult implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final ArrayList<ContainerTypeInfo> containerTypeInfoCollection;

    public SiteGetContainerTypeInfoResult(
        ArrayList<ContainerTypeInfo> containerTypeInfoCollection) {
        this.containerTypeInfoCollection = containerTypeInfoCollection;
    }

    public ArrayList<ContainerTypeInfo> getContainerTypeInfoCollection() {
        return containerTypeInfoCollection;
    }
}
