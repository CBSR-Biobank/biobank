/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.EntityPeer;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ReportBaseWrapper;

public class EntityBaseWrapper extends ModelWrapper<Entity> {

    public EntityBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public EntityBaseWrapper(WritableApplicationService appService,
        Entity wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Entity> getWrappedClass() {
        return Entity.class;
    }

    @Override
    public Property<Integer, ? super Entity> getIdProperty() {
        return EntityPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Entity>> getProperties() {
        return EntityPeer.PROPERTIES;
    }

    public String getName() {
        return getProperty(EntityPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(EntityPeer.NAME, trimmed);
    }

    public String getClassName() {
        return getProperty(EntityPeer.CLASS_NAME);
    }

    public void setClassName(String className) {
        String trimmed = className == null ? null : className.trim();
        setProperty(EntityPeer.CLASS_NAME, trimmed);
    }

    public List<ReportWrapper> getReportCollection(boolean sort) {
        boolean notCached = !isPropertyCached(EntityPeer.REPORT_COLLECTION);
        List<ReportWrapper> reportCollection = getWrapperCollection(EntityPeer.REPORT_COLLECTION, ReportWrapper.class, sort);
        if (notCached) {
            for (ReportBaseWrapper e : reportCollection) {
                e.setEntityInternal(this);
            }
        }
        return reportCollection;
    }

    public void addToReportCollection(List<? extends ReportBaseWrapper> reportCollection) {
        addToWrapperCollection(EntityPeer.REPORT_COLLECTION, reportCollection);
        for (ReportBaseWrapper e : reportCollection) {
            e.setEntityInternal(this);
        }
    }

    void addToReportCollectionInternal(List<? extends ReportBaseWrapper> reportCollection) {
        if (isInitialized(EntityPeer.REPORT_COLLECTION)) {
            addToWrapperCollection(EntityPeer.REPORT_COLLECTION, reportCollection);
        } else {
            getElementQueue().add(EntityPeer.REPORT_COLLECTION, reportCollection);
        }
    }

    public void removeFromReportCollection(List<? extends ReportBaseWrapper> reportCollection) {
        removeFromWrapperCollection(EntityPeer.REPORT_COLLECTION, reportCollection);
        for (ReportBaseWrapper e : reportCollection) {
            e.setEntityInternal(null);
        }
    }

    void removeFromReportCollectionInternal(List<? extends ReportBaseWrapper> reportCollection) {
        if (isPropertyCached(EntityPeer.REPORT_COLLECTION)) {
            removeFromWrapperCollection(EntityPeer.REPORT_COLLECTION, reportCollection);
        } else {
            getElementQueue().remove(EntityPeer.REPORT_COLLECTION, reportCollection);
        }
    }

    public void removeFromReportCollectionWithCheck(List<? extends ReportBaseWrapper> reportCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(EntityPeer.REPORT_COLLECTION, reportCollection);
        for (ReportBaseWrapper e : reportCollection) {
            e.setEntityInternal(null);
        }
    }

    void removeFromReportCollectionWithCheckInternal(List<? extends ReportBaseWrapper> reportCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(EntityPeer.REPORT_COLLECTION, reportCollection);
    }

}
