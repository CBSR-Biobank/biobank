package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableSelection;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;

/**
 * Base class for data all BioBank2 view and entry forms. This class is the
 * superclass for {@link BiobankEntryForm} and {@link BiobankViewForm}. Please
 * extend from these two classes instead of <code>BiobankFormBase</code>.
 * <p>
 * Form creation is called in a non-UI thread so making calls to the ORM layer
 * possible. See {@link #createFormContent()}
 */
public abstract class BiobankFormBase extends EditorPart implements
    ISelectionProvider {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(BiobankFormBase.class.getName());

    protected BiobankApplicationService appService;

    protected AdapterBase adapter;

    protected ManagedForm mform;

    protected FormToolkit toolkit;

    protected ScrolledPageBook book;

    protected ScrolledForm form;

    protected Composite page;

    private Map<String, Control> widgets;

    protected WidgetCreator widgetCreator;

    public static List<BiobankFormBase> currentLinkedForms;

    public List<BiobankFormBase> linkedForms;

    protected IDoubleClickListener collectionDoubleClickListener = new IDoubleClickListener() {
        @Override
        public void doubleClick(DoubleClickEvent event) {
            Object selection = event.getSelection();
            if (selection instanceof StructuredSelection) {
                Object element = ((StructuredSelection) selection)
                    .getFirstElement();
                if (element instanceof AdapterBase) {
                    ((AdapterBase) element).performDoubleClick();
                } else if (element instanceof ModelWrapper<?>) {
                    SessionManager.openViewForm((ModelWrapper<?>) element);
                }
            } else if (selection instanceof InfoTableSelection) {
                InfoTableSelection tableSelection = (InfoTableSelection) selection;
                if (tableSelection.getObject() instanceof ModelWrapper<?>) {
                    SessionManager
                        .openViewForm((ModelWrapper<?>) tableSelection
                            .getObject());
                }
            }
        }
    };

    public BiobankFormBase() {
        widgets = new HashMap<String, Control>();
        widgetCreator = new WidgetCreator(widgets);
    }

    protected void addWidget(String widgetName, Control widget) {
        widgets.put(widgetName, widget);
    }

    protected Control getWidget(String widgetName) {
        return widgets.get(widgetName);
    }

    @Override
    public void setFocus() {
        if ((adapter != null) && (adapter.getId() != null)) {
            SessionManager.setSelectedNode(adapter);
            // if selection fails, then the adapter needs to be matched at the
            // id level
            if (SessionManager.getSelectedNode() == null) {
                AdapterBase node = SessionManager.searchFirstNode(adapter
                    .getModelObject());
                SessionManager.setSelectedNode(node);
            }
        }
    }

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
    protected abstract void init() throws Exception;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        if (!(input instanceof FormInput))
            throw new PartInitException("Invalid editor input");
        FormInput formInput = (FormInput) input;
        setSite(editorSite);
        setInput(input);

        adapter = (AdapterBase) formInput.getAdapter(AdapterBase.class);
        if (adapter != null) {
            Assert.isNotNull(adapter, "Bad editor input (null value)");
            appService = (BiobankApplicationService) adapter.getAppService();
            if (!formInput.hasPreviousForm()) {
                synchronized (currentLinkedForms) {
                    currentLinkedForms = new ArrayList<BiobankFormBase>();
                }
            }
            linkedForms = currentLinkedForms;
            linkedForms.add(this);
        }
        try {
            init();
        } catch (final RemoteConnectFailureException exp) {
            BiobankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (Exception e) {
            logger.error("BioBankFormBase.createPartControl Error", e);
        }
        getSite().setSelectionProvider(this);
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
        widgetCreator.setToolkit(toolkit);
        form = mform.getForm();
        toolkit.decorateFormHeading(form.getForm());

        form.getBody().setLayout(new GridLayout());
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        form.getBody().setLayoutData(gd);

        book = toolkit.createPageBook(form.getBody(), SWT.V_SCROLL);
        book.setLayout(new GridLayout());
        GridData gd2 = new GridData();
        gd2.grabExcessHorizontalSpace = true;
        gd2.grabExcessVerticalSpace = true;
        gd2.horizontalAlignment = SWT.FILL;
        gd2.verticalAlignment = SWT.FILL;
        book.setLayoutData(gd2);
        page = book.createPage("page");
        book.showPage("page");

        // start a new runnable so that database objects are populated in a
        // separate thread.
        BusyIndicator.showWhile(parent.getDisplay(), new Runnable() {
            @Override
            public void run() {
                try {
                    form.setImage(BiobankPlugin.getDefault().getImage(adapter));
                    createFormContent();
                    form.reflow(true);
                } catch (final RemoteConnectFailureException exp) {
                    BiobankPlugin.openRemoteConnectErrorMessage(exp);
                } catch (Exception e) {
                    BiobankPlugin.openError(
                        "BioBankFormBase.createPartControl Error", e);
                }
            }
        });
    }

    /**
     * Called in a non-UI thread to create the widgets that make up the form.
     */
    protected abstract void createFormContent() throws Exception;

    protected Section createSection(String title) {
        Section section = toolkit.createSection(page, Section.TWISTIE
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

    protected Composite sectionAddClient(Section section) {
        Composite client = toolkit.createComposite(section);
        section.setClient(client);
        client.setLayout(new GridLayout(2, false));
        toolkit.paintBordersFor(client);
        return client;
    }

    protected Composite createSectionWithClient(String title) {
        return sectionAddClient(createSection(title));
    }

    protected void addSectionToolbar(Section section, String tooltip,
        SelectionListener listener) {
        addSectionToolbar(section, tooltip, listener, null);
    }

    protected void addSectionToolbar(Section section, String tooltip,
        SelectionListener listener, Class<?> wrapperTypeToAdd) {
        addSectionToolbar(section, tooltip, listener, wrapperTypeToAdd, null);
    }

    protected void addSectionToolbar(Section section, String tooltip,
        SelectionListener listener, Class<?> wrapperTypeToAdd, String imageKey) {
        if (wrapperTypeToAdd == null
            || SessionManager.canCreate(wrapperTypeToAdd)) {
            ToolBar tbar = (ToolBar) section.getTextClient();
            if (tbar == null) {
                tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
                section.setTextClient(tbar);
            }

            ToolItem titem = new ToolItem(tbar, SWT.NULL);
            if (imageKey == null) {
                imageKey = BiobankPlugin.IMG_ADD;
            }
            titem.setImage(BiobankPlugin.getDefault().getImageRegistry()
                .get(imageKey));
            titem.setToolTipText(tooltip);
            titem.addSelectionListener(listener);
        }
    }

    public FormToolkit getToolkit() {
        return toolkit;
    }

    public AdapterBase getAdapter() {
        return adapter;
    }

    protected <T> ComboViewer createComboViewer(Composite parent,
        String fieldLabel, Collection<T> input, T selection) {
        return widgetCreator.createComboViewer(parent, fieldLabel, input,
            selection);
    }

    protected Control createWidget(Composite parent, Class<?> widgetClass,
        int widgetOptions, String value) {
        return widgetCreator.createWidget(parent, widgetClass, widgetOptions,
            value);
    }

    protected Control createLabelledWidget(Composite parent,
        Class<?> widgetClass, int widgetOptions, String fieldLabel, String value) {
        return widgetCreator.createLabelledWidget(parent, widgetClass,
            widgetOptions, fieldLabel, value);
    }

    protected Control createLabelledWidget(Composite parent,
        Class<?> widgetClass, int widgetOptions, String fieldLabel) {
        return createLabelledWidget(parent, widgetClass, widgetOptions,
            fieldLabel, null);
    }

    protected void createWidgetsFromMap(Map<String, FieldInfo> fieldsMap,
        Composite parent) {
        widgetCreator.createWidgetsFromMap(fieldsMap, parent);
    }

    protected BiobankText createReadOnlyLabelledField(Composite parent,
        int widgetOptions, String fieldLabel, String value) {
        return widgetCreator.createReadOnlyLabelledField(parent, widgetOptions,
            fieldLabel, value, false);
    }

    protected BiobankText createReadOnlyLabelledField(Composite parent,
        int widgetOptions, String fieldLabel, String value,
        boolean useBackgroundColor) {
        return widgetCreator.createReadOnlyLabelledField(parent, widgetOptions,
            fieldLabel, value, useBackgroundColor);
    }

    protected BiobankText createReadOnlyLabelledField(Composite parent,
        int widgetOptions, String fieldLabel) {
        return createReadOnlyLabelledField(parent, widgetOptions, fieldLabel,
            null);
    }

    public static void setTextValue(BiobankText label, String value) {
        if (value != null && !label.isDisposed()) {
            label.setText(value);
        }
    }

    protected BiobankText createReadOnlyWidget(Composite parent,
        int widgetOptions, String value) {
        BiobankText result = (BiobankText) createWidget(parent,
            BiobankText.class, SWT.READ_ONLY | widgetOptions, value);
        return result;
    }

    public static void setTextValue(BiobankText label, Object value) {
        if (value != null) {
            setTextValue(label, value.toString());
        }
    }

    public static void setCheckBoxValue(Button button, Boolean value) {
        if (value != null) {
            button.setSelection(value.booleanValue());
        }
    }

    public void setBroughtToTop() {
        synchronized (currentLinkedForms) {
            currentLinkedForms = linkedForms;
        }
    }

    public void setDeactivated() {
        if (linkedForms != null) {
            linkedForms.remove(this);
        }
    }

    private IObservableValue createBeansObservable(Object bean,
        String propertyName) {
        if (bean == null)
            return null;
        Assert.isNotNull(propertyName);
        return BeansObservables.observeValue(bean, propertyName);
    }

    public Control createBoundWidget(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions, Label label,
        String[] widgetValues, Object bean, String propertyName,
        AbstractValidator validator) {
        return widgetCreator.createBoundWidget(composite, widgetClass,
            widgetOptions, label, widgetValues,
            createBeansObservable(bean, propertyName), validator);
    }

    public Control createBoundWidget(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions, Label label,
        String[] widgetValues, Object bean, String propertyName,
        AbstractValidator validator, String bindingKey) {
        return widgetCreator.createBoundWidget(composite, widgetClass,
            widgetOptions, label, widgetValues,
            createBeansObservable(bean, propertyName), validator, bindingKey);

    }

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String fieldLabel, String[] widgetValues, Object bean,
        String propertyName, AbstractValidator validator) {
        return widgetCreator.createBoundWidgetWithLabel(composite, widgetClass,
            widgetOptions, fieldLabel, widgetValues,
            createBeansObservable(bean, propertyName), validator);
    }

    public DateTimeWidget createDateTimeWidget(Composite client, Label label,
        Date date, Object bean, String propertyName,
        AbstractValidator validator, int typeShown, String bindingKey) {
        return widgetCreator.createDateTimeWidget(client, label, date,
            createBeansObservable(bean, propertyName), validator, typeShown,
            bindingKey);
    }

    protected DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, Object bean, String propertyName,
        AbstractValidator validator) {
        return createDateTimeWidget(client, nameLabel, date, bean,
            propertyName, validator, SWT.DATE | SWT.TIME);
    }

    public DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, Object bean, String propertyName,
        AbstractValidator validator, int typeShown) {
        return widgetCreator.createDateTimeWidget(client, nameLabel, date,
            createBeansObservable(bean, propertyName), validator, typeShown,
            null);
    }

    // implementation of ISelectionProvider

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        // Do nothing
    }

    @Override
    public ISelection getSelection() {
        if (adapter != null)
            return new StructuredSelection(adapter);
        return null;
    }

    @Override
    public void removeSelectionChangedListener(
        ISelectionChangedListener listener) {
        // Do nothing
    }

    @Override
    public void setSelection(ISelection selection) {
        // Do nothing
    }
}
