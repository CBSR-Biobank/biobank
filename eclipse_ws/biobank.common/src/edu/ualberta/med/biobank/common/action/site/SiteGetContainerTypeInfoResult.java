package edu.ualberta.med.biobank.common.action.site;

import java.util.ArrayList;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.info.SiteContainerTypeInfo;

public class SiteGetContainerTypeInfoResult implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final ArrayList<SiteContainerTypeInfo> containerTypeInfoCollection;

    public SiteGetContainerTypeInfoResult(
        ArrayList<SiteContainerTypeInfo> containerTypeInfoCollection) {
        this.containerTypeInfoCollection = containerTypeInfoCollection;
    }

    public ArrayList<SiteContainerTypeInfo> getContainerTypeInfoCollection() {
        return containerTypeInfoCollection;
    }
}
