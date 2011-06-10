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

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

public class SpecimenTypeDialog extends BiobankDialog {

    private static final String TITLE = "Specimen Type ";

    private static final String MSG_NO_ST_NAME = "Specimen type must have a name.";
    private static final String MSG_NO_ST_SNAME = "Specimen type must have a short name.";

    // this is the object that is modified via the bound widgets
    private SpecimenTypeWrapper specimenType;

    private String message;

    private String currentTitle;

    private MultiSelectWidget multiSelectChildren;

    private Map<Integer, SpecimenTypeWrapper> allOthersTypesObjects;
    private LinkedHashMap<Integer, String> allOthersTypesStrings;

    public SpecimenTypeDialog(Shell parent, SpecimenTypeWrapper specimenType,
        String message, List<SpecimenTypeWrapper> allTypes) {
        super(parent);
        Assert.isNotNull(specimenType);
        this.specimenType = specimenType;
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
        currentTitle = ((specimenType.getName() == null) ? "Add " : "Edit ")
            + TITLE;
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

        createBoundWidgetWithLabel(content, BiobankText.class, SWT.BORDER,
            "Name", null, specimenType, "name", new NonEmptyStringValidator(
                MSG_NO_ST_NAME));

        createBoundWidgetWithLabel(content, BiobankText.class, SWT.BORDER,
            "Short Name", null, specimenType, "nameShort",
            new NonEmptyStringValidator(MSG_NO_ST_SNAME));

        multiSelectChildren = new MultiSelectWidget(content, SWT.NONE,
            "Available types", "Child types", 300);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.widthHint = 700;
        gd.horizontalSpan = 2;
        multiSelectChildren.setLayoutData(gd);

        List<Integer> selectedTypes = new ArrayList<Integer>();
        for (SpecimenTypeWrapper child : specimenType
            .getChildSpecimenTypeCollection(false)) {
            selectedTypes.add(child.getId());
        }
        multiSelectChildren.setSelections(allOthersTypesStrings, selectedTypes);
    }

    @Override
    protected void okPressed() {
        List<SpecimenTypeWrapper> addedTypes = new ArrayList<SpecimenTypeWrapper>();
        for (Integer addedId : multiSelectChildren.getAddedToSelection()) {
            addedTypes.add(allOthersTypesObjects.get(addedId));
        }
        specimenType.addToChildSpecimenTypeCollection(addedTypes);
        List<SpecimenTypeWrapper> removedTypes = new ArrayList<SpecimenTypeWrapper>();
        for (Integer removedId : multiSelectChildren.getRemovedToSelection()) {
            removedTypes.add(allOthersTypesObjects.get(removedId));
        }
        specimenType.removeFromChildSpecimenTypeCollection(removedTypes);
        super.okPressed();
    }
}
