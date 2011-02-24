package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class LinkFormPatientManagement {

    private SiteWrapper site;

    private boolean patientNumberTextModified = false;
    protected BiobankText patientNumberText;
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
    private BiobankText visitText;
    private Label visitComboLabel;
    protected ProcessingEventWrapper currentVisitSelected;
    private BiobankText visitProcessedText;

    public LinkFormPatientManagement(WidgetCreator widgetCreator,
        AbstractAliquotAdminForm aliquotAdminForm) {
        this.widgetCreator = widgetCreator;
        this.aliquotAdminForm = aliquotAdminForm;
    }

    protected void createPatientNumberText(Composite parent) {
        patientLabel = widgetCreator.createLabel(parent,
            Messages.getString("ScanLink.patientNumber.label"));
        patientLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        patientValidator = new NonEmptyStringValidator(
            Messages.getString("ScanLink.patientNumber.validationMsg"));//$NON-NLS-1$
        patientNumberText = (BiobankText) widgetCreator.createBoundWidget(
            parent, BiobankText.class, SWT.NONE, patientLabel, new String[0],
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
        patientNumberText.addKeyListener(aliquotAdminForm.textFieldKeyListener);
        GridData gd = (GridData) patientNumberText.getLayoutData();
        gd.horizontalSpan = 2;
        setFirstControl();
    }

    protected void createVisitWidgets(Composite compositeFields) {
        visitComboLabel = widgetCreator.createLabel(compositeFields,
            Messages.getString("ScanLink.visit.label.drawn"));
        viewerVisits = widgetCreator.createComboViewer(compositeFields,
            visitComboLabel, null, null,
            Messages.getString("ScanLink.visit.validationMsg"), false, null,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    currentVisitSelected = (ProcessingEventWrapper) selectedObject;
                    if (currentVisitSelected == null) {
                        visitProcessedText.setText("");
                    } else {
                        visitProcessedText.setText(currentVisitSelected
                            .getFormattedDateProcessed());
                    }
                }
            }); //$NON-NLS-1$
        viewerVisits.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ProcessingEventWrapper)
                    return ((ProcessingEventWrapper) element)
                        .getFormattedDateDrawn();
                return element.toString();
            }
        });
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        viewerVisits.getCombo().setLayoutData(gridData);

        viewerVisits.getCombo().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                IStructuredSelection selection = (IStructuredSelection) viewerVisits
                    .getSelection();
                if (selection != null && selection.size() > 0) {
                    ProcessingEventWrapper pv = (ProcessingEventWrapper) selection
                        .getFirstElement();
                    if (pv != null) {
                        aliquotAdminForm.appendLogNLS(
                            "linkAssign.activitylog.visit.selection", pv //$NON-NLS-1$
                                .getCenter().getNameShort(), pv
                                .getFormattedDateDrawn(), pv
                                .getFormattedDateProcessed(), pv.getCenter()
                                .getName());
                    }
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

        // Will replace the combo in some specific situations (like cabinet
        // form):
        visitTextLabel = widgetCreator.createLabel(compositeFields,
            Messages.getString("ScanLink.visit.label.drawn"));
        visitTextLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        visitText = (BiobankText) widgetCreator.createWidget(compositeFields,
            BiobankText.class, SWT.NONE, "");
        visitText.setEnabled(false);
        ((GridData) visitText.getLayoutData()).horizontalSpan = 2;
        widgetCreator.hideWidget(visitTextLabel);
        widgetCreator.hideWidget(visitText);

        // Display only:
        visitProcessedText = widgetCreator.createReadOnlyLabelledField(
            compositeFields, SWT.NONE,
            Messages.getString("ScanLink.visit.label.processed"), "", true);
        visitProcessedText.setEnabled(false);

        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        if (((GridLayout) compositeFields.getLayout()).numColumns == 3) {
            gd.horizontalSpan = 2;
        }
        visitProcessedText.setLayoutData(gd);
    }

    protected ProcessingEventWrapper getSelectedPatientVisit() {
        return currentVisitSelected;
    }

    protected void setPatientSelected() {
        currentPatient = null;
        try {
            currentPatient = PatientWrapper.getPatient(
                aliquotAdminForm.appService, patientNumberText.getText());
            if (currentPatient != null) {
                aliquotAdminForm.appendLog("--------");
                aliquotAdminForm.appendLogNLS("linkAssign.activitylog.patient", //$NON-NLS-1$
                    currentPatient.getPnumber());
            }
        } catch (ApplicationException e) {
            BioBankPlugin.openError(
                Messages.getString("ScanLink.dialog.patient.errorMsg"), e); //$NON-NLS-1$
        }
        setVisitsList();
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
        visitProcessedText.setText("");
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
        ProcessingEventWrapper patientVisit) throws Exception {
        // FIXME need to reload otherwise get a database access problem ??
        patient.reload();
        this.currentPatient = patient;
        patientNumberText.setText(patient.getPnumber());
        List<ProcessingEventWrapper> collection = patient
            .getProcessingEventCollection();
        viewerVisits.setInput(collection);
        viewerVisits.setSelection(new StructuredSelection(patientVisit));
        if (visitText != null) {
            visitText.setText(patientVisit.getFormattedDateDrawn());
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

    public void setFirstControl() {
        aliquotAdminForm.setFirstControl(patientNumberText);
    }

    public boolean fieldsValid() {
        IStructuredSelection selection = (IStructuredSelection) viewerVisits
            .getSelection();
        return patientValidator.validate(patientNumberText.getText()).equals(
            Status.OK_STATUS)
            && selection.size() > 0;
    }

    public void setSite(SiteWrapper site) {
        this.site = site;
    }

    @Deprecated
    public void setVisitsList() {
        // TODO Auto-generated method stub

    }

}
