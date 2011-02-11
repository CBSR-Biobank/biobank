package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchInfoBaseWrapper;
import edu.ualberta.med.biobank.model.DispatchInfo;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchInfoWrapper extends DispatchInfoBaseWrapper {

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

    public List<SiteWrapper> getDestSiteCollection() {
        return getDestSiteCollection(false);
    }

    public void addDestSites(Collection<SiteWrapper> newDestSites)
        throws BiobankCheckException {
        if (newDestSites == null || newDestSites.isEmpty()) {
            return;
        }

        // new
        for (SiteWrapper site : newDestSites) {
            if (site.getStudyCollection().contains(getStudy())) {
                throw new BiobankCheckException(
                    "Site "
                        + site.getNameShort()
                        + " cannot be a destination site to dispatch aliquots from study "
                        + getStudy().getNameShort()
                        + ": this study should be in its studies list.");
            }
        }
        addToDestSiteCollection(new ArrayList<SiteWrapper>(newDestSites));
    }

    public void removeDestSites(Collection<SiteWrapper> destSitesToRemove) {
        removeFromDestSiteCollection(new ArrayList<SiteWrapper>(
            destSitesToRemove));
    }
}
