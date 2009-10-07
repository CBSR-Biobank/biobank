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

    private AdapterBase visitChildren(AdapterBase node) {
        node.loadChildren(true);
        for (AdapterBase child : node.getChildren()) {
            AdapterBase foundChild = child.accept(this);
            if (foundChild != null) {
                return foundChild;
            }
        }
        return null;
    }

    public AdapterBase visit(SessionAdapter session) {
        if (typeSearched == Site.class) {
            return session.getChild(id);
        }
        return visitChildren(session);
    }

    public AdapterBase visit(SiteAdapter siteAdapter) {
        AdapterBase groupNode = null;
        if (typeSearched == Study.class || typeSearched == Patient.class
            || typeSearched == PatientVisit.class
            || typeSearched == Sample.class) {
            groupNode = siteAdapter.getChild(SiteAdapter.STUDIES_NODE_ID);
        } else if (typeSearched == Clinic.class) {
            groupNode = siteAdapter.getChild(SiteAdapter.CLINICS_NODE_ID);
        } else if (typeSearched == ContainerType.class) {
            groupNode = siteAdapter.getChild(SiteAdapter.STORAGE_TYPES_NODE_ID);
        } else if (typeSearched == Container.class) {
            groupNode = siteAdapter
                .getChild(SiteAdapter.STORAGE_CONTAINERS_NODE_ID);
        }
        if (groupNode != null) {
            return groupNode.accept(this);
        }
        return null;
    }

    public AdapterBase visit(StudyGroup sGroup) {
        if (typeSearched == Study.class) {
            return sGroup.getChild(id, true);
        }
        return null;
    }

    public AdapterBase visit(StudyAdapter study) {
        if (typeSearched == Patient.class) {
            return study.getChild(id, true);
        }
        return visitChildren(study);
    }

    public AdapterBase visit(PatientAdapter patient) {
        if (typeSearched == PatientVisit.class) {
            return patient.getChild(id, true);
        }
        return visitChildren(patient);
    }

    public AdapterBase visit(SampleTypeAdapter sampleType) {
        if (typeSearched == Sample.class) {
            return sampleType.getChild(id, true);
        }
        return null;
    }

    public AdapterBase visit(ClinicGroup clinics) {
        if (typeSearched == Clinic.class) {
            return clinics.getChild(id, true);
        }
        return null;
    }

    public AdapterBase visit(ContainerTypeGroup stGroup) {
        if (typeSearched == ContainerType.class) {
            return stGroup.getChild(id, true);
        }
        return null;
    }

    public AdapterBase visit(ContainerGroup scGroup) {
        if (typeSearched == Container.class) {
            AdapterBase child = scGroup.getChild(id, true);
            if (child == null) {
                return visitChildren(scGroup);
            }
            return child;
        }
        return null;
    }

    public AdapterBase visit(ContainerAdapter container) {
        if (typeSearched == Container.class) {
            AdapterBase child = container.getChild(id, true);
            if (child == null) {
                return visitChildren(container);
            }
            return child;
        }
        return null;
    }
}
