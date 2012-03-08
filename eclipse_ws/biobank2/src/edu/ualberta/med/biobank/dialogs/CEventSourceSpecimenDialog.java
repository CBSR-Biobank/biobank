package edu.ualberta.med.biobank.dialogs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeInfo;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.InventoryIdValidator;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class CEventSourceSpecimenDialog extends PagedDialog {

    private Specimen editedSpecimen;

    private ComboViewer specimenTypeComboViewer;

    private Map<String, SourceSpecimen> mapStudySourceSpecimen;

    private List<SpecimenType> allSpecimenTypes;

    private String currentTitle;

    private boolean dialogCreated = false;

    private DateTimeWidget timeDrawnWidget;
    private Label timeDrawnLabel;
    private Label quantityLabel;
    private BgcBaseText inventoryIdWidget;
    private BgcBaseText quantityText;
    private DoubleNumberValidator quantityTextValidator;
    private ComboViewer activityStatusComboViewer;

    private Date defaultTimeDrawn;

    private Specimen internalSpecimen;

    private List<String> inventoryIdExcludeList;

    public CEventSourceSpecimenDialog(Shell parent, Specimen spec,
        List<SourceSpecimen> studySourceSpecimen,
        List<SpecimenType> allSpecimenTypes,
        List<String> inventoryIdExcludeList, NewListener listener,
        Date defaultTimeDrawn) {
        super(parent, listener, spec == null);
        this.defaultTimeDrawn = defaultTimeDrawn;
        this.inventoryIdExcludeList = inventoryIdExcludeList;
        Assert.isNotNull(studySourceSpecimen);
        internalSpecimen = new Specimen();
        if (spec == null) {
            // FIXME ugly
            internalSpecimen.setActivityStatus(ActivityStatus.ACTIVE);
            internalSpecimen.setCreatedAt(defaultTimeDrawn);
        } else {
            internalSpecimen.setId(spec.getId());
            internalSpecimen.setSpecimenType(spec.getSpecimenType());
            internalSpecimen.setInventoryId(spec.getInventoryId());
            internalSpecimen.setQuantity(spec.getQuantity());
            internalSpecimen.setCreatedAt(spec.getCreatedAt());
            internalSpecimen.setComments(spec.getComments());
            internalSpecimen.setActivityStatus(spec.getActivityStatus());
            editedSpecimen = spec;
        }
        mapStudySourceSpecimen = new HashMap<String, SourceSpecimen>();
        for (SourceSpecimen ss : studySourceSpecimen) {
            mapStudySourceSpecimen.put(ss.getSpecimenType().getName(), ss);
        }
        this.allSpecimenTypes = allSpecimenTypes;
        if (addMode) {
            currentTitle = Messages.CEventSourceSpecimenDialog_title_add;
        } else {
            currentTitle = Messages.CEventSourceSpecimenDialog_title_edit;
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        if (addMode) {
            return Messages.CEventSourceSpecimenDialog_msg_add;
        }
        return Messages.CEventSourceSpecimenDialog_msg_edit;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(3, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        inventoryIdWidget =
            (BgcBaseText) createBoundWidgetWithLabel(
                contents,
                BgcBaseText.class,
                SWT.NONE,
                Messages.CEventSourceSpecimenDialog_field_inventoryId_label,
                null,
                internalSpecimen,
                SpecimenPeer.INVENTORY_ID.getName(),
                new InventoryIdValidator(
                    inventoryIdExcludeList,
                    Messages.CEventSourceSpecimenDialog_field_inventoryID_validator_msg,
                    internalSpecimen));
        GridData gd = (GridData) inventoryIdWidget.getLayoutData();
        gd.horizontalSpan = 2;

        addSpecimenTypeWidgets(contents);

        timeDrawnLabel =
            widgetCreator.createLabel(contents,
                Messages.CEventSourceSpecimenDialog_field_time_label);
        timeDrawnLabel
            .setToolTipText(Messages.CEventSourceSpecimenDialog_field_time_tooltip);
        timeDrawnWidget =
            createDateTimeWidget(
                contents,
                timeDrawnLabel,
                internalSpecimen.getCreatedAt(),
                internalSpecimen,
                SpecimenPeer.CREATED_AT.getName(),
                new NotNullValidator(
                    Messages.CEventSourceSpecimenDialog_field_time_validation_msg),
                SWT.DATE | SWT.TIME, null);
        gd = (GridData) timeDrawnWidget.getLayoutData();
        gd.horizontalSpan = 2;

        activityStatusComboViewer =
            widgetCreator.createComboViewer(contents,
                Messages.CEventSourceSpecimenDialog_label_activity,
                ActivityStatus.valuesList(),
                internalSpecimen.getActivityStatus(),
                Messages.CEventSourceSpecimenDialog_validation_activity,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        internalSpecimen
                            .setActivityStatus((ActivityStatus) selectedObject);
                    }
                }, new BiobankLabelProvider() {
                    @Override
                    public String getText(Object element) {
                        return ((ActivityStatus) element).getName();
                    }
                });
        gd = (GridData) activityStatusComboViewer.getControl().getLayoutData();
        gd.horizontalSpan = 2;

        BgcBaseText commentWidget =
            (BgcBaseText) createBoundWidgetWithLabel(contents,
                BgcBaseText.class, SWT.MULTI,
                Messages.CEventSourceSpecimenDialog_label_comments, null,
                internalSpecimen, SpecimenPeer.COMMENTS.getName(),
                null);
        gd = (GridData) commentWidget.getLayoutData();
        gd.horizontalSpan = 2;
        gd.widthHint = 400;

        quantityLabel =
            widgetCreator.createLabel(contents,
                Messages.CEventSourceSpecimenDialog_field_quantity_label);
        quantityLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        quantityTextValidator =
            new DoubleNumberValidator(
                Messages.CEventSourceSpecimenDialog_field_quantity_validation_msg);
        quantityText =
            (BgcBaseText) createBoundWidget(contents, BgcBaseText.class,
                SWT.BORDER, quantityLabel, new String[0], internalSpecimen,
                SpecimenPeer.QUANTITY.getName(), quantityTextValidator);
        gd = (GridData) quantityText.getLayoutData();
        gd.horizontalSpan = 2;

        dialogCreated = true;
        updateWidgetVisibilityAndValues();
    }

    private void addSpecimenTypeWidgets(Composite contents) {
        boolean useStudyOnlySourceSpecimens = true;
        SourceSpecimen ss = null;
        SpecimenTypeInfo typeInfo = new SpecimenTypeInfo();
        typeInfo.type = internalSpecimen.getSpecimenType();
        if (typeInfo.type != null) {
            ss = mapStudySourceSpecimen.get(typeInfo.type.getName());
        }
        if (ss == null && typeInfo.type != null
            && allSpecimenTypes.contains(typeInfo)) {
            useStudyOnlySourceSpecimens = false;
        }
        specimenTypeComboViewer =
            getWidgetCreator().createComboViewer(contents,
                Messages.CEventSourceSpecimenDialog_field_type_label,
                mapStudySourceSpecimen.values(), ss,
                Messages.CEventSourceSpecimenDialog_field_type_validation_msg,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        if (selectedObject instanceof SourceSpecimen) {
                            internalSpecimen
                                .setSpecimenType(((SourceSpecimen) selectedObject)
                                    .getSpecimenType());
                        } else {
                            internalSpecimen
                                .setSpecimenType(((SpecimenTypeInfo) selectedObject).type);
                        }
                        updateWidgetVisibilityAndValues();
                    }
                }, new BiobankLabelProvider() {
                    @Override
                    public String getText(Object element) {
                        if (element instanceof SourceSpecimen) {
                            return ((SourceSpecimen) element).getSpecimenType()
                                .getNameShort();
                        }
                        return ((SpecimenTypeInfo) element).type.getNameShort();
                    }
                });
        if (!useStudyOnlySourceSpecimens) {
            specimenTypeComboViewer.setInput(allSpecimenTypes);
            specimenTypeComboViewer.setSelection(new StructuredSelection(
                typeInfo));
        }

        final Button allSpecimenTypesCheckBox = new Button(contents, SWT.CHECK);
        allSpecimenTypesCheckBox
            .setText(Messages.CEventSourceSpecimenDialog_field_type_checkbox);
        allSpecimenTypesCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (allSpecimenTypesCheckBox.getSelection()) {
                    specimenTypeComboViewer.setInput(mapStudySourceSpecimen
                        .values());
                } else {
                    specimenTypeComboViewer.setInput(allSpecimenTypes);
                }
            }
        });
        allSpecimenTypesCheckBox.setSelection(useStudyOnlySourceSpecimens);
    }

    public void updateWidgetVisibilityAndValues() {
        if (!dialogCreated) return;

        SourceSpecimen ss = null;
        SpecimenType type = internalSpecimen.getSpecimenType();
        if (type != null) {
            ss = mapStudySourceSpecimen.get(type.getName());
        }
        boolean enableVolume =
            (type != null)
                && (ss == null || Boolean.TRUE.equals(ss
                    .getNeedOriginalVolume()));
        boolean isVolumeRequired =
            ss != null && Boolean.TRUE.equals(ss.getNeedOriginalVolume());

        if (defaultTimeDrawn != null) {
            timeDrawnWidget.setDate(defaultTimeDrawn);
        }
        quantityLabel.setVisible(enableVolume);
        quantityText.setVisible(enableVolume);
        quantityTextValidator.setAllowEmpty(!enableVolume || !isVolumeRequired);
        String originalText = quantityText.getText();
        quantityText.setText(originalText + "*"); //$NON-NLS-1$
        quantityText.setText(originalText);
        if (!enableVolume) {
            internalSpecimen.setQuantity(null);
        }
    }

    /**
     * Used only when editing
     */
    @Override
    protected void okPressed() {
        copy(editedSpecimen);
        super.okPressed();
    }

    @Override
    protected Specimen getNew() {
        return new Specimen();
    }

    @Override
    protected void resetFields() {
        inventoryIdWidget.setText(""); //$NON-NLS-1$
        inventoryIdWidget.setFocus();
        quantityText.setText(""); //$NON-NLS-1$
        timeDrawnWidget.setDate(null);
        specimenTypeComboViewer.getCombo().deselectAll();
        activityStatusComboViewer.setSelection(
            new StructuredSelection(ActivityStatus.ACTIVE));
        updateWidgetVisibilityAndValues();
    }

    @Override
    protected void copy(Object newModelObject) {
        Specimen spec = (Specimen) newModelObject;
        spec.setInventoryId(internalSpecimen.getInventoryId());
        spec.setSpecimenType(internalSpecimen.getSpecimenType());
        spec.setQuantity(internalSpecimen.getQuantity());
        spec.setCreatedAt(internalSpecimen.getCreatedAt());
        spec.setComments(internalSpecimen.getComments());
        spec.setActivityStatus(internalSpecimen.getActivityStatus());
        inventoryIdExcludeList.add(internalSpecimen.getInventoryId());
    }
}
