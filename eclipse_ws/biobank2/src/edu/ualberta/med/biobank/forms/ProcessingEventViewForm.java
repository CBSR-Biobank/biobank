package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable.ColumnsShown;

public class ProcessingEventViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ProcessingEventViewForm"; //$NON-NLS-1$

    private static BgcLogger logger = BgcLogger
        .getLogger(ProcessingEventViewForm.class.getName());

    private ProcessingEventAdapter pEventAdapter;
    private ProcessingEventWrapper pEvent;

    private BgcBaseText centerLabel;

    private BgcBaseText worksheetLabel;

    private BgcBaseText dateCreationLabel;

    private BgcBaseText commentLabel;

    private SpecimenInfoTable sourceSpecimenTable;

    private BgcBaseText activityLabel;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ProcessingEventAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        pEventAdapter = (ProcessingEventAdapter) adapter;
        pEvent = pEventAdapter.getWrapper();
        retrieveProcessingEvent();
        setPartName(NLS.bind(Messages.ProcessingEventViewForm_title,
            pEvent.getFormattedCreatedAt()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.ProcessingEventViewForm_title,
            pEvent.getFormattedCreatedAt()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createMainSection();
        createSourceSpecimensSection();
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        centerLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ProcessingEvent_field_center_label);
        worksheetLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ProcessingEvent_field_worksheet_label);
        dateCreationLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ProcessingEvent_field_date_label);
        activityLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_activity);

        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.label_comments);

        setValues();
    }

    private void setValues() {
        setTextValue(centerLabel, pEvent.getCenter().getName());
        setTextValue(worksheetLabel, pEvent.getWorksheet());
        setTextValue(dateCreationLabel, pEvent.getFormattedCreatedAt());
        setTextValue(activityLabel, pEvent.getActivityStatus().getName());
        setTextValue(commentLabel, pEvent.getComment());
    }

    private void createSourceSpecimensSection() {
        Composite client = createSectionWithClient(Messages.ProcessingEventViewForm_specimens_title);
        sourceSpecimenTable = new SpecimenInfoTable(client,
            pEvent.getSpecimenCollection(true), ColumnsShown.SOURCE_SPECIMENS,
            10);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
        sourceSpecimenTable.addClickListener(collectionDoubleClickListener);
    }

    @Override
    public void reload() {
        retrieveProcessingEvent();
        setPartName(NLS.bind(Messages.ProcessingEventViewForm_title,
            pEvent.getFormattedCreatedAt()));
        form.setText(NLS.bind(Messages.ProcessingEventViewForm_title,
            pEvent.getFormattedCreatedAt()));
        setValues();
        sourceSpecimenTable.setCollection(pEvent.getSpecimenCollection(true));
    }

    private void retrieveProcessingEvent() {
        try {
            pEvent.reload();
        } catch (Exception ex) {
            logger.error(Messages.format(
                "Error while retrieving processing event {0}/{1}/{2}", pEvent //$NON-NLS-1$
                    .getFormattedCreatedAt(),
                pEvent.getCenter().getNameShort(), pEvent.getWorksheet()), ex);
        }
    }
}
