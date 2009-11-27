package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.Date;

import org.acegisecurity.AccessDeniedException;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.forms.input.FormInput;
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

    private static Logger LOGGER = Logger.getLogger(BiobankEntryForm.class
        .getName());

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
        if (!confirmAction.isEnabled())
            monitor.setCanceled(true);
        doSaveInternal();
    }

    protected void doSaveInternal() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    if (confirmAction.isEnabled())
                        saveForm();
                    else
                        throw new BiobankCheckException(
                            "Form in invalid state, save failed.");
                    // TODO: prevent this tab from closing
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
                    BioBankPlugin
                        .openAsyncError("Save error", bce.getMessage());
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
        editorSite.getShell().addListener(SWT.CLOSE, new Listener() {

            @Override
            public void handleEvent(Event event) {
                // TODO Auto-generated method stub

            }
        });
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

    protected void createBoundWidgetsFromMap(ListOrderedMap fieldsMap,
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
        String errorMessage) {
        return widgetCreator.createComboViewerWithNoSelectionValidator(parent,
            fieldLabel, input, selection, errorMessage);
    }

    protected DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, Object observedObject,
        String propertyName, final String emptyMessage) {
        return widgetCreator.createDateTimeWidget(client, nameLabel, date,
            observedObject, propertyName, emptyMessage);
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
        // ControlContribution reset = new ControlContribution("Reset") {
        // @Override
        // protected Control createControl(Composite parent) {
        // Button resetButton = new Button(parent, SWT.PUSH);
        // resetButton.setText("Reset");
        // resetButton.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // reset();
        // }
        // });
        // return resetButton;
        // }
        // };
        Action reset = new Action("Reset") {
            @Override
            public void run() {
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    public void run() {
                        reset();
                    }
                });
            }
        };
        reset.setImageDescriptor(resetActionImage);
        form.getToolBarManager().add(reset);

        // ControlContribution cancel = new ControlContribution("Cancel") {
        // @Override
        // protected Control createControl(Composite parent) {
        // Button cancelButton = new Button(parent, SWT.PUSH);
        // cancelButton.setText("Cancel");
        // cancelButton.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // cancel();
        // }
        // });
        // return cancelButton;
        // }
        // };
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

        // ControlContribution confirm = new ControlContribution("Confirm") {
        // @Override
        // protected Control createControl(Composite parent) {
        // confirmButton = new Button(parent, SWT.PUSH);
        // confirmButton.setText("Confirm");
        // confirmButton.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // confirm();
        // }
        // });
        // return confirmButton;
        // }
        // };
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
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().closeEditor(this, true);
                if (getNextOpenedFormID() != null) {
                    AdapterBase.openForm(new FormInput(getAdapter()),
                        getNextOpenedFormID());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Can't save the form", e);
        }
    }

    public void cancel() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().closeEditor(this, false);
        } catch (Exception e) {
            LOGGER.error("Can't close the form", e);
        }
    }

    public void reset() {
        try {
            adapter.resetObject();
        } catch (Exception e) {
            LOGGER.error("Can't reset the form", e);
        }
    }

    /**
     * Return the ID of the form that should be opened after the save action is
     * performed and the current form closed
     */
    public abstract String getNextOpenedFormID();

}
