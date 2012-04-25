package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.validators.StringLengthValidator;

/**
 * Allows the user to move a container and its contents to a new location
 */
public class MoveContainerDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(MoveContainerDialog.class);

    private final ContainerWrapper dstContainer;

    private final String title;

    @SuppressWarnings("nls")
    private String newLabel = StringUtil.EMPTY_STRING;

    @SuppressWarnings("nls")
    public MoveContainerDialog(Shell parent, ContainerWrapper srcContainer,
        ContainerWrapper dstContainer) {
        super(parent);
        Assert.isNotNull(srcContainer);
        this.dstContainer = dstContainer;

        // TR: dialog title
        title = i18n.tr("Move Container {0}", srcContainer.getLabel());
    }

    @Override
    protected String getDialogShellTitle() {
        return title;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // TR: dialog title are message
        return i18n.tr("Select the destination for this container.");
    }

    @Override
    protected String getTitleAreaTitle() {
        return title;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        BgcBaseText bbt =
            (BgcBaseText) createBoundWidgetWithLabel(
                contents,
                BgcBaseText.class,
                SWT.FILL,
                i18n.tr("Destination Address"),
                null,
                this,
                "newLabel",
                new StringLengthValidator(2,
                    // TR: validation error message
                    i18n.tr("Destination label must be another container (4 characters minimum).")));
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
