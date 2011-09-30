package edu.ualberta.med.biobank.gui.common.forms;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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

import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;

public abstract class BgcFormBase extends EditorPart implements
    ISelectionProvider {

    private static BgcLogger logger = BgcLogger.getLogger(BgcFormBase.class
        .getName());

    protected ManagedForm mform;

    protected FormToolkit toolkit;

    protected ScrolledPageBook book;

    protected ScrolledForm form;

    protected Composite page;

    protected Map<String, Control> widgets;

    protected BgcWidgetCreator widgetCreator;

    public static List<BgcFormBase> currentLinkedForms;

    public List<BgcFormBase> linkedForms;

    protected IDoubleClickListener collectionDoubleClickListener = new IDoubleClickListener() {
        @Override
        public void doubleClick(DoubleClickEvent event) {
            performDoubleClick(event);
        }
    };

    public BgcFormBase() {
        widgets = new HashMap<String, Control>();
        widgetCreator = createWidgetCreator();
    }

    protected void addWidget(String widgetName, Control widget) {
        widgets.put(widgetName, widget);
    }

    protected Control getWidget(String widgetName) {
        return widgets.get(widgetName);
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        setSite(editorSite);
        setInput(input);
        try {
            init();
        } catch (final RemoteConnectFailureException exp) {
            BgcPlugin.openRemoteConnectErrorMessage(exp);
        } catch (Exception e) {
            logger.error("BgcFormBase.createPartControl Error", e); //$NON-NLS-1$
        }
    }

    /**
     * The initialisation method for the derived form.
     * 
     * @param adapter the corresponding model adapter the form is to edit /
     *            view.
     */
    protected abstract void init() throws Exception;

    protected abstract void performDoubleClick(DoubleClickEvent event);

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    protected BgcWidgetCreator createWidgetCreator() {
        return new BgcWidgetCreator(widgets);
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
        book.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            true));
        page = book.createPage("page"); //$NON-NLS-1$
        book.showPage("page"); //$NON-NLS-1$

        // start a new runnable so that database objects are populated in a
        // separate thread.
        BusyIndicator.showWhile(parent.getDisplay(), new Runnable() {
            @Override
            public void run() {
                try {
                    form.setImage(getFormImage());
                    createFormContent();
                    form.reflow(true);
                } catch (final RemoteConnectFailureException exp) {
                    BgcPlugin.openRemoteConnectErrorMessage(exp);
                } catch (Exception e) {
                    BgcPlugin.openError(
                        "BioBankFormBase.createPartControl Error", e); //$NON-NLS-1$
                }
            }
        });
    }

    protected abstract Image getFormImage();

    /**
     * Called in a non-UI thread to create the widgets that make up the form.
     */
    protected abstract void createFormContent() throws Exception;

    protected Section createSection(String title, Composite parent, int style) {
        Section section = toolkit.createSection(parent, style);
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

    protected Section createSection(String title, Composite parent) {
        return createSection(title, parent, Section.TWISTIE | Section.TITLE_BAR
            | Section.EXPANDED);
    }

    protected Section createSection(String title) {
        return createSection(title, page);
    }

    protected Composite sectionAddClient(Section section) {
        Composite client = toolkit.createComposite(section);
        section.setClient(client);
        client.setLayout(new GridLayout(2, false));
        toolkit.paintBordersFor(client);
        return client;
    }

    protected Composite createSectionWithClient(String title, Composite parent) {
        return sectionAddClient(createSection(title, parent));

    }

    protected Composite createSectionWithClient(String title) {
        return sectionAddClient(createSection(title, page));
    }

    public FormToolkit getToolkit() {
        return toolkit;
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

    protected BgcBaseText createReadOnlyLabelledField(Composite parent,
        int widgetOptions, String fieldLabel, String value) {
        return widgetCreator.createReadOnlyLabelledField(parent, widgetOptions,
            fieldLabel, value, false);
    }

    protected BgcBaseText createReadOnlyLabelledField(Composite parent,
        int widgetOptions, String fieldLabel, String value,
        boolean useBackgroundColor) {
        return widgetCreator.createReadOnlyLabelledField(parent, widgetOptions,
            fieldLabel, value, useBackgroundColor);
    }

    protected BgcBaseText createReadOnlyLabelledField(Composite parent,
        int widgetOptions, String fieldLabel) {
        return createReadOnlyLabelledField(parent, widgetOptions, fieldLabel,
            null);
    }

    public static void setTextValue(BgcBaseText label, String value) {
        if ((label != null) && !label.isDisposed()) {
            if (value == null)
                value = ""; //$NON-NLS-1$
            label.setText(value);
        }
    }

    protected BgcBaseText createReadOnlyWidget(Composite parent,
        int widgetOptions, String value) {
        BgcBaseText result = (BgcBaseText) createWidget(parent,
            BgcBaseText.class, SWT.READ_ONLY | widgetOptions, value);
        return result;
    }

    public static void setTextValue(BgcBaseText label, Object value) {
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
        currentLinkedForms = linkedForms;
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

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String fieldLabel, String[] widgetValues, Object bean,
        String propertyName, AbstractValidator validator, String bindingKey) {
        return widgetCreator.createBoundWidgetWithLabel(composite, widgetClass,
            widgetOptions, fieldLabel, widgetValues,
            createBeansObservable(bean, propertyName), validator, bindingKey);
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
    public void removeSelectionChangedListener(
        ISelectionChangedListener listener) {
        // Do nothing
    }

    @Override
    public void setSelection(ISelection selection) {
        // Do nothing
    }

    @Override
    public ISelection getSelection() {
        return null;
    }
}
