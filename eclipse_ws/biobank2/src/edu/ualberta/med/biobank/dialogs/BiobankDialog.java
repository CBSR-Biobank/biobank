package edu.ualberta.med.biobank.dialogs;

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
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.forms.FieldInfo;
import edu.ualberta.med.biobank.forms.FormUtils;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class BiobankDialog extends Dialog {

    protected DataBindingContext dbc;

    private Label statusLabel;

    private Boolean enabledOkButton;

    protected BiobankDialog(Shell parentShell) {
        super(parentShell);
        dbc = new DataBindingContext();
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control contents = super.createButtonBar(parent);
        if (enabledOkButton != null) {
            // in case the binding wanted to modify it before its creation
            setOkButtonEnabled(enabledOkButton);
        }
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        statusLabel = new Label(contents, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        bindChangeListener();
        return contents;
    }

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<?> widgetClass, int widgetOptions, String fieldLabel,
        String[] widgetValues, IObservableValue modelObservableValue,
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
        Class<?> widgetClass, int widgetOptions, String fieldLabel,
        String[] widgetValues, IObservableValue modelObservableValue,
        AbstractValidator validator) {
        Label label;

        label = new Label(composite, SWT.LEFT);
        label.setText(fieldLabel + ":");
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        return createBoundWidget(composite, widgetClass, widgetOptions, label,
            widgetValues, modelObservableValue, validator);

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
            Text text = new Text(composite, widgetOptions);
            text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            dbc.bindValue(SWTObservables.observeText(text, SWT.Modify),
                modelObservableValue, uvs, null);
            return text;
        } else if (widgetClass == Combo.class) {
            Combo combo = new Combo(composite, SWT.READ_ONLY);
            combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            Assert.isNotNull(widgetValues, "combo values not assigned");
            combo.setItems(widgetValues);

            dbc.bindValue(SWTObservables.observeSelection(combo),
                modelObservableValue, uvs, null);
            return combo;
        } else if (widgetClass == CCombo.class) {
            CCombo combo = new CCombo(composite, SWT.READ_ONLY);
            combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            Assert.isNotNull(widgetValues, "combo values not assigned");
            combo.setItems(widgetValues);

            dbc.bindValue(SWTObservables.observeSelection(combo),
                modelObservableValue, uvs, null);
            return combo;
        } else if (widgetClass == Button.class) {
            Button button = new Button(composite, SWT.CHECK);
            dbc.bindValue(SWTObservables.observeSelection(button),
                modelObservableValue, uvs, null);
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
    protected ComboViewer createComboViewerWithNoSelectionValidator(
        Composite parent, String fieldLabel, Collection<?> input,
        String errorMessage) {
        Label label;

        label = new Label(parent, SWT.LEFT);
        label.setText(fieldLabel + ":");

        ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new BiobankLabelProvider());
        if (input != null) {
            comboViewer.setInput(input);
        }

        Combo combo = comboViewer.getCombo();
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        NonEmptyString validator = new NonEmptyString(errorMessage);
        validator.setControlDecoration(FormUtils.createDecorator(label,
            errorMessage));
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(validator);
        IObservableValue selectedValue = new WritableValue("", String.class);
        dbc.bindValue(SWTObservables.observeSelection(combo), selectedValue,
            uvs, null);
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

            createBoundWidgetWithLabel(client, fi.widgetClass,
                fi.widgetOptions, fi.label, fi.widgetValues, PojoObservables
                    .observeValue(pojo, key), fi.validatorClass, fi.errMsg);
        }
    }

    protected void bindChangeListener() {
        final IObservableValue statusObservable = new WritableValue();
        dbc.bindValue(statusObservable, new AggregateValidationStatus(dbc
            .getBindings(), AggregateValidationStatus.MAX_SEVERITY));

        statusObservable.addChangeListener(new IChangeListener() {
            public void handleChange(ChangeEvent event) {
                IObservableValue validationStatus = (IObservableValue) event
                    .getSource();
                IStatus status = (IStatus) validationStatus.getValue();

                if (status.getSeverity() == IStatus.OK) {
                    setStatusMessage("", Display.getCurrent().getSystemColor(
                        SWT.COLOR_BLACK));
                    setOkButtonEnabled(true);
                } else {
                    setStatusMessage(status.getMessage(), Display.getCurrent()
                        .getSystemColor(SWT.COLOR_RED));
                    setOkButtonEnabled(false);
                }
            }
        });
    }

    protected void setStatusMessage(String text, Color systemColor) {
        if ((statusLabel != null) && !statusLabel.isDisposed()) {
            statusLabel.setText(text);
            statusLabel.setForeground(systemColor);
        }
    }

    protected void setStatusMessage(String msg) {
        setStatusMessage(msg, Display.getCurrent().getSystemColor(
            SWT.COLOR_BLACK));
    }

    protected void setOkButtonEnabled(boolean enabled) {
        Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null && !okButton.isDisposed()) {
            okButton.setEnabled(enabled);
        } else {
            enabledOkButton = enabled;
        }
    }
}
