package edu.ualberta.med.biobank.test.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public interface ReportDataSource {
    public List<SiteWrapper> getSites() throws Exception;

    public List<SpecimenTypeWrapper> getSpecimenTypes() throws Exception;

    public List<AliquotedSpecimenWrapper> getSampleStorages() throws Exception;

    public List<SpecimenWrapper> getSpecimens() throws Exception;

    public List<ContainerWrapper> getContainers() throws Exception;

    public List<StudyWrapper> getStudies() throws Exception;

    public List<ProcessingEventWrapper> getPatientVisits() throws Exception;

    public List<PatientWrapper> getPatients() throws Exception;

    public WritableApplicationService getAppService();
}
