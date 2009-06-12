package edu.ualberta.med.biobank.views;

import java.util.List;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public enum SearchType {
	Site {
		@Override
		public Node search(WritableApplicationService appService,
				String searchValue, SessionAdapter sessionAdapter)
				throws Exception {
			Site site = new Site();
			site.setName(searchValue);
			List<Site> sites = appService.search(Site.class, site);
			if (sites.size() == 0) {
				BioBankPlugin.openMessage("Search", "No site found with name "
						+ searchValue);
			} else if (sites.size() == 1) {
				site = sites.get(0);
				return sessionAdapter.accept(new NodeSearchVisitor(Site.class,
					site.getId()));
			} else {
				throw new Exception("Should not find more than one entry ?");
			}
			return null;
		}
	},
	Study {
		@Override
		public Node search(WritableApplicationService appService,
				String searchValue, SessionAdapter sessionAdapter)
				throws Exception {
			Study study = new Study();
			study.setName(searchValue);
			List<Study> studies = appService.search(Study.class, study);
			if (studies.size() == 0) {
				BioBankPlugin.openMessage("Search", "No study found with name "
						+ searchValue);
			} else if (studies.size() == 1) {
				study = studies.get(0);
				return sessionAdapter.accept(new NodeSearchVisitor(Study.class,
					study.getId()));
			} else {
				throw new Exception("Should not find more than one entry ?");
			}
			return null;
		}
	},
	Patient {
		@Override
		public Node search(WritableApplicationService appService,
				String searchValue, SessionAdapter sessionAdapter)
				throws Exception {
			Patient patient = new Patient();
			patient.setNumber(searchValue);
			List<Patient> patients = appService.search(Patient.class, patient);
			if (patients.size() == 0) {
				BioBankPlugin.openMessage("Search",
					"No patient found with number " + searchValue);
			} else if (patients.size() == 1) {
				patient = patients.get(0);
				return sessionAdapter.accept(new NodeSearchVisitor(
					Patient.class, patient.getId()));
			} else {
				throw new Exception("Should not find more than one entry ?");
			}
			return null;
		}
	},
	Sample {
		@Override
		public Node search(WritableApplicationService appService,
				String searchValue, SessionAdapter sessionAdapter)
				throws Exception {
			Sample sample = new Sample();
			sample.setInventoryId(searchValue);
			List<Sample> samples = appService.search(Sample.class, sample);
			if (samples.size() == 0) {
				BioBankPlugin.openMessage("Search",
					"No sample found with inventoryId " + searchValue);
			} else if (samples.size() == 1) {
				sample = samples.get(0);
				return sessionAdapter.accept(new NodeSearchVisitor(
					Sample.class, sample.getId()));
			} else {
				throw new Exception("Should not find more than one entry ?");
			}
			return null;
		}
	},
	Container {
		@Override
		public Node search(WritableApplicationService appService,
				String searchValue, SessionAdapter sessionAdapter)
				throws Exception {
			StorageContainer storageContainer = new StorageContainer();
			storageContainer.setBarcode(searchValue);
			List<StorageContainer> containers = appService.search(
				StorageContainer.class, storageContainer);
			if (containers.size() == 0) {
				BioBankPlugin.openMessage("Search",
					"No storage container found with barcode " + searchValue);
			} else if (containers.size() == 1) {
				storageContainer = containers.get(0);
				return sessionAdapter.accept(new NodeSearchVisitor(
					StorageContainer.class, storageContainer.getId()));
			} else {
				throw new Exception("Should not find more than one entry ?");
			}
			return null;
		}
	};

	public abstract Node search(WritableApplicationService appService,
			String searchValue, SessionAdapter sessionAdapter) throws Exception;
}
