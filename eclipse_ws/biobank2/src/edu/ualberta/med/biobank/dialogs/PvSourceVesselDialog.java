package edu.ualberta.med.biobank.dialogs;

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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.infotables.entry.PvSourceVesselEntryInfoTable;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class PvSourceVesselDialog extends BiobankDialog {

    private static final String TITLE = "Source Vessel";

    private PvSourceVesselWrapper editedSourceVessel;

    private PvSourceVesselWrapper internalSourceVessel;

    private ComboViewer sourceVesselsComboViewer;

    private Map<String, SourceSpecimenWrapper> mapStudySourceVessel;

    private BiobankText quantityText;

    private DateTimeWidget timeDrawnWidget;

    private BiobankText volumeText;

    private List<SourceVesselWrapper> allSourceVessels;

    private Label timeDrawnLabel;

    private Label volumeLabel;

    private PvSourceVesselEntryInfoTable infotable;

    private ProcessingEventWrapper patientVisit;

    private boolean addMode;

    private String currentTitle;

    private boolean dialogCreated = false;

    private DoubleNumberValidator volumeTextValidator;

    public PvSourceVesselDialog(Shell parent,
        PvSourceVesselWrapper pvSourceVessel,
        List<SourceSpecimenWrapper> studySourceVessels,
        List<SourceVesselWrapper> allSourceVessels,
        PvSourceVesselEntryInfoTable infoTable) {
        super(parent);
        Assert.isNotNull(studySourceVessels);
        internalSourceVessel = new PvSourceVesselWrapper(
            SessionManager.getAppService());
        if (pvSourceVessel == null) {
            addMode = true;
        } else {
            internalSourceVessel.setSourceVessel(pvSourceVessel
                .getSourceVessel());
            internalSourceVessel.setQuantity(pvSourceVessel.getQuantity());
            internalSourceVessel.setVolume(pvSourceVessel.getVolume());
            internalSourceVessel.setTimeDrawn(pvSourceVessel.getTimeDrawn());
            editedSourceVessel = pvSourceVessel;
            addMode = false;
        }
        mapStudySourceVessel = new HashMap<String, SourceSpecimenWrapper>();
        for (SourceSpecimenWrapper ssv : studySourceVessels) {
            mapStudySourceVessel.put(ssv.getSourceVessel().getName(), ssv);
        }
        this.allSourceVessels = allSourceVessels;
        this.infotable = infoTable;
        if (addMode) {
            currentTitle = "Add " + TITLE;
        } else {
            currentTitle = "Edit " + TITLE;
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        if (addMode) {
            return "Add a source vessel to a patient visit";
        } else {
            return "Edit a source vessel in a patient visit";
        }
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected Image getTitleAreaImage() {
        // FIXME should use another icon
        return BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_COMPUTER_KEY);
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(3, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        boolean useStudyOnlySourceVessels = true;
        SourceSpecimenWrapper ssv = null;
        SourceVesselWrapper currentSourceVessel = internalSourceVessel
            .getSourceVessel();
        if (currentSourceVessel != null) {
            ssv = mapStudySourceVessel.get(currentSourceVessel.getName());
        }
        if (ssv == null && currentSourceVessel != null
            && allSourceVessels.contains(currentSourceVessel)) {
            useStudyOnlySourceVessels = false;
        }
        sourceVesselsComboViewer = getWidgetCreator().createComboViewer(
            contents, "Source Vessel", mapStudySourceVessel.values(), ssv,
            "A source vessel should be selected", new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (selectedObject instanceof SourceSpecimenWrapper) {
                        internalSourceVessel
                            .setSourceVessel(((SourceSpecimenWrapper) selectedObject)
                                .getSourceVessel());
                    } else {
                        internalSourceVessel
                            .setSourceVessel((SourceVesselWrapper) selectedObject);
                    }
                    updateWidgetVisibilityAndValues();
                }
            });
        if (!useStudyOnlySourceVessels) {
            sourceVesselsComboViewer.setInput(allSourceVessels);
            sourceVesselsComboViewer.setSelection(new StructuredSelection(
                currentSourceVessel));
        }

        final Button allSourceVesselCheckBox = new Button(contents, SWT.CHECK);
        allSourceVesselCheckBox.setText("Show only study source vessels");
        allSourceVesselCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (allSourceVesselCheckBox.getSelection()) {
                    sourceVesselsComboViewer.setInput(mapStudySourceVessel
                        .values());
                } else {
                    sourceVesselsComboViewer.setInput(allSourceVessels);
                }
            }
        });
        allSourceVesselCheckBox.setSelection(useStudyOnlySourceVessels);

        quantityText = (BiobankText) createBoundWidgetWithLabel(contents,
            BiobankText.class, SWT.BORDER, "Quantity", new String[0],
            internalSourceVessel, "quantity", new IntegerNumberValidator(
                "quantity should be a whole number", false));
        GridData gd = (GridData) quantityText.getLayoutData();
        gd.horizontalSpan = 2;

        timeDrawnLabel = widgetCreator.createLabel(contents, "Time drawn");
        timeDrawnWidget = createDateTimeWidget(contents, timeDrawnLabel,
            internalSourceVessel.getTimeDrawn(), internalSourceVessel,
            "timeDrawn", null, SWT.TIME, null);
        gd = (GridData) timeDrawnWidget.getLayoutData();
        gd.horizontalSpan = 2;

        volumeLabel = widgetCreator.createLabel(contents, "Volume (ml)");
        volumeLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        volumeTextValidator = new DoubleNumberValidator("Volume is required.");
        volumeText = (BiobankText) createBoundWidget(contents,
            BiobankText.class, SWT.BORDER, volumeLabel, new String[0],
            internalSourceVessel, "volume", volumeTextValidator);
        gd = (GridData) volumeText.getLayoutData();
        gd.horizontalSpan = 2;

        dialogCreated = true;
        updateWidgetVisibilityAndValues();
    }

    public void updateWidgetVisibilityAndValues() {
        if (!dialogCreated)
            return;

        SourceSpecimenWrapper ssv = null;
        SourceVesselWrapper currentSourceVessel = internalSourceVessel
            .getSourceVessel();
        if (currentSourceVessel != null) {
            ssv = mapStudySourceVessel.get(currentSourceVessel.getName());
        }
        boolean enableTimeDrawn = (currentSourceVessel != null)
            && (ssv == null || Boolean.TRUE.equals(ssv.getNeedTimeDrawn()));
        boolean enableVolume = (currentSourceVessel != null)
            && (ssv == null || Boolean.TRUE.equals(ssv.getNeedOriginalVolume()));
        boolean isVolumeRequired = ssv != null
            && Boolean.TRUE.equals(ssv.getNeedOriginalVolume());

        timeDrawnLabel.setVisible(enableTimeDrawn);
        timeDrawnWidget.setVisible(enableTimeDrawn);
        volumeLabel.setVisible(enableVolume);
        volumeText.setVisible(enableVolume);
        if (!enableTimeDrawn) {
            internalSourceVessel.setTimeDrawn(null);
        }
        volumeTextValidator.setAllowEmpty(!enableVolume || !isVolumeRequired);
        String originalText = volumeText.getText();
        volumeText.setText(originalText + "*");
        volumeText.setText(originalText);
        if (!enableVolume) {
            internalSourceVessel.setVolume(null);
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
        editedSourceVessel.setSourceVessel(internalSourceVessel
            .getSourceVessel());
        editedSourceVessel.setQuantity(internalSourceVessel.getQuantity());
        editedSourceVessel.setVolume(internalSourceVessel.getVolume());
        editedSourceVessel.setTimeDrawn(internalSourceVessel.getTimeDrawn());
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
                    addNewPvSourceVessel();
                }
                setReturnCode(OK);
                close();
            } else if (IDialogConstants.NEXT_ID == buttonId) {
                addNewPvSourceVessel();
            }
        } else {
            super.buttonPressed(buttonId);
        }
    }

    private void addNewPvSourceVessel() {
        try {
            PvSourceVesselWrapper newPvSourceVessel = new PvSourceVesselWrapper(
                SessionManager.getAppService());
            newPvSourceVessel.initObjectWith(internalSourceVessel);
            newPvSourceVessel.setPatientVisit(patientVisit);
            infotable.addPvSourceVessel(newPvSourceVessel);
            internalSourceVessel.reset();
            quantityText.setText("");
            timeDrawnWidget.setDate(null);
            volumeText.setText("");
            sourceVesselsComboViewer.getCombo().deselectAll();
            sourceVesselsComboViewer.getCombo().setFocus();
            updateWidgetVisibilityAndValues();
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error adding the source vessel", e);
        }
    }

    public void setPatientVisit(ProcessingEventWrapper patientVisit) {
        this.patientVisit = patientVisit;
    }
}
