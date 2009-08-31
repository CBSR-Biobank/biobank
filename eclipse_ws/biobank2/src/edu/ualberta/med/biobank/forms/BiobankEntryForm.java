package edu.ualberta.med.biobank.forms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.acegisecurity.AccessDeniedException;
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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
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
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;

/**
 * Base class for data entry forms.
 * 
 * Notes: - saveForm() is called in it's own thread so making calls to the
 * database is possible.
 * 
 */
public abstract class BiobankEntryForm extends BiobankFormBase {

    protected String sessionName;

    private boolean dirty = false;

    protected IStatus currentStatus;

    protected DataBindingContext dbc;

    private CancelConfirmWidget cancelConfirmWidget;

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

    public BiobankEntryForm() {
        super();
        dbc = new DataBindingContext();
    }

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
                    setDirty(true);
                } catch (final RemoteAccessException exp) {
                    BioBankPlugin.openRemoteAccessErrorMessage();
                    setDirty(true);
                } catch (final AccessDeniedException ade) {
                    BioBankPlugin.openAccessDeniedErrorMessage();
                    setDirty(true);
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
        bindChangeListener();
    }

    abstract protected void saveForm() throws Exception;

    @Override
    public void setFocus() {
        form.setFocus();
    }

    protected void initCancelConfirmWidget(Composite parent) {
        initConfirmButton(parent, false);
    }

    protected void initConfirmButton(Composite parent, boolean showtextField) {
        cancelConfirmWidget = new CancelConfirmWidget(parent, this,
            showtextField);
    }

    public abstract void cancelForm() throws Exception;

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String fieldLabel, String[] widgetValues,
        IObservableValue modelObservableValue,
        Class<? extends AbstractValidator> validatorClass,
        String validatorErrMsg) {
        AbstractValidator validator = null;
        if (validatorClass != null) {
            validator = createValidator(validatorClass, validatorErrMsg);
        }
        return createBoundWidgetWithLabel(composite, widgetClass,
            widgetOptions, fieldLabel, widgetValues, modelObservableValue,
            validator);
    }

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String fieldLabel, String[] widgetValues,
        IObservableValue modelObservableValue, AbstractValidator validator) {
        Label label;

        label = toolkit.createLabel(composite, fieldLabel + ":", SWT.LEFT);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        return createBoundWidget(composite, widgetClass, widgetOptions, label,
            widgetValues, modelObservableValue, validator);

    }

    protected Control createBoundWidget(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String[] widgetValues, IObservableValue modelObservableValue,
        IValidator validator) {

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
        } else if (widgetClass == CCombo.class) {
            CCombo combo = new CCombo(composite, SWT.READ_ONLY);
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
        Class<? extends Widget> widgetClass, int widgetOptions, Label label,
        String[] widgetValues, IObservableValue modelObservableValue,
        AbstractValidator validator) {

        if (validator != null) {
            validator.setControlDecoration(FormUtils.createDecorator(label,
                validator.getErrorMessage()));
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
    protected <T> ComboViewer createCComboViewerWithNoSelectionValidator(
        Composite parent, String fieldLabel, Collection<?> input, T selection,
        String errorMessage) {
        Label label = toolkit.createLabel(parent, fieldLabel + ":", SWT.LEFT);

        CCombo combo = new CCombo(parent, SWT.READ_ONLY);
        ComboViewer comboViewer = new ComboViewer(combo);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new BiobankLabelProvider());
        if (input != null) {
            comboViewer.setInput(input);
        }

        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        AbstractValidator validator = new NonEmptyString(errorMessage);
        validator.setControlDecoration(FormUtils.createDecorator(label,
            errorMessage));
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(validator);
        IObservableValue selectedValue = new WritableValue("", String.class);
        dbc.bindValue(SWTObservables.observeSelection(combo), selectedValue,
            uvs, null);

        if (selection != null) {
            comboViewer.setSelection(new StructuredSelection(selection));
        }
        combo.addModifyListener(modifyListener);
        return comboViewer;
    }

    protected AbstractValidator createValidator(
        Class<? extends AbstractValidator> validatorClass,
        String validatorErrMsg) {
        try {
            Class<?>[] types = new Class[] { String.class };
            Constructor<?> cons = validatorClass.getConstructor(types);
            Object[] args = new Object[] { validatorErrMsg };
            return (AbstractValidator) cons.newInstance(args);
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

    protected void bindChangeListener() {
        final IObservableValue statusObservable = new WritableValue();
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
            setFormHeaderErrorMessage(getOkMessage(), IMessageProvider.NONE);
            if (cancelConfirmWidget != null) {
                cancelConfirmWidget.setConfirmEnabled(true);
            }
        } else {
            setFormHeaderErrorMessage(status.getMessage(),
                IMessageProvider.ERROR);
            if (cancelConfirmWidget != null) {
                cancelConfirmWidget.setConfirmEnabled(false);
            }
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

    /**
     * Return the ID of the form that should be opened after the save action is
     * performed and the current form closed
     */
    public abstract String getNextOpenedFormID();
}
