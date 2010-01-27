package edu.ualberta.med.biobank.widgets;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentPatientsWidget extends BiobankWidget {

    private ShipmentWrapper shipment;

    private InfoTableWidget<PatientWrapper> patientTable;

    private Text newPatientText;

    private SiteWrapper currentSite;

    private boolean editable;

    public ShipmentPatientsWidget(Composite parent, int style,
        ShipmentWrapper ship, final SiteWrapper site, FormToolkit toolkit,
        boolean editable) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        this.shipment = ship;
        this.currentSite = site;
        this.editable = editable;

        setLayout(new GridLayout(2, false));
        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        toolkit.paintBordersFor(this);

        if (editable) {
            newPatientText = toolkit.createText(this, "");
            newPatientText.addListener(SWT.DefaultSelection, new Listener() {
                public void handleEvent(Event e) {
                    boolean newPatient = addPatient();
                    newPatientText.setFocus();
                    if (!newPatient) {
                        newPatientText.setText("");
                    }
                }
            });
            Button addButton = toolkit.createButton(this, "", SWT.PUSH);
            addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
                .get(BioBankPlugin.IMG_ADD));
            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addPatient();
                }
            });
        }

        String[] headings = new String[] { "Patient Number", "Study Name", };
        int[] bounds = new int[] { 150, 150, -1, -1, -1, -1 };
        patientTable = new InfoTableWidget<PatientWrapper>(this, shipment
            .getPatientCollection(), headings, bounds);
        patientTable.adaptToToolkit(toolkit, true);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        patientTable.setLayoutData(gd);

        patientTable.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                BiobankCollectionModel item = (BiobankCollectionModel) ((StructuredSelection) selection)
                    .getFirstElement();
                Assert.isTrue(item.o instanceof PatientWrapper,
                    "Invalid class where patient expected: "
                        + item.o.getClass());

                PatientWrapper patient = (PatientWrapper) item.o;
                ShipmentAdministrationView.currentInstance
                    .displayPatient(patient);
            }
        });
        addTableMenu();
    }

    private boolean addPatient() {
        String patientNumber = newPatientText.getText().trim();
        if (!patientNumber.isEmpty()) {
            try {
                PatientWrapper patient = PatientWrapper.getPatientInSite(
                    shipment.getAppService(), patientNumber, currentSite);
                if (patient == null) {
                    boolean create = BioBankPlugin.openConfirm(
                        "Patient not found",
                        "Do you want to create this patient ?");
                    if (create) {
                        patient = new PatientWrapper(SessionManager
                            .getAppService());
                        patient.setPnumber(patientNumber);
                        ShipmentAdministrationView.currentInstance
                            .displayPatient(patient);
                        return true;
                    }
                } else {
                    addPatient(patient);
                }
            } catch (ApplicationException ae) {
                BioBankPlugin.openAsyncError("Error while looking up patient",
                    ae);
            }
        }
        return false;
    }

    private void addPatient(PatientWrapper patient) {
        List<PatientWrapper> patients = shipment.getPatientCollection();
        if (patients != null && patients.contains(patient)) {
            BioBankPlugin.openAsyncError("Error", "Patient "
                + patient.getPnumber()
                + " has already been added to this shipment");
            return;
        }
        shipment.addPatients(patient);
        patientTable.setCollection(shipment.getPatientCollection());
        notifyListeners();
    }

    private void addTableMenu() {
        if (editable) {
            Menu menu = new Menu(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), SWT.NONE);
            patientTable.getTableViewer().getTable().setMenu(menu);

            MenuItem item = new MenuItem(menu, SWT.PUSH);
            item.setText("Delete");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    PatientWrapper patient = patientTable.getSelection();
                    boolean confirm = MessageDialog.openConfirm(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Delete Patient",
                        "Are you sure you want to remove patient \""
                            + patient.getPnumber() + "\" for this shipment ?");

                    if (confirm) {
                        shipment.removePatients(patient);
                        patientTable.setCollection(shipment
                            .getPatientCollection());
                        notifyListeners();
                    }
                }
            });
        }
    }
}
