package edu.ualberta.med.biobank.wizards.pages;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;

public abstract class BiobankWizardPage extends WizardPage {
    private BgcWidgetCreator widgetCreator;

    protected BiobankWizardPage(String pageName, String title,
        ImageDescriptor titleImage) {
        super(pageName, title, titleImage);

        widgetCreator = new BgcWidgetCreator(null);
        widgetCreator.initDataBinding();
    }

    @Override
    public void createControl(Composite parent) {
        try {
            createDialogAreaInternal(parent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        bindChangeListener();
    }

    protected abstract void createDialogAreaInternal(Composite parent)
        throws Exception;

    private void bindChangeListener() {
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

    private void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            setErrorMessage(null);
            setPageComplete(true);
        } else {
            setErrorMessage(status.getMessage());
            setPageComplete(false);
        }
    }

    protected BgcWidgetCreator getWidgetCreator() {
        return widgetCreator;
    }
}
