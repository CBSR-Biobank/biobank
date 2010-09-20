package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.logs.LogQuery;
import edu.ualberta.med.biobank.views.LoggingView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.LoggingInfoTable;

public class LoggingForm extends BiobankViewForm {

    public static String ID = "edu.ualberta.med.biobank.forms.LoggingForm";

    private BiobankText userLabel;
    private BiobankText typeLabel;
    private BiobankText actionLabel;
    private BiobankText patientNumLabel;
    private BiobankText inventoryIDLabel;
    private BiobankText locationLabel;
    private BiobankText detailsLabel;
    private BiobankText startDateLabel;
    private BiobankText endDateLabel;

    private BiobankText siteLabel;

    // private BiobankText containerTypeLabel;
    // private BiobankText containerLabelLabel;

    @Override
    public void init() throws Exception {
        setPartName("Logging: results");
    }

    @Override
    protected void createFormContent() throws Exception {

        PlatformUI.getWorkbench().getViewRegistry().find(LoggingView.ID);

        form.setText("Logging: Browse through your search results");
        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // FIXME study icon is not correct for the log form
        form.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_STUDY));

        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Composite leftClient = toolkit.createComposite(client);
        leftClient.setLayout(new GridLayout(2, false));
        leftClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
            | GridData.VERTICAL_ALIGN_BEGINNING));
        toolkit.paintBordersFor(leftClient);

        Composite rightClient = toolkit.createComposite(client);
        rightClient.setLayout(new GridLayout(2, false));
        rightClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
            | GridData.VERTICAL_ALIGN_BEGINNING));
        toolkit.paintBordersFor(rightClient);

        /* a grid might make this easier */
        siteLabel = createReadOnlyLabelledField(leftClient, SWT.NONE, "Site");
        userLabel = createReadOnlyLabelledField(leftClient, SWT.NONE, "User");
        typeLabel = createReadOnlyLabelledField(leftClient, SWT.NONE, "Type");
        actionLabel = createReadOnlyLabelledField(leftClient, SWT.NONE,
            "Action");
        startDateLabel = createReadOnlyLabelledField(leftClient, SWT.NONE,
            "Start Date");

        patientNumLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            "Patient #");
        locationLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            "Location");
        inventoryIDLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            "Inventory ID");
        detailsLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            "Details");
        endDateLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            "End Date");

        getSearchRequestFields();

        generateSearchQueryTable();
    }

    private void generateSearchQueryTable() {

        LogQuery.getInstance().queryDatabase();

        Composite client = createSectionWithClient("Search Results");
        LoggingInfoTable loggingTable = new LoggingInfoTable(client, LogQuery
            .getInstance().getDatabaseResults());
        loggingTable.adaptToToolkit(toolkit, true);
        loggingTable.setVisible(true);
        loggingTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(loggingTable);
    }

    private void getSearchRequestFields() throws Exception {
        siteLabel.setText(LogQuery.getInstance().getSearchQueryItem("site"));
        userLabel.setText(LogQuery.getInstance().getSearchQueryItem("user"));
        actionLabel
            .setText(LogQuery.getInstance().getSearchQueryItem("action"));
        typeLabel.setText(LogQuery.getInstance().getSearchQueryItem("type"));
        startDateLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "startDate"));
        endDateLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "endDate"));
        patientNumLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "patientNumber"));
        inventoryIDLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "inventoryId"));
        locationLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "location"));

        detailsLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "details"));
        /*
         * containerTypeLabel = createReadOnlyLabelledField(client, SWT.NONE,
         * "Container Type"); containerLabelLabel
         * =createReadOnlyLabelledField(client, SWT.NONE, "Container Label");
         */
        /*
         * containerTypeLabel.setText(LogQuery.getInstance().getSearchQueryItem
         * ( "containerType"));
         * 
         * containerLabelLabel
         * .setText(LogQuery.getInstance().getSearchQueryItem(
         * "containerLabel"));
         */
    }

    @Override
    public void reload() throws Exception {
    }

}
