package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction.PEventInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
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

    private CommentsInfoTable commentTable;

    private ProcessingEventWrapper pevent = new ProcessingEventWrapper(
        SessionManager.getAppService());

    private List<SpecimenInfo> specimens;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ProcessingEventAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        setPEventInfo(adapter.getId());

        setPartName(NLS.bind(Messages.ProcessingEventViewForm_title,
            DateFormatter.formatAsDateTime(pevent.getCreatedAt())));
    }

    private void setPEventInfo(Integer id) throws Exception {
        if (id == null) {
            ProcessingEvent p = new ProcessingEvent();
            p.setActivityStatus(ActivityStatus.ACTIVE);
            pevent.setWrappedObject(p);
            specimens = new ArrayList<SpecimenInfo>();
        } else {
            PEventInfo read =
                SessionManager.getAppService().doAction(
                    new ProcessingEventGetInfoAction(adapter.getId()));
            pevent.setWrappedObject(read.pevent);
            specimens = read.sourceSpecimenInfos;
            SessionManager.logLookup(read.pevent);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.ProcessingEventViewForm_title,
            DateFormatter.formatAsDateTime(pevent.getCreatedAt())));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createMainSection();
        createSourceSpecimensSection();
        setValues();
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
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable = new CommentsInfoTable(client,
            pevent.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    @Override
    public void setValues() throws Exception {
        setPartName(NLS.bind(Messages.ProcessingEventViewForm_title,
            DateFormatter.formatAsDateTime(pevent.getCreatedAt())));
        form.setText(NLS.bind(Messages.ProcessingEventViewForm_title,
            DateFormatter.formatAsDateTime(pevent.getCreatedAt())));
        sourceSpecimenTable.setList(specimens);
        commentTable.setList(pevent.getCommentCollection(false));

        setTextValue(centerLabel, pevent.getCenter().getName());
        setTextValue(worksheetLabel, pevent.getWorksheet());
        setTextValue(dateCreationLabel,
            DateFormatter.formatAsDateTime(pevent.getCreatedAt()));
        setTextValue(activityLabel, pevent.getActivityStatus()
            .getName());
    }

    private void createSourceSpecimensSection() {
        Composite client =
            createSectionWithClient(Messages.ProcessingEventViewForm_specimens_title);
        sourceSpecimenTable =
            new NewSpecimenInfoTable(client, specimens,
                ColumnsShown.PEVENT_SOURCE_SPECIMENS, 10);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
        sourceSpecimenTable
            .addClickListener(new IInfoTableDoubleClickItemListener<SpecimenInfo>() {

                @Override
                public void doubleClick(InfoTableEvent<SpecimenInfo> event) {
                    Specimen s =
                        ((SpecimenInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).specimen;
                    AdapterBase.openForm(
                        new FormInput(
                            new SpecimenAdapter(null,
                                new SpecimenWrapper(SessionManager
                                    .getAppService(), s))),
                        SpecimenViewForm.ID);
                }
            });
        sourceSpecimenTable
            .addEditItemListener(new IInfoTableEditItemListener<SpecimenInfo>() {
                @Override
                public void editItem(InfoTableEvent<SpecimenInfo> event) {
                    Specimen s =
                        ((SpecimenInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).specimen;
                    AdapterBase.openForm(
                        new FormInput(
                            new SpecimenAdapter(null,
                                new SpecimenWrapper(SessionManager
                                    .getAppService(), s))),
                        SpecimenEntryForm.ID);
                }
            });
    }

}
