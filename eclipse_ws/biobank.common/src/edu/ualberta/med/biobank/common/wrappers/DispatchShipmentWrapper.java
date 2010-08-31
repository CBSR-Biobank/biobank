package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.DispatchContainer;
import edu.ualberta.med.biobank.model.DispatchShipment;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DispatchShipmentWrapper extends
    AbstractShipmentWrapper<DispatchShipment> {

    private SiteWrapper sender;
    private SiteWrapper receiver;

    private Set<DispatchContainerWrapper> sentContainersAdded = new HashSet<DispatchContainerWrapper>();
    private Set<DispatchContainerWrapper> sentContainersRemoved = new HashSet<DispatchContainerWrapper>();

    public DispatchShipmentWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchShipmentWrapper(WritableApplicationService appService,
        DispatchShipment ship) {
        super(appService, ship);
    }

    @Override
    public Class<DispatchShipment> getWrappedClass() {
        return DispatchShipment.class;
    }

    @Override
    protected String[] getPropertyChangeNames() {
        String[] properties = super.getPropertyChangeNames();
        List<String> list = new ArrayList<String>(Arrays.asList(properties));
        list.addAll(Arrays.asList("sender", "receiver",
            "sentContainerCollection"));
        return list.toArray(new String[] {});
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        if (getSender() == null) {
            throw new BiobankCheckException("Sender should be set");
        }
        if (getReceiver() == null) {
            throw new BiobankCheckException("Receiver should be set");
        }
        if (!checkWaybillUniqueForSender()) {
            throw new BiobankCheckException("A dispatch shipment with waybill "
                + getWaybill() + " already exists for sending site "
                + getSender().getNameShort());
        }
        checkSenderCanSendToReceiver();
    }

    private void checkSenderCanSendToReceiver() throws BiobankCheckException,
        ApplicationException {
        if (getSender() != null && getReceiver() != null) {
            // FIXME should know for which study...
            List<SiteWrapper> possibleReceivers = getSender()
                .getStudyDispachSites(null);
            if (!possibleReceivers.contains(getReceiver())) {
                throw new BiobankCheckException(getSender().getNameShort()
                    + " cannot dispatch aliquots to "
                    + getReceiver().getNameShort());
            }
        }
    }

    private boolean checkWaybillUniqueForSender() throws ApplicationException,
        BiobankCheckException {
        String isSameShipment = "";
        List<Object> params = new ArrayList<Object>();
        SiteWrapper sender = getSender();
        if (sender == null) {
            throw new BiobankCheckException("sender site cannot be null");
        }
        params.add(sender.getId());
        params.add(getWaybill());
        if (!isNew()) {
            isSameShipment = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from "
            + DispatchShipment.class.getName()
            + " where sender.id=? and waybill = ?" + isSameShipment, params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    public SiteWrapper getSender() {
        if (sender == null) {
            Site s = wrappedObject.getSender();
            if (s == null)
                return null;
            sender = new SiteWrapper(appService, s);
        }
        return sender;
    }

    public void setSender(SiteWrapper sender) {
        this.sender = sender;
        Site oldSender = wrappedObject.getSender();
        Site newSender = null;
        if (sender != null) {
            newSender = sender.getWrappedObject();
        }
        wrappedObject.setSender(newSender);
        propertyChangeSupport
            .firePropertyChange("sender", oldSender, newSender);
    }

    public SiteWrapper getReceiver() {
        if (receiver == null) {
            Site r = wrappedObject.getReceiver();
            if (r == null)
                return null;
            receiver = new SiteWrapper(appService, r);
        }
        return receiver;
    }

    public void setReceiver(SiteWrapper receiver) {
        this.receiver = receiver;
        Site oldReceiver = wrappedObject.getReceiver();
        Site newReceiver = null;
        if (receiver != null) {
            newReceiver = receiver.getWrappedObject();
        }
        wrappedObject.setReceiver(newReceiver);
        propertyChangeSupport.firePropertyChange("receiver", oldReceiver,
            newReceiver);
    }

    @SuppressWarnings("unchecked")
    public List<DispatchContainerWrapper> getSentContainerCollection(
        boolean sort) {
        List<DispatchContainerWrapper> sentContainerCollection = (List<DispatchContainerWrapper>) propertiesMap
            .get("sentContainerCollection");
        if (sentContainerCollection == null) {
            Collection<DispatchContainer> children = wrappedObject
                .getSentContainerCollection();
            if (children != null) {
                sentContainerCollection = new ArrayList<DispatchContainerWrapper>();
                for (DispatchContainer container : children) {
                    sentContainerCollection.add(new DispatchContainerWrapper(
                        appService, container));
                }
                propertiesMap.put("sentContainerCollection",
                    sentContainerCollection);
            }
        }
        if ((sentContainerCollection != null) && sort)
            Collections.sort(sentContainerCollection);
        return sentContainerCollection;
    }

    public List<DispatchContainerWrapper> getSentContainerCollection() {
        return getSentContainerCollection(true);
    }

    private void setSentContainerCollection(
        Collection<DispatchContainer> allSentContainerObjects,
        List<DispatchContainerWrapper> allSentContainerWrappers) {
        Collection<DispatchContainer> oldContainers = wrappedObject
            .getSentContainerCollection();
        wrappedObject.setSentContainerCollection(allSentContainerObjects);
        propertyChangeSupport.firePropertyChange("sentContainerCollection",
            oldContainers, allSentContainerObjects);
        propertiesMap.put("sentContainerCollection", allSentContainerWrappers);
    }

    public void addSentContainers(List<DispatchContainerWrapper> newContainers) {
        if (newContainers != null && newContainers.size() > 0) {
            Collection<DispatchContainer> allContainersObjects = new HashSet<DispatchContainer>();
            List<DispatchContainerWrapper> allContainersWrappers = new ArrayList<DispatchContainerWrapper>();
            // already in list
            List<DispatchContainerWrapper> containersList = getSentContainerCollection();
            if (containersList != null) {
                for (DispatchContainerWrapper container : containersList) {
                    allContainersObjects.add(container.getWrappedObject());
                    allContainersWrappers.add(container);
                }
            }
            // new containers
            for (DispatchContainerWrapper container : newContainers) {
                container.setShipment(this);
                sentContainersAdded.add(container);
                sentContainersRemoved.remove(container);
                allContainersObjects.add(container.getWrappedObject());
                allContainersWrappers.add(container);
            }
            setSentContainerCollection(allContainersObjects,
                allContainersWrappers);
        }
    }

    public void removeSentContainers(
        List<DispatchContainerWrapper> containersToRemove) {
        if ((containersToRemove == null) || (containersToRemove.size() == 0))
            return;

        sentContainersAdded.removeAll(containersToRemove);
        sentContainersRemoved.addAll(containersToRemove);
        Collection<DispatchContainer> allContainerObjects = new HashSet<DispatchContainer>();
        List<DispatchContainerWrapper> allContainerWrappers = new ArrayList<DispatchContainerWrapper>();
        // already in list
        List<DispatchContainerWrapper> containersList = getSentContainerCollection();
        if (containersList != null) {
            for (DispatchContainerWrapper container : containersList) {
                if (!containersToRemove.contains(container)) {
                    allContainerObjects.add(container.getWrappedObject());
                    allContainerWrappers.add(container);
                }
            }
        }
        setSentContainerCollection(allContainerObjects, allContainerWrappers);
    }

    @Override
    protected void deleteChecks() throws Exception {

    }

    @Override
    public void resetInternalFields() {
        sentContainersAdded.clear();
        sentContainersRemoved.clear();
        sender = null;
    }
}
