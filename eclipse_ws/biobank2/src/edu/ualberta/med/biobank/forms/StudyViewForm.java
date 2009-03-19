package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class StudyViewForm extends EditorPart {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StudyViewForm";
    
    private StudyAdapter studyAdapter;
    private Study study;

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {
        if ( !(input instanceof NodeInput)) 
            throw new PartInitException("Invalid editor input"); 
        
        Node node = ((NodeInput) input).getNode();
        Assert.notNull(node, "Null editor input");

        if (node instanceof StudyAdapter) {
            studyAdapter = (StudyAdapter) node;
            study = studyAdapter.getStudy();
            setPartName("Study " + study.getName());
        }
        else {
            Assert.isTrue(false, "Invalid editor input: object of type "
                + node.getClass().getName());
        }

    }

    @Override
    public void createPartControl(Composite parent) {
    }

    @Override
    public void setFocus() {
    }

}
