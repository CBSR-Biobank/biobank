package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

public class SpecimenTypeDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenTypeDialog.class);

    private final SpecimenTypeWrapper origSpecimenType;

    // this is the object that is modified via the bound widgets
    private final SpecimenTypeWrapper tmpSpecimenType;

    private final String message;

    private final String currentTitle;

    private MultiSelectWidget<SpecimenTypeWrapper> multiSelectChildren;

    private final List<SpecimenTypeWrapper> allOthersTypes;

    @SuppressWarnings("nls")
    public SpecimenTypeDialog(Shell parent, SpecimenTypeWrapper specimenType,
        String message, List<SpecimenTypeWrapper> allTypes) {
        super(parent);
        Assert.isNotNull(specimenType);
        this.origSpecimenType = specimenType;
        this.tmpSpecimenType = new SpecimenTypeWrapper(null);
        simpleCopyTo(origSpecimenType, tmpSpecimenType);
        this.message = message;

        currentTitle = (specimenType.getName() == null
            // TR: dialog title
            ? i18n.tr("Add Specimen Type")
            // TR: dialog title
            : i18n.tr("Edit Specimen Type"));

        allOthersTypes = new ArrayList<SpecimenTypeWrapper>(allTypes);
        allOthersTypes.remove(specimenType);
    }

    private void simpleCopyTo(SpecimenTypeWrapper src, SpecimenTypeWrapper dest) {
        dest.setName(src.getName());
        dest.setNameShort(src.getNameShort());
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return message;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(content, BgcBaseText.class, SWT.BORDER,
            HasName.Property.NAME.toString(),
            null, tmpSpecimenType,
            SpecimenTypePeer.NAME.getName(), new NonEmptyStringValidator(
                // TR: validation error message
                i18n.tr("Specimen type must have a name.")));

        createBoundWidgetWithLabel(content, BgcBaseText.class, SWT.BORDER,
            HasName.Property.NAME_SHORT.toString(),
            null, tmpSpecimenType,
            SpecimenTypePeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                // TR: validation error message
                i18n.tr("Specimen type must have a short name.")));

        multiSelectChildren = new MultiSelectWidget<SpecimenTypeWrapper>(
            content, SWT.NONE,
            // TR: combo box label
            i18n.tr("Available types"),
            // TR: combo box label
            i18n.tr("Child types"), 300) {

            @Override
            protected String getTextForObject(SpecimenTypeWrapper nodeObject) {
                return nodeObject.getName();
            }
        };
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.widthHint = 700;
        gd.horizontalSpan = 2;
        multiSelectChildren.setLayoutData(gd);

        // use origSpecimenType on purpose. tmpSpecimenType doesn't have the
        // children.
        multiSelectChildren.setSelections(allOthersTypes,
            origSpecimenType.getChildSpecimenTypeCollection(false));
    }

    @Override
    protected void okPressed() {
        simpleCopyTo(tmpSpecimenType, origSpecimenType);
        origSpecimenType.addToChildSpecimenTypeCollection(multiSelectChildren
            .getAddedToSelection());
        origSpecimenType
            .removeFromChildSpecimenTypeCollection(multiSelectChildren
                .getRemovedFromSelection());
        super.okPressed();
    }
}
