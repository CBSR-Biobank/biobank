package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sdata;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.Study;

public class BiobankContentProvider implements IStructuredContentProvider {   
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Study[]) {
            return (Study[])inputElement;
        }
        else if (inputElement instanceof Clinic[]) {
            return (Clinic[])inputElement;
        }
        else if (inputElement instanceof Patient[]) {
            return (Patient[])inputElement;
        }
        else if (inputElement instanceof StorageContainer[]) {
            return (StorageContainer[])inputElement;
        }
        else if (inputElement instanceof Sdata[]) {
            return (Sdata[])inputElement;
        }
        Assert.isTrue(false, "invalid type for inputElement: " 
                + inputElement.getClass().getName());
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        
    }

}
