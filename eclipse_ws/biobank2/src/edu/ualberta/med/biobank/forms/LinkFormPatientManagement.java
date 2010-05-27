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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class LinkFormPatientManagement {

    private boolean patientNumberTextModified = false;
    protected Text patientNumberText;
    protected ComboViewer viewerVisits;
    private Button visitsListCheck;

    private static Boolean visitsListCheckSelection = true;

    // currentPatient
    protected PatientWrapper currentPatient;

    private WidgetCreator widgetCreator;

    private AbstractAliquotAdminForm aliquotAdminForm;

    private PatientTextCallback patientTextCallback;
    private Label patientLabel;
    private NonEmptyStringValidator patientValidator;
    private Label visitTextLabel;
    private Text visitText;
    private Label visitComboLabel;

    public LinkFormPatientManagement(WidgetCreator widgetCreator,
        AbstractAliquotAdminForm aliquotAdminForm) {
        this.widgetCreator = widgetCreator;
        this.aliquotAdminForm = aliquotAdminForm;
    }

    protected void createPatientNumberText(Composite parent) {
        patientLabel = widgetCreator.createLabel(parent, Messages
            .getString("ScanLink.patientNumber.label"));
        patientLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        patientValidator = new NonEmptyStringValidator(Messages
            .getString("ScanLink.patientNumber.validationMsg"));//$NON-NLS-1$
        patientNumberText = (Text) widgetCreator.createBoundWidget(parent,
            Text.class, SWT.NONE, patientLabel, new String[0],
            new WritableValue("", String.class), patientValidator);
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (patientNumberTextModified) {
                    setPatientSelected();
                    if (patientTextCallback != null) {
                        patientTextCallback.focusLost();
                    }
                }
                patientNumberTextModified = false;
                viewerVisits.getCombo().setFocus();
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
        patientNumberText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        GridData gd = (GridData) patientNumberText.getLayoutData();
        gd.horizontalSpan = 2;
        aliquotAdminForm.setFirstControl(patientNumberText);
    }

    protected void createVisitCombo(Composite compositeFields) {
        visitComboLabel = widgetCreator.createLabel(compositeFields, Messages
            .getString("ScanLink.visit.label"));
        viewerVisits = widgetCreator.createComboViewerWithNoSelectionValidator(
            compositeFields, visitComboLabel, null, null, Messages
                .getString("ScanLink.visit.validationMsg"), false, null); //$NON-NLS-1$
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        viewerVisits.getCombo().setLayoutData(gridData);

        viewerVisits.getCombo().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                PatientVisitWrapper pv = getSelectedPatientVisit();
                if (pv != null) {
                    aliquotAdminForm.appendLogNLS(
                        "linkAssign.activitylog.visit.selection", pv //$NON-NLS-1$
                            .getFormattedDateProcessed(), pv.getShipment()
                            .getClinic().getName());
                }
            }
        });
        visitsListCheck = aliquotAdminForm.toolkit.createButton(
            compositeFields, "Last 7 days", SWT.CHECK);
        visitsListCheck.setSelection(visitsListCheckSelection);
        visitsListCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setVisitsList();
            }
        });
    }

    /**
     * Specific to Cabinet move mode
     */
    protected void createVisitText(Composite compositeFields) {
        visitTextLabel = widgetCreator.createLabel(compositeFields, Messages
            .getString("ScanLink.visit.label"));
        visitTextLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        visitText = (Text) widgetCreator.createWidget(compositeFields,
            Text.class, SWT.NONE, "");
        visitText.setEnabled(false);
        ((GridData) visitText.getLayoutData()).horizontalSpan = 2;
    }

    protected PatientVisitWrapper getSelectedPatientVisit() {
        if (viewerVisits.getSelection() != null
            && viewerVisits.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) viewerVisits
                .getSelection();
            if (selection.size() == 1)
                return (PatientVisitWrapper) selection.getFirstElement();
        }
        return null;
    }

    protected void setPatientSelected() {
        currentPatient = null;
        try {
            currentPatient = PatientWrapper.getPatientInSite(
                aliquotAdminForm.appService, patientNumberText.getText(),
                SessionManager.getInstance().getCurrentSite());
            if (currentPatient != null) {
                aliquotAdminForm.appendLog("--------");
                aliquotAdminForm.appendLogNLS("linkAssign.activitylog.patient", //$NON-NLS-1$
                    currentPatient.getPnumber());
            }
        } catch (ApplicationException e) {
            BioBankPlugin.openError(Messages
                .getString("ScanLink.dialog.patient.errorMsg"), e); //$NON-NLS-1$
        }
        setVisitsList();
    }

    protected void setVisitsList() {
        if (currentPatient != null) {
            // show visits list
            List<PatientVisitWrapper> collection = null;
            if (visitsListCheck.getSelection()) {
                try {
                    collection = currentPatient.getLast7DaysPatientVisits();
                } catch (ApplicationException e) {
                    BioBankPlugin.openAsyncError("Visits problem",
                        "Problem getting last 7 days visits. All visits will "
                            + "be displayed into the list");
                    aliquotAdminForm.getErrorLogger().error(
                        "Last 7 days visits error", e);
                }
            }
            if (collection == null) {
                collection = currentPatient.getPatientVisitCollection(true,
                    false);
            }
            viewerVisits.setInput(collection);
            viewerVisits.getCombo().setFocus();
            if (collection != null && collection.size() == 1) {
                viewerVisits.getCombo().select(0);
            } else {
                viewerVisits.getCombo().deselectAll();
            }
        } else {
            viewerVisits.setInput(null);
        }
        if (visitText != null) {
            visitText.setText("");
        }
    }

    public void onClose() {
        if (aliquotAdminForm.finished) {
            visitsListCheckSelection = true;
        } else {
            visitsListCheckSelection = visitsListCheck.getSelection();
        }
    }

    public void reset(boolean resetAll) {
        viewerVisits.setInput(null);
        currentPatient = null;
        if (resetAll) {
            patientNumberText.setText(""); //$NON-NLS-1$
            if (visitText != null) {
                visitText.setText("");
            }
        }
    }

    public PatientWrapper getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatientAndVisit(PatientWrapper patient,
        PatientVisitWrapper patientVisit) {
        this.currentPatient = patient;
        patientNumberText.setText(patient.getPnumber());
        List<PatientVisitWrapper> collection = patient
            .getPatientVisitCollection();
        viewerVisits.setInput(collection);
        viewerVisits.setSelection(new StructuredSelection(patientVisit));
        if (visitText != null) {
            visitText.setText(patientVisit.getFormattedDateProcessed());
        }
    }

    public void enabledPatientText(boolean enabled) {
        patientNumberText.setEnabled(enabled);
    }

    public void enabledVisitsList(boolean enabled) {
        viewerVisits.getCombo().setEnabled(enabled);
        visitsListCheck.setEnabled(enabled);
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
            patientNumberText.setText("");
            viewerVisits.getCombo().deselectAll();
        } else {
            patientNumberText.setText("?");
            viewerVisits.setInput(new String[] { "?" });
            viewerVisits.getCombo().select(0);
        }
    }

    public void showVisitText(boolean show) {
        widgetCreator.showWidget(visitComboLabel, !show);
        widgetCreator.showWidget(visitsListCheck, !show);
        widgetCreator.showWidget(viewerVisits.getCombo(), !show);
        if (visitText != null) {
            widgetCreator.showWidget(visitTextLabel, show);
            widgetCreator.showWidget(visitText, show);
        }
    }

    public boolean fieldsValid() {
        IStructuredSelection selection = (IStructuredSelection) viewerVisits
            .getSelection();
        return patientValidator.validate(patientNumberText.getText()).equals(
            Status.OK_STATUS)
            && selection.size() > 0;
    }

}
