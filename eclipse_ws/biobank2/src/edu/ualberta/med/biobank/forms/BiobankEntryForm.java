package edu.ualberta.med.biobank.forms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Base class for data entry forms.
 * 
 * Notes: - saveForm() is called in it's own thread so making calls to the
 * database is possible.
 * 
 */
public abstract class BiobankEntryForm extends BiobankFormBase {

    protected WritableApplicationService appService;

    protected String sessionName;

    private boolean dirty = false;

    protected IStatus currentStatus;

    protected DataBindingContext dbc = new DataBindingContext();

    private Button confirmButton;

    private Button cancelButton;

    protected KeyListener keyListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent e) {
            if ((e.keyCode & SWT.MODIFIER_MASK) == 0) {
                setDirty(true);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };

    SelectionListener selectionListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            setDirty(true);
        }
    };

    ModifyListener modifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            setDirty(true);
        }
    };

    @Override
    public void doSave(IProgressMonitor monitor) {
        setDirty(false);
        doSaveInternal();
    }

    protected void doSaveInternal() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    saveForm();
                } catch (final RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (final RemoteAccessException exp) {
                    BioBankPlugin.openRemoteAccessErrorMessage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void doSaveAs() {
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
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        bindChangeListener();
    }

    abstract protected void saveForm() throws Exception;

    @Override
    public void setFocus() {
        form.setFocus();
    }

    protected void initConfirmButton(Composite parent,
        boolean doSaveInternalAction, boolean doSaveEditorAction) {
        confirmButton = toolkit.createButton(parent, "Confirm", SWT.PUSH);
        if (doSaveInternalAction) {
            confirmButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    doSaveInternal();
                }
            });
        }
        if (doSaveEditorAction) {
            confirmButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().saveEditor(BiobankEntryForm.this,
                            false);
                }
            });
        }
    }

    protected void initCancelButton(Composite parent) {
        cancelButton = toolkit.createButton(parent, "Cancel", SWT.PUSH);
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelForm();
            }
        });
    }

    protected abstract void cancelForm();

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setAppService(WritableApplicationService appService) {
        Assert.isNotNull(appService, "appService is null");
        this.appService = appService;
    }

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<?> widgetClass, int widgetOptions, String fieldLabel,
        String[] widgetValues, IObservableValue modelObservableValue,
        Class<?> validatorClass, String validatorErrMsg) {
        Label label;

        label = toolkit.createLabel(composite, fieldLabel + ":", SWT.LEFT);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        return createBoundWidget(composite, widgetClass, widgetOptions, label,
            widgetValues, modelObservableValue, validatorClass, validatorErrMsg);

    }

    protected Control createBoundWidget(Composite composite,
        Class<?> widgetClass, int widgetOptions, String[] widgetValues,
        IObservableValue modelObservableValue, IValidator validator) {

        UpdateValueStrategy uvs = null;
        if (validator != null) {
            uvs = new UpdateValueStrategy();
            uvs.setAfterGetValidator(validator);
        }
        if (widgetClass == Text.class) {
            if (widgetOptions == SWT.NONE) {
                widgetOptions = SWT.SINGLE;
            }
            Text text = toolkit.createText(composite, "", widgetOptions);
            text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            text.addKeyListener(keyListener);

            dbc.bindValue(SWTObservables.observeText(text, SWT.Modify),
                modelObservableValue, uvs, null);
            return text;
        } else if (widgetClass == Combo.class) {
            Combo combo = new Combo(composite, SWT.READ_ONLY);
            combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            Assert.isNotNull(widgetValues, "combo values not assigned");
            combo.setItems(widgetValues);
            toolkit.adapt(combo, true, true);

            dbc.bindValue(SWTObservables.observeSelection(combo),
                modelObservableValue, uvs, null);

            combo.addSelectionListener(selectionListener);
            combo.addModifyListener(modifyListener);
            return combo;
        } else if (widgetClass == Button.class) {
            Button button = new Button(composite, SWT.CHECK);
            toolkit.adapt(button, true, true);
            dbc.bindValue(SWTObservables.observeSelection(button),
                modelObservableValue, uvs, null);

            button.addSelectionListener(selectionListener);
            return button;
        } else {
            Assert.isTrue(false, "invalid widget class "
                + widgetClass.getName());
        }
        return null;
    }

    protected Control createBoundWidget(Composite composite,
        Class<?> widgetClass, int widgetOptions, Label label,
        String[] widgetValues, IObservableValue modelObservableValue,
        Class<?> validatorClass, String validatorErrMsg) {
        IValidator validator = null;

        if (validatorClass != null) {
            validator = createValidator(validatorClass, FormUtils
                .createDecorator(label, validatorErrMsg), validatorErrMsg);
        }

        return createBoundWidget(composite, widgetClass, widgetOptions,
            widgetValues, modelObservableValue, validator);
    }

    /**
     * Create a combo using ArrayContentProvider as content provider and
     * BiobankLabelProvider as Label provider. You should use
     * comboViewer.getSelection() to update datas.
     * 
     * @see BiobankLabelProvider#getColumnText
     */
    protected ComboViewer createComboViewerWithNoSelectionValidator(
        Composite parent, String fieldLabel, Collection<?> input,
        String errorMessage) {
        Label label = toolkit.createLabel(parent, fieldLabel + ":", SWT.LEFT);

        ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new BiobankLabelProvider());
        if (input != null) {
            comboViewer.setInput(input);
        }

        Combo combo = comboViewer.getCombo();
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        comboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    setDirty(true);
                }
            });
        IValidator validator = createValidator(NonEmptyString.class, FormUtils
            .createDecorator(label, errorMessage), errorMessage);
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(validator);
        IObservableValue selectedValue = new WritableValue("", String.class);
        dbc.bindValue(SWTObservables.observeSelection(combo), selectedValue,
            uvs, null);
        return comboViewer;
    }

    protected IValidator createValidator(Class<?> validatorClass,
        ControlDecoration dec, String validatorErrMsg) {
        try {
            Class<?>[] types = new Class[] { String.class,
                ControlDecoration.class };
            Constructor<?> cons = validatorClass.getConstructor(types);
            Object[] args = new Object[] { validatorErrMsg, dec };
            return (IValidator) cons.newInstance(args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void createBoundWidgetsFromMap(ListOrderedMap fieldsMap,
        Object pojo, Composite client) {
        FieldInfo fi;

        MapIterator it = fieldsMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            fi = (FieldInfo) it.getValue();

            Control control = createBoundWidgetWithLabel(client,
                fi.widgetClass, fi.widgetOptions, fi.label, fi.widgetValues,
                PojoObservables.observeValue(pojo, key), fi.validatorClass,
                fi.errMsg);
            controls.put(key, control);
        }
    }

    protected Combo createSessionSelectionWidget(Composite client) {
        String[] sessionNames = SessionManager.getInstance().getSessionNames();

        if (sessionNames.length > 1) {
            toolkit.createLabel(client, "Session:", SWT.LEFT);
            Combo session = new Combo(client, SWT.READ_ONLY);
            session.setItems(sessionNames);
            session.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            return session;
        }
        return null;
    }

    protected void bindChangeListener() {
        IObservableValue statusObservable = new WritableValue();
        statusObservable.addChangeListener(new IChangeListener() {
            public void handleChange(ChangeEvent event) {
                IObservableValue validationStatus = (IObservableValue) event
                    .getSource();
                handleStatusChanged((IStatus) validationStatus.getValue());
            }
        });

        dbc
            .bindValue(statusObservable, new AggregateValidationStatus(dbc
                .getBindings(), AggregateValidationStatus.MAX_SEVERITY), null,
                null);
    }

    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            confirmButton.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            confirmButton.setEnabled(false);
        }
    }

    protected abstract String getOkMessage();

    protected void addSeparator() {
        Label separator = toolkit.createSeparator(form.getBody(),
            SWT.HORIZONTAL);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        separator.setLayoutData(gd);
    }

    protected void addBooleanBinding(WritableValue writableValue,
        IObservableValue observableValue, final String errorMsg) {
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterConvertValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    return ValidationStatus.error(errorMsg);
                } else {
                    return Status.OK_STATUS;
                }
            }

        });
        dbc.bindValue(writableValue, observableValue, uvs, uvs);
    }

    public Button getConfirmButton() {
        return confirmButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
