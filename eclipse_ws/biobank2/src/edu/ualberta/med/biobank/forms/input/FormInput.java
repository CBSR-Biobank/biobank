package edu.ualberta.med.biobank.forms.input;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StorageContainerAdapter;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class FormInput implements IEditorInput {
    private Node node;

    public FormInput(Node o) {
        node = o;
    }
    
    public int getIndex() {
        if (node != null) return node.getId();
        return 0;
    }
    
    public Node getNode() {
        return node;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        if (node == null) return null;

        String name = node.getName();
        if (name != null) {
            if (node instanceof SiteAdapter) return "Site " + name;
            else if (node instanceof StudyAdapter) return "Study " + name;
            else if (node instanceof ClinicAdapter) return "Clinic " + name;
            else if (node instanceof StorageTypeAdapter) return "Storage Type " + name;
            else if (node instanceof PatientAdapter) return "Patient " + name;
            else if (node instanceof PatientVisitAdapter) return "Patient " + name;
            else if (node instanceof StorageContainerAdapter) return "Storage Container " + name;
            else Assert.isTrue(false, "tooltip name for "
                    + node.getClass().getName() + " not implemented");
        }
        else {
            if (node instanceof SiteAdapter) return "New Site";
            else if (node instanceof StudyAdapter) return "New Study";
            else if (node instanceof ClinicAdapter) return "New Clinic";
            else if (node instanceof StorageTypeAdapter) return "New Storage Type";
            else if (node instanceof PatientAdapter) return "New Patient";
            else if (node instanceof PatientVisitAdapter) return "New Patient Visit";
            else if (node instanceof StorageContainerAdapter) return "New Storage Container ";
            else Assert.isTrue(false, "tooltip name for "
                    + node.getClass().getName() + " not implemented");
        }
        return null;
    }
    
    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }
    
    public boolean equals(Object o) {
        if ((node == null) || (o == null)) return false;
        
        if (o instanceof FormInput) {
            if (node.getClass() != ((FormInput)o).node.getClass()) return false;
            
            return (getIndex() == ((FormInput)o).getIndex()); 
        }
        return false;
    }

}
