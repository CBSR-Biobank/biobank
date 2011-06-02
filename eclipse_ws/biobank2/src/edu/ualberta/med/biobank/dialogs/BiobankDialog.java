package edu.ualberta.med.biobank.dialogs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;

public abstract class BiobankDialog extends TitleAreaDialog {

    protected WidgetCreator widgetCreator;

    protected Boolean okButtonEnabled;

    protected boolean setupFinished = false;

    public BiobankDialog(Shell parentShell) {
        super(parentShell);
        widgetCreator = new WidgetCreator(null);
        widgetCreator.initDataBinding();
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getDialogShellTitle());
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitleImage(getTitleAreaImage());
        setTitle(getTitleAreaTitle());
        setMessage(getTitleAreaMessage(), getTitleAreaMessageType());
        return contents;
    }

    protected Image getTitleAreaImage() {
        return BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_DIALOGS);
    }

    protected int getTitleAreaMessageType() {
        return IMessageProvider.NONE;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    protected abstract String getTitleAreaMessage();

    protected abstract String getTitleAreaTitle();

    protected abstract String getDialogShellTitle();

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
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(GridData.FILL_BOTH));

        try {
            createDialogAreaInternal(contents);
        } catch (final RemoteConnectFailureException exp) {
            BiobankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (final RemoteAccessException exp) {
            BiobankPlugin.openRemoteAccessErrorMessage(exp);
        } catch (final AccessDeniedException ade) {
            BiobankPlugin.openAccessDeniedErrorMessage(ade);
        } catch (BiobankCheckException bce) {
            BiobankPlugin.openAsyncError("Save error", bce);
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
            @Override
            public void handleChange(ChangeEvent event) {
                IObservableValue validationStatus = (IObservableValue) event
                    .getSource();
                handleStatusChanged((IStatus) validationStatus.getValue());
            }
        });
        widgetCreator.addGlobalBindValue(statusObservable);
    }

    protected void handleStatusChanged(IStatus status) {
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

    protected void setOkButtonEnabled(boolean enabled) {
        Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null && !okButton.isDisposed()) {
            okButton.setEnabled(enabled);
        } else {
            okButtonEnabled = enabled;
        }
    }

    private IObservableValue createPojoObservable(Object pojo,
        String propertyName) {
        if (pojo == null)
            return null;
        Assert.isNotNull(propertyName);
        return PojoObservables.observeValue(pojo, propertyName);
    }

    public Control createBoundWidget(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions, Label label,
        String[] widgetValues, Object pojo, String propertyName,
        AbstractValidator validator) {
        return widgetCreator.createBoundWidget(composite, widgetClass,
            widgetOptions, label, widgetValues,
            createPojoObservable(pojo, propertyName), validator);
    }

    protected Control createBoundWidgetWithLabel(Composite composite,
        Class<? extends Widget> widgetClass, int widgetOptions,
        String fieldLabel, String[] widgetValues, Object pojo,
        String propertyName, AbstractValidator validator) {
        return widgetCreator.createBoundWidgetWithLabel(composite, widgetClass,
            widgetOptions, fieldLabel, widgetValues,
            createPojoObservable(pojo, propertyName), validator);
    }

    public DateTimeWidget createDateTimeWidget(Composite client, Label label,
        Date date, Object pojo, String propertyName,
        AbstractValidator validator, int typeShown, String bindingKey) {
        return widgetCreator.createDateTimeWidget(client, label, date,
            createPojoObservable(pojo, propertyName), validator, typeShown,
            bindingKey);
    }

    protected DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, Object pojo, String propertyName,
        AbstractValidator validator) {
        return createDateTimeWidget(client, nameLabel, date, pojo,
            propertyName, validator, SWT.DATE | SWT.TIME);
    }

    public DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, Object pojo, String propertyName,
        AbstractValidator validator, int typeShown) {
        return widgetCreator.createDateTimeWidget(client, nameLabel, date,
            createPojoObservable(pojo, propertyName), validator, typeShown,
            null);
    }

    protected WidgetCreator getWidgetCreator() {
        return widgetCreator;
    }

}
