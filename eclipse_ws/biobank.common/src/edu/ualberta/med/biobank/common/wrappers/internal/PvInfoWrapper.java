package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PvInfoWrapper extends ModelWrapper<PvInfo> {

    public PvInfoWrapper(WritableApplicationService appService,
        PvInfo wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "label", "possibleValues", "pvInfoPossible",
            "pvInfoType" };
    }

    @Override
    public Class<PvInfo> getWrappedClass() {
        return PvInfo.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (isUsedByPatientVisits()) {
            throw new BiobankCheckException(
                "Unable to delete PvInfoData with id " + getId()
                    + ". A patient visit using it exists in storage."
                    + " Remove all instances before deleting this type.");
        }
    }

    public boolean isUsedByPatientVisits() throws ApplicationException,
        BiobankCheckException {
        String queryString = "select count(pvd) from "
            + PvInfoData.class.getName() + " as pvd where pvd.pvInfo = ?)";
        HQLCriteria c = new HQLCriteria(queryString, Arrays
            .asList(new Object[] { wrappedObject }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0) > 0;
    }

    public String getLabel() {
        return wrappedObject.getLabel();
    }

    public void setLabel(String label) {
        String oldLabel = wrappedObject.getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    public String getAllowedValues() {
        return wrappedObject.getAllowedValues();
    }

    public void setAllowedValues(String possibleValues) {
        String oldPV = wrappedObject.getAllowedValues();
        wrappedObject.setAllowedValues(possibleValues);
        propertyChangeSupport.firePropertyChange("possibleValues", oldPV,
            possibleValues);
    }

    public PvInfoPossibleWrapper getPvInfoPossible() {
        return new PvInfoPossibleWrapper(appService, wrappedObject
            .getPvInfoPossible());
    }

    public void setPvInfoPossible(PvInfoPossible pvInfoPossible) {
        PvInfoPossible oldPVInfoPossible = wrappedObject.getPvInfoPossible();
        wrappedObject.setPvInfoPossible(pvInfoPossible);
        propertyChangeSupport.firePropertyChange("pvInfoPossible",
            oldPVInfoPossible, pvInfoPossible);
    }

    public void setPvInfoPossible(PvInfoPossibleWrapper pvInfoPossible) {
        setPvInfoPossible(pvInfoPossible.getWrappedObject());
    }

    public PvInfoTypeWrapper getPvInfoType() {
        return new PvInfoTypeWrapper(appService, wrappedObject
            .getPvInfoPossible().getPvInfoType());
    }

    @Override
    public int compareTo(ModelWrapper<PvInfo> o) {
        return 0;
    }
}
