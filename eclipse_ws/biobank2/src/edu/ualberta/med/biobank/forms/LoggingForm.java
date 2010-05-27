package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankText;

//XXX based on studyviewform
public class LoggingForm extends BiobankViewForm {

    public static String ID = "edu.ualberta.med.biobank.forms.LoggingForm";

    private StudyWrapper study;

    private BiobankText userLabel;
    private BiobankText nameLabel;
    private BiobankText actionLabel;
    private BiobankText patientNumLabel;
    private BiobankText inventoryIDLabel;
    private BiobankText detailsLabel;
    private BiobankText containerTypeLabel;
    private BiobankText containerLabelLabel;

    @Override
    public void init() throws Exception {
        setPartName("Logging Form");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Logging: Search through logs.");

        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_STUDY));

        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        userLabel = createReadOnlyLabelledField(client, SWT.NONE, "User");
        nameLabel = createReadOnlyLabelledField(client, SWT.NONE, "Form");
        actionLabel = createReadOnlyLabelledField(client, SWT.NONE, "Action");
        patientNumLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Patient #");
        inventoryIDLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Inventory ID");
        detailsLabel = createReadOnlyLabelledField(client, SWT.NONE, "Details");
        containerTypeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Container Type");
        containerLabelLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Container Label");
    }

    @Override
    protected void reload() throws Exception {
    }

}
