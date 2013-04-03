package edu.ualberta.med.biobank.gui.common.dialogs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;

public abstract class BgcBaseDialog extends TitleAreaDialog {

    protected BgcWidgetCreator widgetCreator;

    protected Boolean okButtonEnabled;

    protected boolean setupFinished = false;

    private boolean closed;

    public BgcBaseDialog(Shell parentShell) {
        super(parentShell);
        widgetCreator = new BgcWidgetCreator(null);
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
        return BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_DIALOGS);
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
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        try {
            createDialogAreaInternal(contents);
        } catch (final RemoteConnectFailureException exp) {
            BgcPlugin.openRemoteConnectErrorMessage(exp);
        } catch (final RemoteAccessException exp) {
            BgcPlugin.openRemoteAccessErrorMessage(exp);
        } catch (final AccessDeniedException ade) {
            BgcPlugin.openAccessDeniedErrorMessage(ade);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Build the separator line
        Label bottomSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        bottomSeparator.setLayoutData(
            new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

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

    @Override
    public boolean close() {
        this.closed = true;
        return super.close();
    }

    @Override
    public void setErrorMessage(String newErrorMessage) {
        if (!closed)
            // test that dialog is not closed to avoid sometimes
            // NullPointerException or disposed problems.
            super.setErrorMessage(newErrorMessage);
    }

    protected void setOkButtonEnabled(boolean enabled) {
        Button okButton = getButton(IDialogConstants.OK_ID);
        if ((okButton != null) && !okButton.isDisposed()) {
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

    protected <T> ComboViewer createComboViewer(Composite parent,
        String fieldLabel, Collection<T> input, T selection,
        String errorMessage, ComboSelectionUpdate csu,
        IBaseLabelProvider labelProvider) {
        return widgetCreator.createComboViewer(parent, fieldLabel, input,
            selection, errorMessage, csu, labelProvider);
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

    protected BgcWidgetCreator getWidgetCreator() {
        return widgetCreator;
    }

    protected Section createSection(final Composite contents, String title,
        String addTooltip, SelectionListener addListener) {
        Section section = new Section(contents, Section.TWISTIE
            | Section.TITLE_BAR | Section.EXPANDED);
        section.setText(title);
        section.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        section.setLayoutData(gd);

        ToolBar tbar = (ToolBar) section.getTextClient();
        if (tbar == null) {
            tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
            section.setTextClient(tbar);
        }

        if (addListener != null) {
            ToolItem titem = new ToolItem(tbar, SWT.NULL);
            titem.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(BgcPlugin.IMG_ADD));
            titem.setToolTipText(addTooltip);
            titem.addSelectionListener(addListener);
        }
        return section;
    }

}
