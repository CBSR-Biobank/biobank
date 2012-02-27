package edu.ualberta.med.biobank.forms;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryFormActions;
import edu.ualberta.med.biobank.gui.common.forms.BgcFormBase;
import edu.ualberta.med.biobank.gui.common.forms.FieldInfo;
import edu.ualberta.med.biobank.gui.common.forms.IBgcEntryForm;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.sourceproviders.ConfirmState;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

/**
 * Base class for data entry forms.
 * 
 * Notes: - saveForm() is called in it's own thread so making calls to the
 * database is possible.
 * 
 */
public abstract class BiobankEntryForm extends BiobankFormBase implements
    IBgcEntryForm {

    private static final String CONTEXT_ENTRY_FORM =
        "biobank.context.entryForm"; //$NON-NLS-1$

    protected String sessionName;

    private boolean dirty = false;

    // The widget that is to get the focus when the form is created
    private Control firstControl;

    protected BgcEntryFormActions formActions;

    protected KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if ((e.keyCode & SWT.MODIFIER_MASK) == 0) {
                setDirty(true);
            }
        }
    };

    protected ModifyListener modifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            setDirty(true);
        }
    };

    protected SelectionListener selectionListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            setDirty(true);
        }
    };

    public BiobankEntryForm() {
        super();
        firstControl = null;
        widgetCreator.initDataBinding();
        widgetCreator.setKeyListener(keyListener);
        widgetCreator.setModifyListener(modifyListener);
        widgetCreator.setSelectionListener(selectionListener);
    }

    public void formClosed() throws Exception {
        // TODO: is this necessary if making copies?
        if (adapter instanceof AdapterBase)
            if ((adapter != null)
                && (((AdapterBase) adapter).getModelObject() != null)) {
                ((AdapterBase) adapter).getModelObject().reload();
            }

        // not everything is well initialized on the adapter before it is really
        // saved. Should not do that now..
        // SessionManager.updateAdapterTreeNode(adapter);
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        setDirty(false);
        if (!formActions.getConfirmAction().isEnabled()) {
            monitor.setCanceled(true);
            setDirty(true);
            BgcPlugin.openAsyncError(
                Messages.BiobankEntryForm_state_error_title,
                Messages.BiobankEntryForm_state_error_msg);
            return;
        }
        doSaveInternal(monitor);
    }

    protected void doSaveInternal(final IProgressMonitor monitor) {
        IRunnableContext context = new ProgressMonitorDialog(Display
            .getDefault().getActiveShell());
        try {
            doBeforeSave();
            context.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {

                    try {
                        monitor.beginTask(Messages.BiobankEntryForm_saving,
                            IProgressMonitor.UNKNOWN);
                        saveForm();
                        // this needs to be done there if we want the new node
                        // to be in the tree and to be selected and to see the
                        // right label (needs to be done when save is finished,
                        // not when the form close)
                        SessionManager.updateAllSimilarNodes(adapter, true);
                        monitor.done();
                    } catch (Exception ex) {
                        saveErrorCatch(ex, monitor, true);
                    }
                }
            });
            doAfterSave();
        } catch (Exception e) {
            setDirty(true);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void cancelSave(IProgressMonitor monitor) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                setDirty(true);
            }
        });
        super.cancelSave(monitor);
    }

    /**
     * Called before the monitor start. Can be used to get values on the GUI
     * objects.
     */
    protected void doBeforeSave() throws Exception {
        // do nothing by default
    }

    /**
     * Called after the monitor start. Can be used to get values on the GUI
     * objects.
     */
    protected void doAfterSave() throws Exception {
        // default does nothing
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);
        setDirty(false);
        checkEditAccess();
    }

    protected void checkEditAccess() {
        // FIXME what should be done for new adapters?
        if (adapter instanceof AdapterBase) {
            if (adapter != null
                && ((AdapterBase) adapter).getObjectClazz() != null
                && !SessionManager.canUpdate(((AdapterBase) adapter)
                    .getObjectClazz())) {
                BgcPlugin.openAccessDeniedErrorMessage();
                throw new RuntimeException(
                    Messages.BiobankEntryForm_access_denied_error_msg);
            }
        }
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    protected void setDirty(boolean d) {
        dirty = d;
        firePropertyChange(ISaveablePart.PROP_DIRTY);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        addToolbarButtons();
        bindChangeListener();

        IContextService contextService = (IContextService) getSite()
            .getService(IContextService.class);
        contextService.activateContext(CONTEXT_ENTRY_FORM);
    }

    abstract protected void saveForm() throws Exception;

    @Override
    public void setFocus() {
        super.setFocus();
        Assert.isNotNull(firstControl, "first control widget is not set"); //$NON-NLS-1$
        if (!firstControl.isDisposed()) {
            firstControl.setFocus();
        }
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    protected Control getFirstControl() {
        return firstControl;
    }

    public void setFirstControl(Control c) {
        firstControl = c;
    }

    public Binding addBooleanBinding(WritableValue writableValue,
        IObservableValue observableValue, final String errorMsg) {
        return addBooleanBinding(writableValue, observableValue, errorMsg,
            IStatus.ERROR);
    }

    public Binding addBooleanBinding(WritableValue writableValue,
        IObservableValue observableValue, String errorMsg, int statusType) {
        return widgetCreator.addBooleanBinding(writableValue, observableValue,
            errorMsg, statusType);
    }

    protected void createBoundWidgetsFromMap(Map<String, FieldInfo> fieldsMap,
        Object bean, Composite client) {
        widgetCreator.createBoundWidgetsFromMap(fieldsMap, bean, client);
    }

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String fieldLabel, String[] widgetValues,
        IObservableValue modelObservableValue, AbstractValidator validator) {
        return widgetCreator.createBoundWidgetWithLabel(composite, widgetClass,
            widgetOptions, fieldLabel, widgetValues, modelObservableValue,
            validator);
    }

    protected <T> ComboViewer createComboViewer(Composite parent,
        String fieldLabel, Collection<T> input, T selection,
        String errorMessage, ComboSelectionUpdate csu) {
        return widgetCreator.createComboViewer(parent, fieldLabel, input,
            selection, errorMessage, csu, new BiobankLabelProvider());
    }

    protected DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, IObservableValue modelObservableValue,
        AbstractValidator validator) {
        return widgetCreator.createDateTimeWidget(client, nameLabel, date,
            modelObservableValue, validator);
    }

    /*
     * Applies a background color to the read only field.
     */
    @Override
    protected BgcBaseText createReadOnlyLabelledField(Composite parent,
        int widgetOptions, String fieldLabel, String value) {
        return createReadOnlyLabelledField(parent, widgetOptions, fieldLabel,
            value, true);
    }

    protected void bindChangeListener() {
        final IObservableValue statusObservable = new WritableValue();
        statusObservable.addChangeListener(new IChangeListener() {
            @Override
            public void handleChange(ChangeEvent event) {
                IObservableValue validationStatus = (IObservableValue) event
                    .getSource();
                handleStatusChanged((IStatus) validationStatus.getValue());
            }
        });

        widgetCreator.addGlobalBindValue(statusObservable);
    }

    protected void bindValue(IObservableValue targetObservableValue,
        IObservableValue modelObservableValue,
        UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget) {
        widgetCreator.bindValue(targetObservableValue, modelObservableValue,
            targetToModel, modelToTarget);
    }

    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            setFormHeaderErrorMessage(getOkMessage(), IMessageProvider.NONE);
            setConfirmEnabled(true);
        } else {
            setFormHeaderErrorMessage(status.getMessage(),
                IMessageProvider.ERROR);
            setConfirmEnabled(false);
        }
    }

    protected void setConfirmEnabled(boolean enabled) {
        ISourceProviderService service = (ISourceProviderService) PlatformUI
            .getWorkbench().getActiveWorkbenchWindow()
            .getService(ISourceProviderService.class);
        ConfirmState confirmSourceProvider = (ConfirmState) service
            .getSourceProvider(ConfirmState.SESSION_STATE);
        if (confirmSourceProvider != null) {
            confirmSourceProvider.setState(enabled);
            Action confirmAction = formActions.getConfirmAction();
            if (confirmAction != null) {
                confirmAction.setEnabled(enabled);
            }
        }
        form.getToolBarManager().update(true);
    }

    public void setFormHeaderErrorMessage(String message, int type) {
        if (!form.getForm().getHead().isDisposed()) {
            form.setMessage(message, type);
        }
    }

    /**
     * Called to get the string to display when the for is not in an error
     * state.
     * 
     * @return the string to display at the top of the form.
     */
    protected abstract String getOkMessage();

    protected void addSeparator() {
        Label separator = toolkit.createSeparator(page, SWT.HORIZONTAL);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        separator.setLayoutData(gd);
    }

    @Override
    public ScrolledForm getScrolledForm() {
        return form;
    }

    protected void addToolbarButtons() {
        formActions = new BgcEntryFormActions(this);
        addResetAction();
        addCancelAction();
        addConfirmAction();
        form.updateToolBar();
    }

    protected void addConfirmAction() {
        formActions.addConfirmAction(Actions.BIOBANK_CONFIRM);
    }

    protected void addResetAction() {
        formActions.addResetAction(Actions.BIOBANK_RELOAD);
    }

    protected void addCancelAction() {
        formActions.addCancelAction(Actions.BIOBANK_CANCEL);
    }

    protected void addPrintAction() {
        formActions.addPrintAction();
    }

    protected void setEnablePrintAction(boolean enable) {
        formActions.setEnablePrintAction(enable);
    }

    @Override
    public boolean print() {
        // override me
        return false;
    }

    @Override
    public void confirm() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().saveEditor(this, false);
            if (!isDirty()) {
                closeEntryOpenView(true, openViewAfterSaving());
            }
        } catch (Exception e) {
            LOGGER.error("Can't save the form", e); //$NON-NLS-1$
        }
    }

    protected boolean openViewAfterSaving() {
        return true;
    }

    protected void closeEntryOpenView(boolean saveOnClose, boolean openView) {
        int entryIndex = linkedForms.indexOf(this);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .closeEditor(this, saveOnClose);
        if (openView && getNextOpenedFormID() != null) {
            AdapterBase.openForm(new FormInput(getAdapter()),
                getNextOpenedFormID(), true);

            int previousFormIndex = entryIndex - 1;
            if (previousFormIndex >= 0
                && previousFormIndex < linkedForms.size()) {
                BgcFormBase form = linkedForms.get(previousFormIndex);
                IWorkbenchPage page = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();
                page.bringToTop(form);
            }
        }
    }

    @Override
    public void cancel() {
        try {
            boolean openView = adapter.getId() != null;
            if (adapter instanceof AdapterBase)
                openView &= !((AdapterBase) adapter).getModelObject().isNew();
            closeEntryOpenView(true, openView);
        } catch (Exception e) {
            LOGGER.error("Can't cancel the form", e); //$NON-NLS-1$
        }
    }

    /**
     * Return the ID of the form that should be opened after the save action is
     * performed and the current form closed
     */
    public abstract String getNextOpenedFormID();

}
