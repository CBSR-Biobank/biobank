package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.ProcessingEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable.ColumnsShown;

public class ProcessingEventViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ProcessingEventViewForm";

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ProcessingEventViewForm.class.getName());

    private ProcessingEventAdapter pEventAdapter;
    private ProcessingEventWrapper pEvent;

    private BiobankText centreLabel;

    private BiobankText worksheetLabel;

    private BiobankText dateCreationLabel;

    private BiobankText commentLabel;

    private SpecimenInfoTable sourceSpecimenTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof CollectionEventAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        pEventAdapter = (ProcessingEventAdapter) adapter;
        pEvent = pEventAdapter.getWrapper();
        retrieveProcessingEvent();
        setPartName(Messages.getString("ProcessingEventViewForm.title",
            pEvent.getFormattedCreatedAt()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ProcessingEventViewForm.title",
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

        centreLabel = createReadOnlyLabelledField(client, SWT.NONE, "Centre");
        worksheetLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Worksheet");
        dateCreationLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Creation date");

        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments"));

        setValues();
    }

    private void setValues() {
        setTextValue(centreLabel, pEvent.getCenter().getName());
        setTextValue(worksheetLabel, pEvent.getWorksheet());
        setTextValue(dateCreationLabel, pEvent.getFormattedCreatedAt());
        setTextValue(commentLabel, pEvent.getComment());
    }

    private void createSourceSpecimensSection() {
        Composite client = createSectionWithClient(Messages
            .getString("CollectionEventViewForm.specimens.title"));
        sourceSpecimenTable = new SpecimenInfoTable(client,
            pEvent.getChildSpecimenCollection(true), ColumnsShown.EVENT_FORM,
            10);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
    }

    @Override
    public void reload() {
        retrieveProcessingEvent();
        setPartName(Messages.getString("ProcessingEventViewForm.title",
            pEvent.getFormattedCreatedAt()));
        form.setText(Messages.getString("ProcessingEventViewForm.title",
            pEvent.getFormattedCreatedAt()));
        setValues();
        sourceSpecimenTable.setCollection(pEvent
            .getChildSpecimenCollection(true));
    }

    private void retrieveProcessingEvent() {
        try {
            pEvent.reload();
        } catch (Exception ex) {
            logger.error(
                "Error while retrieving processing event "
                    + pEvent.getFormattedCreatedAt() + "/"
                    + pEvent.getCenter().getNameShort() + "/"
                    + pEvent.getWorksheet(), ex);
        }
    }
}
