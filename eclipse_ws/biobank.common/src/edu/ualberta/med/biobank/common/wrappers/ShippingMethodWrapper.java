package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShippingMethodPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ShippingMethodBaseWrapper;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ShippingMethodWrapper extends ShippingMethodBaseWrapper {

    public static final String DROP_OFF_NAME = "Drop-off";
    public static final String PICK_UP_NAME = "Pick-up";

    public ShippingMethodWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShippingMethodWrapper(WritableApplicationService appService,
        ShippingMethod sc) {
        super(appService, sc);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ShippingMethodWrapper)
            return ((ShippingMethodWrapper) object).getName().equals(
                this.getName());
        else
            return false;
    }

    @Override
    public int compareTo(ModelWrapper<ShippingMethod> o) {
        if (o instanceof ShippingMethodWrapper) {
            return getName().compareTo(o.getWrappedObject().getName());
        }
        return 0;
    }

    public static List<ShippingMethodWrapper> getShippingMethods(
        WritableApplicationService appService) throws ApplicationException {
        List<ShippingMethod> objects = appService.search(ShippingMethod.class,
            new ShippingMethod());
        List<ShippingMethodWrapper> wrappers = new ArrayList<ShippingMethodWrapper>();
        for (ShippingMethod sm : objects) {
            wrappers.add(new ShippingMethodWrapper(appService, sm));
        }
        return wrappers;
    }

    @Override
    public String toString() {
        return getName();
    }

    private static final String IS_USED_HQL = "select count(si) from "
        + ShipmentInfo.class.getName() + " as si where si."
        + ShipmentInfoPeer.SHIPPING_METHOD.getName() + "=?";

    public boolean isUsed() throws ApplicationException, BiobankException {
        if (isNew())
            return false;
        HQLCriteria c = new HQLCriteria(IS_USED_HQL,
            Arrays.asList(new Object[] { wrappedObject }));
        return getCountResult(appService, c) > 0;
    }

    // TODO: is this needed anymore?
    public static void persistShippingMethods(
        List<ShippingMethodWrapper> addedOrModifiedTypes,
        List<ShippingMethodWrapper> typesToDelete)
        throws BiobankCheckException, Exception {
        if (addedOrModifiedTypes != null) {
            for (ShippingMethodWrapper ss : addedOrModifiedTypes) {
                ss.persist();
            }
        }
        if (typesToDelete != null) {
            for (ShippingMethodWrapper ss : typesToDelete) {
                ss.delete();
            }
        }
    }

    public boolean needDate() {
        String name = getName();
        return name != null && !name.equals(PICK_UP_NAME)
            && !name.equals(DROP_OFF_NAME);
    }

    @Override
    protected TaskList getPersistTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().uniqueAndNotNull(ShippingMethodPeer.NAME));

        tasks.add(super.getPersistTasks());

        return tasks;
    }

    @Override
    protected TaskList getDeleteTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().notUsedBy(ShipmentInfo.class,
            ShipmentInfoPeer.SHIPPING_METHOD));

        tasks.add(super.getDeleteTasks());

        return tasks;
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        getPersistTasks().execute(appService);
    }

    @Override
    public void delete() throws Exception {
        getDeleteTasks().execute(appService);
    }
}
