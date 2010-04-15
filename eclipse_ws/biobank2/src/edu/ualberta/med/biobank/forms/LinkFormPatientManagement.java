package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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

    private static IObservableValue visitsListCheckValue = new WritableValue(
        Boolean.TRUE, Boolean.class);

    // currentPatient
    protected PatientWrapper currentPatient;

    private WidgetCreator widgetCreator;

    private AbstractAliquotAdminForm aliquotAdminForm;

    private PatientTextCallback patientTextCallback;

    public LinkFormPatientManagement(WidgetCreator widgetCreator,
        AbstractAliquotAdminForm aliquotAdminForm) {
        this.widgetCreator = widgetCreator;
        this.aliquotAdminForm = aliquotAdminForm;
    }

    protected void initPatientNumberText(Composite parent) {
        patientNumberText = (Text) widgetCreator.createBoundWidgetWithLabel(
            parent, Text.class, SWT.NONE, Messages
                .getString("ScanLink.patientNumber.label"), new String[0], //$NON-NLS-1$
            new WritableValue("", String.class), new NonEmptyStringValidator( //$NON-NLS-1$
                Messages.getString("ScanLink.patientNumber.validationMsg"))); //$NON-NLS-1$
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (patientNumberTextModified) {
                    setPatientSelected();
                    if (patientTextCallback != null) {
                        patientTextCallback.callback();
                    }
                }
                patientNumberTextModified = false;
            }
        });
        patientNumberText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                patientNumberTextModified = true;
            }
        });
        patientNumberText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        GridData gd = (GridData) patientNumberText.getLayoutData();
        gd.horizontalSpan = 2;
        aliquotAdminForm.firstControl = patientNumberText;
    }

    protected void createVisitCombo(Composite compositeFields) {
        viewerVisits = widgetCreator.createComboViewerWithNoSelectionValidator(
            compositeFields,
            Messages.getString("ScanLink.visit.label"), null, null, //$NON-NLS-1$
            Messages.getString("ScanLink.visit.validationMsg")); //$NON-NLS-1$
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        viewerVisits.getCombo().setLayoutData(gridData);

        viewerVisits.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                PatientVisitWrapper pv = (PatientVisitWrapper) element;
                return pv.getFormattedDateProcessed() + " - " //$NON-NLS-1$
                    + pv.getShipment().getWaybill();
            }
        });
        // viewerVisits.getCombo().addKeyListener(new KeyAdapter() {
        // @Override
        // public void keyReleased(KeyEvent e) {
        // if (e.keyCode == 13) {
        // // focusOnPlateToScanText();
        // e.doit = false;
        // }
        // }
        // });
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
        visitsListCheck.setSelection(true);
        visitsListCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setVisitsList();
            }
        });
        widgetCreator.bindValue(SWTObservables
            .observeSelection(visitsListCheck), visitsListCheckValue, null,
            null);
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
                collection = currentPatient.getPatientVisitCollection();
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
    }

    public void onClose() {
        if (!aliquotAdminForm.finished) {
            visitsListCheckValue.setValue(true);
        }
    }

    public void reset(boolean resetAll) {
        viewerVisits.setInput(null);
        currentPatient = null;
        if (resetAll) {
            patientNumberText.setText(""); //$NON-NLS-1$
        }
    }

    public PatientWrapper getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatientAndVisit(PatientWrapper patient,
        PatientVisitWrapper patientVisit) {
        visitsListCheckValue.setValue(false);
        this.currentPatient = patient;
        patientNumberText.setText(patient.getPnumber());
        List<PatientVisitWrapper> collection = patient
            .getPatientVisitCollection();
        viewerVisits.setInput(collection);
        viewerVisits.setSelection(new StructuredSelection(patientVisit));
    }

    public void enabledPatientText(boolean enabled) {
        patientNumberText.setEnabled(enabled);
    }

    public void enabledVisitsList(boolean enabled) {
        viewerVisits.getCombo().setEnabled(enabled);
        visitsListCheck.setEnabled(enabled);
    }

    public void setPatientTextCallback(PatientTextCallback callback) {
        this.patientTextCallback = callback;
    }

    protected static interface PatientTextCallback {
        public void callback();
    }
}
