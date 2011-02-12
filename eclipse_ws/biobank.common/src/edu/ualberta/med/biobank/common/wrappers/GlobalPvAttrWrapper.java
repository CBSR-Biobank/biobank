package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.base.GlobalPvAttrBaseWrapper;
import edu.ualberta.med.biobank.model.GlobalPvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class GlobalPvAttrWrapper extends GlobalPvAttrBaseWrapper {

    public GlobalPvAttrWrapper(WritableApplicationService appService,
        GlobalPvAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public GlobalPvAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        // FIXME if used by any study then it cannot be deleted
    }

    public String getTypeName() {
        return getPvAttrType().getName();
    }

    @Override
    public int compareTo(ModelWrapper<GlobalPvAttr> o) {
        return 0;
    }

    @Override
    public String toString() {
        return "" + getId() + ":" + getLabel() + ":"
            + getPvAttrType().getName();
    }

    @Override
    public void reload() throws Exception {
        super.reload();
    }

    public static final String ALL_GLOBAL_PV_ATTRS_QRY = "from "
        + GlobalPvAttr.class.getName();

    public static List<GlobalPvAttrWrapper> getAllGlobalPvAttrs(
        WritableApplicationService appService) throws ApplicationException {

        List<GlobalPvAttrWrapper> pvAttrs = new ArrayList<GlobalPvAttrWrapper>();

        HQLCriteria c = new HQLCriteria(ALL_GLOBAL_PV_ATTRS_QRY);
        List<GlobalPvAttr> result = appService.query(c);
        for (GlobalPvAttr pvAttr : result) {
            pvAttrs.add(new GlobalPvAttrWrapper(appService, pvAttr));
        }

        Collections.sort(pvAttrs);
        return pvAttrs;
    }

}