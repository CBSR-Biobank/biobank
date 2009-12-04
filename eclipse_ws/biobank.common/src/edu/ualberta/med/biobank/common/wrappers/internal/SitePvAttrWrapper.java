package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.PvAttrType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SitePvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SitePvAttrWrapper extends ModelWrapper<SitePvAttr> {

    public SitePvAttrWrapper(WritableApplicationService appService,
        SitePvAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public SitePvAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "label", "pvAttrType", "site" };
    }

    @Override
    public Class<SitePvAttr> getWrappedClass() {
        return SitePvAttr.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        // FIXME make sure another object with same label is not present
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        // FIXME if used by any study then it cannot be deleted
    }

    public String getLabel() {
        return wrappedObject.getLabel();
    }

    public void setLabel(String label) {
        String oldLabel = getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    public PvAttrTypeWrapper getPvAttrType() {
        PvAttrType pvAttrType = wrappedObject.getPvAttrType();
        if (pvAttrType == null) {
            return null;
        }
        return new PvAttrTypeWrapper(appService, pvAttrType);
    }

    public void setPvAttrType(PvAttrType pvAttrType) {
        PvAttrType oldPvInfo = wrappedObject.getPvAttrType();
        wrappedObject.setPvAttrType(pvAttrType);
        propertyChangeSupport.firePropertyChange("pvAttrType", oldPvInfo,
            pvAttrType);
    }

    public void setPvAttrType(PvAttrTypeWrapper pvAttrType) {
        setPvAttrType(pvAttrType.getWrappedObject());
    }

    public SiteWrapper getSite() {
        Site site = wrappedObject.getSite();
        if (site == null) {
            return null;
        }
        return new SiteWrapper(appService, site);
    }

    public void setSite(Site site) {
        Site oldSite = wrappedObject.getSite();
        wrappedObject.setSite(site);
        propertyChangeSupport.firePropertyChange("site", oldSite, site);
    }

    public void setSite(SiteWrapper site) {
        if (site == null) {
            setSite((Site) null);
        } else {
            setSite(site.getWrappedObject());
        }
    }

    public static List<SitePvAttrWrapper> transformToWrapperList(
        WritableApplicationService appService, List<SitePvAttr> pipList) {
        List<SitePvAttrWrapper> list = new ArrayList<SitePvAttrWrapper>();
        for (SitePvAttr type : pipList) {
            list.add(new SitePvAttrWrapper(appService, type));
        }
        return list;
    }

    @Override
    public int compareTo(ModelWrapper<SitePvAttr> o) {
        return 0;
    }

    @Override
    public String toString() {
        SiteWrapper site = getSite();
        return "" + getId() + ":" + getLabel() + ":"
            + getPvAttrType().getName() + ":"
            + ((site == null) ? "no site" : site);
    }
}