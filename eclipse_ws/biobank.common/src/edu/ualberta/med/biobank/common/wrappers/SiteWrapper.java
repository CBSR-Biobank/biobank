package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.wrappers.base.SiteBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.helpers.SiteQuery;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SiteWrapper extends SiteBaseWrapper {
    private static final String TOP_CONTAINER_COLLECTION_CACHE_KEY =
        "topContainerCollection"; //$NON-NLS-1$

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
        super(appService, wrappedObject);
    }

    public SiteWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public List<ContainerTypeWrapper> getContainerTypeCollection() {
        return getContainerTypeCollection(false);
    }

    public List<ContainerWrapper> getContainerCollection() {
        return getContainerCollection(false);
    }

    @SuppressWarnings("unchecked")
    public List<ContainerWrapper> getTopContainerCollection(boolean sort)
        throws Exception {
        List<ContainerWrapper> topContainerCollection =
            (List<ContainerWrapper>) cache
                .get(TOP_CONTAINER_COLLECTION_CACHE_KEY);

        if (topContainerCollection == null) {
            topContainerCollection = SiteQuery.getTopContainerCollection(this);
            if (sort)
                Collections.sort(topContainerCollection);
            cache.put(TOP_CONTAINER_COLLECTION_CACHE_KEY,
                topContainerCollection);
        }
        return topContainerCollection;
    }

    public List<ContainerWrapper> getTopContainerCollection() throws Exception {
        return getTopContainerCollection(false);
    }

    public void clearTopContainerCollection() {
        cache.put(TOP_CONTAINER_COLLECTION_CACHE_KEY, null);
    }

    public Set<ClinicWrapper> getWorkingClinicCollection() {
        List<StudyWrapper> studies = getStudyCollection();
        Set<ClinicWrapper> clinics = new HashSet<ClinicWrapper>();
        for (StudyWrapper study : studies) {
            clinics.addAll(study.getClinicCollection());
        }
        return clinics;
    }

    @Override
    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(true);
    }
}
