package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.model.AbstractContainer;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractContainerWrapper<S extends AbstractContainer>
    extends ModelWrapper<S> {

    private ContainerTypeWrapper containerType;
    private ActivityStatusWrapper activityStatus;

    public AbstractContainerWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public AbstractContainerWrapper(WritableApplicationService appService,
        S wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "activityStatus", "containerType", "parent",
            "productBarcode", "comment" };
    }

    public static AbstractContainerWrapper<?> createInstance(
        WritableApplicationService appService, AbstractContainer container) {
        if (container instanceof Container) {
            return new ContainerWrapper(appService, (Container) container);
        }
        return null;
    }

    public Integer getRowCapacity() {
        ContainerTypeWrapper type = getContainerType();
        if (type == null) {
            return null;
        }
        return type.getRowCapacity();
    }

    public Integer getColCapacity() {
        ContainerTypeWrapper type = getContainerType();
        if (type == null) {
            return null;
        }
        return type.getColCapacity();
    }

    public void setContainerType(ContainerTypeWrapper containerType) {
        this.containerType = containerType;
        ContainerType oldType = wrappedObject.getContainerType();
        ContainerType newType = null;
        if (containerType != null) {
            newType = containerType.getWrappedObject();
        }
        wrappedObject.setContainerType(newType);
        propertyChangeSupport.firePropertyChange("containerType", oldType,
            newType);
    }

    public ContainerTypeWrapper getContainerType() {
        if (containerType == null) {
            ContainerType c = wrappedObject.getContainerType();
            if (c == null)
                return null;
            containerType = new ContainerTypeWrapper(appService, c);
        }
        return containerType;
    }

    public ActivityStatusWrapper getActivityStatus() {
        if (activityStatus == null) {
            ActivityStatus a = wrappedObject.getActivityStatus();
            if (a == null)
                return null;
            activityStatus = new ActivityStatusWrapper(appService, a);
        }
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        this.activityStatus = activityStatus;
        ActivityStatus oldActivityStatus = wrappedObject.getActivityStatus();
        ActivityStatus rawObject = null;
        if (activityStatus != null) {
            rawObject = activityStatus.getWrappedObject();
        }
        wrappedObject.setActivityStatus(rawObject);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setComment(String comment) {
        String oldComment = wrappedObject.getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public String getProductBarcode() {
        return wrappedObject.getProductBarcode();
    }

    public void setProductBarcode(String barcode) {
        String oldBarcode = getProductBarcode();
        wrappedObject.setProductBarcode(barcode);
        propertyChangeSupport.firePropertyChange("productBarcode", oldBarcode,
            barcode);
    }

    @Override
    protected void resetInternalFields() {
        super.resetInternalFields();
        containerType = null;
        activityStatus = null;
    }

    public abstract SiteWrapper getSite();

}
