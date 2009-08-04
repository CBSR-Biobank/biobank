package edu.ualberta.med.biobank.forms;

import java.util.HashMap;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
import edu.ualberta.med.biobank.treeview.AdaptorBase;

/**
 * Base class for data all BioBank2 view and entry forms. This class is the
 * superclass for {@link BiobankEntryForm} and {@link BiobankViewForm}. Please
 * extend from these two classes instead of <code>BiobankFormBase</code>.
 * <p>
 * Form creation is called in a non-UI thread so making calls to the ORM layer
 * possible. See {@link #createFormContent()}
 */
public abstract class BiobankFormBase extends EditorPart {

    protected ManagedForm mform;

    protected FormToolkit toolkit;

    protected ScrolledForm form;

    protected HashMap<String, Control> controls = new HashMap<String, Control>();

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    /**
     * The initialisation method for the derived form.
     * 
     * @param adapter the corresponding model adapter the form is to edit /
     *            view.
     */
    protected abstract void init(AdaptorBase adapter);

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        if (!(input instanceof FormInput))
            throw new PartInitException("Invalid editor input");
        setSite(editorSite);
        setInput(input);
        AdaptorBase adapter = ((FormInput) input).getNode();
        Assert.isNotNull(adapter, "Bad editor input (null value)");
        init(adapter);
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

    /**
     * Called in a non-UI thread to create the widgets that make up the form.
     */
    protected abstract void createFormContent();

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

    protected Control createWidget(Composite parent, Class<?> widgetClass,
        int widgetOptions, String fieldLabel) {
        Label label = toolkit.createLabel(parent, fieldLabel + ":", SWT.LEFT);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        if ((widgetClass == Combo.class) || (widgetClass == Text.class)
            || (widgetClass == Label.class)) {
            if (widgetOptions == SWT.NONE) {
                widgetOptions = SWT.SINGLE;
            }
            Label field = toolkit.createLabel(parent, "", widgetOptions
                | SWT.LEFT | SWT.BORDER);
            field.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
                false));

            return field;
        } else if (widgetClass == Button.class) {
            Button button = new Button(parent, SWT.CHECK | widgetOptions);
            button.setEnabled(false);
            toolkit.adapt(button, true, true);
            return button;
        } else {
            Assert.isTrue(false, "invalid widget class "
                + widgetClass.getName());
        }
        return null;
    }

    protected void createWidgetsFromMap(ListOrderedMap fieldsMap,
        Composite parent) {
        FieldInfo fi;

        MapIterator it = fieldsMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            fi = (FieldInfo) it.getValue();

            Control control = createWidget(parent, fi.widgetClass, SWT.NONE,
                fi.label);
            controls.put(key, control);
        }
    }
}
