package edu.ualberta.med.biobank.widgets.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.forms.FieldInfo;
import edu.ualberta.med.biobank.forms.FormUtils;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.validators.DateNotNulValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class WidgetCreator {

    protected DataBindingContext dbc;

    protected HashMap<String, Control> controls;

    private FormToolkit toolkit;

    private KeyListener keyListener;

    private ModifyListener modifyListener;

    private SelectionListener selectionListener;

    public WidgetCreator(HashMap<String, Control> controls) {
        this.controls = controls;
    }

    public void initDataBinding() {
        dbc = new DataBindingContext();
    }

    public void setToolkit(FormToolkit toolkit) {
        this.toolkit = toolkit;
    }

    public void createBoundWidgetsFromMap(ListOrderedMap fieldsMap,
        Object bean, Composite client) {
        FieldInfo fi;
        MapIterator it = fieldsMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            fi = (FieldInfo) it.getValue();

            Control control = createBoundWidgetWithLabel(client,
                fi.widgetClass, fi.widgetOptions, fi.label, fi.widgetValues,
                BeansObservables.observeValue(bean, key), fi.validatorClass,
                fi.errMsg);
            if (controls != null) {
                controls.put(key, control);
            }
        }
    }

    public Control createBoundWidgetWithLabel(Composite composite,
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

    public Control createBoundWidgetWithLabel(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String fieldLabel, String[] widgetValues,
        IObservableValue modelObservableValue, AbstractValidator validator) {
        Label label;

        label = createLabel(composite, fieldLabel);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        return createBoundWidget(composite, widgetClass, widgetOptions, label,
            widgetValues, modelObservableValue, validator);

    }

    public Control createBoundWidget(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String[] widgetValues, IObservableValue modelObservableValue,
        IValidator validator) {
        Assert.isNotNull(dbc);
        UpdateValueStrategy uvs = null;
        if (validator != null) {
            uvs = new UpdateValueStrategy();
            uvs.setAfterGetValidator(validator);
        }
        if (widgetClass == Text.class) {
            return createText(composite, widgetOptions, modelObservableValue,
                uvs);
        } else if (widgetClass == Combo.class) {
            return createCombo(composite, widgetOptions, widgetValues,
                modelObservableValue, uvs);
        } else if (widgetClass == Button.class) {
            return createButton(composite, modelObservableValue, uvs);
        } else {
            Assert.isTrue(false, "invalid widget class "
                + widgetClass.getName());
        }
        return null;
    }

    private Control createButton(Composite composite,
        IObservableValue modelObservableValue, UpdateValueStrategy uvs) {
        Button button = new Button(composite, SWT.CHECK);
        toolkit.adapt(button, true, true);
        dbc.bindValue(SWTObservables.observeSelection(button),
            modelObservableValue, uvs, null);
        button.addSelectionListener(selectionListener);
        return button;
    }

    private Combo createCombo(Composite composite, int options,
        String[] widgetValues, final IObservableValue modelObservableValue,
        UpdateValueStrategy uvs) {
        final Combo combo = new Combo(composite, SWT.READ_ONLY | SWT.BORDER
            | options);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        Assert.isNotNull(widgetValues, "combo values not assigned");
        combo.setItems(widgetValues);
        if (toolkit != null) {
            toolkit.adapt(combo, true, true);
        }
        dbc.bindValue(SWTObservables.observeSelection(combo),
            modelObservableValue, uvs, null);
        if (selectionListener != null) {
            combo.addSelectionListener(selectionListener);
        }
        if (modifyListener != null) {
            combo.addModifyListener(modifyListener);
        }
        final IValueChangeListener changeListener = new IValueChangeListener() {
            @Override
            public void handleValueChange(ValueChangeEvent event) {
                if (event.getObservableValue().getValue() == null
                    || event.getObservableValue().getValue().toString()
                        .isEmpty()) {
                    combo.deselectAll();
                }
            }
        };
        modelObservableValue.addValueChangeListener(changeListener);
        combo.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                modelObservableValue.removeValueChangeListener(changeListener);
            }
        });
        return combo;
    }

    private Text createText(Composite composite, int widgetOptions,
        IObservableValue modelObservableValue, UpdateValueStrategy uvs) {
        if (widgetOptions == SWT.NONE) {
            widgetOptions = SWT.SINGLE;
        }
        Text text = null;
        if (toolkit == null) {
            text = new Text(composite, widgetOptions);
        } else {
            text = toolkit.createText(composite, "", widgetOptions);
        }
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        if ((widgetOptions & SWT.MULTI) != 0) {
            gd.heightHint = 40;
        }
        text.setLayoutData(gd);
        if (keyListener != null) {
            text.addKeyListener(keyListener);
        }
        if (modelObservableValue != null) {
            dbc.bindValue(SWTObservables.observeText(text, SWT.Modify),
                modelObservableValue, uvs, null);
        }
        return text;
    }

    public Control createBoundWidget(Composite composite,
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
    public <T> ComboViewer createComboViewerWithNoSelectionValidator(
        Composite parent, String fieldLabel, Collection<T> input, T selection,
        String errorMessage) {
        Assert.isNotNull(dbc);
        Label label = createLabel(parent, fieldLabel);

        Combo combo = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);
        ComboViewer comboViewer = new ComboViewer(combo);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new BiobankLabelProvider());
        if (input != null) {
            comboViewer.setInput(input);
        }

        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        NonEmptyStringValidator validator = new NonEmptyStringValidator(
            errorMessage);
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
        if (modifyListener != null) {
            combo.addModifyListener(modifyListener);
        }
        return comboViewer;
    }

    public Label createLabel(Composite parent, String fieldLabel) {
        return createLabel(parent, fieldLabel, SWT.LEFT, true);
    }

    private Label createLabel(Composite parent, String fieldLabel, int options,
        boolean addColon) {
        Label label = null;
        String text = fieldLabel;
        if (addColon) {
            text += ":";
        }
        if (toolkit == null) {
            label = new Label(parent, options);
            label.setText(text);
        } else {
            label = toolkit.createLabel(parent, text, options);
        }
        return label;
    }

    public DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, Object observedObject,
        String propertyName, final String emptyMessage) {
        Label label = createLabel(client, nameLabel, SWT.NONE, true);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        final DateTimeWidget widget = new DateTimeWidget(client, SWT.NONE, date);
        widget.addSelectionListener(selectionListener);
        if (toolkit != null) {
            widget.adaptToToolkit(toolkit, true);
        }

        if (observedObject != null && propertyName != null) {
            final IObservableValue dateValue = BeansObservables.observeValue(
                observedObject, propertyName);
            widget.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    dateValue.setValue(widget.getDate());
                }
            });
            final IValueChangeListener changeListener = new IValueChangeListener() {
                @Override
                public void handleValueChange(ValueChangeEvent event) {
                    widget.setDate((Date) dateValue.getValue());
                }
            };
            dateValue.addValueChangeListener(changeListener);
            widget.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent e) {
                    dateValue.removeValueChangeListener(changeListener);
                }
            });

            if (emptyMessage != null && !emptyMessage.isEmpty()) {
                DateNotNulValidator validator = new DateNotNulValidator(
                    emptyMessage);
                validator.setControlDecoration(FormUtils.createDecorator(label,
                    validator.getErrorMessage()));
                UpdateValueStrategy uvs = new UpdateValueStrategy();
                uvs.setAfterConvertValidator(validator);
                bindValue(new WritableValue(null, Date.class), dateValue, uvs,
                    uvs);
            }
        }
        return widget;
    }

    public void addBooleanBinding(WritableValue writableValue,
        IObservableValue observableValue, final String errorMsg) {
        Assert.isNotNull(dbc);
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

    public void addGlobalBindValue(IObservableValue statusObservable) {
        Assert.isNotNull(dbc);
        dbc.bindValue(statusObservable, new AggregateValidationStatus(dbc
            .getBindings(), AggregateValidationStatus.MAX_SEVERITY));
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

    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
    }

    public void setModifyListener(ModifyListener modifyListener) {
        this.modifyListener = modifyListener;

    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    public Binding bindValue(IObservableValue targetObservableValue,
        IObservableValue modelObservableValue,
        UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget) {
        Assert.isNotNull(dbc);
        return dbc.bindValue(targetObservableValue, modelObservableValue,
            targetToModel, modelToTarget);
    }

    public void removeBinding(Binding binding) {
        Assert.isNotNull(dbc);
        dbc.removeBinding(binding);
    }

    public void addBinding(Binding binding) {
        Assert.isNotNull(dbc);
        dbc.addBinding(binding);
    }

    public <T> ComboViewer createComboViewer(Composite parent,
        String fieldLabel, Collection<?> input, T selection) {
        createLabel(parent, fieldLabel);

        Combo combo = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);
        ComboViewer comboViewer = new ComboViewer(combo);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new BiobankLabelProvider());
        if (input != null) {
            comboViewer.setInput(input);
        }

        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        if (selection != null) {
            comboViewer.setSelection(new StructuredSelection(selection));
        }
        return comboViewer;
    }

    public void createWidgetsFromMap(ListOrderedMap fieldsMap, Composite parent) {
        FieldInfo fi;

        MapIterator it = fieldsMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            fi = (FieldInfo) it.getValue();

            Control control = createWidget(parent, fi.widgetClass, SWT.NONE,
                fi.label, null);
            controls.put(key, control);
        }
    }

    public Control createWidget(Composite parent, Class<?> widgetClass,
        int widgetOptions, String fieldLabel, String value) {
        Label label = createLabel(parent, fieldLabel);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        if ((widgetClass == Combo.class) || (widgetClass == Text.class)
            || (widgetClass == Label.class)) {
            if (widgetOptions == SWT.NONE) {
                widgetOptions = SWT.SINGLE;
            }
            Label field = createLabel(parent, "", widgetOptions | SWT.LEFT
                | SWT.BORDER, false);
            field.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            if (value != null) {
                field.setText(value);
            }
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
}
