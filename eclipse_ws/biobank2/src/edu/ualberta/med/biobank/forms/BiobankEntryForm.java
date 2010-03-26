package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.acegisecurity.AccessDeniedException;
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

/**
 * Base class for data entry forms.
 * 
 * Notes: - saveForm() is called in it's own thread so making calls to the
 * database is possible.
 * 
 */
public abstract class BiobankEntryForm extends BiobankFormBase {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(BiobankEntryForm.class.getName());

    private static final Color READ_ONLY_TEXT_BGR = Display.getCurrent()
        .getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);

    protected String sessionName;

    private boolean dirty = false;

    protected IStatus currentStatus;

    // The widget that is to get the focus when the form is created
    protected Control firstControl;

    private Action confirmAction;

    private static ImageDescriptor resetActionImage = ImageDescriptor
        .createFromImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RESET_FORM));

    private static ImageDescriptor cancelActionImage = ImageDescriptor
        .createFromImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_CANCEL_FORM));

    private static ImageDescriptor confirmActionImage = ImageDescriptor
        .createFromImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_CONFIRM_FORM));

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

    @Override
    public void doSave(IProgressMonitor monitor) {
        setDirty(false);
        if (!confirmAction.isEnabled()) {
            monitor.setCanceled(true);
            setDirty(true);
            BioBankPlugin.openAsyncError("Form state",
                "Form in invalid state, save failed.");
            return;
        }
        doSaveInternal(monitor);
    }

    protected void doSaveInternal(final IProgressMonitor monitor) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    saveForm();
                } catch (final RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                    setDirty(true);
                    monitor.setCanceled(true);
                } catch (final RemoteAccessException exp) {
                    BioBankPlugin.openRemoteAccessErrorMessage();
                    setDirty(true);
                    monitor.setCanceled(true);
                } catch (final AccessDeniedException ade) {
                    BioBankPlugin.openAccessDeniedErrorMessage();
                    setDirty(true);
                    monitor.setCanceled(true);
                } catch (BiobankCheckException bce) {
                    setDirty(true);
                    monitor.setCanceled(true);
                    BioBankPlugin.openAsyncError("Save error", bce);
                } catch (Exception e) {
                    setDirty(true);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);
        setDirty(false);
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
    }

    abstract protected void saveForm() throws Exception;

    @Override
    public void setFocus() {
        super.setFocus();
        Assert.isNotNull(firstControl, "first control widget is not set");
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

    protected void addBooleanBinding(WritableValue writableValue,
        IObservableValue observableValue, final String errorMsg) {
        widgetCreator.addBooleanBinding(writableValue, observableValue,
            errorMsg);
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

    protected <T> ComboViewer createComboViewerWithNoSelectionValidator(
        Composite parent, String fieldLabel, Collection<T> input, T selection,
        String errorMessage, boolean useDefaultComparator) {
        return widgetCreator.createComboViewerWithNoSelectionValidator(parent,
            fieldLabel, input, selection, errorMessage, useDefaultComparator);
    }

    protected <T> ComboViewer createComboViewerWithNoSelectionValidator(
        Composite parent, String fieldLabel, Collection<T> input, T selection,
        String errorMessage) {
        return createComboViewerWithNoSelectionValidator(parent, fieldLabel,
            input, selection, errorMessage, false);
    }

    protected DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, Object observedObject,
        String propertyName, final String emptyMessage) {
        return widgetCreator.createDateTimeWidget(client, nameLabel, date,
            observedObject, propertyName, emptyMessage);
    }

    /*
     * Applies a background color to the read only field.
     */
    @Override
    protected Text createReadOnlyField(Composite parent, int widgetOptions,
        String fieldLabel, String value) {
        Text widget = super.createReadOnlyField(parent, widgetOptions,
            fieldLabel, value);
        widget.setBackground(READ_ONLY_TEXT_BGR);
        return widget;
    }

    protected void bindChangeListener() {
        final IObservableValue statusObservable = new WritableValue();
        statusObservable.addChangeListener(new IChangeListener() {
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
        if (confirmAction != null) {
            confirmAction.setEnabled(enabled);
        }
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
        Label separator = toolkit.createSeparator(form.getBody(),
            SWT.HORIZONTAL);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        separator.setLayoutData(gd);
    }

    private void addToolbarButtons() {
        Action reset = new Action("Reset") {
            @Override
            public void run() {
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    public void run() {
                        try {
                            reset();
                        } catch (Exception ex) {
                            BioBankPlugin.openAsyncError("Error on reset", ex);
                        }
                    }
                });
            }
        };
        reset.setImageDescriptor(resetActionImage);
        form.getToolBarManager().add(reset);

        Action cancel = new Action("Cancel") {
            @Override
            public void run() {
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    public void run() {
                        cancel();
                    }
                });
            }
        };
        cancel.setImageDescriptor(cancelActionImage);
        form.getToolBarManager().add(cancel);

        confirmAction = new Action("Confirm") {
            @Override
            public void run() {
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    public void run() {
                        confirm();
                    }
                });
            }
        };
        confirmAction.setImageDescriptor(confirmActionImage);
        form.getToolBarManager().add(confirmAction);

        form.updateToolBar();
    }

    public void confirm() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().saveEditor(this, false);
            if (!isDirty()) {
                closeEntryOpenView(true, true);
            }
        } catch (Exception e) {
            logger.error("Can't save the form", e);
        }
    }

    private void closeEntryOpenView(boolean saveOnClose, boolean openView) {
        int entryIndex = linkedForms.indexOf(this);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .closeEditor(this, saveOnClose);
        if (openView && getNextOpenedFormID() != null) {
            AdapterBase.openForm(new FormInput(getAdapter()),
                getNextOpenedFormID());

            int previousFormIndex = entryIndex - 1;
            if (previousFormIndex >= 0 && previousFormIndex < linkedForms.size()) {
                BiobankFormBase form = linkedForms.get(previousFormIndex);
                IWorkbenchPage page = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();
                page.bringToTop(form);
            }
        }
    }

    public void cancel() {
        try {
            adapter.resetObject();
            boolean openView = !adapter.getModelObject().isNew();
            closeEntryOpenView(false, openView);
        } catch (Exception e) {
            logger.error("Can't cancel the form", e);
        }
    }

    public void reset() throws Exception {
        adapter.resetObject();
        setDirty(false);
    }

    /**
     * Return the ID of the form that should be opened after the save action is
     * performed and the current form closed
     */
    public abstract String getNextOpenedFormID();

    protected void callPersistWithProgressDialog() throws Exception {
        IRunnableContext context = new ProgressMonitorDialog(Display
            .getDefault().getActiveShell());
        context.run(true, false, new IRunnableWithProgress() {
            @Override
            public void run(final IProgressMonitor monitor) {
                monitor.beginTask("Saving...", IProgressMonitor.UNKNOWN);
                try {
                    adapter.getModelObject().persist();
                } catch (final RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                    setDirty(true);
                } catch (final RemoteAccessException exp) {
                    BioBankPlugin.openRemoteAccessErrorMessage();
                    setDirty(true);
                } catch (final AccessDeniedException ade) {
                    BioBankPlugin.openAccessDeniedErrorMessage();
                    setDirty(true);
                } catch (BiobankCheckException bce) {
                    setDirty(true);
                    BioBankPlugin.openAsyncError("Save error", bce);
                } catch (Exception e) {
                    setDirty(true);
                    throw new RuntimeException(e);
                }
                monitor.done();
            }
        });
    }

}
