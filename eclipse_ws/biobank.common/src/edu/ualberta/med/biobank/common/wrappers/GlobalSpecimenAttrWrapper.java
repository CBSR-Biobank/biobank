package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.wrappers.base.GlobalSpecimenAttrBaseWrapper;
import edu.ualberta.med.biobank.model.GlobalSpecimenAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class GlobalSpecimenAttrWrapper extends GlobalSpecimenAttrBaseWrapper {

    public GlobalSpecimenAttrWrapper(WritableApplicationService appService,
        GlobalSpecimenAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public GlobalSpecimenAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void deleteChecks() throws BiobankDeleteException,
        ApplicationException {
        // FIXME if used by any study then it cannot be deleted
    }

    public String getTypeName() {
        return getSpecimenAttrType().getName();
    }

    @Override
    public int compareTo(ModelWrapper<GlobalSpecimenAttr> o) {
        return 0;
    }

    @Override
    public String toString() {
        return "" + getId() + ":" + getLabel() + ":"
            + getSpecimenAttrType().getName();
    }

    @Override
    public void reload() throws Exception {
        super.reload();
    }

    public static final String ALL_GLOBAL_EVENT_ATTRS_QRY = "from "
        + GlobalSpecimenAttr.class.getName();

    public static List<GlobalSpecimenAttrWrapper> getAllGlobalSpecimenAttrs(
        WritableApplicationService appService) throws ApplicationException {

        List<GlobalSpecimenAttrWrapper> SpecimenAttrs = new ArrayList<GlobalSpecimenAttrWrapper>();

        HQLCriteria c = new HQLCriteria(ALL_GLOBAL_EVENT_ATTRS_QRY);
        List<GlobalSpecimenAttr> result = appService.query(c);
        for (GlobalSpecimenAttr SpecimenAttr : result) {
            SpecimenAttrs.add(new GlobalSpecimenAttrWrapper(appService, SpecimenAttr));
        }

        Collections.sort(SpecimenAttrs);
        return SpecimenAttrs;
    }

}