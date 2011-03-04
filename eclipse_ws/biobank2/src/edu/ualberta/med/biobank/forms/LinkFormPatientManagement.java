package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class LinkFormPatientManagement {

    private boolean patientNumberTextModified = false;
    protected BiobankText patientNumberText;
    protected ComboViewer viewerCollectionEvents;
    private Button cEventListCheck;

    // currentPatient
    protected PatientWrapper currentPatient;

    private WidgetCreator widgetCreator;

    private AbstractSpecimenAdminForm aliquotAdminForm;

    private PatientTextCallback patientTextCallback;
    private Label patientLabel;
    private NonEmptyStringValidator patientValidator;
    private Label cEventTextLabel;
    private BiobankText cEventText;
    private Label cEventComboLabel;
    protected CollectionEventWrapper currentCEventSelected;
    private BiobankText worksheetText;
    protected String currentWorksheetNumber;
    protected boolean worksheetNumberModified;
    private List<SpecimenWrapper> cEventSpecimensForWorksheet;

    public LinkFormPatientManagement(WidgetCreator widgetCreator,
        AbstractSpecimenAdminForm aliquotAdminForm) {
        this.widgetCreator = widgetCreator;
        this.aliquotAdminForm = aliquotAdminForm;
    }

    protected void createPatientNumberText(Composite parent) {
        patientLabel = widgetCreator.createLabel(parent,
            Messages.getString("ScanLink.patientNumber.label")); //$NON-NLS-1$
        patientLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        patientValidator = new NonEmptyStringValidator(
            Messages.getString("ScanLink.patientNumber.validationMsg"));//$NON-NLS-1$
        patientNumberText = (BiobankText) widgetCreator.createBoundWidget(
            parent, BiobankText.class, SWT.NONE, patientLabel, new String[0],
            new WritableValue("", String.class), patientValidator); //$NON-NLS-1$
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (patientNumberTextModified) {
                    initFieldWithPatientSelection();
                    if (patientTextCallback != null) {
                        patientTextCallback.focusLost();
                    }
                }
                patientNumberTextModified = false;
                viewerCollectionEvents.getCombo().setFocus();
            }
        });
        patientNumberText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                patientNumberTextModified = true;
                if (patientTextCallback != null) {
                    patientTextCallback.textModified();
                }
            }
        });
        patientNumberText.addKeyListener(aliquotAdminForm.textFieldKeyListener);
        setFirstControl();
    }

    protected void createWorksheetText(Composite parent) {
        worksheetText = (BiobankText) widgetCreator.createBoundWidgetWithLabel(
            parent, BiobankText.class, SWT.NONE,
            Messages.getString("ScanLink.worksheet.label"), new String[0], //$NON-NLS-1$
            new WritableValue("", String.class), null); //$NON-NLS-1$
        worksheetText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (worksheetNumberModified) {
                    currentWorksheetNumber = worksheetText.getText();
                    checkWorksheetNumber();
                }
                worksheetNumberModified = false;
            }
        });
        worksheetText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                worksheetNumberModified = true;
            }
        });
        worksheetText.addKeyListener(aliquotAdminForm.textFieldKeyListener);
        setFirstControl();
    }

    protected void checkWorksheetNumber() {
        boolean ok = true;
        try {
            if (getSelectedCollectionEvent() != null) {
                cEventSpecimensForWorksheet = getSelectedCollectionEvent()
                    .getSpecimensInCollectionEventsForWorksheet(true,
                        currentWorksheetNumber);
                if (cEventSpecimensForWorksheet.size() == 0) {
                    BiobankPlugin.openAsyncError(Messages
                        .getString("ScanLink.worksheet.specimens.error.title"), //$NON-NLS-1$
                        Messages.getString(
                            "ScanLink.worksheet.specimens.error.msg", //$NON-NLS-1$
                            currentWorksheetNumber));
                    ok = false;
                }
            } else {
                cEventSpecimensForWorksheet = null;
                List<ProcessingEventWrapper> peList = ProcessingEventWrapper
                    .getProcessingEventsWithWorksheet(
                        SessionManager.getAppService(), currentWorksheetNumber);
                if (peList.size() == 0) {
                    BiobankPlugin.openAsyncError(Messages
                        .getString("ScanLink.worksheet.error.title"), Messages //$NON-NLS-1$
                        .getString("ScanLink.worksheet.error.msg", //$NON-NLS-1$
                            currentWorksheetNumber));
                    ok = false;
                }
            }
        } catch (Exception e) {
            BiobankPlugin.openAsyncError(
                Messages.getString("ScanLink.worksheet.error.title"), e); //$NON-NLS-1$
            ok = false;
        } finally {
            if (!ok) {
                // FIXME don't hit scan
            }
        }
    }

    protected void createCollectionEventWidgets(Composite compositeFields) {
        cEventComboLabel = widgetCreator.createLabel(compositeFields,
            Messages.getString("ScanLink.visit.label.drawn")); //$NON-NLS-1$
        viewerCollectionEvents = widgetCreator.createComboViewer(
            compositeFields, cEventComboLabel, null, null,
            Messages.getString("ScanLink.visit.validationMsg"), false, null, //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    currentCEventSelected = (CollectionEventWrapper) selectedObject;
                }
            }); //$NON-NLS-1$
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        viewerCollectionEvents.getCombo().setLayoutData(gridData);

        viewerCollectionEvents.getCombo().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                IStructuredSelection selection = (IStructuredSelection) viewerCollectionEvents
                    .getSelection();
                if (selection != null && selection.size() > 0) {
                    CollectionEventWrapper ce = (CollectionEventWrapper) selection
                        .getFirstElement();
                    if (ce != null) {
                        // FIXME what correct message do we want ?
                        // aliquotAdminForm.appendLogNLS(
                        //                            "linkAssign.activitylog.visit.selection", ce //$NON-NLS-1$
                        // .getCenter().getNameShort(), ce
                        // .getFormattedDateDrawn(), ce
                        // .getFormattedDateProcessed(), ce.getCenter()
                        // .getName());
                    }
                }
            }
        });

        // Will replace the combo in some specific situations (like cabinet
        // form):
        cEventTextLabel = widgetCreator.createLabel(compositeFields,
            Messages.getString("ScanLink.visit.label.drawn")); //$NON-NLS-1$
        cEventTextLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        cEventText = (BiobankText) widgetCreator.createWidget(compositeFields,
            BiobankText.class, SWT.NONE, ""); //$NON-NLS-1$
        cEventText.setEnabled(false);
        widgetCreator.hideWidget(cEventTextLabel);
        widgetCreator.hideWidget(cEventText);
    }

    protected CollectionEventWrapper getSelectedCollectionEvent() {
        return currentCEventSelected;
    }

    protected void initFieldWithPatientSelection() {
        currentPatient = null;
        try {
            currentPatient = PatientWrapper.getPatient(
                aliquotAdminForm.appService, patientNumberText.getText());
            if (currentPatient != null) {
                aliquotAdminForm.appendLog("--------"); //$NON-NLS-1$
                aliquotAdminForm.appendLogNLS("linkAssign.activitylog.patient", //$NON-NLS-1$
                    currentPatient.getPnumber());
            }
        } catch (ApplicationException e) {
            BiobankPlugin.openError(
                Messages.getString("ScanLink.dialog.patient.errorMsg"), e); //$NON-NLS-1$
        }
        setCollectionEventList();
    }

    public void reset(boolean resetAll) {
        viewerCollectionEvents.setInput(null);
        currentPatient = null;
        if (resetAll) {
            patientNumberText.setText(""); //$NON-NLS-1$
            if (cEventText != null) {
                cEventText.setText(""); //$NON-NLS-1$
            }
        }
    }

    public PatientWrapper getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatientAndVisit(PatientWrapper patient,
        CollectionEventWrapper cEvent) throws Exception {
        patient.reload();
        this.currentPatient = patient;
        patientNumberText.setText(patient.getPnumber());
        List<ProcessingEventWrapper> collection = patient
            .getProcessingEventCollection();
        viewerCollectionEvents.setInput(collection);
        viewerCollectionEvents.setSelection(new StructuredSelection(cEvent));
        if (cEventText != null) {
            cEventText.setText(cEvent.getVisitNumber().toString());
        }
    }

    public void enabledPatientText(boolean enabled) {
        patientNumberText.setEnabled(enabled);
    }

    public void enabledVisitsList(boolean enabled) {
        viewerCollectionEvents.getCombo().setEnabled(enabled);
        cEventListCheck.setEnabled(enabled);
        showVisitText(!enabled);
    }

    public void setPatientTextCallback(PatientTextCallback callback) {
        this.patientTextCallback = callback;
    }

    protected static interface PatientTextCallback {
        public void focusLost();

        public void textModified();
    }

    public void enableValidators(boolean enabled) {
        if (enabled) {
            patientNumberText.setText(""); //$NON-NLS-1$
            viewerCollectionEvents.getCombo().deselectAll();
        } else {
            patientNumberText.setText("?"); //$NON-NLS-1$
            viewerCollectionEvents.setInput(new String[] { "?" }); //$NON-NLS-1$
            viewerCollectionEvents.getCombo().select(0);
        }
    }

    public void showVisitText(boolean show) {
        widgetCreator.showWidget(cEventComboLabel, !show);
        widgetCreator.showWidget(cEventListCheck, !show);
        widgetCreator.showWidget(viewerCollectionEvents.getCombo(), !show);
        if (cEventText != null) {
            widgetCreator.showWidget(cEventTextLabel, show);
            widgetCreator.showWidget(cEventText, show);
        }
    }

    public void setFirstControl() {
        aliquotAdminForm.setFirstControl(patientNumberText);
    }

    public boolean fieldsValid() {
        IStructuredSelection selection = (IStructuredSelection) viewerCollectionEvents
            .getSelection();
        return patientValidator.validate(patientNumberText.getText()).equals(
            Status.OK_STATUS)
            && selection.size() > 0;
    }

    public void setCollectionEventList() {
        if (viewerCollectionEvents != null) {
            if (currentPatient != null) {
                List<CollectionEventWrapper> collection = currentPatient
                    .getCollectionEventCollection(true, false);
                viewerCollectionEvents.setInput(collection);
                viewerCollectionEvents.getCombo().setFocus();
                if (collection != null && collection.size() == 1) {
                    viewerCollectionEvents
                        .setSelection(new StructuredSelection(collection.get(0)));
                } else {
                    viewerCollectionEvents.getCombo().deselectAll();
                }
            } else {
                viewerCollectionEvents.setInput(null);
            }
            if (cEventText != null) {
                cEventText.setText(""); //$NON-NLS-1$
            }
        }

    }

    public String getCurrentWorksheetNumber() {
        return currentWorksheetNumber;
    }

    public List<SpecimenWrapper> getSpecimensInCollectionEventsForWorksheet() {
        if (cEventSpecimensForWorksheet == null)
            cEventSpecimensForWorksheet = getSelectedCollectionEvent()
                .getSpecimensInCollectionEventsForWorksheet(true,
                    currentWorksheetNumber);
        return cEventSpecimensForWorksheet;
    }
}
