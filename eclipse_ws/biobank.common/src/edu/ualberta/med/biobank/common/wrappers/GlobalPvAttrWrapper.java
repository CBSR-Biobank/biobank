package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;

@SuppressWarnings("unused")
@Deprecated
public class GlobalPvAttrWrapper {

    public static class GlobalPvAttr {

    }

    public GlobalPvAttrWrapper(WritableApplicationService appService,
        GlobalPvAttr wrappedObject) {
    }

    public GlobalPvAttrWrapper(WritableApplicationService appService) {
    }

    protected void deleteChecks() {
    }

    public String getTypeName() {
        return null;
    }

    public int compareTo(ModelWrapper<GlobalPvAttr> o) {
        return 0;
    }

    public void reload() throws Exception {
    }

    public static List<GlobalPvAttrWrapper> getAllGlobalPvAttrs(
        WritableApplicationService appService) throws ApplicationException {
        return null;
    }

}