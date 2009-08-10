package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.BiobankCollectionTable;
import edu.ualberta.med.biobank.widgets.ClinicStudyInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicViewForm";

    private ClinicAdapter clinicAdapter;

    private Clinic clinic;

    private BiobankCollectionTable studiesTable;

    private Label activityStatusLabel;

    private Label commentLabel;

    @Override
    protected void init() {
        Assert.isTrue(adapter instanceof ClinicAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        clinicAdapter = (ClinicAdapter) adapter;
        retrieveClinic();
        address = clinic.getAddress();
        setPartName("Clinic: " + clinic.getName());
    }

    private void retrieveClinic() {
        List<Clinic> result;
        Clinic searchClinic = new Clinic();
        searchClinic.setId(clinicAdapter.getClinic().getId());
        try {
            result = clinicAdapter.getAppService().search(Clinic.class,
                searchClinic);
            Assert.isTrue(result.size() == 1);
            clinic = result.get(0);
            clinicAdapter.setClinic(clinic);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Clinic: " + clinic.getName());
        addRefreshToolbarAction();

        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createClinicSection();
        createAddressSection();
        createStudiesSection();
        createButtonsSection();
    }

    private void createClinicSection() {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        activityStatusLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Activity Status");
        commentLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Comments");

        setClinicValues();
    }

    private void setClinicValues() {
        FormUtils.setTextValue(activityStatusLabel, clinic.getActivityStatus());
        FormUtils.setTextValue(commentLabel, clinic.getComment());
    }

    protected void createStudiesSection() {
        Composite client = createSectionWithClient("Studies");

        studiesTable = new ClinicStudyInfoTable(client, appService, clinic);
        studiesTable.adaptToToolkit(toolkit);
        toolkit.paintBordersFor(studiesTable);

        studiesTable.getTableViewer().addDoubleClickListener(
            FormUtils.getBiobankCollectionDoubleClickListener());
    }

    private StudyAdapter[] getStudiesAdapters() {
        Collection<Study> studies = clinic.getStudyCollection();

        StudyAdapter[] studyAdapters = new StudyAdapter[studies.size()];
        int count = 0;
        for (Study study : studies) {
            studyAdapters[count] = new StudyAdapter(clinicAdapter.getParent(),
                study);
            count++;
        }
        return studyAdapters;
    }

    protected void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(4, false));
        toolkit.paintBordersFor(client);

        final Button edit = toolkit.createButton(client, "Edit Clinic Info",
            SWT.PUSH);
        edit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AdapterBase.openForm(new FormInput(clinicAdapter),
                    ClinicEntryForm.ID);
            }
        });
    }

    @Override
    protected void reload() {
        retrieveClinic();
        setPartName("Clinic: " + clinic.getName());
        form.setText("Clinic: " + clinic.getName());
        setClinicValues();
        setAdressValues();
        studiesTable.getTableViewer().setInput(getStudiesAdapters());
    }
}
