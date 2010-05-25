package edu.ualberta.med.biobank.widgets.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.forms.FieldInfo;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.validators.DateNotNulValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.BiobankWidget;
import edu.ualberta.med.biobank.widgets.DateTimeObservableValue;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class WidgetCreator {

    protected DataBindingContext dbc;

    protected Map<String, Control> controls;

    protected Map<String, Binding> bindings;

    private FormToolkit toolkit;

    private KeyListener keyListener;

    private ModifyListener modifyListener;

    private SelectionListener selectionListener;

    public WidgetCreator(Map<String, Control> controls) {
        this.controls = controls;
    }

    public void initDataBinding() {
        dbc = new DataBindingContext();
        bindings = new HashMap<String, Binding>();
    }

    public void setToolkit(FormToolkit toolkit) {
        this.toolkit = toolkit;
    }

    public void createBoundWidgetsFromMap(Map<String, FieldInfo> fieldsMap,
        Object bean, Composite client) {
        FieldInfo fi;
        for (String label : fieldsMap.keySet()) {
            fi = fieldsMap.get(label);

            Control control = createBoundWidgetWithLabel(client,
                fi.widgetClass, fi.widgetOptions, fi.label, fi.widgetValues,
                BeansObservables.observeValue(bean, label), fi.validatorClass,
                fi.errMsg);
            if (controls != null) {
                controls.put(label, control);
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
        return createBoundWidgetWithLabel(composite, widgetClass,
            widgetOptions, fieldLabel, widgetValues, modelObservableValue,
            validator, null);
    }

    public Control createBoundWidgetWithLabel(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String fieldLabel, String[] widgetValues,
        IObservableValue modelObservableValue, AbstractValidator validator,
        String bindingKey) {
        Label label;

        label = createLabel(composite, fieldLabel);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        return createBoundWidget(composite, widgetClass, widgetOptions, label,
            widgetValues, modelObservableValue, validator, bindingKey);

    }

    public Control createBoundWidget(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String[] widgetValues, IObservableValue modelObservableValue,
        IValidator validator) {
        return createBoundWidget(composite, widgetClass, widgetOptions,
            widgetValues, modelObservableValue, validator, null);
    }

    public Control createBoundWidget(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String[] widgetValues, IObservableValue modelObservableValue,
        IValidator validator, String bindingKey) {
        Assert.isNotNull(dbc);
        UpdateValueStrategy uvs = null;
        if (validator != null) {
            uvs = new UpdateValueStrategy();
            uvs.setAfterGetValidator(validator);
        }
        if (widgetClass == BiobankText.class) {
            return createText(composite, widgetOptions, modelObservableValue,
                uvs, bindingKey);
        } else if (widgetClass == Combo.class) {
            return createCombo(composite, widgetOptions, widgetValues,
                modelObservableValue, uvs, bindingKey);
        } else if (widgetClass == Button.class) {
            return createButton(composite, modelObservableValue, uvs,
                bindingKey);
        } else {
            Assert.isTrue(false, "invalid widget class "
                + widgetClass.getName());
        }
        return null;
    }

    private Control createButton(Composite composite,
        IObservableValue modelObservableValue, UpdateValueStrategy uvs,
        String bindingKey) {
        Button button = new Button(composite, SWT.CHECK);
        if (toolkit != null) {
            toolkit.adapt(button, true, true);
        }
        Binding binding = dbc.bindValue(
            SWTObservables.observeSelection(button), modelObservableValue, uvs,
            null);
        if (bindingKey != null) {
            bindings.put(bindingKey, binding);
        }
        if (selectionListener != null) {
            button.addSelectionListener(selectionListener);
        }
        return button;
    }

    private Combo createCombo(Composite composite, int options,
        String[] widgetValues, final IObservableValue modelObservableValue,
        UpdateValueStrategy uvs, String bindingKey) {
        final Combo combo = new Combo(composite, SWT.READ_ONLY | SWT.BORDER
            | options);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        Assert.isNotNull(widgetValues, "combo values not assigned");
        combo.setItems(widgetValues);
        if (toolkit != null) {
            toolkit.adapt(combo, true, true);
        }
        Binding binding = dbc.bindValue(SWTObservables.observeSelection(combo),
            modelObservableValue, uvs, null);
        if (bindingKey != null) {
            bindings.put(bindingKey, binding);
        }
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

    public BiobankText createText(Composite composite, int widgetOptions,
        IObservableValue modelObservableValue, UpdateValueStrategy uvs) {
        return createText(composite, widgetOptions, modelObservableValue, uvs,
            null);
    }

    public BiobankText createText(Composite composite, int widgetOptions,
        IObservableValue modelObservableValue, UpdateValueStrategy uvs,
        String bindingKey) {
        if (widgetOptions == SWT.NONE) {
            widgetOptions = SWT.SINGLE;
        }

        if ((widgetOptions & SWT.MULTI) != 0) {
            widgetOptions = widgetOptions | SWT.V_SCROLL;
        }

        BiobankText text = null;
        if (toolkit == null) {
            text = new BiobankText(composite, widgetOptions);
        } else {
            text = new BiobankText(composite, widgetOptions, toolkit);
        }
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        if ((widgetOptions & SWT.MULTI) != 0) {
            gd.heightHint = 90;
            text.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.keyCode == 9) { // TAB
                        ((Control) e.widget).traverse(SWT.TRAVERSE_TAB_NEXT);
                        // cancel default tab behaviour of the text
                        e.doit = false;
                    }
                }
            });
        }
        text.setLayoutData(gd);
        if (keyListener != null) {
            text.addKeyListener(keyListener);
        }
        if (modelObservableValue != null) {
            Binding binding = dbc.bindValue(SWTObservables.observeText(text
                .getTextBox(), SWT.Modify), modelObservableValue, uvs, null);
            if (bindingKey != null) {
                bindings.put(bindingKey, binding);
            }
        }
        return text;
    }

    public Control createBoundWidget(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions, Label label,
        String[] widgetValues, IObservableValue modelObservableValue,
        AbstractValidator validator) {
        return createBoundWidget(composite, widgetClass, widgetOptions, label,
            widgetValues, modelObservableValue, validator, null);
    }

    public Control createBoundWidget(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions, Label label,
        String[] widgetValues, IObservableValue modelObservableValue,
        AbstractValidator validator, String bindingKey) {

        if (validator != null) {
            validator.setControlDecoration(BiobankWidget.createDecorator(label,
                validator.getErrorMessage()));
        }

        return createBoundWidget(composite, widgetClass, widgetOptions,
            widgetValues, modelObservableValue, validator, bindingKey);
    }

    public <T> ComboViewer createComboViewerWithNoSelectionValidator(
        Composite parent, String fieldLabel, Collection<T> input, T selection,
        String errorMessage) {
        return createComboViewerWithNoSelectionValidator(parent, fieldLabel,
            input, selection, errorMessage, false);
    }

    public <T> ComboViewer createComboViewerWithNoSelectionValidator(
        Composite parent, String fieldLabel, Collection<T> input, T selection,
        String errorMessage, boolean useDefaultComparator) {
        return createComboViewerWithNoSelectionValidator(parent, fieldLabel,
            input, selection, errorMessage, useDefaultComparator, null);
    }

    public <T> ComboViewer createComboViewerWithNoSelectionValidator(
        Composite parent, String fieldLabel, Collection<T> input, T selection,
        String errorMessage, boolean useDefaultComparator, String bindingKey) {
        Label label = createLabel(parent, fieldLabel);
        return createComboViewerWithNoSelectionValidator(parent, label, input,
            selection, errorMessage, useDefaultComparator, bindingKey);
    }

    /**
     * Create a combo using ArrayContentProvider as content provider and
     * BiobankLabelProvider as Label provider. You should use
     * comboViewer.getSelection() to update datas.
     * 
     * @see BiobankLabelProvider#getColumnText
     */
    public <T> ComboViewer createComboViewerWithNoSelectionValidator(
        Composite parent, Label fieldLabel, Collection<T> input, T selection,
        String errorMessage, boolean useDefaultComparator, String bindingKey) {
        Assert.isNotNull(dbc);
        Combo combo = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);
        ComboViewer comboViewer = new ComboViewer(combo);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new BiobankLabelProvider());
        if (useDefaultComparator) {
            comboViewer.setComparator(new ViewerComparator());
        }
        if (input != null) {
            comboViewer.setInput(input);
        }

        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        NonEmptyStringValidator validator = new NonEmptyStringValidator(
            errorMessage);
        validator.setControlDecoration(BiobankWidget.createDecorator(
            fieldLabel, errorMessage));
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(validator);
        IObservableValue selectedValue = new WritableValue("", String.class);
        Binding binding = dbc.bindValue(SWTObservables.observeSelection(combo),
            selectedValue, uvs, null);
        if (bindingKey != null) {
            bindings.put(bindingKey, binding);
        }

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
        String nameLabel, Date date, IObservableValue modelObservableValue,
        final String emptyMessage) {
        return createDateTimeWidget(client, nameLabel, date,
            modelObservableValue, emptyMessage, null);
    }

    public DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, IObservableValue modelObservableValue,
        final String emptyMessage, String bindingKey) {
        return createDateTimeWidget(client, nameLabel, date,
            modelObservableValue, emptyMessage, SWT.DATE | SWT.TIME, bindingKey);
    }

    public DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, IObservableValue modelObservableValue,
        final String emptyMessage, int typeShown) {
        return createDateTimeWidget(client, nameLabel, date,
            modelObservableValue, emptyMessage, typeShown, null);
    }

    public DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, IObservableValue modelObservableValue,
        final String emptyMessage, int typeShown, String bindingKey) {
        Label label = createLabel(client, nameLabel, SWT.NONE, true);
        return createDateTimeWidget(client, label, date, modelObservableValue,
            emptyMessage, typeShown, bindingKey);
    }

    public DateTimeWidget createDateTimeWidget(Composite client, Label label,
        Date date, IObservableValue modelObservableValue,
        final String emptyMessage, int typeShown) {
        return createDateTimeWidget(client, label, date, modelObservableValue,
            emptyMessage, typeShown, null);
    }

    public DateTimeWidget createDateTimeWidget(Composite client, Label label,
        Date date, IObservableValue modelObservableValue,
        final String emptyMessage, int typeShown, String bindingKey) {
        final DateTimeWidget widget = new DateTimeWidget(client, typeShown,
            date);
        if (selectionListener != null) {
            widget.addModifyListener(modifyListener);
        }
        if (toolkit != null) {
            widget.adaptToToolkit(toolkit, true);
        }
        if (modelObservableValue != null) {
            UpdateValueStrategy uvs = null;
            if (emptyMessage != null && !emptyMessage.isEmpty()) {
                DateNotNulValidator validator = new DateNotNulValidator(
                    emptyMessage);
                validator.setControlDecoration(BiobankWidget.createDecorator(
                    label, validator.getErrorMessage()));
                uvs = new UpdateValueStrategy();
                uvs.setAfterConvertValidator(validator);
            }
            Binding binding = dbc.bindValue(
                new DateTimeObservableValue(widget), modelObservableValue, uvs,
                null);
            if (bindingKey != null) {
                bindings.put(bindingKey, binding);
            }
        }
        return widget;
    }

    public void addBooleanBinding(WritableValue writableValue,
        IObservableValue observableValue, final String errorMsg) {
        addBooleanBinding(writableValue, observableValue, errorMsg,
            IStatus.ERROR);
    }

    public void addBooleanBinding(WritableValue writableValue,
        IObservableValue observableValue, final String errorMsg,
        final int statusType) {
        Assert.isNotNull(dbc);
        UpdateValueStrategy uvs = null;
        if (errorMsg != null) {
            uvs = new UpdateValueStrategy();
            uvs.setAfterConvertValidator(new IValidator() {
                @Override
                public IStatus validate(Object value) {
                    if (value instanceof Boolean && !(Boolean) value) {
                        if (statusType == IStatus.WARNING) {
                            return ValidationStatus.warning(errorMsg);
                        }
                        return ValidationStatus.error(errorMsg);
                    } else {
                        return Status.OK_STATUS;
                    }
                }

            });
        }
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

    public void removeBinding(String bindingKey) {
        Assert.isNotNull(dbc);
        Binding binding = bindings.get(bindingKey);
        Assert.isNotNull(binding);
        dbc.removeBinding(binding);
    }

    public void addBinding(Binding binding) {
        Assert.isNotNull(dbc);
        dbc.addBinding(binding);
    }

    public void addBinding(String bindingKey) {
        Assert.isNotNull(dbc);
        Binding binding = bindings.get(bindingKey);
        Assert.isNotNull(binding);
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

    public void createWidgetsFromMap(Map<String, FieldInfo> fieldsMap,
        Composite parent) {
        FieldInfo fi;

        for (String label : fieldsMap.keySet()) {
            fi = fieldsMap.get(label);

            Control control = createLabelledWidget(parent, fi.widgetClass,
                SWT.NONE, fi.label, null);
            controls.put(label, control);
        }
    }

    public Control createWidget(Composite parent, Class<?> widgetClass,
        int widgetOptions, String value) {
        if ((widgetClass == Combo.class) || (widgetClass == BiobankText.class)
            || (widgetClass == Label.class)) {
            if (widgetOptions == SWT.NONE) {
                widgetOptions = SWT.SINGLE;
            }

            BiobankText field = createText(parent, widgetOptions | SWT.LEFT,
                null, null);

            // Label field = createLabel(parent, "", widgetOptions | SWT.LEFT
            // | SWT.BORDER, false);
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

    public Control createLabelledWidget(Composite parent, Class<?> widgetClass,
        int widgetOptions, String fieldLabel, String value) {
        Label label = createLabel(parent, fieldLabel);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        return createWidget(parent, widgetClass, widgetOptions, value);
    }

    public void showWidget(Control widget, boolean show) {
        widget.setVisible(show);
        ((GridData) widget.getLayoutData()).exclude = !show;
    }

    public void showWidget(Control widget) {
        showWidget(widget, true);
    }

    public void hideWidget(Control widget) {
        showWidget(widget, false);
    }
}
