package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Site;
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

    private AdaptorBase visitChildren(AdaptorBase node) {
        node.loadChildren(true);
        for (AdaptorBase child : node.getChildren()) {
            AdaptorBase foundChild = child.accept(this);
            if (foundChild != null) {
                return foundChild;
            }
        }
        return null;
    }

    public AdaptorBase visit(SessionAdapter session) {
        if (typeSearched == Site.class) {
            return session.getChild(id);
        }
        return visitChildren(session);
    }

    public AdaptorBase visit(SiteAdapter siteAdapter) {
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
        if (typeSearched == ContainerType.class) {
            return siteAdapter.getChild(SiteAdapter.STORAGE_TYPES_NODE_ID)
                .accept(this);
        }
        if (typeSearched == Container.class) {
            return siteAdapter.getChild(SiteAdapter.STORAGE_CONTAINERS_NODE_ID)
                .accept(this);
        }
        return null;
    }

    public AdaptorBase visit(StudyGroup sGroup) {
        if (typeSearched == Study.class) {
            return sGroup.getChild(id, true);
        }
        return visitChildren(sGroup);
    }

    public AdaptorBase visit(StudyAdapter study) {
        if (typeSearched == Patient.class) {
            return study.getChild(StudyAdapter.PATIENTS_NODE_ID).accept(this);
        }
        return visitChildren(study);
    }

    public AdaptorBase visit(PatientGroup pGroup) {
        return visitChildren(pGroup);
    }

    public AdaptorBase visit(PatientSubGroup pGroup) {
        if (typeSearched == Patient.class) {
            return pGroup.getChild(id, true);
        }
        return visitChildren(pGroup);
    }

    public AdaptorBase visit(PatientAdapter patient) {
        if (typeSearched == PatientVisit.class) {
            return patient.getChild(id, true);
        }
        return visitChildren(patient);
    }

    public AdaptorBase visit(PatientVisitAdapter patientVisit) {
        if (typeSearched == Sample.class) {
            return visitChildren(patientVisit);
        }
        return null;
    }

    public AdaptorBase visit(SampleTypeAdapter sampleType) {
        if (typeSearched == Sample.class) {
            return sampleType.getChild(id, true);
        }
        return null;
    }

    public AdaptorBase visit(ClinicGroup clinics) {
        if (typeSearched == Clinic.class) {
            return clinics.getChild(id, true);
        }
        return null;
    }

    public AdaptorBase visit(ContainerTypeGroup stGroup) {
        if (typeSearched == ContainerType.class) {
            return stGroup.getChild(id, true);
        }
        return null;
    }

    public AdaptorBase visit(ContainerGroup scGroup) {
        if (typeSearched == Container.class) {
            AdaptorBase child = scGroup.getChild(id, true);
            if (child == null) {
                return visitChildren(scGroup);
            }
            return child;
        }
        return null;
    }

    public AdaptorBase visit(ContainerAdapter container) {
        if (typeSearched == Container.class) {
            AdaptorBase child = container.getChild(id, true);
            if (child == null) {
                return visitChildren(container);
            }
            return child;
        }
        return null;
    }
}
