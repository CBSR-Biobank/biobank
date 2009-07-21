package edu.ualberta.med.biobank.forms;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import edu.ualberta.med.biobank.forms.input.FormInput;

/**
 * Base class for data entry forms.
 * 
 * Notes: - createFormContent() is called in it's own thread so making calls to
 * the database is possible.
 * 
 */
public abstract class BiobankFormBase extends EditorPart {

    protected ManagedForm mform;

    protected FormToolkit toolkit;

    protected ScrolledForm form;

    protected HashMap<String, Control> controls;

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        if (!(input instanceof FormInput))
            throw new PartInitException("Invalid editor input");
        setSite(editorSite);
        setInput(input);
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
    public void createPartControl(Composite parent) {
        mform = new ManagedForm(parent);
        toolkit = mform.getToolkit();
        form = mform.getForm();

        // start a new runnable so that database objects are populated in a
        // separate thread.
        BusyIndicator.showWhile(parent.getDisplay(), new Runnable() {
            public void run() {
                try {
                    createFormContent();
                    form.reflow(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    abstract protected void createFormContent();

    protected Section createSection(String title) {
        Section section = toolkit.createSection(form.getBody(), Section.TWISTIE
            | Section.TITLE_BAR | Section.EXPANDED);
        if (title != null) {
            section.setText(title);
        }
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        section.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(false);
            }
        });
        return section;
    }

    protected Composite createSectionWithClient(String title) {
        Section section = createSection(title);
        Composite client;
        client = toolkit.createComposite(section);
        section.setClient(client);

        client.setLayout(new GridLayout(2, false));
        toolkit.paintBordersFor(client);
        return client;
    }

}
