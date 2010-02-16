package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SamplePositionWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleWrapper extends
    AbstractPositionHolder<Sample, SamplePosition> {

    public SampleWrapper(WritableApplicationService appService,
        Sample wrappedObject) {
        super(appService, wrappedObject);
    }

    public SampleWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "inventoryId", "patientVisit", "position",
            "linkDate", "sampleType", "quantity", "oldComment", "quantityUsed" };
    }

    @Override
    public Class<Sample> getWrappedClass() {
        return Sample.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        checkPatientVisitNotNull();
        checkInventoryIdUnique();
        checkParentAcceptSampleType();
        super.persistChecks();
    }

    private void checkPatientVisitNotNull() throws BiobankCheckException {
        if (getPatientVisit() == null) {
            throw new BiobankCheckException("patient visit should be set");
        }
    }

    public String getInventoryId() {
        return wrappedObject.getInventoryId();
    }

    public void setInventoryId(String inventoryId) {
        String oldInventoryId = inventoryId;
        wrappedObject.setInventoryId(inventoryId);
        propertyChangeSupport.firePropertyChange("inventoryId", oldInventoryId,
            inventoryId);
    }

    public void checkInventoryIdUnique() throws BiobankCheckException,
        ApplicationException {
        List<SampleWrapper> samples = getSamplesInSite(appService,
            getInventoryId(), getSite());
        boolean alreadyExists = false;
        if (samples.size() > 0 && isNew()) {
            alreadyExists = true;
        } else {
            for (SampleWrapper sample : samples) {
                if (!sample.getId().equals(getId())) {
                    alreadyExists = true;
                    break;
                }
            }
        }
        if (alreadyExists) {
            throw new BiobankCheckException("A sample with inventoryId \""
                + getInventoryId() + "\" already exists.");
        }
    }

    private void checkParentAcceptSampleType() throws BiobankCheckException {
        ContainerWrapper parent = getParent();
        if (parent != null) {
            List<SampleTypeWrapper> types = getParent().getContainerType()
                .getSampleTypeCollection();
            if (types == null || !types.contains(getSampleType())) {
                throw new BiobankCheckException("Container "
                    + getParent().getFullInfoLabel()
                    + " does not allow inserts of sample type "
                    + getSampleType().getName() + ".");
            }
        }
    }

    @Override
    public SiteWrapper getSite() {
        if (getPatientVisit() != null) {
            return getPatientVisit().getPatient().getStudy().getSite();
        }
        return null;
    }

    protected void setPatientVisit(PatientVisit patientVisit) {
        PatientVisit oldPV = patientVisit;
        wrappedObject.setPatientVisit(patientVisit);
        propertyChangeSupport.firePropertyChange("patientVisit", oldPV,
            patientVisit);
    }

    public void setPatientVisit(PatientVisitWrapper patientVisit) {
        if (patientVisit == null) {
            setPatientVisit((PatientVisit) null);
        } else {
            setPatientVisit(patientVisit.getWrappedObject());
        }
    }

    public PatientVisitWrapper getPatientVisit() {
        PatientVisit pv = wrappedObject.getPatientVisit();
        if (pv == null) {
            return null;
        }
        return new PatientVisitWrapper(appService, pv);
    }

    public void setSamplePositionFromString(String positionString,
        ContainerWrapper parentContainer) throws Exception {
        RowColPos rcp = LabelingScheme.getRowColFromPositionString(
            positionString, parentContainer.getContainerType());
        if ((rcp.row > -1) && (rcp.col > -1)) {
            setPosition(rcp);
        } else {
            throw new Exception("Position " + positionString + " not valid");
        }
    }

    /**
     * Method used to check if the current position of this sample is available
     * on the container. Return true if the position is free, false otherwise
     */
    public boolean isPositionFree(ContainerWrapper parentContainer)
        throws ApplicationException {
        RowColPos position = getPosition();
        if (position != null) {
            HQLCriteria criteria = new HQLCriteria("from "
                + Sample.class.getName()
                + " where samplePosition.row=? and samplePosition.col=?"
                + " and samplePosition.container=?", Arrays
                .asList(new Object[] { position.row, position.col,
                    parentContainer.getWrappedObject() }));

            List<Sample> samples = appService.query(criteria);
            if (samples.size() > 0) {
                return false;
            }
        }
        return true;
    }

    protected void setSampleType(SampleType type) {
        SampleType oldType = wrappedObject.getSampleType();
        wrappedObject.setSampleType(type);
        propertyChangeSupport.firePropertyChange("sampleType", oldType, type);
    }

    public void setSampleType(SampleTypeWrapper type) {
        if (type == null) {
            setSampleType((SampleType) null);
        } else {
            setSampleType(type.wrappedObject);
        }
    }

    public SampleTypeWrapper getSampleType() {
        SampleType type = wrappedObject.getSampleType();
        if (type == null) {
            return null;
        }
        return new SampleTypeWrapper(appService, type);
    }

    public void setLinkDate(Date date) {
        Date oldDate = getLinkDate();
        wrappedObject.setLinkDate(date);
        propertyChangeSupport.firePropertyChange("linkDate", oldDate, date);
    }

    public Date getLinkDate() {
        return wrappedObject.getLinkDate();
    }

    public String getFormattedLinkDate() {
        return DateFormatter.formatAsDateTime(wrappedObject.getLinkDate());
    }

    public void setQuantity(Double quantity) {
        Double oldQuantity = wrappedObject.getQuantity();
        wrappedObject.setQuantity(quantity);
        propertyChangeSupport.firePropertyChange("quantity", oldQuantity,
            quantity);
    }

    public Double getQuantityUsed() {
        return wrappedObject.getQuantityUsed();
    }

    public void setQuantityUsed(Double quantityUsed) {
        Double oldQuantityUsed = wrappedObject.getQuantityUsed();
        wrappedObject.setQuantityUsed(quantityUsed);
        propertyChangeSupport.firePropertyChange("quantityUsed",
            oldQuantityUsed, quantityUsed);
    }

    public Double getQuantity() {
        return wrappedObject.getQuantity();
    }

    public void setComment(String comment) {
        String oldComment = wrappedObject.getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public String getPositionString() {
        return getPositionString(true, true);
    }

    public String getPositionString(boolean fullString,
        boolean addTopParentShortName) {
        RowColPos position = getPosition();
        if (position == null) {
            return "none";
        }

        if (!fullString) {
            return LabelingScheme.getPositionString(position, getParent());
        }
        ContainerWrapper directParent = getParent();
        ContainerWrapper topContainer = directParent;
        while (topContainer.hasParent()) {
            topContainer = topContainer.getParent();
        }
        String nameShort = topContainer.getContainerType().getNameShort();
        if (addTopParentShortName && nameShort != null)
            return nameShort + "-" + directParent.getLabel()
                + LabelingScheme.getPositionString(position, directParent);
        return directParent.getLabel()
            + LabelingScheme.getPositionString(position, directParent);
    }

    public static SampleWrapper createNewSample(
        WritableApplicationService appService, String inventoryId,
        PatientVisitWrapper pv, SampleTypeWrapper type,
        Collection<SampleStorageWrapper> sampleStorageWrappers) {
        Sample sample = new Sample();
        sample.setInventoryId(inventoryId);
        sample.setPatientVisit(pv.getWrappedObject());
        sample.setLinkDate(new Date());
        sample.setSampleType(type.getWrappedObject());
        Double volume = null;
        for (SampleStorageWrapper ss : sampleStorageWrappers) {
            if (ss.getSampleType().getId().equals(type.getId())) {
                volume = ss.getVolume();
            }
        }
        sample.setQuantity(volume);
        return new SampleWrapper(appService, sample);
    }

    public void setQuantityFromType() {
        StudyWrapper study = getPatientVisit().getPatient().getStudy();
        Double volume = null;
        Collection<SampleStorageWrapper> sampleStorageCollection = study
            .getSampleStorageCollection();
        if (sampleStorageCollection != null) {
            for (SampleStorageWrapper ss : sampleStorageCollection) {
                if (ss.getSampleType().getId().equals(getSampleType().getId())) {
                    volume = ss.getVolume();
                }
            }
        }
        setQuantity(volume);
    }

    @Override
    public void loadAttributes() throws Exception {
        super.loadAttributes();
        getPositionString();
        wrappedObject.getSampleType().getName();
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public static List<SampleWrapper> getSamplesInSite(
        WritableApplicationService appService, String inventoryId,
        SiteWrapper siteWrapper) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Sample.class.getName()
                + " where inventoryId = ? and patientVisit.patient.study.site.id = ?",
            Arrays.asList(new Object[] { inventoryId, siteWrapper.getId() }));
        List<Sample> samples = appService.query(criteria);
        List<SampleWrapper> list = new ArrayList<SampleWrapper>();
        for (Sample sample : samples) {
            if (sample.getInventoryId().equals(inventoryId)) {
                list.add(new SampleWrapper(appService, sample));
            }
        }
        return list;
    }

    @Override
    public int compareTo(ModelWrapper<Sample> o) {
        if (o instanceof SampleWrapper) {
            return getInventoryId().compareTo(
                ((SampleWrapper) o).getInventoryId());
        }
        return 0;
    }

    public static List<SampleWrapper> getRandomSamplesAlreadyLinked(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " as s where s.patientVisit.patient.study.site.id = ?", Arrays
            .asList(new Object[] { siteId }));
        List<Sample> samples = appService.query(criteria);
        List<SampleWrapper> list = new ArrayList<SampleWrapper>();
        int i = 0;
        for (Sample sample : samples) {
            // return a list of 10 maximum
            if (i == 10) {
                return list;
            }
            list.add(new SampleWrapper(appService, sample));
            i++;
        }
        return list;
    }

    public static List<SampleWrapper> getRandomSamplesAlreadyAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " as s where s in (select sp.sample from "
            + SamplePosition.class.getName()
            + " as sp) and s.patientVisit.patient.study.site.id = ?", Arrays
            .asList(new Object[] { siteId }));
        List<Sample> samples = appService.query(criteria);
        List<SampleWrapper> list = new ArrayList<SampleWrapper>();
        int i = 0;
        for (Sample sample : samples) {
            // return a list of 10 maximum
            if (i == 10) {
                return list;
            }
            list.add(new SampleWrapper(appService, sample));
            i++;
        }
        return list;
    }

    public static List<SampleWrapper> getRandomSamplesNotAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " as s where s not in (select sp.sample from "
            + SamplePosition.class.getName()
            + " as sp) and s.patientVisit.patient.study.site.id = ?", Arrays
            .asList(new Object[] { siteId }));
        List<Sample> samples = appService.query(criteria);
        List<SampleWrapper> list = new ArrayList<SampleWrapper>();
        for (Sample sample : samples) {
            list.add(new SampleWrapper(appService, sample));
        }
        return list;
    }

    @Override
    protected AbstractPositionWrapper<SamplePosition> getSpecificPositionWrapper(
        boolean initIfNoPosition) {
        SamplePosition pos = wrappedObject.getSamplePosition();
        if (pos != null) {
            return new SamplePositionWrapper(appService, pos);
        } else if (initIfNoPosition) {
            SamplePositionWrapper posWrapper = new SamplePositionWrapper(
                appService);
            posWrapper.setSample(this);
            wrappedObject.setSamplePosition(posWrapper.getWrappedObject());
            return posWrapper;
        }
        return null;
    }

    @Override
    public String toString() {
        return getInventoryId();
    }
}
