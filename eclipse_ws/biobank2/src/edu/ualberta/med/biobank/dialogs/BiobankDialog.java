package edu.ualberta.med.biobank.dialogs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.forms.FieldInfo;
import edu.ualberta.med.biobank.forms.FormUtils;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class BiobankDialog extends Dialog {

    protected DataBindingContext dbc = new DataBindingContext();

    protected BiobankDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        return contents;
    }

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<?> widgetClass, int widgetOptions, String fieldLabel,
        String[] widgetValues, IObservableValue modelObservableValue,
        Class<?> validatorClass, String validatorErrMsg) {
        Label label;

        label = new Label(composite, SWT.LEFT);
        label.setText(fieldLabel + ":");
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

            createBoundWidgetWithLabel(client, fi.widgetClass,
                fi.widgetOptions, fi.label, fi.widgetValues, PojoObservables
                    .observeValue(pojo, key), fi.validatorClass, fi.errMsg);
        }
    }

}
