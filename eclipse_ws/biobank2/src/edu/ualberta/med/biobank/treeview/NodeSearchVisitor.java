package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class NodeSearchVisitor {

    protected ModelWrapper<?> wrapper;

    public NodeSearchVisitor(ModelWrapper<?> wrapper) {
        this.wrapper = wrapper;
    }

    protected AdapterBase visitChildren(AdapterBase node) {
        // node.loadChildren(true);
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
        if (wrapper instanceof SiteWrapper) {
            return session.getChild(wrapper.getId(), true);
        }
        return visitChildren(session);
    }

    public AdapterBase visit(SiteAdapter site) {
        return visitChildren(site);
    }

    public AdapterBase visit(StudyGroup sGroup) {
        if (wrapper instanceof StudyWrapper) {
            return sGroup.getChild(wrapper.getId(), true);
        }
        return null;
    }

    public AdapterBase visit(StudyAdapter study) {
        if (wrapper instanceof PatientWrapper) {
            return study.getChild(wrapper.getId(), true);
        }
        return visitChildren(study);
    }

    public AdapterBase visit(PatientAdapter patient) {
        if (wrapper instanceof PatientVisitWrapper) {
            return patient.getChild(wrapper.getId(), true);
        }
        return visitChildren(patient);
    }

    public AdapterBase visit(SampleTypeAdapter sampleType) {
        if (wrapper instanceof AliquotWrapper) {
            return sampleType.getChild(wrapper.getId(), true);
        }
        return null;
    }

    public AdapterBase visit(ClinicGroup clinics) {
        if (wrapper instanceof ClinicWrapper) {
            return clinics.getChild(wrapper.getId(), true);
        }
        return null;
    }

    public AdapterBase visit(ContainerTypeGroup stGroup) {
        if (wrapper instanceof ContainerTypeWrapper) {
            return stGroup.getChild(wrapper.getId(), true);
        }
        return null;
    }

    public AdapterBase visit(ContainerGroup scGroup) {
        if (wrapper instanceof ContainerWrapper) {
            ContainerWrapper container = (ContainerWrapper) wrapper;
            if (container.getContainerType() != null) {
                if (Boolean.TRUE.equals(container.getContainerType()
                    .getTopLevel())) {
                    return scGroup.getChild(wrapper.getId(), true);
                } else {
                    List<ContainerWrapper> parents = new ArrayList<ContainerWrapper>();
                    ContainerWrapper currentContainer = container;
                    while (currentContainer.hasParent()) {
                        currentContainer = currentContainer.getParent();
                        parents.add(currentContainer);
                    }
                    for (AdapterBase child : scGroup.getChildren()) {
                        if (child instanceof ContainerAdapter) {
                            visitChildContainers((ContainerAdapter) child,
                                parents);
                        } else {
                            AdapterBase foundChild = child.accept(this);
                            if (foundChild != null) {
                                return foundChild;
                            }
                        }
                    }
                    return visitChildren(scGroup);
                }
            }
        }
        return null;
    }

    public AdapterBase visit(ContainerAdapter container) {
        return visit(container, null);
    }

    public AdapterBase visit(ContainerAdapter container,
        List<ContainerWrapper> parents) {
        if (wrapper instanceof ContainerWrapper) {
            ContainerWrapper containerWrapper = (ContainerWrapper) wrapper;
            if (parents == null) {
                parents = new ArrayList<ContainerWrapper>();
                ContainerWrapper currentContainer = containerWrapper;
                while (currentContainer.hasParent()) {
                    currentContainer = currentContainer.getParent();
                    parents.add(currentContainer);
                }
            }
            return visitChildContainers(container, parents);
        }
        return null;
    }

    private AdapterBase visitChildContainers(ContainerAdapter container,
        final List<ContainerWrapper> parents) {
        if (parents.contains(container.getContainer())) {
            AdapterBase child = container.getChild(wrapper.getId(), true);
            if (child == null) {
                for (AdapterBase childContainer : container.getChildren()) {
                    AdapterBase foundChild;
                    if (childContainer instanceof ContainerAdapter) {
                        foundChild = visitChildContainers(
                            (ContainerAdapter) childContainer, parents);
                    } else {
                        foundChild = childContainer.accept(this);
                    }
                    if (foundChild != null) {
                        return foundChild;
                    }
                }
            } else {
                return child;
            }
        }
        return null;
    }

    public AdapterBase visit(ShipmentAdapter shipment) {
        if (wrapper instanceof PatientVisitWrapper) {
            return shipment.getChild(wrapper.getId(), true);
        }
        return null;
    }

    public AdapterBase visit(
        @SuppressWarnings("unused") AbstractSearchedNode abstractSearchedNode) {
        return null;
    }

    public AdapterBase visit(
        @SuppressWarnings("unused") AbstractTodayNode abstractTodayNode) {
        return null;
    }

    public AdapterBase visit(
        @SuppressWarnings("unused") ClinicAdapter clinicAdapter) {
        return null;
    }
}
