package edu.ualberta.med.biobank.test.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public interface ReportDataSource {
    public List<SiteWrapper> getSites() throws Exception;

    public List<SampleTypeWrapper> getSampleTypes() throws Exception;

    public List<SampleStorageWrapper> getSampleStorages() throws Exception;

    public List<AliquotWrapper> getAliquots() throws Exception;

    public List<ContainerWrapper> getContainers() throws Exception;

    public List<StudyWrapper> getStudies() throws Exception;

    public List<PatientVisitWrapper> getPatientVisits() throws Exception;

    public List<PatientWrapper> getPatients() throws Exception;

    public WritableApplicationService getAppService();
}
