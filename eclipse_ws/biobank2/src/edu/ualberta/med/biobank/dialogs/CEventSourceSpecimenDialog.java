package edu.ualberta.med.biobank.dialogs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.InventoryIdValidator;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CEventSourceSpecimenDialog extends BiobankDialog {

    private SpecimenWrapper editedSpecimen;

    private SpecimenWrapper internalSpecimen;

    private ComboViewer specimenTypeComboViewer;

    private Map<String, SourceSpecimenWrapper> mapStudySourceSpecimen;

    private List<SpecimenTypeWrapper> allSpecimenTypes;

    private List<ActivityStatusWrapper> allActivityStatus;

    private boolean addMode;

    private String currentTitle;

    private boolean dialogCreated = false;

    private DateTimeWidget timeDrawnWidget;
    private Label timeDrawnLabel;
    private Label quantityLabel;
    private BiobankText inventoryIdWidget;
    private BiobankText quantityText;
    private DoubleNumberValidator quantityTextValidator;
    private ComboViewer activityStatusComboViewer;

    private NewSpecimenListener newSpecimenListener;

    private ActivityStatusWrapper activeActivityStatus;

    private Date defaultTimeDrawn;

    public CEventSourceSpecimenDialog(Shell parent, SpecimenWrapper specimen,
        List<SourceSpecimenWrapper> studySourceSpecimen,
        List<SpecimenTypeWrapper> allSpecimenTypes,
        NewSpecimenListener listener, Date defaultTimeDrawn) {
        super(parent);
        this.defaultTimeDrawn = defaultTimeDrawn;
        try {
            activeActivityStatus = ActivityStatusWrapper
                .getActiveActivityStatus(SessionManager.getAppService());
        } catch (Exception e) {
            // ok if don't find default
        }
        Assert.isNotNull(studySourceSpecimen);
        newSpecimenListener = listener;
        internalSpecimen = new SpecimenWrapper(SessionManager.getAppService());
        if (specimen == null) {
            addMode = true;
            internalSpecimen.setActivityStatus(activeActivityStatus);
            internalSpecimen.setCreatedAt(defaultTimeDrawn);
        } else {
            internalSpecimen.setSpecimenType(specimen.getSpecimenType());
            internalSpecimen.setInventoryId(specimen.getInventoryId());
            internalSpecimen.setQuantity(specimen.getQuantity());
            internalSpecimen.setCreatedAt(specimen.getCreatedAt());
            internalSpecimen.setComment(specimen.getComment());
            internalSpecimen.setActivityStatus(specimen.getActivityStatus());
            editedSpecimen = specimen;
            addMode = false;
        }
        mapStudySourceSpecimen = new HashMap<String, SourceSpecimenWrapper>();
        for (SourceSpecimenWrapper ssw : studySourceSpecimen) {
            mapStudySourceSpecimen.put(ssw.getSpecimenType().getName(), ssw);
        }
        this.allSpecimenTypes = allSpecimenTypes;
        if (addMode) {
            currentTitle = Messages
                .getString("CEventSourceSpecimenDialog.title.add"); //$NON-NLS-1$
        } else {
            currentTitle = Messages
                .getString("CEventSourceSpecimenDialog.title.edit"); //$NON-NLS-1$
        }
        try {
            allActivityStatus = ActivityStatusWrapper
                .getAllActivityStatuses(SessionManager.getAppService());
        } catch (ApplicationException e) {
            BiobankPlugin.openAsyncError(Messages
                .getString("CEventSourceSpecimenDialog.activity.error.msg"), e); //$NON-NLS-1$
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        if (addMode) {
            return Messages.getString("CEventSourceSpecimenDialog.msg.add"); //$NON-NLS-1$
        } else {
            return Messages.getString("CEventSourceSpecimenDialog.msg.edit"); //$NON-NLS-1$
        }
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected Image getTitleAreaImage() {
        // FIXME should use another icon
        return BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_COMPUTER_KEY);
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(3, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        inventoryIdWidget = (BiobankText) createBoundWidgetWithLabel(
            contents,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("SourceSpecimen.field.inventoryId.label"), //$NON-NLS-1$
            null,
            internalSpecimen,
            SpecimenPeer.INVENTORY_ID.getName(),
            new InventoryIdValidator(
                Messages
                    .getString("CEventSourceSpecimenDialog.field.inventoryID.validator.msg"))); //$NON-NLS-1$
        GridData gd = (GridData) inventoryIdWidget.getLayoutData();
        gd.horizontalSpan = 2;

        addSpecimenTypeWidgets(contents);

        timeDrawnLabel = widgetCreator.createLabel(contents,
            Messages.getString("CEventSourceSpecimenDialog.field.time.label")); //$NON-NLS-1$
        timeDrawnLabel.setToolTipText(Messages
            .getString("CEventSourceSpecimenDialog.field.time.tooltip")); //$NON-NLS-1$
        timeDrawnWidget = createDateTimeWidget(
            contents,
            timeDrawnLabel,
            internalSpecimen.getCreatedAt(),
            internalSpecimen,
            SpecimenPeer.CREATED_AT.getName(),
            new NotNullValidator(
                Messages
                    .getString("CEventSourceSpecimenDialog.field.time.validation.msg")), //$NON-NLS-1$
            SWT.DATE | SWT.TIME, null);
        gd = (GridData) timeDrawnWidget.getLayoutData();
        gd.horizontalSpan = 2;

        activityStatusComboViewer = widgetCreator.createComboViewer(contents,
            Messages.getString("label.activity"), allActivityStatus, //$NON-NLS-1$
            internalSpecimen.getActivityStatus(),
            Messages.getString("validation.activity"), //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    internalSpecimen
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });
        gd = (GridData) activityStatusComboViewer.getControl().getLayoutData();
        gd.horizontalSpan = 2;

        BiobankText commentWidget = (BiobankText) createBoundWidgetWithLabel(
            contents, BiobankText.class, SWT.NONE,
            Messages.getString("label.comments"), null, internalSpecimen, //$NON-NLS-1$
            SpecimenPeer.COMMENT.getName(), null);
        gd = (GridData) commentWidget.getLayoutData();
        gd.horizontalSpan = 2;

        quantityLabel = widgetCreator.createLabel(contents, Messages
            .getString("CEventSourceSpecimenDialog.field.quantity.label")); //$NON-NLS-1$
        quantityLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        quantityTextValidator = new DoubleNumberValidator(
            Messages
                .getString("CEventSourceSpecimenDialog.field.quantity.validation.msg")); //$NON-NLS-1$
        quantityText = (BiobankText) createBoundWidget(contents,
            BiobankText.class, SWT.BORDER, quantityLabel, new String[0],
            internalSpecimen, SpecimenPeer.QUANTITY.getName(),
            quantityTextValidator);
        gd = (GridData) quantityText.getLayoutData();
        gd.horizontalSpan = 2;

        dialogCreated = true;
        updateWidgetVisibilityAndValues();
    }

    private void addSpecimenTypeWidgets(Composite contents) {
        boolean useStudyOnlySourceSpecimens = true;
        SourceSpecimenWrapper ssw = null;
        SpecimenTypeWrapper type = internalSpecimen.getSpecimenType();
        if (type != null) {
            ssw = mapStudySourceSpecimen.get(type.getName());
        }
        if (ssw == null && type != null && allSpecimenTypes.contains(type)) {
            useStudyOnlySourceSpecimens = false;
        }
        specimenTypeComboViewer = getWidgetCreator()
            .createComboViewer(
                contents,
                Messages
                    .getString("CEventSourceSpecimenDialog.field.type.label"), //$NON-NLS-1$
                mapStudySourceSpecimen.values(),
                ssw,
                Messages
                    .getString("CEventSourceSpecimenDialog.field.type.validation.msg"), //$NON-NLS-1$
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        if (selectedObject instanceof SourceSpecimenWrapper) {
                            internalSpecimen
                                .setSpecimenType(((SourceSpecimenWrapper) selectedObject)
                                    .getSpecimenType());
                        } else {
                            internalSpecimen
                                .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                        }
                        updateWidgetVisibilityAndValues();
                    }
                });
        if (!useStudyOnlySourceSpecimens) {
            specimenTypeComboViewer.setInput(allSpecimenTypes);
            specimenTypeComboViewer.setSelection(new StructuredSelection(type));
        }

        final Button allSpecimenTypesCheckBox = new Button(contents, SWT.CHECK);
        allSpecimenTypesCheckBox.setText(Messages
            .getString("CEventSourceSpecimenDialog.field.type.checkbox")); //$NON-NLS-1$
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
        if (!dialogCreated)
            return;

        SourceSpecimenWrapper ssw = null;
        SpecimenTypeWrapper type = internalSpecimen.getSpecimenType();
        if (type != null) {
            ssw = mapStudySourceSpecimen.get(type.getName());
        }
        boolean enableTimeDrawn = (type != null)
            && (ssw == null || Boolean.TRUE.equals(ssw.getNeedTimeDrawn()));
        boolean enableVolume = (type != null)
            && (ssw == null || Boolean.TRUE.equals(ssw.getNeedOriginalVolume()));
        boolean isVolumeRequired = ssw != null
            && Boolean.TRUE.equals(ssw.getNeedOriginalVolume());

        if (!enableTimeDrawn && defaultTimeDrawn != null) {
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

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        if (addMode) {
            createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
            createButton(parent, IDialogConstants.FINISH_ID,
                IDialogConstants.FINISH_LABEL, false);
            createButton(parent, IDialogConstants.NEXT_ID,
                IDialogConstants.NEXT_LABEL, true);
        } else {
            super.createButtonsForButtonBar(parent);
        }
    }

    @Override
    protected void setOkButtonEnabled(boolean enabled) {
        if (addMode) {
            Button nextButton = getButton(IDialogConstants.NEXT_ID);
            Button finishButton = getButton(IDialogConstants.FINISH_ID);
            if (nextButton != null && !nextButton.isDisposed()
                && finishButton != null && !finishButton.isDisposed()) {
                nextButton.setEnabled(enabled);
                finishButton.setEnabled(enabled);
            } else {
                okButtonEnabled = enabled;
            }
        } else {
            super.setOkButtonEnabled(enabled);
        }
    }

    /**
     * Used only when editing
     */
    @Override
    protected void okPressed() {
        editedSpecimen.setInventoryId(internalSpecimen.getInventoryId());
        editedSpecimen.setSpecimenType(internalSpecimen.getSpecimenType());
        editedSpecimen.setQuantity(internalSpecimen.getQuantity());
        editedSpecimen.setCreatedAt(internalSpecimen.getCreatedAt());
        editedSpecimen.setComment(internalSpecimen.getComment());
        editedSpecimen.setActivityStatus(internalSpecimen.getActivityStatus());
        super.okPressed();
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (addMode) {
            if (IDialogConstants.CANCEL_ID == buttonId)
                super.buttonPressed(buttonId);
            else if (IDialogConstants.FINISH_ID == buttonId) {
                Button nextButton = getButton(IDialogConstants.NEXT_ID);
                if (nextButton.isEnabled()) {
                    addNewSpecimen();
                }
                setReturnCode(OK);
                close();
            } else if (IDialogConstants.NEXT_ID == buttonId) {
                addNewSpecimen();
            }
        } else {
            super.buttonPressed(buttonId);
        }
    }

    private void addNewSpecimen() {
        try {
            SpecimenWrapper newSpecimen = new SpecimenWrapper(
                SessionManager.getAppService());
            newSpecimen.initObjectWith(internalSpecimen);
            if (newSpecimenListener != null)
                newSpecimenListener.newSpecimenAdded(newSpecimen);

            // then reset fields
            internalSpecimen.reset();
            inventoryIdWidget.setText(""); //$NON-NLS-1$
            inventoryIdWidget.setFocus();
            quantityText.setText(""); //$NON-NLS-1$
            timeDrawnWidget.setDate(null);
            quantityText.setText(""); //$NON-NLS-1$
            specimenTypeComboViewer.getCombo().deselectAll();
            activityStatusComboViewer.getCombo().deselectAll();
            activityStatusComboViewer.setSelection(new StructuredSelection(
                activeActivityStatus));
            updateWidgetVisibilityAndValues();
        } catch (Exception e) {
            BiobankPlugin
                .openAsyncError(
                    Messages
                        .getString("CEventSourceSpecimenDialog.specimen.dialog.error.msg"), //$NON-NLS-1$
                    e);
        }
    }

    public void setNewSpecimenListener(NewSpecimenListener newListener) {
        this.newSpecimenListener = newListener;
    }

    public static abstract class NewSpecimenListener {
        public abstract void newSpecimenAdded(SpecimenWrapper spec);
    }
}
