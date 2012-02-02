package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
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

    private SpecimenInfoTable aliquotedSpecimenTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ProcessingEventAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        pEventAdapter = (ProcessingEventAdapter) adapter;
        pEvent = pEventAdapter.getWrapper();
        retrieveProcessingEvent();
        setPartName(Messages.getString("ProcessingEventViewForm.title", //$NON-NLS-1$
            pEvent.getFormattedCreatedAt()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ProcessingEventViewForm.title", //$NON-NLS-1$
            pEvent.getFormattedCreatedAt()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createMainSection();
        createSourceSpecimensSection();
        createAliquotedSpecimensSection();

    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        centerLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("ProcessingEvent.field.center.label")); //$NON-NLS-1$
        worksheetLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("ProcessingEvent.field.worksheet.label")); //$NON-NLS-1$
        dateCreationLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("ProcessingEvent.field.date.label")); //$NON-NLS-1$
        activityLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.activity")); //$NON-NLS-1$

        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments")); //$NON-NLS-1$

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
        Composite client = createSectionWithClient(Messages
            .getString("ProcessingEventViewForm.specimens.title")); //$NON-NLS-1$
        sourceSpecimenTable = new SpecimenInfoTable(client,
            pEvent.getSpecimenCollection(true), ColumnsShown.SOURCE_SPECIMENS,
            10);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
    }

    private void createAliquotedSpecimensSection() {
        // FIXME should we show that to clinics ?
        Composite client = createSectionWithClient(Messages
            .getString("CollectionEventViewForm.aliquotedspecimens.title"));
        aliquotedSpecimenTable = new SpecimenInfoTable(client,
            pEvent.getDerivedSpecimenCollection(true), ColumnsShown.ALIQUOTS,
            10);
        aliquotedSpecimenTable.adaptToToolkit(toolkit, true);
        aliquotedSpecimenTable.addClickListener(collectionDoubleClickListener);
    }

    @Override
    public void reload() {
        retrieveProcessingEvent();
        setPartName(Messages.getString("ProcessingEventViewForm.title", //$NON-NLS-1$
            pEvent.getFormattedCreatedAt()));
        form.setText(Messages.getString("ProcessingEventViewForm.title", //$NON-NLS-1$
            pEvent.getFormattedCreatedAt()));
        setValues();
        sourceSpecimenTable.setCollection(pEvent.getSpecimenCollection(true));
    }

    private void retrieveProcessingEvent() {
        try {
            pEvent.reload();
        } catch (Exception ex) {
            logger.error("Error while retrieving processing event " //$NON-NLS-1$
                + pEvent.getFormattedCreatedAt() + "/" //$NON-NLS-1$
                + pEvent.getCenter().getNameShort() + "/" //$NON-NLS-1$
                + pEvent.getWorksheet(), ex);
        }
    }
}
