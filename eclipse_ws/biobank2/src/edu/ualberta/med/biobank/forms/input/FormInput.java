package edu.ualberta.med.biobank.forms.input;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.ualberta.med.biobank.treeview.AdapterBase;

public class FormInput implements IEditorInput {
    protected Object obj;
    private boolean hasPreviousForm;
    private String name;

    public FormInput(Object o) {
        this(o, false);
    }

    public FormInput(Object o, String name) {
        obj = o;
        this.name = name;
    }

    public FormInput(Object o, boolean hasPreviousForm) {
        obj = o;
        this.hasPreviousForm = hasPreviousForm;
    }

    public int getIndex() {
        if (obj instanceof AdapterBase) {
            AdapterBase adapter = (AdapterBase) obj;
            if (adapter != null) {
                Integer id = adapter.getId();
                if (id != null)
                    return id.intValue();
            }
        } else {
            Assert.isTrue(false, "invalid type for form input object");
        }
        return -1;
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
        if ((obj == null) && (name != null)) {
            return name;
        }

        if (obj == null) {
            return null;
        }

        if (obj instanceof AdapterBase) {
            return ((AdapterBase) obj).getTooltipText();
        }
        return name;
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return getName();
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public Object getAdapter(Class adapter) {
        if (obj == null)
            return null;

        if ((adapter == AdapterBase.class) && (obj instanceof AdapterBase)) {
            return obj;
        } else if (adapter == obj.getClass()) {
            return obj;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if ((obj == null) || (o == null))
            return false;

        if (o instanceof FormInput) {
            if ((obj == null) || (((FormInput) o).obj == null))
                return false;

            if (obj.getClass() != ((FormInput) o).obj.getClass())
                return false;

            if (obj instanceof AdapterBase) {
                int myIndex = getIndex();
                int oIndex = ((FormInput) o).getIndex();

                return ((myIndex != -1) && (oIndex != -1) && (myIndex == oIndex));
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public boolean hasPreviousForm() {
        return hasPreviousForm;
    }

    public void setHasPreviousForm(boolean hasPreviousForm) {
        this.hasPreviousForm = hasPreviousForm;
    }

}
