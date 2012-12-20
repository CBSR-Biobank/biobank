package edu.ualberta.med.biobank.action.info;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.center.Container;

/**
 * 
 * @author jferland
 * 
 */
public class SiteInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final Site site;
    private final List<SiteContainerTypeInfo> containerTypeInfos;
    private final List<StudyCountInfo> studyCountInfo;
    private final List<Container> topContainers;
    private final Long patientCount;
    private final Long processingEventCount;
    private final Long specimenCount;

    public Site getSite() {
        return site;
    }

    public List<SiteContainerTypeInfo> getContainerTypeInfos() {
        return containerTypeInfos;
    }

    public List<StudyCountInfo> getStudyCountInfos() {
        return studyCountInfo;
    }

    public List<Container> getTopContainers() {
        return topContainers;
    }

    public Long getContainerTypeCount() {
        return new Long(containerTypeInfos.size());
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

    public Long getProcessingEventCount() {
        return processingEventCount;
    }

    public Long getSpecimenCount() {
        return specimenCount;
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

    private SiteInfo(Site site, List<SiteContainerTypeInfo> containerTypes,
        List<StudyCountInfo> studies, List<Container> topContainers,
        Long patientCount, Long processingEventCount,
        Long aliquotedSpecimenCount) {
        this.site = site;
        this.containerTypeInfos = containerTypes;
        this.studyCountInfo = studies;
        this.topContainers = topContainers;
        this.patientCount = patientCount;
        this.processingEventCount = processingEventCount;
        this.specimenCount = aliquotedSpecimenCount;
    }

    public static class Builder {
        private Site site = new Site();
        private List<SiteContainerTypeInfo> containerTypes =
            new ArrayList<SiteContainerTypeInfo>();
        private List<StudyCountInfo> studies = new ArrayList<StudyCountInfo>();
        private List<Container> topContainers = new ArrayList<Container>();
        private Long patientCount;
        private Long processingEventCount;
        private Long specimenCount;

        public Builder() {
            site.setAddress(new Address());
        }

        public Builder setSite(Site site) {
            this.site = site;
            return this;
        }

        public Builder setContainerTypes(
            List<SiteContainerTypeInfo> containerTypes) {
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

        public Builder setProcessingEventCount(Long processingEventCount) {
            this.processingEventCount = processingEventCount;
            return this;
        }

        public Builder setSpecimenCount(Long specimenCount) {
            this.specimenCount = specimenCount;
            return this;
        }

        public SiteInfo build() {
            return new SiteInfo(site, containerTypes, studies, topContainers,
                patientCount, processingEventCount, specimenCount);
        }
    }
}