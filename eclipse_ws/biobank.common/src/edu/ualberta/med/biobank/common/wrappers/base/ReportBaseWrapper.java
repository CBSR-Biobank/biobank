/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.ReportPeer;
import edu.ualberta.med.biobank.common.wrappers.EntityWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.EntityBaseWrapper;
import java.util.Arrays;

public class ReportBaseWrapper extends ModelWrapper<Report> {

    public ReportBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ReportBaseWrapper(WritableApplicationService appService,
        Report wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Report> getWrappedClass() {
        return Report.class;
    }

    @Override
   protected Report getNewObject() throws Exception {
        Report newObject = super.getNewObject();
        newObject.setIsCount(false);
        newObject.setIsPublic(false);
        return newObject;
    }

    @Override
    public Property<Integer, ? super Report> getIdProperty() {
        return ReportPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Report>> getProperties() {
        return ReportPeer.PROPERTIES;
    }

    public Boolean getIsCount() {
        return getProperty(ReportPeer.IS_COUNT);
    }

    public void setIsCount(Boolean isCount) {
        setProperty(ReportPeer.IS_COUNT, isCount);
    }

    public String getDescription() {
        return getProperty(ReportPeer.DESCRIPTION);
    }

    public void setDescription(String description) {
        String trimmed = description == null ? null : description.trim();
        setProperty(ReportPeer.DESCRIPTION, trimmed);
    }

    public Integer getUserId() {
        return getProperty(ReportPeer.USER_ID);
    }

    public void setUserId(Integer userId) {
        setProperty(ReportPeer.USER_ID, userId);
    }

    public String getName() {
        return getProperty(ReportPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(ReportPeer.NAME, trimmed);
    }

    public Boolean getIsPublic() {
        return getProperty(ReportPeer.IS_PUBLIC);
    }

    public void setIsPublic(Boolean isPublic) {
        setProperty(ReportPeer.IS_PUBLIC, isPublic);
    }

    public EntityWrapper getEntity() {
        boolean notCached = !isPropertyCached(ReportPeer.ENTITY);
        EntityWrapper entity = getWrappedProperty(ReportPeer.ENTITY, EntityWrapper.class);
        if (entity != null && notCached) ((EntityBaseWrapper) entity).addToReportCollectionInternal(Arrays.asList(this));
        return entity;
    }

    public void setEntity(EntityBaseWrapper entity) {
        if (isInitialized(ReportPeer.ENTITY)) {
            EntityBaseWrapper oldEntity = getEntity();
            if (oldEntity != null) oldEntity.removeFromReportCollectionInternal(Arrays.asList(this));
        }
        if (entity != null) entity.addToReportCollectionInternal(Arrays.asList(this));
        setWrappedProperty(ReportPeer.ENTITY, entity);
    }

    void setEntityInternal(EntityBaseWrapper entity) {
        setWrappedProperty(ReportPeer.ENTITY, entity);
    }

}
