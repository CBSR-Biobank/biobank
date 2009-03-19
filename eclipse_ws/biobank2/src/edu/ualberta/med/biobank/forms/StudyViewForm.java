package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import org.eclipse.swt.widgets.Table;

public class StudyViewForm extends EditorPart {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StudyViewForm";
    
    private StudyAdapter studyAdapter;
    private Study study;

    protected FormToolkit toolkit;
    
    protected Form form;

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
        
        setSite(editorSite);
        setInput(input);
        
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
        
        toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createForm(parent);  

        if (study.getName() != null) {
            form.setText("Study: " + study.getName());
        }
        
        toolkit.decorateFormHeading(form);
        //form.setMessage(OK_MESSAGE);
        
        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Composite sbody = toolkit.createComposite(form.getBody());
        sbody.setLayout(new GridLayout(2, false));
        sbody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        toolkit.paintBordersFor(sbody); 
        
        Label label = FormUtils.createLabelledField(toolkit, sbody, "Short Name:");
        label.setText(study.getNameShort());

        label = toolkit.createLabel(sbody, "Clinics:", SWT.LEFT);
        Table tbl = toolkit.createTable(sbody, SWT.NONE);
        Collection<Clinic> clinics = study.getClinicCollection();
        for (Object obj : clinics.toArray(new Object[clinics.size()])) {
            TableItem item = new TableItem(tbl, 0);
            item.setText(((Clinic) obj).getName());
        }
    }

    @Override
    public void setFocus() {
    }

}
