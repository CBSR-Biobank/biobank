package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class NodeSearchVisitor {

    private Class<? extends ModelWrapper<?>> typeSearched;
    private int id;

    @SuppressWarnings("unchecked")
    public NodeSearchVisitor(ModelWrapper<?> wrapper) {
        this((Class<? extends ModelWrapper<?>>) wrapper.getClass(), wrapper
            .getId());
    }

    public NodeSearchVisitor(Class<? extends ModelWrapper<?>> typeSearch, int id) {
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

    public AdapterBase visit(RootNode root) {
        return visitChildren(root);
    }

    public AdapterBase visit(SessionAdapter session) {
        if (typeSearched == SiteWrapper.class) {
            return session.getChild(id);
        }
        return visitChildren(session);
    }

    public AdapterBase visit(SiteAdapter site) {
        return visitChildren(site);
    }

    public AdapterBase visit(StudyGroup sGroup) {
        if (typeSearched == StudyWrapper.class) {
            return sGroup.getChild(id, true);
        }
        return null;
    }

    public AdapterBase visit(StudyAdapter study) {
        if (typeSearched == PatientWrapper.class) {
            return study.getChild(id, true);
        }
        return visitChildren(study);
    }

    public AdapterBase visit(PatientAdapter patient) {
        if (typeSearched == PatientVisitWrapper.class) {
            return patient.getChild(id, true);
        }
        return visitChildren(patient);
    }

    public AdapterBase visit(SampleTypeAdapter sampleType) {
        if (typeSearched == SampleWrapper.class) {
            return sampleType.getChild(id, true);
        }
        return null;
    }

    public AdapterBase visit(ClinicGroup clinics) {
        if (typeSearched == ClinicWrapper.class) {
            return clinics.getChild(id, true);
        }
        return null;
    }

    public AdapterBase visit(ContainerTypeGroup stGroup) {
        if (typeSearched == ContainerTypeWrapper.class) {
            return stGroup.getChild(id, true);
        }
        return null;
    }

    public AdapterBase visit(ContainerGroup scGroup) {
        if (typeSearched == ContainerWrapper.class) {
            AdapterBase child = scGroup.getChild(id, true);
            if (child == null) {
                return visitChildren(scGroup);
            }
            return child;
        }
        return null;
    }

    public AdapterBase visit(ContainerAdapter container) {
        if (typeSearched == ContainerWrapper.class) {
            AdapterBase child = container.getChild(id, true);
            if (child == null) {
                return visitChildren(container);
            }
            return child;
        }
        return null;
    }

    public AdapterBase visit(ShipmentAdapter shipment) {
        if (typeSearched == PatientVisitWrapper.class) {
            return shipment.getChild(id, true);
        }
        return null;
    }
}
