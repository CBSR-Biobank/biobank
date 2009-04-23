package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageContainerAdapter;

public class StorageContainerEntryForm extends BiobankEntryForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StorageContainerEntryForm";

    public static final String NEW_STORAGE_CONTAINER_OK_MESSAGE =
        "Creating a new storage container.";

    public static final String STORAGE_CONTAINER_OK_MESSAGE =
        "Editing an existing storage container.";
    
    public static final String NO_CONTAINER_NAME_MESSAGE =
        "Storage container must have a name";
    
    private StorageContainerAdapter storageContainerAdapter;
    
    private StorageContainer storageContainer;

    private Study study;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        storageContainerAdapter = (StorageContainerAdapter) node;
        appService = storageContainerAdapter.getAppService();
        storageContainer = storageContainerAdapter.getStorageContainer();

        if (storageContainer.getId() == null) {
            setPartName("Storage Container");
        }
        else {
            setPartName("Storage Container" + storageContainer.getId());
        }
    }

    @Override
    protected void createFormContent() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveForm() {
        // TODO Auto-generated method stub

    }

}
