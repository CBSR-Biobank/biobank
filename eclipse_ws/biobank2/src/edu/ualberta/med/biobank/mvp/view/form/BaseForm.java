package edu.ualberta.med.biobank.mvp.view.form;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class BaseForm {
    private static final String PAGE_KEY = "page";
    private final SectionMonitor sectionMonitor = new SectionMonitor();
    private final ScrolledForm scrolledForm;
    private final Composite page;
    private final FormToolkit toolkit;
    private final ToolBarButtonManager toolBarButtonManager;

    public BaseForm(Composite parent) {
        ManagedForm managedForm = new ManagedForm(parent);
        toolkit = managedForm.getToolkit();
        scrolledForm = managedForm.getForm();
        toolkit.decorateFormHeading(scrolledForm.getForm());

        Composite formBody = scrolledForm.getBody();
        formBody.setLayout(new GridLayout());
        formBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        ScrolledPageBook book = toolkit.createPageBook(formBody, SWT.V_SCROLL);
        book.setLayout(new GridLayout());
        book.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        page = book.createPage(PAGE_KEY);
        page.setLayout(new GridLayout());
        page.setLayoutData(new GridData(SWT.FILL, SWT.FILL | SWT.TOP, true,
            false));

        IToolBarManager toolBarManager = scrolledForm.getToolBarManager();
        toolBarButtonManager = new ToolBarButtonManager(toolBarManager);

        book.showPage(PAGE_KEY);
    }

    public ToolBarButtonManager getToolbar() {
        return toolBarButtonManager;
    }

    public void setTitle(String title) {
        scrolledForm.setText(title);
    }

    public void setMessage(String message) {
        setMessage(message, IMessageProvider.NONE);
    }

    public void setErrorMessage(String errorMessage) {
        setMessage(errorMessage, IMessageProvider.ERROR);
    }

    private void setMessage(String message, int newType) {
        // TODO: there is a list of IMessages that can be set?
        scrolledForm.setMessage(message, newType);
    }

    public FormToolkit getToolkit() {
        return toolkit;
    }

    public Composite getPage() {
        return page;
    }

    public Composite createClient(Section section) {
        Composite client = toolkit.createComposite(section);
        client.setLayout(new GridLayout());
        client.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        section.setClient(client);
        toolkit.paintBordersFor(client);

        return client;
    }

    public Composite createSectionWithClient(String title) {
        Section section = createSection(title);
        Composite client = createClient(section);
        return client;
    }

    public Section createSection(String title) {
        return createSection(title, page);
    }

    public void reflow(boolean flushCache) {
        scrolledForm.reflow(flushCache);
    }

    public void adapt() {
        adaptToToolkit(toolkit, true);
    }

    public void adaptToToolkit(FormToolkit toolkit, boolean paintBorder) {
        toolkit.adapt(scrolledForm, true, true);
        adaptAllChildren(scrolledForm, toolkit);
        if (paintBorder) {
            toolkit.paintBordersFor(scrolledForm);
        }
    }

    public static void addSectionToolbar(Section section,
        String tooltip, SelectionListener listener,
        Class<?> wrapperTypeToAdd, String imageKey) {
        // TODO: remove canCreate stuff? Ungh...
        if (wrapperTypeToAdd == null
            || SessionManager.canCreate(wrapperTypeToAdd)) {
            ToolBar tbar = (ToolBar) section.getTextClient();
            if (tbar == null) {
                tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
                section.setTextClient(tbar);
            }

            ToolItem titem = new ToolItem(tbar, SWT.NULL);
            if (imageKey == null) {
                imageKey = BgcPlugin.IMG_ADD;
            }
            titem.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(imageKey));
            titem.setToolTipText(tooltip);
            titem.addSelectionListener(listener);
        }
    }

    private Section createSection(String title, Composite parent, int style) {
        Section section = toolkit.createSection(parent, style);

        if (title != null) section.setText(title);

        section.setLayout(new GridLayout());
        section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            false));
        section.addExpansionListener(sectionMonitor);

        return section;
    }

    private Section createSection(String title, Composite parent) {
        return createSection(title, parent, Section.TWISTIE
            | Section.TITLE_BAR | Section.EXPANDED);
    }

    private void adaptAllChildren(Composite container, FormToolkit toolkit) {
        Control[] children = null;

        if ((container instanceof Section)) {
            // kludge to get around the way eclipse sets the section background
            // colour in forms
            Composite client =
                (Composite) ((Section) container).getClient();
            if (client == null) return;

            toolkit.adapt(client, true, true);
            children = client.getChildren();
        } else {
            children = container.getChildren();
        }

        for (Control child : children) {
            toolkit.adapt(child, true, true);
            if (child instanceof Composite) {
                adaptAllChildren((Composite) child, toolkit);
            }
        }
    }

    private class SectionMonitor extends ExpansionAdapter {
        @Override
        public void expansionStateChanged(ExpansionEvent e) {
            scrolledForm.reflow(false);
        }
    }
}
