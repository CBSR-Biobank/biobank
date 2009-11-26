package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.ShptSampleSource;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ShipmentWrapper extends ModelWrapper<Shipment> {

    public ShipmentWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShipmentWrapper(WritableApplicationService appService,
        Shipment wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        // FIXME Auto-generated method stub

    }

    @Override
    protected void persistDependencies(Shipment origObject)
        throws BiobankCheckException, ApplicationException, WrapperException {
        if (origObject != null) {
            removeDeletedShptSampleSources(origObject);
        }
    }

    private void removeDeletedShptSampleSources(Shipment shptDatabase)
        throws BiobankCheckException, ApplicationException, WrapperException {
        List<ShptSampleSourceWrapper> newSampleSources = getShptSampleSourceCollection();
        List<ShptSampleSourceWrapper> oldSampleSources = new ShipmentWrapper(
            appService, shptDatabase).getShptSampleSourceCollection();
        if (oldSampleSources != null) {
            for (ShptSampleSourceWrapper ss : oldSampleSources) {
                if ((newSampleSources == null)
                    || !newSampleSources.contains(ss)) {
                    ss.delete();
                }
            }
        }
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "dateShipped", "dateReceived", "clinic",
            "comment", "shptSampleSourceCollection", "patientVisitCollection",
            "waybill" };
    }

    @Override
    public Class<Shipment> getWrappedClass() {
        return Shipment.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        if (getDateShipped() == null) {
            throw new BiobankCheckException(
                "A date shipped should be set on this shipment");
        }
        if (getClinic() != null && !checkDateShippedUnique()) {
            throw new BiobankCheckException("A shipment with date shipped "
                + getDateShipped() + " already exist in clinic "
                + getClinic().getName() + ".");
        }
    }

    private boolean checkDateShippedUnique() throws ApplicationException {
        String isSameShipment = "";
        List<Object> params = new ArrayList<Object>();
        params.add(getClinic().getId());
        params.add(getDateShipped());
        if (!isNew()) {
            isSameShipment = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from " + Shipment.class.getName()
            + " where clinic.id=? and dateShipped = ?" + isSameShipment, params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    @Override
    public int compareTo(ModelWrapper<Shipment> wrapper) {
        Date v1Date = wrappedObject.getDateShipped();
        Date v2Date = wrapper.wrappedObject.getDateShipped();
        return ((v1Date.compareTo(v2Date) > 0) ? 1 : (v1Date.equals(v2Date) ? 0
            : -1));
    }

    public Date getDateShipped() {
        return wrappedObject.getDateShipped();
    }

    public String getFormattedDateShipped() {
        return DateFormatter.formatAsDateTime(getDateShipped());
    }

    public void setDateShipped(Date date) {
        Date oldDate = getDateShipped();
        wrappedObject.setDateShipped(date);
        propertyChangeSupport.firePropertyChange("dateShipped", oldDate, date);
    }

    public Date getDateReceived() {
        return wrappedObject.getDateReceived();
    }

    public String getFormattedDateReceived() {
        return DateFormatter.formatAsDateTime(getDateReceived());
    }

    public void setDateReceived(Date date) {
        Date oldDate = getDateReceived();
        wrappedObject.setDateReceived(date);
        propertyChangeSupport.firePropertyChange("dateReceived", oldDate, date);
    }

    public ClinicWrapper getClinic() {
        Clinic clinic = wrappedObject.getClinic();
        if (clinic == null) {
            return null;
        }
        return new ClinicWrapper(appService, clinic);
    }

    public void setClinic(Clinic clinic) {
        Clinic oldClinic = wrappedObject.getClinic();
        wrappedObject.setClinic(clinic);
        propertyChangeSupport.firePropertyChange("clinic", oldClinic, clinic);
    }

    public void setClinic(ClinicWrapper clinic) {
        if (clinic == null) {
            setClinic((Clinic) null);
        } else {
            setClinic(clinic.wrappedObject);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ShptSampleSourceWrapper> getShptSampleSourceCollection(
        boolean sort) {
        List<ShptSampleSourceWrapper> shptSampleSourceCollection = (List<ShptSampleSourceWrapper>) propertiesMap
            .get("shptSampleSourceCollection");
        if (shptSampleSourceCollection == null) {
            Collection<ShptSampleSource> children = wrappedObject
                .getShptSampleSourceCollection();
            if (children != null) {
                shptSampleSourceCollection = new ArrayList<ShptSampleSourceWrapper>();
                for (ShptSampleSource pvSampleSource : children) {
                    shptSampleSourceCollection.add(new ShptSampleSourceWrapper(
                        appService, pvSampleSource));
                }
                propertiesMap.put("shptSampleSourceCollection",
                    shptSampleSourceCollection);
            }
        }
        if ((shptSampleSourceCollection != null) && sort)
            Collections.sort(shptSampleSourceCollection);
        return shptSampleSourceCollection;
    }

    public List<ShptSampleSourceWrapper> getShptSampleSourceCollection() {
        return getShptSampleSourceCollection(false);
    }

    public void setShptSampleSourceCollection(
        Collection<ShptSampleSource> shptSampleSources, boolean setNull) {
        Collection<ShptSampleSource> oldCollection = wrappedObject
            .getShptSampleSourceCollection();
        wrappedObject.setShptSampleSourceCollection(shptSampleSources);
        propertyChangeSupport.firePropertyChange("shptSampleSourceCollection",
            oldCollection, shptSampleSources);
        if (setNull) {
            propertiesMap.put("shptSampleSourceCollection", null);
        }
    }

    public void setShptSampleSourceCollection(
        Collection<ShptSampleSourceWrapper> shptSampleSources) {
        Collection<ShptSampleSource> shptCollection = new HashSet<ShptSampleSource>();
        for (ShptSampleSourceWrapper pv : shptSampleSources) {
            shptCollection.add(pv.getWrappedObject());
        }
        setShptSampleSourceCollection(shptCollection, false);
        propertiesMap.put("shptSampleSourceCollection", shptSampleSources);
    }

    @SuppressWarnings("unchecked")
    public List<PatientVisitWrapper> getPatientVisitCollection() {
        List<PatientVisitWrapper> patientVisitCollection = (List<PatientVisitWrapper>) propertiesMap
            .get("patientVisitCollection");
        if (patientVisitCollection == null) {
            Collection<PatientVisit> children = wrappedObject
                .getPatientVisitCollection();
            if (children != null) {
                patientVisitCollection = new ArrayList<PatientVisitWrapper>();
                for (PatientVisit pv : children) {
                    patientVisitCollection.add(new PatientVisitWrapper(
                        appService, pv));
                }
                propertiesMap.put("patientVisitCollection",
                    patientVisitCollection);
            }
        }
        return patientVisitCollection;
    }

    public void setPatientVisitCollection(
        Collection<PatientVisit> patientVisitCollection, boolean setNull) {
        Collection<PatientVisit> oldCollection = wrappedObject
            .getPatientVisitCollection();
        wrappedObject.setPatientVisitCollection(patientVisitCollection);
        propertyChangeSupport.firePropertyChange("patientVisitCollection",
            oldCollection, patientVisitCollection);
        if (setNull) {
            propertiesMap.put("patientVisitCollection", null);
        }
    }

    public void setPatientVisitCollection(
        Collection<PatientVisitWrapper> patientVisitCollection) {
        Collection<PatientVisit> pvCollection = new HashSet<PatientVisit>();
        for (PatientVisitWrapper pv : patientVisitCollection) {
            pvCollection.add(pv.getWrappedObject());
        }
        setPatientVisitCollection(pvCollection, false);
        propertiesMap.put("patientVisitCollection", patientVisitCollection);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setComment(String comment) {
        String oldComment = getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public String getWaybill() {
        return wrappedObject.getWaybill();
    }

    public void setWaybill(String waybill) {
        String old = getWaybill();
        wrappedObject.setWaybill(waybill);
        propertyChangeSupport.firePropertyChange("waybill", old, waybill);
    }

    @Override
    public String toString() {
        return getFormattedDateShipped();
    }

    /**
     * Search for a shipment in the site with the given waybill
     */
    public static ShipmentWrapper getShipmentInSite(
        WritableApplicationService appService, String waybill, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Shipment.class.getName()
            + " where clinic.site.id = ? and waybill = ?", Arrays
            .asList(new Object[] { site.getId(), waybill }));
        List<Shipment> shipments = appService.query(criteria);
        if (shipments.size() == 1) {
            return new ShipmentWrapper(appService, shipments.get(0));
        }
        return null;
    }
}
