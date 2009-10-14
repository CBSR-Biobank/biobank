package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.PvInfoType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PvInfoPossibleWrapper extends ModelWrapper<PvInfoPossible> {

    public PvInfoPossibleWrapper(WritableApplicationService appService,
        PvInfoPossible wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvInfoPossibleWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "label", "isDefault", "pvInfoType", "site" };
    }

    @Override
    public Class<PvInfoPossible> getWrappedClass() {
        return PvInfoPossible.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
    }

    public String getLabel() {
        return wrappedObject.getLabel();
    }

    public void setLabel(String label) {
        String oldLabel = getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    public Boolean getIsDefault() {
        return wrappedObject.getIsDefault();
    }

    public void setIsDefault(Boolean isDefault) {
        Boolean oldIsDefault = getIsDefault();
        wrappedObject.setIsDefault(isDefault);
        propertyChangeSupport.firePropertyChange("isDefault", oldIsDefault,
            isDefault);
    }

    public PvInfoTypeWrapper getPvInfoType() {
        return new PvInfoTypeWrapper(appService, wrappedObject.getPvInfoType());
    }

    public void setPvInfoType(PvInfoType pvInfoType) {
        PvInfoType oldPvInfo = wrappedObject.getPvInfoType();
        wrappedObject.setPvInfoType(pvInfoType);
        propertyChangeSupport.firePropertyChange("pvInfoType", oldPvInfo,
            pvInfoType);
    }

    public void setPvInfoType(PvInfoTypeWrapper pvInfoType) {
        setPvInfoType(pvInfoType.getWrappedObject());
    }

    public SiteWrapper getSite() {
        Site site = wrappedObject.getSite();
        if (site == null) {
            return null;
        }
        return new SiteWrapper(appService, site);
    }

    public void setSite(SiteWrapper siteWrapper) {
        Site oldSite = wrappedObject.getSite();
        Site newSite = siteWrapper.getWrappedObject();
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("site", oldSite, newSite);
    }

    public static List<PvInfoPossibleWrapper> getAllWrappers(
        WritableApplicationService appService) throws ApplicationException {
        List<PvInfoPossible> objects = appService.search(PvInfoPossible.class,
            new PvInfoPossible());
        List<PvInfoPossibleWrapper> wrappers = new ArrayList<PvInfoPossibleWrapper>();
        for (PvInfoPossible pv : objects) {
            wrappers.add(new PvInfoPossibleWrapper(appService, pv));
        }
        return wrappers;
    }

    public static List<PvInfoPossibleWrapper> transformToWrapperList(
        WritableApplicationService appService, List<PvInfoPossible> pipList) {
        List<PvInfoPossibleWrapper> list = new ArrayList<PvInfoPossibleWrapper>();
        for (PvInfoPossible type : pipList) {
            list.add(new PvInfoPossibleWrapper(appService, type));
        }
        return list;
    }

    public static List<PvInfoPossibleWrapper> getGlobalPvInfoPossible(
        WritableApplicationService appService, boolean sort)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria("from "
            + PvInfoPossible.class.getName() + " where site = null");

        List<PvInfoPossible> pipList = appService.query(c);
        List<PvInfoPossibleWrapper> list = transformToWrapperList(appService,
            pipList);
        if (sort)
            Collections.sort(list);
        return list;
    }

    @Override
    public int compareTo(ModelWrapper<PvInfoPossible> o) {
        return 0;
    }
}