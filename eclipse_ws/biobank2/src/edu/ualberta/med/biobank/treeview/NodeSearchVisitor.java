package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.model.Study;

public class NodeSearchVisitor {

	@SuppressWarnings("unchecked")
	private Class typeSearched;
	private int id;

	@SuppressWarnings("unchecked")
	public NodeSearchVisitor(Class typeSearch, int id) {
		this.typeSearched = typeSearch;
		this.id = id;
	}

	private Node visitChildren(Node node) {
		node.loadChildren();
		for (Node child : node.getChildren()) {
			return child.accept(this);
		}
		return null;
	}

	public Node visit(SessionAdapter session) {
		if (typeSearched == Site.class) {
			return session.getChild(id);
		}
		return visitChildren(session);
	}

	public Node visit(SiteAdapter siteAdapter) {
		if (typeSearched == Study.class || typeSearched == Patient.class
				|| typeSearched == PatientVisit.class
				|| typeSearched == Sample.class) {
			return siteAdapter.getChild(SiteAdapter.STUDIES_NODE_ID).accept(
				this);
		}
		if (typeSearched == Clinic.class) {
			return siteAdapter.getChild(SiteAdapter.CLINICS_NODE_ID).accept(
				this);
		}
		if (typeSearched == StorageType.class) {
			return siteAdapter.getChild(SiteAdapter.STORAGE_TYPES_NODE_ID)
				.accept(this);
		}
		if (typeSearched == StorageContainer.class) {
			return siteAdapter.getChild(SiteAdapter.STORAGE_CONTAINERS_NODE_ID)
				.accept(this);
		}
		return null;
	}

	public Node visit(StudyGroup sGroup) {
		if (typeSearched == Study.class) {
			return sGroup.getChild(id, true);
		}
		return visitChildren(sGroup);
	}

	public Node visit(StudyAdapter study) {
		if (typeSearched == Patient.class) {
			return study.getChild(StudyAdapter.PATIENTS_NODE_ID).accept(this);
		}
		return visitChildren(study);
	}

	public Node visit(PatientGroup pGroup) {
		if (typeSearched == Patient.class) {
			return pGroup.getChild(id, true);
		}
		return visitChildren(pGroup);
	}

	public Node visit(PatientAdapter patient) {
		if (typeSearched == PatientVisit.class) {
			return patient.getChild(id, true);
		}
		return visitChildren(patient);
	}

	public Node visit(PatientVisitAdapter patientVisit) {
		if (typeSearched == Sample.class) {
			return visitChildren(patientVisit);
		}
		return null;
	}

	public Node visit(SampleTypeAdapter sampleType) {
		if (typeSearched == Sample.class) {
			return sampleType.getChild(id, true);
		}
		return null;
	}

	public Node visit(ClinicGroup clinics) {
		if (typeSearched == Clinic.class) {
			return clinics.getChild(id, true);
		}
		return null;
	}

	public Node visit(StorageTypeGroup stGroup) {
		if (typeSearched == StorageType.class) {
			return stGroup.getChild(id, true);
		}
		return null;
	}

	public Node visit(StorageContainerGroup scGroup) {
		if (typeSearched == StorageContainer.class) {
			Node child = scGroup.getChild(id, true);
			if (child == null) {
				return visitChildren(scGroup);
			}
			return child;
		}
		return null;
	}

	public Node visit(StorageContainerAdapter container) {
		if (typeSearched == StorageContainer.class) {
			Node child = container.getChild(id, true);
			if (child == null) {
				return visitChildren(container);
			}
			return child;
		}
		return null;
	}
}
