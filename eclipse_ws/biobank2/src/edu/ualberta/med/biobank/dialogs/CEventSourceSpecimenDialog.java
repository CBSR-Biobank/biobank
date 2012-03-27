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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
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
import edu.ualberta.med.biobank.widgets.infotables.entry.CommentedSpecimenInfo;

public class CEventSourceSpecimenDialog extends PagedDialog {

    private CommentedSpecimenInfo editedSpecimen;

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

    private CommentedSpecimenInfo internalSpecimen;

    private List<String> inventoryIdExcludeList;

    private CommentWrapper commentWrapper = new CommentWrapper(
        SessionManager.getAppService());

    private BgcBaseText commentWidget;

    public CEventSourceSpecimenDialog(Shell parent, CommentedSpecimenInfo spec,
        List<SourceSpecimen> studySourceSpecimen,
        List<SpecimenType> allSpecimenTypes,
        List<String> inventoryIdExcludeList, NewListener listener,
        Date defaultTimeDrawn) {
        super(parent, listener, spec == null);
        this.defaultTimeDrawn = defaultTimeDrawn;
        this.inventoryIdExcludeList = inventoryIdExcludeList;
        Assert.isNotNull(studySourceSpecimen);
        internalSpecimen = new CommentedSpecimenInfo(new SpecimenInfo());
        internalSpecimen.specimen = new Specimen();
        if (spec == null) {
            // FIXME ugly
            internalSpecimen.specimen.setActivityStatus(ActivityStatus.ACTIVE);
            internalSpecimen.specimen.setCreatedAt(defaultTimeDrawn);
        } else {
            internalSpecimen.specimen.setId(spec.specimen.getId());
            internalSpecimen.specimen.setSpecimenType(spec.specimen
                .getSpecimenType());
            internalSpecimen.specimen.setInventoryId(spec.specimen
                .getInventoryId());
            internalSpecimen.specimen.setQuantity(spec.specimen.getQuantity());
            internalSpecimen.specimen
                .setCreatedAt(spec.specimen.getCreatedAt());
            internalSpecimen.specimen.setActivityStatus(spec.specimen
                .getActivityStatus());
            // comments is special
            internalSpecimen.comments = spec.comments;

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
                internalSpecimen.specimen,
                SpecimenPeer.INVENTORY_ID.getName(),
                new InventoryIdValidator(
                    inventoryIdExcludeList,
                    Messages.CEventSourceSpecimenDialog_field_inventoryID_validator_msg,
                    internalSpecimen.specimen));
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
                internalSpecimen.specimen.getCreatedAt(),
                internalSpecimen.specimen,
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
                internalSpecimen.specimen.getActivityStatus(),
                Messages.CEventSourceSpecimenDialog_validation_activity,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        internalSpecimen.specimen
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

        createCommentSection(contents);

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
                SWT.BORDER, quantityLabel, new String[0],
                internalSpecimen.specimen,
                SpecimenPeer.QUANTITY.getName(), quantityTextValidator);
        gd = (GridData) quantityText.getLayoutData();
        gd.horizontalSpan = 2;

        dialogCreated = true;
        updateWidgetVisibilityAndValues();
    }

    private void createCommentSection(Composite contents) {
        commentWidget =
            (BgcBaseText) createBoundWidgetWithLabel(contents,
                BgcBaseText.class, SWT.MULTI,
                Messages.CEventSourceSpecimenDialog_label_comments, null,
                commentWrapper, "message", //$NON-NLS-1$
                null);
        GridData gd = new GridData();
        gd = (GridData) commentWidget.getLayoutData();
        gd.horizontalSpan = 2;
        gd.widthHint = 400;
        commentWidget.setLayoutData(gd);
    }

    private void addSpecimenTypeWidgets(Composite contents) {
        boolean useStudyOnlySourceSpecimens = true;
        SourceSpecimen ss = null;
        SpecimenType type = internalSpecimen.specimen.getSpecimenType();
        if (type != null) {
            ss = mapStudySourceSpecimen.get(type.getName());
        }
        if (ss == null && type != null
            && allSpecimenTypes.contains(type)) {
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
                            .specimen
                                .setSpecimenType(((SourceSpecimen) selectedObject)
                                    .getSpecimenType());
                        } else {
                            internalSpecimen
                            .specimen
                                .setSpecimenType(((SpecimenType) selectedObject));
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
                        return ((SpecimenType) element).getNameShort();
                    }
                });
        if (!useStudyOnlySourceSpecimens) {
            specimenTypeComboViewer.setInput(allSpecimenTypes);
            specimenTypeComboViewer.setSelection(new StructuredSelection(
                type));
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
        SpecimenType type = internalSpecimen.specimen.getSpecimenType();
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
            internalSpecimen.specimen.setQuantity(null);
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
    protected CommentedSpecimenInfo getNew() {
        return new CommentedSpecimenInfo(new SpecimenInfo());
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
        commentWidget.setText(""); //$NON-NLS-1$
        updateWidgetVisibilityAndValues();
    }

    @Override
    protected void copy(Object newModelObject) {
        CommentedSpecimenInfo spec =
            (CommentedSpecimenInfo) newModelObject;
        spec.specimen
            .setInventoryId(internalSpecimen.specimen.getInventoryId());
        spec.specimen.setSpecimenType(internalSpecimen.specimen
            .getSpecimenType());
        spec.specimen.setQuantity(internalSpecimen.specimen.getQuantity());
        spec.specimen.setCreatedAt(internalSpecimen.specimen.getCreatedAt());
        if (commentWrapper.getMessage() != null
            && !commentWrapper.getMessage().equals("")) { //$NON-NLS-1$
            spec.comments.add(commentWrapper.getMessage());
        }
        if (spec.comments.size() > 0)
            spec.comment = Messages.CEventSourceSpecimenDialog_yes;
        else
            spec.comment = Messages.CEventSourceSpecimenDialog_no;
        spec.specimen.setActivityStatus(internalSpecimen.specimen
            .getActivityStatus());
        inventoryIdExcludeList.add(internalSpecimen.specimen.getInventoryId());
    }
}
