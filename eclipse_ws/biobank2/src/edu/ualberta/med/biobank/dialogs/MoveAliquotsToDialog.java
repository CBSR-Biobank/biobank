package edu.ualberta.med.biobank.dialogs;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

/**
 * Allows the user to choose a container to which aliquots will be moved
 */
public class MoveAliquotsToDialog extends BiobankDialog {

    private class ContainerLabelPojo {
        private String label;

        @SuppressWarnings("unused")
        public void setLabel(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    };

    private ContainerLabelPojo containerLabelPojo;

    private ContainerWrapper oldContainer;

    private HashMap<String, ContainerWrapper> map;

    public MoveAliquotsToDialog(Shell parent, ContainerWrapper oldContainer) {
        super(parent);
        Assert.isNotNull(oldContainer);
        this.oldContainer = oldContainer;
    }

    @Override
    protected String getDialogShellTitle() {
        return "Move aliquots from one container to another";
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Select the new container that can hold the aliquots.\n"
            + "It should be initialized, empty, as big as the previous one,"
            + " and should accept these aliquots.";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Move aliquots from container " + oldContainer.getLabel()
            + " to another";
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        List<SampleTypeWrapper> typesFromOlContainer = oldContainer
            .getContainerType().getSampleTypeCollection();
        List<ContainerWrapper> conts = ContainerWrapper
            .getEmptyContainersHoldingSampleType(
                SessionManager.getAppService(),
                SessionManager.getCurrentSite(), typesFromOlContainer,
                oldContainer.getRowCapacity(), oldContainer.getColCapacity());

        map = new HashMap<String, ContainerWrapper>();
        for (ContainerWrapper cont : conts) {
            map.put(cont.getLabel(), cont);
        }
        AbstractValidator validator = new AbstractValidator(
            "Destination container should accept these aliquots, "
                + "must be initialized but empty, "
                + " and as big as the previous one.") {

            @Override
            public IStatus validate(Object value) {
                if (!(value instanceof String)) {
                    throw new RuntimeException(
                        "Not supposed to be called for non-strings.");
                }

                ContainerWrapper cont = map.get(value);
                if (cont == null) {
                    showDecoration();
                    return ValidationStatus.error(errorMessage);
                } else {
                    hideDecoration();
                    return Status.OK_STATUS;
                }
            }
        };
        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.FILL,
            "New Container Label", null, containerLabelPojo, "label", validator);
    }

    public ContainerWrapper getNewContainer() {
        return map.get(containerLabelPojo.getLabel());
    }

}
