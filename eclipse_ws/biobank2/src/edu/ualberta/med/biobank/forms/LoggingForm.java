package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.LoggingInfoTable;

//XXX based on studyviewform
public class LoggingForm extends BiobankViewForm {

    public static String ID = "edu.ualberta.med.biobank.forms.LoggingForm";

    private SiteAdapter siteAdapter;
    private SiteWrapper site;

    @Override
    public void init() throws Exception {
        setPartName("Logging: results");
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        siteAdapter = (SiteAdapter) adapter;
        site = siteAdapter.getWrapper();
    }

    @Override
    protected void createFormContent() throws Exception {
        BiobankText userLabel;
        BiobankText nameLabel;
        BiobankText actionLabel;
        BiobankText patientNumLabel;
        BiobankText inventoryIDLabel;
        BiobankText detailsLabel;
        BiobankText containerTypeLabel;
        BiobankText containerLabelLabel;
        BiobankText startDateLabel;
        BiobankText endDateLabel;

        form.setText("Logging: Browse through logs.");
        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_STUDY));

        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(4, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        userLabel = createReadOnlyLabelledField(client, SWT.NONE, "User");
        containerTypeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Container Type");
        nameLabel = createReadOnlyLabelledField(client, SWT.NONE, "Form");
        containerLabelLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Container Label");
        actionLabel = createReadOnlyLabelledField(client, SWT.NONE, "Action");
        startDateLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Start Date");
        patientNumLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Patient #");
        endDateLabel = createReadOnlyLabelledField(client, SWT.NONE, "End Date");
        inventoryIDLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Inventory ID");
        detailsLabel = createReadOnlyLabelledField(client, SWT.NONE, "Details");

        Section section = createSection("Search Results");

        LoggingInfoTable loggingTable = new LoggingInfoTable(section, null,
            new String[] { "Fat", "Cat", "Rat" }, new int[] { 5, 5, 5 }, 10);
        loggingTable.adaptToToolkit(toolkit, true);
        // loggingTable.setCollection(site.getClinicCollection(true));

    }

    @Override
    protected void reload() throws Exception {
    }

}
