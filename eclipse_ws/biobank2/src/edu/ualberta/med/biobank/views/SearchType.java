package edu.ualberta.med.biobank.views;

import java.util.List;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.exception.MultipleSearchResultException;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdaptorBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public enum SearchType {
    Site {
        @Override
        public AdaptorBase search(WritableApplicationService appService,
            String searchValue, AdaptorBase node) throws Exception {
            List<Site> sites = ModelUtils.queryProperty(appService, Site.class,
                "name", searchValue, true);
            if (sites.size() == 0) {
                BioBankPlugin.openMessage("Search", "No site found with name "
                    + searchValue);
            } else if (sites.size() == 1) {
                Site site = sites.get(0);
                return node.accept(new NodeSearchVisitor(Site.class, site
                    .getId()));
            } else {
                throw new MultipleSearchResultException();
            }
            return null;
        }
    },
    Study {
        @Override
        public AdaptorBase search(WritableApplicationService appService,
            String searchValue, AdaptorBase node) throws Exception {
            List<Study> studies = ModelUtils.queryProperty(appService,
                Study.class, "nameShort", searchValue, true);
            if (studies.size() == 0) {
                BioBankPlugin.openMessage("Search", "No study found with name "
                    + searchValue);
            } else if (studies.size() == 1) {
                Study study = studies.get(0);
                return node.accept(new NodeSearchVisitor(Study.class, study
                    .getId()));
            } else {
                throw new MultipleSearchResultException();
            }
            return null;
        }
    },
    Patient {
        @Override
        public AdaptorBase search(WritableApplicationService appService,
            String searchValue, AdaptorBase node) throws Exception {
            List<Patient> patients = ModelUtils.queryProperty(appService,
                Patient.class, "number", searchValue, true);
            if (patients.size() == 0) {
                BioBankPlugin.openMessage("Search",
                    "No patient found with number " + searchValue);
            } else if (patients.size() == 1) {
                Patient patient = patients.get(0);
                return node.accept(new NodeSearchVisitor(Patient.class, patient
                    .getId()));
            } else {
                throw new MultipleSearchResultException();
            }
            return null;
        }
    },
    Sample {
        @Override
        public AdaptorBase search(WritableApplicationService appService,
            String searchValue, AdaptorBase node) throws Exception {
            List<Sample> samples = ModelUtils.queryProperty(appService,
                Sample.class, "inventoryId", searchValue, true);
            if (samples.size() == 0) {
                BioBankPlugin.openMessage("Search",
                    "No sample found with inventoryId " + searchValue);
            } else if (samples.size() == 1) {
                Sample sample = samples.get(0);
                PatientVisitAdapter pvAdapter = (PatientVisitAdapter) node
                    .accept(new NodeSearchVisitor(PatientVisit.class, sample
                        .getPatientVisit().getId()));
                pvAdapter.setSelectedSample(sample);
                return pvAdapter;
            } else {
                throw new MultipleSearchResultException();
            }
            return null;
        }
    },
    Container {
        @Override
        public AdaptorBase search(WritableApplicationService appService,
            String searchValue, AdaptorBase node)
            throws MultipleSearchResultException, Exception {
            List<Container> containers = ModelUtils.queryProperty(appService,
                Container.class, "name", searchValue, true);
            if (containers.size() == 0) {
                BioBankPlugin.openMessage("Search",
                    "No storage container found with barcode " + searchValue);
            } else if (containers.size() == 1) {
                Container container = containers.get(0);
                return node.accept(new NodeSearchVisitor(Container.class,
                    container.getId()));
            } else {
                throw new MultipleSearchResultException();
            }
            return null;
        }
    };

    public abstract AdaptorBase search(WritableApplicationService appService,
        String searchValue, AdaptorBase node) throws MultipleSearchResultException,
        Exception;

}
