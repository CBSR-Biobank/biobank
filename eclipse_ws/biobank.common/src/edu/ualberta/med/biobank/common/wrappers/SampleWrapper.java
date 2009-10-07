package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleWrapper extends ModelWrapper<Sample> {

    public SampleWrapper(WritableApplicationService appService,
        Sample wrappedObject) {
        super(appService, wrappedObject);
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
        return new String[] { "inventoryId", "patientVisit", "samplePosition",
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
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " where inventoryId  = ? and patientVisit.patient.study.site=?",
            Arrays.asList(new Object[] { getInventoryId(), getSite() }));
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

    public Site getSite() {
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

    public PatientVisit getPatientVisit() {
        return wrappedObject.getPatientVisit();
    }

    public void setSamplePositionFromString(String positionString,
        ContainerWrapper parentContainer) throws Exception {
        RowColPos rcp = LabelingScheme.getRowColFromPositionString(
            positionString, parentContainer.getContainerType()
                .getWrappedObject());
        if ((rcp.row > -1) && (rcp.col > -1)) {
            SamplePosition sp = wrappedObject.getSamplePosition();
            if (sp == null) {
                sp = new SamplePosition();
                setSamplePosition(sp);
            }
            sp.setSample(wrappedObject);
            // TODO check if update works well when sampleposition already
            // exists
            sp.setRow(rcp.row);
            sp.setCol(rcp.col);
        } else {
            throw new Exception("Position " + positionString + " not valid");
        }
    }

    public void setSamplePosition(SamplePosition sp) {
        SamplePosition old = wrappedObject.getSamplePosition();
        wrappedObject.setSamplePosition(sp);
        propertyChangeSupport.firePropertyChange("samplePosition", old, sp);
    }

    public void setSamplePosition(SamplePositionWrapper sp) {
        setSamplePosition(sp.wrappedObject);
    }

    public SamplePositionWrapper getSamplePosition() {
        SamplePosition sp = wrappedObject.getSamplePosition();
        if (sp == null) {
            return null;
        }
        return new SamplePositionWrapper(appService, sp);
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
        SampleType oldType = getSampleType();
        wrappedObject.setSampleType(type);
        propertyChangeSupport.firePropertyChange("sampleType", oldType, type);
    }

    public void setSampleType(SampleTypeWrapper type) {
        setSampleType(type.wrappedObject);
    }

    public SampleType getSampleType() {
        return wrappedObject.getSampleType();
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
        Collection<SampleStorage> sampleStorages) {
        Sample sample = new Sample();
        sample.setInventoryId(inventoryId);
        sample.setPatientVisit(pv.getWrappedObject());
        sample.setLinkDate(new Date());
        sample.setSampleType(type.getWrappedObject());
        Double volume = null;
        for (SampleStorage ss : sampleStorages) {
            if (ss.getSampleType().getId().equals(type.getId())) {
                volume = ss.getVolume();
            }
        }
        sample.setQuantity(volume);
        return new SampleWrapper(appService, sample);
    }

    public void setQuantityFromType() {
        Study study = getPatientVisit().getPatient().getStudy();
        Double volume = null;
        for (SampleStorage ss : study.getSampleStorageCollection()) {
            if (ss.getSampleType().getId().equals(getSampleType().getId())) {
                volume = ss.getVolume();
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
        // TODO Auto-generated method stub
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

    public static boolean exists(WritableApplicationService appService,
        String inventoryId) throws ApplicationException {

        Sample sample = new Sample();
        sample.setInventoryId(inventoryId);
        List<Sample> samples = appService.search(Sample.class, sample);
        if (samples.size() == 0) {
            return false;
        }
        return true;
    }

}
