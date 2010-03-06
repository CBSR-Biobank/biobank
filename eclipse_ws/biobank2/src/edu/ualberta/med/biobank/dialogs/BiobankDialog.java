package edu.ualberta.med.biobank.dialogs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;

public abstract class BiobankDialog extends TitleAreaDialog {

    protected WidgetCreator widgetCreator;

    private Boolean okButtonEnabled;

    private boolean setupFinished = false;

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
        try {
            createDialogAreaInternal(parentComposite);
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (final RemoteAccessException exp) {
            BioBankPlugin.openRemoteAccessErrorMessage();
        } catch (final AccessDeniedException ade) {
            BioBankPlugin.openAccessDeniedErrorMessage();
        } catch (BiobankCheckException bce) {
            BioBankPlugin.openAsyncError("Save error", bce);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        bindChangeListener();
        setupFinished = true;
        return parentComposite;
    }

    protected abstract void createDialogAreaInternal(Composite parent)
        throws Exception;

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
                    setErrorMessage(null);
                    setOkButtonEnabled(true);
                } else {
                    if (setupFinished) {
                        setErrorMessage(status.getMessage());
                    }
                    setOkButtonEnabled(false);
                }
            }
        });
        widgetCreator.addGlobalBindValue(statusObservable);
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
