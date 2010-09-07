package edu.ualberta.med.biobank.test.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public interface ReportDataSource {
    public List<SiteWrapper> getSites();

    public List<SampleTypeWrapper> getSampleTypes();

    public List<SampleStorageWrapper> getSampleStorages();

    public List<AliquotWrapper> getAliquots();

    public List<ContainerWrapper> getContainers();

    public List<ClinicWrapper> getClinics();

    public List<StudyWrapper> getStudies();

    public List<ContactWrapper> getContacts();

    public List<PatientVisitWrapper> getPatientVisits();

    public List<PatientWrapper> getPatients();

    public WritableApplicationService getAppService();
}
