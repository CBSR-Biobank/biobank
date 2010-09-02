package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.DispatchShipment;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DispatchShipmentWrapper extends
    AbstractShipmentWrapper<DispatchShipment> {

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
        list.addAll(Arrays.asList("sender", "receiver", "aliquotCollection",
            "study"));
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
        if (getStudy() == null) {
            throw new BiobankCheckException("Study should be set");
        }
        if (!checkWaybillUniqueForSender()) {
            throw new BiobankCheckException("A dispatch shipment with waybill "
                + getWaybill() + " already exists for sending site "
                + getSender().getNameShort());
        }
        checkSenderCanSendToReceiver();
    }

    private void checkSenderCanSendToReceiver() throws BiobankCheckException {
        if (getSender() != null && getReceiver() != null && getStudy() != null) {
            List<SiteWrapper> possibleReceivers = getSender()
                .getStudyDispachSites(getStudy());
            if (possibleReceivers == null
                || !possibleReceivers.contains(getReceiver())) {
                throw new BiobankCheckException(getSender().getNameShort()
                    + " cannot dispatch aliquots to "
                    + getReceiver().getNameShort() + " for study "
                    + getStudy().getNameShort());
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
        SiteWrapper sender = (SiteWrapper) propertiesMap.get("sender");
        if (sender == null) {
            Site s = wrappedObject.getSender();
            if (s == null)
                return null;
            sender = new SiteWrapper(appService, s);
            propertiesMap.put("sender", sender);
        }
        return sender;
    }

    public void setSender(SiteWrapper sender) {
        propertiesMap.put("sender", sender);
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
        SiteWrapper receiver = (SiteWrapper) propertiesMap.get("receiver");
        if (receiver == null) {
            Site r = wrappedObject.getReceiver();
            if (r == null)
                return null;
            receiver = new SiteWrapper(appService, r);
            propertiesMap.put("receiver", receiver);
        }
        return receiver;
    }

    public void setReceiver(SiteWrapper receiver) {
        propertiesMap.put("receiver", receiver);
        Site oldReceiver = wrappedObject.getReceiver();
        Site newReceiver = null;
        if (receiver != null) {
            newReceiver = receiver.getWrappedObject();
        }
        wrappedObject.setReceiver(newReceiver);
        propertyChangeSupport.firePropertyChange("receiver", oldReceiver,
            newReceiver);
    }

    public StudyWrapper getStudy() {
        StudyWrapper study = (StudyWrapper) propertiesMap.get("study");
        if (study == null) {
            Study s = wrappedObject.getStudy();
            if (s == null)
                return null;
            study = new StudyWrapper(appService, s);
            propertiesMap.put("study", study);
        }
        return study;
    }

    public void setStudy(StudyWrapper study) {
        propertiesMap.put("study", study);
        Study oldStudy = wrappedObject.getStudy();
        Study newStudy = null;
        if (study != null) {
            newStudy = study.getWrappedObject();
        }
        wrappedObject.setStudy(newStudy);
        propertyChangeSupport.firePropertyChange("study", oldStudy, newStudy);
    }

    @SuppressWarnings("unchecked")
    public List<AliquotWrapper> getAliquotCollection(boolean sort) {
        List<AliquotWrapper> aliquotCollection = (List<AliquotWrapper>) propertiesMap
            .get("aliquotCollection");
        if (aliquotCollection == null) {
            Collection<Aliquot> children = wrappedObject.getAliquotCollection();
            if (children != null) {
                aliquotCollection = new ArrayList<AliquotWrapper>();
                for (Aliquot aliquot : children) {
                    aliquotCollection.add(new AliquotWrapper(appService,
                        aliquot));
                }
                propertiesMap.put("aliquotCollection", aliquotCollection);
            }
        }
        if ((aliquotCollection != null) && sort)
            Collections.sort(aliquotCollection);
        return aliquotCollection;
    }

    public List<AliquotWrapper> getAliquotCollection() {
        return getAliquotCollection(true);
    }

    private void setAliquotCollection(Collection<Aliquot> allAliquotObjects,
        List<AliquotWrapper> allAliquotWrappers) {
        Collection<Aliquot> oldContainers = wrappedObject
            .getAliquotCollection();
        wrappedObject.setAliquotCollection(allAliquotObjects);
        propertyChangeSupport.firePropertyChange("aliquotCollection",
            oldContainers, allAliquotObjects);
        propertiesMap.put("aliquotCollection", allAliquotWrappers);
    }

    public void addAliquots(List<AliquotWrapper> newAliquots)
        throws BiobankCheckException {
        if ((newAliquots == null) || (newAliquots.size() == 0))
            return;

        Collection<Aliquot> allAliquotObjects = new HashSet<Aliquot>();
        List<AliquotWrapper> allAliquotWrappers = new ArrayList<AliquotWrapper>();
        // already added aliquots
        List<AliquotWrapper> currentList = getAliquotCollection();
        if (currentList != null) {
            for (AliquotWrapper aliquot : currentList) {
                allAliquotObjects.add(aliquot.getWrappedObject());
                allAliquotWrappers.add(aliquot);
            }
        }
        // new aliquots added
        for (AliquotWrapper aliquot : newAliquots) {
            if (aliquot.isNew()) {
                throw new BiobankCheckException(
                    "Cannot add aliquot that are not already saved");
            }
            allAliquotObjects.add(aliquot.getWrappedObject());
            allAliquotWrappers.add(aliquot);
        }
        setAliquotCollection(allAliquotObjects, allAliquotWrappers);
    }

    public void removeAliquots(List<AliquotWrapper> aliquotsToRemove) {
        if ((aliquotsToRemove == null) || (aliquotsToRemove.size() == 0))
            return;

        Collection<Aliquot> allAliquotObjects = new HashSet<Aliquot>();
        List<AliquotWrapper> allAliquotWrappers = new ArrayList<AliquotWrapper>();
        // already added aliquots
        List<AliquotWrapper> currentList = getAliquotCollection();
        if (currentList != null) {
            for (AliquotWrapper aliquot : currentList) {
                if (!aliquotsToRemove.contains(aliquot)) {
                    allAliquotObjects.add(aliquot.getWrappedObject());
                    allAliquotWrappers.add(aliquot);
                }
            }
        }
        setAliquotCollection(allAliquotObjects, allAliquotWrappers);
    }

    @Override
    protected void deleteChecks() throws Exception {

    }

}
