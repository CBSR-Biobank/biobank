package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

public class SpecimenTypeDialog extends BgcBaseDialog {

    private SpecimenTypeWrapper origSpecimenType;

    // this is the object that is modified via the bound widgets
    private SpecimenTypeWrapper tmpSpecimenType;

    private String message;

    private String currentTitle;

    private MultiSelectWidget multiSelectChildren;

    private Map<Integer, SpecimenTypeWrapper> allOthersTypesObjects;
    private LinkedHashMap<Integer, String> allOthersTypesStrings;

    public SpecimenTypeDialog(Shell parent, SpecimenTypeWrapper specimenType,
        String message, List<SpecimenTypeWrapper> allTypes) {
        super(parent);
        Assert.isNotNull(specimenType);
        this.origSpecimenType = specimenType;
        this.tmpSpecimenType = new SpecimenTypeWrapper(null);
        simpleCopyTo(origSpecimenType, tmpSpecimenType);
        this.message = message;

        allOthersTypesStrings = new LinkedHashMap<Integer, String>();
        allOthersTypesObjects = new HashMap<Integer, SpecimenTypeWrapper>();
        for (SpecimenTypeWrapper type : allTypes) {
            if (!type.equals(specimenType)) {
                Integer id = type.getId();
                allOthersTypesStrings.put(id, type.getName());
                allOthersTypesObjects.put(id, type);
            }
        }
        currentTitle = (specimenType.getName() == null ? Messages.SpecimenTypeDialog_title_add
            : Messages.SpecimenTypeDialog_title_edit);
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

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(content, BgcBaseText.class, SWT.BORDER,
            Messages.SpecimenTypeDialog_name_label, null, tmpSpecimenType,
            SpecimenTypePeer.NAME.getName(), new NonEmptyStringValidator(
                Messages.SpecimenTypeDialog_name_validation_msg));

        createBoundWidgetWithLabel(content, BgcBaseText.class, SWT.BORDER,
            Messages.SpecimenTypeDialog_nameShort_label, null, tmpSpecimenType,
            SpecimenTypePeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                Messages.SpecimenTypeDialog_shortName_validation_msg));

        multiSelectChildren = new MultiSelectWidget(content, SWT.NONE,
            Messages.SpecimenTypeDialog_availableTypes_label,
            Messages.SpecimenTypeDialog_selectedTypes_label, 300);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.widthHint = 700;
        gd.horizontalSpan = 2;
        multiSelectChildren.setLayoutData(gd);

        // use origSpecimenType on purpose. tmpSpecimenType doesn't have the
        // children.
        List<Integer> selectedTypes = new ArrayList<Integer>();
        for (SpecimenTypeWrapper child : origSpecimenType
            .getChildSpecimenTypeCollection(false)) {
            selectedTypes.add(child.getId());
        }
        multiSelectChildren.setSelections(allOthersTypesStrings, selectedTypes);
    }

    @Override
    protected void okPressed() {
        simpleCopyTo(tmpSpecimenType, origSpecimenType);
        List<SpecimenTypeWrapper> addedTypes = new ArrayList<SpecimenTypeWrapper>();
        for (Integer addedId : multiSelectChildren.getAddedToSelection()) {
            addedTypes.add(allOthersTypesObjects.get(addedId));
        }
        origSpecimenType.addToChildSpecimenTypeCollection(addedTypes);
        List<SpecimenTypeWrapper> removedTypes = new ArrayList<SpecimenTypeWrapper>();
        for (Integer removedId : multiSelectChildren.getRemovedToSelection()) {
            removedTypes.add(allOthersTypesObjects.get(removedId));
        }
        origSpecimenType.removeFromChildSpecimenTypeCollection(removedTypes);
        super.okPressed();
    }
}
