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
import edu.ualberta.med.biobank.common.wrappers.internal.AliquotPositionWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.SampleType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AliquotWrapper extends
    AbstractPositionHolder<Aliquot, AliquotPosition> {

    public AliquotWrapper(WritableApplicationService appService,
        Aliquot wrappedObject) {
        super(appService, wrappedObject);
    }

    public AliquotWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "inventoryId", "patientVisit", "position",
            "linkDate", "sampleType", "quantity", "oldComment" };
    }

    @Override
    public Class<Aliquot> getWrappedClass() {
        return Aliquot.class;
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
        List<AliquotWrapper> aliquots = getSamplesInSite(appService,
            getInventoryId(), getSite());
        boolean alreadyExists = false;
        if (aliquots.size() > 0 && isNew()) {
            alreadyExists = true;
        } else {
            for (AliquotWrapper aliquot : aliquots) {
                if (!aliquot.getId().equals(getId())) {
                    alreadyExists = true;
                    break;
                }
            }
        }
        if (alreadyExists) {
            throw new BiobankCheckException("An aliquot with inventory id \""
                + getInventoryId() + "\" already exists.");
        }
    }

    private void checkParentAcceptSampleType() throws BiobankCheckException {
        ContainerWrapper parent = getParent();
        if (parent != null) {
            ContainerTypeWrapper parentType = getParent().getContainerType();
            try {
                parentType.reload();
            } catch (Exception e) {
                throw new BiobankCheckException(e);
            }
            List<SampleTypeWrapper> types = parentType
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

    public void setAliquotPositionFromString(String positionString,
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
     * Method used to check if the current position of this aliquot is available
     * on the container. Return true if the position is free, false otherwise
     */
    public boolean isPositionFree(ContainerWrapper parentContainer)
        throws ApplicationException {
        RowColPos position = getPosition();
        if (position != null) {
            HQLCriteria criteria = new HQLCriteria("from "
                + Aliquot.class.getName()
                + " where aliquotPosition.row=? and aliquotPosition.col=?"
                + " and aliquotPosition.container=?", Arrays
                .asList(new Object[] { position.row, position.col,
                    parentContainer.getWrappedObject() }));

            List<Aliquot> samples = appService.query(criteria);
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
            return null;
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
            return directParent.getLabel()
                + LabelingScheme.getPositionString(position, directParent)
                + " (" + nameShort + ")";
        return directParent.getLabel()
            + LabelingScheme.getPositionString(position, directParent);
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

    public static List<AliquotWrapper> getSamplesInSite(
        WritableApplicationService appService, String inventoryId,
        SiteWrapper siteWrapper) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Aliquot.class.getName()
                + " where inventoryId = ? and patientVisit.patient.study.site.id = ?",
            Arrays.asList(new Object[] { inventoryId, siteWrapper.getId() }));
        List<Aliquot> aliquots = appService.query(criteria);
        List<AliquotWrapper> list = new ArrayList<AliquotWrapper>();
        for (Aliquot aliquot : aliquots) {
            if (aliquot.getInventoryId().equals(inventoryId)) {
                list.add(new AliquotWrapper(appService, aliquot));
            }
        }
        return list;
    }

    @Override
    public int compareTo(ModelWrapper<Aliquot> o) {
        if (o instanceof AliquotWrapper) {
            return getInventoryId().compareTo(
                ((AliquotWrapper) o).getInventoryId());
        }
        return 0;
    }

    @Override
    protected AbstractPositionWrapper<AliquotPosition> getSpecificPositionWrapper(
        boolean initIfNoPosition) {
        AliquotPosition pos = wrappedObject.getAliquotPosition();
        if (pos != null) {
            return new AliquotPositionWrapper(appService, pos);
        } else if (initIfNoPosition) {
            AliquotPositionWrapper posWrapper = new AliquotPositionWrapper(
                appService);
            posWrapper.setAliquot(this);
            wrappedObject.setAliquotPosition(posWrapper.getWrappedObject());
            return posWrapper;
        }
        return null;
    }

    @Override
    public String toString() {
        return getInventoryId();
    }
}
