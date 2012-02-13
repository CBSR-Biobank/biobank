package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.peer.LogPeer;
import edu.ualberta.med.biobank.export.CsvDataExporter;
import edu.ualberta.med.biobank.export.Data;
import edu.ualberta.med.biobank.export.DataExporter;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.logs.LogQuery;
import edu.ualberta.med.biobank.views.LoggingView;
import edu.ualberta.med.biobank.widgets.infotables.LoggingInfoTable;

public class LoggingForm extends BiobankViewForm {

    public static String ID = "edu.ualberta.med.biobank.forms.LoggingForm"; //$NON-NLS-1$

    private BgcBaseText userLabel;
    private BgcBaseText typeLabel;
    private BgcBaseText actionLabel;
    private BgcBaseText patientNumLabel;
    private BgcBaseText inventoryIDLabel;
    private BgcBaseText locationLabel;
    private BgcBaseText detailsLabel;
    private BgcBaseText startDateLabel;
    private BgcBaseText endDateLabel;

    private BgcBaseText centerLabel;

    private LoggingInfoTable loggingTable;

    @Override
    public void init() throws Exception {
        setPartName(Messages.LoggingForm_title);
    }

    @Override
    protected void createFormContent() throws Exception {

        PlatformUI.getWorkbench().getViewRegistry().find(LoggingView.ID);

        form.setText(Messages.LoggingForm_form_title);
        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_LOGGING));

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
        centerLabel = createReadOnlyLabelledField(leftClient, SWT.NONE,
            Messages.LoggingForm_center_label);
        userLabel = createReadOnlyLabelledField(leftClient, SWT.NONE,
            Messages.LoggingForm_user_label);
        typeLabel = createReadOnlyLabelledField(leftClient, SWT.NONE,
            Messages.LoggingForm_type_label);
        actionLabel = createReadOnlyLabelledField(leftClient, SWT.NONE,
            Messages.LoggingForm_action_label);
        startDateLabel = createReadOnlyLabelledField(leftClient, SWT.NONE,
            Messages.LoggingForm_start_label);

        patientNumLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            Messages.LoggingForm_pnumber_label);
        locationLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            Messages.LoggingForm_location_label);
        inventoryIDLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            Messages.LoggingForm_inventoryid_label);
        detailsLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            Messages.LoggingForm_details_label);
        endDateLabel = createReadOnlyLabelledField(rightClient, SWT.NONE,
            Messages.LoggingForm_end_label);

        getSearchRequestFields();

        generateSearchQueryTable();
    }

    private void generateSearchQueryTable() {
        LogQuery.getInstance().queryDatabase();

        Composite client =
            createSectionWithClient(Messages.LoggingForm_search_title);
        client.setLayout(new GridLayout(1, false));
        Button button = new Button(client, SWT.PUSH);
        button.setText(Messages.LoggingForm_1);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                export(new CsvDataExporter());
            }
        });

        loggingTable = new LoggingInfoTable(client, LogQuery.getInstance()
            .getDatabaseResults());
        loggingTable.adaptToToolkit(toolkit, true);
        loggingTable.setVisible(true);
        loggingTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(loggingTable);
    }

    @SuppressWarnings("unchecked")
    private void export(final DataExporter exporter) {
        final Data data = new Data();
        try {
            data.setColumnNames(Arrays.asList(
                Messages.LoggingForm_center_label,
                Messages.LoggingForm_user_label,
                Messages.LoggingForm_date_label,
                Messages.LoggingForm_action_label,
                Messages.LoggingForm_type_label,
                Messages.LoggingForm_pnumber_label,
                Messages.LoggingForm_inventoryid_label,
                Messages.LoggingForm_location_label,
                Messages.LoggingForm_details_label));
            data.setTitle(Messages.LoggingForm_0);

            data.setRows((List<Object>) (List<?>) LogQuery.getInstance()
                .getDatabaseResults());

            // check if the exporter can export this data
            exporter.canExport(data);
        } catch (Exception e) {
            MessageDialog.openError(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                Messages.ReportEntryForm_export_error_title, e.getMessage());
            return;
        }

        // confirm exporting
        if (!MessageDialog.openQuestion(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            Messages.ReportEntryForm_confirm_title,
            NLS.bind(Messages.ReportEntryForm_export_question,
                exporter.getName()))) {
            return;
        }

        // export
        try {
            exporter.export(data, loggingTable.getLabelProvider());
        } catch (Exception e) {
            MessageDialog.openError(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                Messages.ReportEntryForm_exporting_error_title, e.getMessage());
            return;
        }
    }

    private void getSearchRequestFields() throws Exception {
        centerLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogPeer.CENTER.getName()));
        userLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogPeer.USERNAME.getName()));
        actionLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogPeer.ACTION.getName()));
        typeLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogPeer.TYPE.getName()));
        startDateLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogQuery.START_DATE_KEY));
        endDateLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogQuery.END_DATE_KEY));
        patientNumLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogPeer.PATIENT_NUMBER.getName()));
        inventoryIDLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogPeer.INVENTORY_ID.getName()));
        locationLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogPeer.LOCATION_LABEL.getName()));
        detailsLabel.setText(LogQuery.getInstance().getSearchQueryItem(
            LogPeer.DETAILS.getName()));
    }

    @Override
    public void setValues() throws Exception {
        // TODO Auto-generated method stub

    }

}
