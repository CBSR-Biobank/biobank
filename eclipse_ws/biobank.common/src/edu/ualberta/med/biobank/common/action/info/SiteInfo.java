package edu.ualberta.med.biobank.common.action.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;

/**
 * 
 * @author jferland
 * 
 */
public class SiteInfo implements ActionResult {
    private static final long serialVersionUID = 1L;
    public final Site site;
    public final List<ContainerTypeInfo> containerTypes;
    public final List<StudyCountInfo> studyCountInfo;
    public final List<Container> topContainers;
    public final Long patientCount;
    public final Long collectionEventCount;
    public final Long aliquotedSpecimenCount;

    public Site getSite() {
        return site;
    }

    public List<ContainerTypeInfo> getContainerTypeCollection() {
        return Collections.unmodifiableList(containerTypes);
    }

    public List<StudyCountInfo> getStudyCollection() {
        return Collections.unmodifiableList(studyCountInfo);
    }

    public List<Container> getTopContainerCollection() {
        return Collections.unmodifiableList(topContainers);
    }

    public Long getContainerTypeCount() {
        return new Long(containerTypes.size());
    }

    public Long getStudyCount() {
        return new Long(studyCountInfo.size());
    }

    public Long getTopContainerCount() {
        return new Long(topContainers.size());
    }

    public Long getPatientCount() {
        return patientCount;
    }

    public Long getCollectionEventCount() {
        return collectionEventCount;
    }

    public Long getAliquotedSpecimenCount() {
        return aliquotedSpecimenCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((site == null) ? 0 : site.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SiteInfo other = (SiteInfo) obj;
        if (site == null) {
            if (other.site != null) return false;
        } else if (!site.equals(other.site)) return false;
        return true;
    }

    private SiteInfo(Site site, List<ContainerTypeInfo> containerTypes,
        List<StudyCountInfo> studies, List<Container> topContainers,
        Long patientCount, Long collectionEventCount,
        Long aliquotedSpecimenCount) {
        this.site = site;
        this.containerTypes = containerTypes;
        this.studyCountInfo = studies;
        this.topContainers = topContainers;
        this.patientCount = patientCount;
        this.collectionEventCount = collectionEventCount;
        this.aliquotedSpecimenCount = aliquotedSpecimenCount;
    }

    public static class Builder {
        private Site site = new Site();
        private List<ContainerTypeInfo> containerTypes =
            new ArrayList<ContainerTypeInfo>();
        private List<StudyCountInfo> studies = new ArrayList<StudyCountInfo>();
        private List<Container> topContainers = new ArrayList<Container>();
        private Long patientCount;
        private Long collectionEventCount;
        private Long aliquotedSpecimenCount;

        public Builder() {
            site.setAddress(new Address());
        }

        public Builder setSite(Site site) {
            this.site = site;
            return this;
        }

        public Builder setContainerTypes(List<ContainerTypeInfo> containerTypes) {
            this.containerTypes = containerTypes;
            return this;
        }

        public Builder setStudies(List<StudyCountInfo> studies) {
            this.studies = studies;
            return this;
        }

        public Builder setTopContainers(List<Container> topContainers) {
            this.topContainers = topContainers;
            return this;
        }

        public Builder setPatientCount(Long patientCount) {
            this.patientCount = patientCount;
            return this;
        }

        public Builder setCollectionEventCount(Long collectionEventCount) {
            this.collectionEventCount = collectionEventCount;
            return this;
        }

        public Builder setAliquotedSpecimenCount(Long aliquotedSpecimenCount) {
            this.aliquotedSpecimenCount = aliquotedSpecimenCount;
            return this;
        }

        public SiteInfo build() {
            return new SiteInfo(site, containerTypes, studies, topContainers,
                patientCount, collectionEventCount, aliquotedSpecimenCount);
        }
    }
}