package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentPatientsWidget extends BiobankWidget {

    private ShipmentWrapper shipment;

    private InfoTableWidget<PatientWrapper> patientTable;

    private Text newPatientText;

    public ShipmentPatientsWidget(Composite parent, int style,
        ShipmentWrapper ship, FormToolkit toolkit, boolean editable) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        this.shipment = ship;

        setLayout(new GridLayout(2, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(this);

        if (editable) {
            newPatientText = toolkit.createText(parent, "");
            // TODO add action on return
            Button addButton = toolkit.createButton(parent, "", SWT.PUSH);
            addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
                .get(BioBankPlugin.IMG_ADD));
            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    try {
                        PatientWrapper patient = PatientWrapper
                            .getPatientInSite(shipment.getAppService(),
                                newPatientText.getText(), shipment.getClinic()
                                    .getSite());
                        if (patient != null) {
                            List<PatientWrapper> patients = shipment
                                .getPatientCollection();
                            if (patients == null) {
                                patients = new ArrayList<PatientWrapper>();
                            }
                            patients.add(patient);
                            shipment.setPatientCollection(patients);
                            // TODO check duplicates...
                            patientTable.setCollection(patients);
                        } else {
                            // TODO message
                            System.out.println("patient not found");
                        }
                    } catch (ApplicationException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });
        }

        String[] headings = new String[] { "Patient Number", "Study Name", };
        int[] bounds = new int[] { 150, 150, -1, -1, -1, -1 };
        patientTable = new InfoTableWidget<PatientWrapper>(parent, shipment
            .getPatientCollection(), headings, bounds);
        patientTable.adaptToToolkit(toolkit, true);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        patientTable.setLayoutData(gd);
    }
}
