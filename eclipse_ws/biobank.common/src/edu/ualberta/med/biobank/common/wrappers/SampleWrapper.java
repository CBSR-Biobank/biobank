package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.SamplePositionWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleWrapper extends ModelWrapper<Sample> {

    private SamplePositionWrapper samplePosition;
    private RowColPos position;

    public SampleWrapper(WritableApplicationService appService,
        Sample wrappedObject) {
        super(appService, wrappedObject);
        SamplePosition pos = wrappedObject.getSamplePosition();
        if (pos != null) {
            samplePosition = new SamplePositionWrapper(appService, pos);
        }
    }

    public SampleWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public void setInventoryId(String inventoryId) {
        String oldInventoryId = inventoryId;
        wrappedObject.setInventoryId(inventoryId);
        propertyChangeSupport.firePropertyChange("inventoryId", oldInventoryId,
            inventoryId);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "inventoryId", "patientVisit", "position",
            "linkDate", "sampleType", "quantity", "oldComment", "quantityUsed" };
    }

    @Override
    public Class<Sample> getWrappedClass() {
        return Sample.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    public String getInventoryId() {
        return wrappedObject.getInventoryId();
    }

    public void checkInventoryIdUnique() throws BiobankCheckException,
        ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Sample.class.getName()
                + " where inventoryId  = ? and patientVisit.patient.study.site.id=?",
            Arrays.asList(new Object[] { getInventoryId(), getSite().getId() }));
        List<Sample> samples = appService.query(criteria);
        if (samples.size() == 0) {
            return;
        }
        for (Sample sample : samples) {
            // need to do that for the upper and lower letter (not taken into
            // account in the sql query
            if (sample.getInventoryId().equals(getInventoryId())) {
                throw new BiobankCheckException("A sample with inventoryId \""
                    + getInventoryId() + "\" already exists.");
            }
        }
    }

    public SiteWrapper getSite() {
        if (getPatientVisit() != null) {
            return getPatientVisit().getPatient().getStudy().getSite();
        }
        return null;
    }

    public void setPatientVisit(PatientVisit patientVisit) {
        PatientVisit oldPV = patientVisit;
        wrappedObject.setPatientVisit(patientVisit);
        propertyChangeSupport.firePropertyChange("patientVisit", oldPV,
            patientVisit);
    }

    public void setPatientVisit(PatientVisitWrapper patientVisit) {
        setPatientVisit(patientVisit.getWrappedObject());
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
            positionString, parentContainer.getContainerType()
                .getWrappedObject());
        if ((rcp.row > -1) && (rcp.col > -1)) {
            setPosition(rcp);
        } else {
            throw new Exception("Position " + positionString + " not valid");
        }
    }

    private void initSamplePosition() {
        samplePosition = new SamplePositionWrapper(appService);
        position = new RowColPos();
        samplePosition.setSample(this);
        wrappedObject.setSamplePosition(samplePosition.getWrappedObject());
    }

    public RowColPos getPosition() {
        if (samplePosition == null) {
            return null;
        }
        return position;
    }

    public void setPosition(RowColPos position) {
        RowColPos oldPosition = this.position;
        if (samplePosition == null) {
            initSamplePosition();
        }
        samplePosition.setRow(position.row);
        samplePosition.setCol(position.col);
        this.position = position;
        propertyChangeSupport.firePropertyChange("position", oldPosition,
            position);
    }

    public void setPosition(Integer row, Integer col) {
        setPosition(new RowColPos(row, col));
    }

    public ContainerWrapper getParent() {
        if (samplePosition == null) {
            return null;
        }
        return samplePosition.getContainer();
    }

    public void setParent(ContainerWrapper parent) {
        ContainerWrapper oldValue = null;
        if (samplePosition == null) {
            initSamplePosition();
        } else {
            oldValue = samplePosition.getContainer();
        }
        samplePosition.setContainer(parent);
        propertyChangeSupport.firePropertyChange("parent", oldValue, parent);
    }

    public boolean hasParent() {
        return samplePosition != null;
    }

    public void checkPosition(ContainerWrapper parentContainer)
        throws BiobankCheckException, ApplicationException {
        SamplePosition sp = wrappedObject.getSamplePosition();
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " where samplePosition.row=? and samplePosition.col=?"
            + " and samplePosition.container=?", Arrays.asList(new Object[] {
            sp.getRow(), sp.getCol(), parentContainer.getWrappedObject() }));

        List<Sample> samples = appService.query(criteria);
        if (samples.size() == 0) {
            return;
        }
        Sample sample = samples.get(0);
        throw new BiobankCheckException("Position already in use in container "
            + parentContainer.getLabel() + " by sample "
            + sample.getInventoryId());
    }

    public void setSampleType(SampleType type) {
        SampleType oldType = wrappedObject.getSampleType();
        wrappedObject.setSampleType(type);
        propertyChangeSupport.firePropertyChange("sampleType", oldType, type);
    }

    public void setSampleType(SampleTypeWrapper type) {
        setSampleType(type.wrappedObject);
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
        return getPositionString(true);
    }

    public String getPositionString(boolean fullString) {
        SamplePosition position = wrappedObject.getSamplePosition();
        if (position == null) {
            return "none";
        }

        if (!fullString) {
            return LabelingScheme.getPositionString(position);
        }

        Container container = position.getContainer();
        Container topContainer = container;
        while ((topContainer.getPosition() != null)
            && (topContainer.getPosition().getParentContainer() != null)) {
            topContainer = topContainer.getPosition().getParentContainer();
        }
        String nameShort = topContainer.getContainerType().getNameShort();
        if (nameShort != null)
            return nameShort + "-" + container.getLabel()
                + LabelingScheme.getPositionString(position);
        return container.getLabel()
            + LabelingScheme.getPositionString(position);
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
        getPositionString(true);
        wrappedObject.getSampleType().getName();
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
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
        return transformToWrapperList(appService, samples);
    }

    public static List<SampleWrapper> transformToWrapperList(
        WritableApplicationService appService, List<Sample> samples) {
        List<SampleWrapper> list = new ArrayList<SampleWrapper>();
        for (Sample sample : samples) {
            list.add(new SampleWrapper(appService, sample));
        }
        return list;
    }

    @Override
    public int compareTo(ModelWrapper<Sample> o) {
        return this.getId().compareTo(o.getId());
    }

    public static List<SampleWrapper> getRandomSamplesAlreadyLinked(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " as s where s.patientVisit.patient.study.site.id = ?", Arrays
            .asList(new Object[] { siteId }));
        List<Sample> samples = appService.query(criteria);
        return transformToWrapperList(appService, samples);
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
        return transformToWrapperList(appService, samples);
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
        return transformToWrapperList(appService, samples);
    }
}
