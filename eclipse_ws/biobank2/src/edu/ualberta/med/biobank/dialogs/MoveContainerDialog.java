package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.validators.StringLengthValidator;

/**
 * Allows the user to move a container and its contents to a new location
 */
public class MoveContainerDialog extends BgcBaseDialog {

    private ContainerWrapper srcContainer;
    private ContainerWrapper dstContainer;

    private String newLabel = "";

    public MoveContainerDialog(Shell parent, ContainerWrapper srcContainer,
        ContainerWrapper dstContainer) {
        super(parent);
        Assert.isNotNull(srcContainer);
        this.srcContainer = srcContainer;
        this.dstContainer = dstContainer;
    }

    @Override
    protected String getDialogShellTitle() {
        return "Move Container";
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Select the destination for this container.";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Move Container " + srcContainer.getLabel();
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        BgcBaseText bbt = (BgcBaseText) createBoundWidgetWithLabel(contents,
            BgcBaseText.class, SWT.FILL, "Destination Address", null, this,
            "newLabel", new StringLengthValidator(2,
                "Destination label must be another container "
                    + "(4 characters minimum)."));

        if (this.dstContainer != null)
            bbt.setText(this.dstContainer.getLabel());
    }

    public String getNewLabel() {
        return newLabel.toUpperCase();
    }

    public void setNewLabel(String label) {
        newLabel = label;
    }

}
