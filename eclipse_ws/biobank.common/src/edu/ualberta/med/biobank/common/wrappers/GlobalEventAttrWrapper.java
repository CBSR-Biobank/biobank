package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.GlobalEventAttrBaseWrapper;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class GlobalEventAttrWrapper extends GlobalEventAttrBaseWrapper {

    public GlobalEventAttrWrapper(WritableApplicationService appService,
        GlobalEventAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public GlobalEventAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public String getTypeName() {
        return getEventAttrType().getName();
    }

    @Override
    public int compareTo(ModelWrapper<GlobalEventAttr> o) {
        return 0;
    }

    @Override
    public String toString() {
        return "" + getId() + ":" + getLabel() + ":"
            + getEventAttrType().getName();
    }

    public static final String ALL_GLOBAL_EVENT_ATTRS_QRY = "from "
        + GlobalEventAttr.class.getName();

    public static List<GlobalEventAttrWrapper> getAllGlobalEventAttrs(
        WritableApplicationService appService) throws ApplicationException {

        List<GlobalEventAttrWrapper> EventAttrs = new ArrayList<GlobalEventAttrWrapper>();

        HQLCriteria c = new HQLCriteria(ALL_GLOBAL_EVENT_ATTRS_QRY);
        List<GlobalEventAttr> result = appService.query(c);
        for (GlobalEventAttr EventAttr : result) {
            EventAttrs.add(new GlobalEventAttrWrapper(appService, EventAttr));
        }

        Collections.sort(EventAttrs);
        return EventAttrs;
    }

    @Override
    protected void addDeleteTasks(TaskList tasks) {
        // FIXME if used by any study then it cannot be deleted
        super.addDeleteTasks(tasks);
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        WrapperTransaction.persist(this, appService);
    }

    @Override
    public void delete() throws Exception {
        WrapperTransaction.delete(this, appService);
    }
}