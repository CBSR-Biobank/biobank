package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleWrapper extends ModelWrapper<Sample> {

    public SampleWrapper(WritableApplicationService appService,
        Sample wrappedObject) {
        super(appService, wrappedObject);
    }

    public void setInventoryId(String inventoryId) {
        String oldInventoryId = inventoryId;
        wrappedObject.setInventoryId(inventoryId);
        propertyChangeSupport.firePropertyChange("inventoryId", oldInventoryId,
            inventoryId);
    }

    @Override
    protected void firePropertyChanges(Sample oldWrappedObject,
        Sample newWrappedObject) {
        propertyChangeSupport.firePropertyChange("inventoryId",
            oldWrappedObject, newWrappedObject);
        propertyChangeSupport.firePropertyChange("patientVisit",
            oldWrappedObject, newWrappedObject);
        propertyChangeSupport.firePropertyChange("samplePosition",
            oldWrappedObject, newWrappedObject);
    }

    @Override
    protected Class<Sample> getWrappedClass() {
        return Sample.class;
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        return DatabaseResult.OK;
    }

    public String getInventoryId() {
        return wrappedObject.getInventoryId();
    }

    public DatabaseResult checkInventoryIdUnique() throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " where inventoryId  = ? and patientVisit.patient.study.site=?",
            Arrays.asList(new Object[] { getInventoryId(), getSite() }));
        List<Sample> samples = appService.query(criteria);
        if (samples.size() == 0) {
            return DatabaseResult.OK;
        }
        for (Sample sample : samples) {
            if (sample.getInventoryId().equals(getInventoryId())) {
                return new DatabaseResult("A sample with inventoryId \""
                    + getInventoryId() + "\" already exists.");
            }
        }
        return DatabaseResult.OK;
    }

    private Site getSite() {
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
        Container parentContainer) throws Exception {
        RowColPos rcp = LabelingScheme.getRowColFromPositionString(
            positionString, parentContainer.getContainerType());
        SamplePosition sp = getSamplePosition();
        if (sp == null) {
            sp = new SamplePosition();
            setSamplePosition(sp);
        }
        sp.setSample(wrappedObject);
        // TODO check if update works well when sampleposition already exists
        sp.setRow(rcp.row);
        sp.setCol(rcp.col);

    }

    private void setSamplePosition(SamplePosition sp) {
        SamplePosition old = getSamplePosition();
        wrappedObject.setSamplePosition(sp);
        propertyChangeSupport.firePropertyChange("samplePosition", old, sp);
    }

    public SamplePosition getSamplePosition() {
        return wrappedObject.getSamplePosition();
    }

    public DatabaseResult checkPosition(Container parentContainer)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Sample.class.getName()
                + " where samplePosition.row=? and samplePosition.col=? and samplePosition.container=?",
            Arrays.asList(new Object[] { getSamplePosition().getRow(),
                getSamplePosition().getCol(),
                getSamplePosition().getContainer() }));
        List<Sample> samples = appService.query(criteria);
        if (samples.size() == 0) {
            return DatabaseResult.OK;
        }
        Sample sample = samples.get(0);
        return new DatabaseResult("Position already in use in container "
            + parentContainer.getLabel() + " by sample "
            + sample.getInventoryId());
    }

    public void setSampleType(SampleType type) {
        SampleType oldType = getSampleType();
        wrappedObject.setSampleType(type);
        propertyChangeSupport.firePropertyChange("sampleType", oldType, type);
    }

    public SampleType getSampleType() {
        return wrappedObject.getSampleType();
    }

    public void setLinkDate(Date date) {
        Date oldDate = getLinkDate();
        wrappedObject.setLinkDate(date);
        propertyChangeSupport.firePropertyChange("linkDate", oldDate, date);
    }

    private Date getLinkDate() {
        return wrappedObject.getLinkDate();
    }

    public static String getPositionString(Sample sample) {
        SampleWrapper wrapper = new SampleWrapper(null, sample);
        return wrapper.getPositionString();
    }

    public String getPositionString() {
        SamplePosition position = getSamplePosition();
        if (position == null) {
            return "none";
        } else {
            Container container = position.getContainer();
            Container topContainer = container;
            while (topContainer.getPosition() != null
                && topContainer.getPosition().getParentContainer() != null) {
                topContainer = topContainer.getPosition().getParentContainer();
            }
            return topContainer.getContainerType().getNameShort() + "-"
                + container.getLabel()
                + LabelingScheme.getPositionString(position);
        }
    }
}
