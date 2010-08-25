package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperException;
import edu.ualberta.med.biobank.model.DispatchInfo;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchInfoWrapper extends ModelWrapper<DispatchInfo> {

    private StudyWrapper study;
    private SiteWrapper fromSite;

    public DispatchInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchInfoWrapper(WritableApplicationService appService,
        DispatchInfo info) {
        super(appService, info);
    }

    @Override
    public int compareTo(ModelWrapper<DispatchInfo> o) {
        return 0;
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "study", "fromSite" };
    }

    @Override
    public Class<DispatchInfo> getWrappedClass() {
        return DispatchInfo.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {

    }

    @Override
    protected void deleteChecks() throws Exception {

    }

    public StudyWrapper getStudy() {
        if (study == null) {
            Study s = wrappedObject.getStudy();
            if (s == null)
                return null;
            study = new StudyWrapper(appService, s);
        }
        return study;
    }

    public void setStudy(StudyWrapper study) {
        this.study = study;
        Study oldStudyRaw = wrappedObject.getStudy();
        Study newStudyRaw = study.getWrappedObject();
        wrappedObject.setStudy(newStudyRaw);
        propertyChangeSupport.firePropertyChange("study", oldStudyRaw,
            newStudyRaw);
    }

    public SiteWrapper getFromSite() {
        if (fromSite == null) {
            Site s = wrappedObject.getSrcSite();
            if (s == null)
                return null;
            fromSite = new SiteWrapper(appService, s);
        }
        return fromSite;
    }

    public void setFromSite(SiteWrapper fromSite) {
        this.fromSite = fromSite;
        Site oldSiteRaw = wrappedObject.getSrcSite();
        Site newSiteRaw = fromSite.getWrappedObject();
        wrappedObject.setSrcSite(newSiteRaw);
        propertyChangeSupport.firePropertyChange("fromSite", oldSiteRaw,
            newSiteRaw);
    }

    @SuppressWarnings("unchecked")
    public List<SiteWrapper> getToSiteCollection() {
        List<SiteWrapper> toSiteCollection = (List<SiteWrapper>) propertiesMap
            .get("toSiteCollection");
        if (toSiteCollection == null) {
            Collection<Site> children = wrappedObject.getDestSiteCollection();
            if (children != null) {
                toSiteCollection = new ArrayList<SiteWrapper>();
                for (Site s : children) {
                    toSiteCollection.add(new SiteWrapper(appService, s));
                }
                propertiesMap.put("toSiteCollection", toSiteCollection);
            }
        }
        return toSiteCollection;
    }

    public void addToSites(Collection<SiteWrapper> newToSites) {
        if (newToSites != null && newToSites.size() > 0) {
            Collection<Site> allSiteObjects = new HashSet<Site>();
            List<SiteWrapper> allSiteWrappers = new ArrayList<SiteWrapper>();
            // already added sites
            List<SiteWrapper> currentList = getToSiteCollection();
            if (currentList != null) {
                for (SiteWrapper site : currentList) {
                    allSiteObjects.add(site.getWrappedObject());
                    allSiteWrappers.add(site);
                }
            }
            // new
            for (SiteWrapper site : newToSites) {
                allSiteObjects.add(site.getWrappedObject());
                allSiteWrappers.add(site);
            }
            setToSites(allSiteObjects, allSiteWrappers);
        }
    }

    public void removeToSites(Collection<SiteWrapper> sitesToRemove) {
        if (sitesToRemove != null && sitesToRemove.size() > 0) {
            Collection<Site> allSiteObjects = new HashSet<Site>();
            List<SiteWrapper> allSiteWrappers = new ArrayList<SiteWrapper>();
            // already added
            List<SiteWrapper> currentList = getToSiteCollection();
            if (currentList != null) {
                for (SiteWrapper site : currentList) {
                    if (!sitesToRemove.contains(site)) {
                        allSiteObjects.add(site.getWrappedObject());
                        allSiteWrappers.add(site);
                    }
                }
            }
            setToSites(allSiteObjects, allSiteWrappers);
        }
    }

    private void setToSites(Collection<Site> allSiteObjects,
        List<SiteWrapper> allSiteWrappers) {
        Collection<Site> oldCollection = wrappedObject.getDestSiteCollection();
        wrappedObject.setDestSiteCollection(allSiteObjects);
        propertyChangeSupport.firePropertyChange("toSiteCollection",
            oldCollection, allSiteObjects);
        propertiesMap.put("toSiteCollection", allSiteWrappers);
    }
}
