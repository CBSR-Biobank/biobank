package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.logs.LogQuery;
import edu.ualberta.med.biobank.views.LoggingView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.LoggingInfoTable;

//XXX based on studyviewform
public class LoggingForm extends BiobankViewForm {

    public static String ID = "edu.ualberta.med.biobank.forms.LoggingForm";

    private BiobankText userLabel;
    private BiobankText formLabel;
    private BiobankText actionLabel;
    private BiobankText patientNumLabel;
    private BiobankText inventoryIDLabel;
    private BiobankText detailsLabel;
    private BiobankText containerTypeLabel;
    private BiobankText containerLabelLabel;
    private BiobankText startDateLabel;
    private BiobankText stopDateLabel;

    @Override
    public void init() throws Exception {
        setPartName("Logging: results");
    }

    @Override
    protected void createFormContent() throws Exception {

        PlatformUI.getWorkbench().getViewRegistry().find(LoggingView.ID);

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
        formLabel = createReadOnlyLabelledField(client, SWT.NONE, "Form");
        containerLabelLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Container Label");
        actionLabel = createReadOnlyLabelledField(client, SWT.NONE, "Action");
        startDateLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Start Date");
        patientNumLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Patient #");
        stopDateLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Stop Date");
        inventoryIDLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Inventory ID");
        detailsLabel = createReadOnlyLabelledField(client, SWT.NONE, "Details");

        /* Copy over the search query */
        userLabel.setText(LogQuery.getInstance().getSearchQueryItem("user"));
        containerTypeLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "containerType"));
        formLabel.setText(LogQuery.getInstance().getSearchQueryItem("form"));
        containerLabelLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "containerLabel"));
        actionLabel
            .setText(LogQuery.getInstance().getSearchQueryItem("action"));
        startDateLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "startDate"));
        stopDateLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "stopDate"));
        patientNumLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "patientNumber"));
        inventoryIDLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "inventoryId"));
        detailsLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            "details"));

        Section section = createSection("Search Results");

        LoggingInfoTable loggingTable = new LoggingInfoTable(section, null,
            new String[] { "Fat", "Cat", "Rat" }, new int[] { 5, 5, 5 }, 10);
        loggingTable.adaptToToolkit(toolkit, true);

    }

    @Override
    protected void reload() throws Exception {
    }

}
