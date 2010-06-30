package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.PvAttrType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SitePvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SitePvAttrWrapper extends ModelWrapper<SitePvAttr> {

    private PvAttrTypeWrapper pvAttrType;
    private SiteWrapper site;

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
        if (pvAttrType == null) {
            PvAttrType p = wrappedObject.getPvAttrType();
            if (p == null) {
                return null;
            }
            pvAttrType = new PvAttrTypeWrapper(appService, p);
        }
        return pvAttrType;
    }

    public void setPvAttrType(PvAttrType pvAttrType) {
        if (pvAttrType == null)
            this.pvAttrType = null;
        else
            this.pvAttrType = new PvAttrTypeWrapper(appService, pvAttrType);
        PvAttrType oldPvInfo = wrappedObject.getPvAttrType();
        wrappedObject.setPvAttrType(pvAttrType);
        propertyChangeSupport.firePropertyChange("pvAttrType", oldPvInfo,
            pvAttrType);
    }

    public void setPvAttrType(PvAttrTypeWrapper pvAttrType) {
        setPvAttrType(pvAttrType.getWrappedObject());
    }

    public SiteWrapper getSite() {
        if (site == null) {
            Site s = wrappedObject.getSite();
            if (s == null) {
                return null;
            }
            site = new SiteWrapper(appService, s);
        }
        return site;
    }

    public void setSite(Site site) {
        if (site == null)
            this.site = null;
        else
            this.site = new SiteWrapper(appService, site);
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

    @Override
    public void reload() throws Exception {
        super.reload();
        pvAttrType = null;
        site = null;
    }

}