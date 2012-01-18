package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction.PEventInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable.ColumnsShown;

public class ProcessingEventViewForm extends BiobankViewForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.ProcessingEventViewForm"; //$NON-NLS-1$

    private BgcBaseText centerLabel;

    private BgcBaseText worksheetLabel;

    private BgcBaseText dateCreationLabel;

    private NewSpecimenInfoTable sourceSpecimenTable;

    private BgcBaseText activityLabel;

    private CommentCollectionInfoTable commentTable;

    private PEventInfo peventInfo;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ProcessingEventAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        updatePEventInfo();

        setPartName(NLS.bind(Messages.ProcessingEventViewForm_title,
            DateFormatter.formatAsDateTime(peventInfo.pevent.getCreatedAt())));
    }

    private void updatePEventInfo() throws Exception {
        peventInfo =
            SessionManager.getAppService().doAction(
                new ProcessingEventGetInfoAction(adapter.getId()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.ProcessingEventViewForm_title,
            DateFormatter.formatAsDateTime(peventInfo.pevent.getCreatedAt())));
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

        centerLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ProcessingEvent_field_center_label);
        worksheetLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ProcessingEvent_field_worksheet_label);
        dateCreationLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ProcessingEvent_field_date_label);
        activityLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_activity);

        createCommentsSection();

        setValues();
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable = new CommentCollectionInfoTable(client,
            ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(),
                peventInfo.pevent.getCommentCollection(),
                CommentWrapper.class));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void setValues() {
        setTextValue(centerLabel, peventInfo.pevent.getCenter().getName());
        setTextValue(worksheetLabel, peventInfo.pevent.getWorksheet());
        setTextValue(dateCreationLabel,
            DateFormatter.formatAsDateTime(peventInfo.pevent.getCreatedAt()));
        setTextValue(activityLabel, peventInfo.pevent.getActivityStatus()
            .getName());
    }

    private void createSourceSpecimensSection() {
        Composite client =
            createSectionWithClient(Messages.ProcessingEventViewForm_specimens_title);
        sourceSpecimenTable =
            new NewSpecimenInfoTable(client, peventInfo.sourceSpecimenInfos,
                ColumnsShown.PEVENT_SOURCE_SPECIMENS, 10);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
        sourceSpecimenTable.addClickListener(collectionDoubleClickListener);
        sourceSpecimenTable.createDefaultEditItem();
    }

    @Override
    public void reload() throws Exception {
        updatePEventInfo();
        setPartName(NLS.bind(Messages.ProcessingEventViewForm_title,
            DateFormatter.formatAsDateTime(peventInfo.pevent.getCreatedAt())));
        form.setText(NLS.bind(Messages.ProcessingEventViewForm_title,
            DateFormatter.formatAsDateTime(peventInfo.pevent.getCreatedAt())));
        setValues();
        sourceSpecimenTable.setList(peventInfo.sourceSpecimenInfos);

        // TODO: reload comment table
    }

}
