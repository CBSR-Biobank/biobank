package edu.ualberta.med.biobank.dialogs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;

public class BiobankDialog extends Dialog {

    protected WidgetCreator widgetCreator;

    private Label statusLabel;

    private Boolean okButtonEnabled;

    protected BiobankDialog(Shell parentShell) {
        super(parentShell);
        widgetCreator = new WidgetCreator(null);
        widgetCreator.initDataBinding();
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control contents = super.createButtonBar(parent);
        if (okButtonEnabled != null) {
            // in case the binding wanted to modify it before its creation
            setOkButtonEnabled(okButtonEnabled);
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
        return parentComposite;
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

    protected void bindChangeListener() {
        final IObservableValue statusObservable = new WritableValue();
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
        widgetCreator.addGlobalBindValue(statusObservable);
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
            okButtonEnabled = enabled;
        }
    }

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String fieldLabel, String[] widgetValues,
        IObservableValue modelObservableValue, AbstractValidator validator) {
        return widgetCreator.createBoundWidgetWithLabel(composite, widgetClass,
            widgetOptions, fieldLabel, widgetValues, modelObservableValue,
            validator);
    }

    protected DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, Object observedObject,
        String propertyName, final String emptyMessage) {
        return widgetCreator.createDateTimeWidget(client, nameLabel, date,
            observedObject, propertyName, emptyMessage);
    }

    protected WidgetCreator getWidgetCreator() {
        return widgetCreator;
    }
}
