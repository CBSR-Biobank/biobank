/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.ArrayList;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ContactBaseWrapper;

public abstract class ClinicBaseWrapper extends CenterWrapper<Clinic> {

    public ClinicBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ClinicBaseWrapper(WritableApplicationService appService,
        Clinic wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Clinic> getWrappedClass() {
        return Clinic.class;
    }

    @Override
   protected Clinic getNewObject() throws Exception {
        Clinic newObject = super.getNewObject();
        newObject.setSendsShipments(false);
        return newObject;
    }

    @Override
    public Property<Integer, ? super Clinic> getIdProperty() {
        return ClinicPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Clinic>> getProperties() {
        List<Property<?, ? super Clinic>> superNames = super.getProperties();
        List<Property<?, ? super Clinic>> all = new ArrayList<Property<?, ? super Clinic>>();
        all.addAll(superNames);
        all.addAll(ClinicPeer.PROPERTIES);
        return all;
    }

    public Boolean getSendsShipments() {
        return getProperty(ClinicPeer.SENDS_SHIPMENTS);
    }

    public void setSendsShipments(Boolean sendsShipments) {
        setProperty(ClinicPeer.SENDS_SHIPMENTS, sendsShipments);
    }

    public List<ContactWrapper> getContactCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ClinicPeer.CONTACT_COLLECTION);
        List<ContactWrapper> contactCollection = getWrapperCollection(ClinicPeer.CONTACT_COLLECTION, ContactWrapper.class, sort);
        if (notCached) {
            for (ContactBaseWrapper e : contactCollection) {
                e.setClinicInternal(this);
            }
        }
        return contactCollection;
    }

    public void addToContactCollection(List<? extends ContactBaseWrapper> contactCollection) {
        addToWrapperCollection(ClinicPeer.CONTACT_COLLECTION, contactCollection);
        for (ContactBaseWrapper e : contactCollection) {
            e.setClinicInternal(this);
        }
    }

    void addToContactCollectionInternal(List<? extends ContactBaseWrapper> contactCollection) {
        if (isInitialized(ClinicPeer.CONTACT_COLLECTION)) {
            addToWrapperCollection(ClinicPeer.CONTACT_COLLECTION, contactCollection);
        } else {
            getElementQueue().add(ClinicPeer.CONTACT_COLLECTION, contactCollection);
        }
    }

    public void removeFromContactCollection(List<? extends ContactBaseWrapper> contactCollection) {
        removeFromWrapperCollection(ClinicPeer.CONTACT_COLLECTION, contactCollection);
        for (ContactBaseWrapper e : contactCollection) {
            e.setClinicInternal(null);
        }
    }

    void removeFromContactCollectionInternal(List<? extends ContactBaseWrapper> contactCollection) {
        if (isPropertyCached(ClinicPeer.CONTACT_COLLECTION)) {
            removeFromWrapperCollection(ClinicPeer.CONTACT_COLLECTION, contactCollection);
        } else {
            getElementQueue().remove(ClinicPeer.CONTACT_COLLECTION, contactCollection);
        }
    }

    public void removeFromContactCollectionWithCheck(List<? extends ContactBaseWrapper> contactCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ClinicPeer.CONTACT_COLLECTION, contactCollection);
        for (ContactBaseWrapper e : contactCollection) {
            e.setClinicInternal(null);
        }
    }

    void removeFromContactCollectionWithCheckInternal(List<? extends ContactBaseWrapper> contactCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ClinicPeer.CONTACT_COLLECTION, contactCollection);
    }

}
