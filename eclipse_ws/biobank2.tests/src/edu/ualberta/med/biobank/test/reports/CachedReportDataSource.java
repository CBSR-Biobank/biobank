package edu.ualberta.med.biobank.test.reports;

import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.helpers.SiteQuery;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.ArrayList;
import java.util.List;

public class CachedReportDataSource implements ReportDataSource {
    private WritableApplicationService appService;
    private List<SiteWrapper> sites;
    private List<SpecimenTypeWrapper> sampleTypes;
    private List<AliquotedSpecimenWrapper> sampleStorages;
    private List<SpecimenWrapper> aliquots;
    private List<ContainerWrapper> containers;
    private List<StudyWrapper> studies;
    private List<PatientWrapper> patients;

    public CachedReportDataSource(WritableApplicationService appService) {
        this.appService = appService;
    }

    public List<SiteWrapper> getSites() throws Exception {
        if (sites == null) {
            sites = SiteQuery.getSites(appService);
        }
        return sites;
    }

    public List<SpecimenTypeWrapper> getSpecimenTypes()
        throws ApplicationException {
        if (sampleTypes == null) {
            sampleTypes = SpecimenTypeWrapper.getAllSpecimenTypes(appService,
                false);
        }
        return sampleTypes;
    }

    public List<AliquotedSpecimenWrapper> getAliquotedSpecimens()
        throws ApplicationException {
        if (sampleStorages == null) {
            HQLCriteria criteria = new HQLCriteria("from "
                + AliquotedSpecimen.class.getName());
            List<AliquotedSpecimen> rawAliquotedSpecimen = appService
                .query(criteria);

            sampleStorages = new ArrayList<AliquotedSpecimenWrapper>();
            for (AliquotedSpecimen sampleStorage : rawAliquotedSpecimen) {
                sampleStorages.add(new AliquotedSpecimenWrapper(appService,
                    sampleStorage));
            }
        }
        return sampleStorages;
    }

    public List<SpecimenWrapper> getSpecimens() throws ApplicationException {
        if (aliquots == null) {
            HQLCriteria criteria = new HQLCriteria("from "
                + Specimen.class.getName());
            List<Specimen> rawSpecimens = appService.query(criteria);

            aliquots = new ArrayList<SpecimenWrapper>();
            for (Specimen aliquot : rawSpecimens) {
                aliquots.add(new SpecimenWrapper(appService, aliquot));
            }
        }
        return aliquots;
    }

    public List<ContainerWrapper> getContainers() throws ApplicationException {
        if (containers == null) {
            HQLCriteria criteria = new HQLCriteria("from "
                + Container.class.getName());
            List<Container> tmp = appService.query(criteria);
            containers = ModelWrapper.wrapModelCollection(appService, tmp,
                ContainerWrapper.class);
        }
        return containers;
    }

    public List<StudyWrapper> getStudies() throws ApplicationException {
        if (studies == null) {
            studies = StudyWrapper.getAllStudies(appService);
        }
        return studies;
    }

    public List<ProcessingEventWrapper> getPatientVisits()
        throws ApplicationException {
        return null;
        // FIXME: patient visits need to be converted
        // if (patientVisits == null) {
        // HQLCriteria criteria = new HQLCriteria("from "
        // + ProcessingEvent.class.getName());
        // List<PatientVisit> rawVisits = appService.query(criteria);
        //
        // patientVisits = new ArrayList<ProcessingEventWrapper>();
        // for (PatientVisit visit : rawVisits) {
        // patientVisits.add(new ProcessingEventWrapper(appService, visit));
        // }
        // }
        // return patientVisits;
    }

    public List<PatientWrapper> getPatients() throws ApplicationException {
        if (patients == null) {
            HQLCriteria criteria = new HQLCriteria("from "
                + Patient.class.getName());
            List<Patient> rawPatients = appService.query(criteria);

            patients = new ArrayList<PatientWrapper>();
            for (Patient patient : rawPatients) {
                patients.add(new PatientWrapper(appService, patient));
            }
        }
        return patients;
    }

    public WritableApplicationService getAppService() {
        return appService;
    }
}
