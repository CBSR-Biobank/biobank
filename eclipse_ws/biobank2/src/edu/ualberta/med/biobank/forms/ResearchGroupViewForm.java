package edu.ualberta.med.biobank.forms;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.RequestSubmitAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcFileBrowser;
import edu.ualberta.med.biobank.gui.common.widgets.IBgcFileBrowserListener;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.treeview.admin.ResearchGroupAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ResearchGroupViewForm extends AddressViewFormCommon implements
    IBgcFileBrowserListener {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ResearchGroupViewForm"; //$NON-NLS-1$

    private ResearchGroupWrapper researchGroup = new ResearchGroupWrapper(
        SessionManager.getAppService());

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private BgcBaseText activityStatusLabel;

    private BgcBaseText studyLabel;

    private BgcFileBrowser csvSelector;

    private Button uploadButton;

    private CommentsInfoTable commentTable;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ResearchGroupAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        setRgInfo(adapter.getId());
        setPartName(NLS.bind(Messages.ResearchGroupViewForm_title,
            researchGroup.getNameShort()));
    }

    private void setRgInfo(Integer id) throws ApplicationException {
        if (id == null) {
            ResearchGroup rg = new ResearchGroup();
            researchGroup.setWrappedObject(rg);
            researchGroup.setActivityStatus(ActivityStatus.ACTIVE);
        } else {
            ResearchGroupReadInfo read =
                SessionManager.getAppService().doAction(
                    new ResearchGroupGetInfoAction(id));
            researchGroup.setWrappedObject(read.researchGroup);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.ResearchGroupViewForm_title,
            researchGroup.getName()));

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createResearchGroupSection();
        createAddressSection(researchGroup);
        createUploadSection();
    }

    private void createUploadSection() {
        Composite client =
            createSectionWithClient(Messages.ResearchGroupViewForm_request_upload_title);
        client.setLayout(new GridLayout(3, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        toolkit.createLabel(client,
            Messages.ResearchGroupViewForm_submit_request_rg_label);
        csvSelector =
            new BgcFileBrowser(client,
                Messages.ResearchGroupViewForm_csv_file_label, SWT.NONE,
                new String[] { "*.csv" }); //$NON-NLS-1$
        csvSelector.addFileSelectedListener(this);
        csvSelector.adaptToToolkit(toolkit, true);
        uploadButton = new Button(client, SWT.PUSH);
        uploadButton.setText(Messages.ResearchGroupViewForm_upload_button);
        uploadButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    saveRequest();
                } catch (Exception e1) {
                    BgcPlugin.openAsyncError(
                        Messages.ResearchGroupViewForm_error_title,
                        "There was an error creating the request.");
                }
            }
        });
        uploadButton.setEnabled(false);
    }

    @Override
    public void fileSelected(String filename) {
        uploadButton.setEnabled(true);
    }

    public void saveRequest() throws Exception {
        RequestWrapper request =
            new RequestWrapper(SessionManager.getAppService());

        FileReader f = new FileReader(csvSelector.getFilePath());
        int newLines = 0;
        while (f.ready() && newLines < 4) {
            char c = (char) f.read();
            if (c == '\n') newLines++;
        }

        ICsvBeanReader reader =
            new CsvBeanReader(f, CsvPreference.STANDARD_PREFERENCE);

        final CellProcessor[] processors =
            new CellProcessor[] { null, null,
                new ParseDate("yyyy-MM-dd"), null, null, null }; //$NON-NLS-1$

        List<RequestInput> requests = new ArrayList<RequestInput>();

        try {
            // Peer class not used because this refers to RequestInput fields
            String[] header = new String[] { "pnumber", "inventoryID", //$NON-NLS-1$ //$NON-NLS-2$
                "dateDrawn", "specimenTypeNameShort", "location", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                "activityStatus" }; //$NON-NLS-1$
            RequestInput srequest;
            while ((srequest =
                reader.read(RequestInput.class, header, processors)) != null) {
                if (!srequest.getInventoryID().equals(""))
                    requests.add(srequest);
            }
        } catch (SuperCSVException e) {
            throw new Exception(NLS.bind(
                Messages.ResearchGroupViewForm_parse_error_msg,
                reader.getLineNumber())
                + "\n" + e.getCsvContext()); //$NON-NLS-1$
        } finally {
            reader.close();
        }
        List<String> specs = new ArrayList<String>();

        for (RequestInput ob : requests) {
            specs.add(ob.getInventoryID());
        }

        RequestSubmitAction action =
            new RequestSubmitAction(researchGroup.getId(), specs);
        SessionManager.getAppService().doAction(action);

        BgcPlugin.openMessage(Messages.ResearchGroupViewForm_success_title,
            Messages.ResearchGroupViewForm_success_msg);
        SpecimenTransitView.reloadCurrent();
    }

    private void createResearchGroupSection() {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel =
            createReadOnlyLabelledField(client, SWT.NONE, Messages.label_name);
        nameShortLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_nameShort);
        studyLabel = createReadOnlyLabelledField(client, SWT.NONE, "Study"); //$NON-NLS-1$
        activityStatusLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_activity);

        createCommentsSection();
        setResearchGroupValues();
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable =
            new CommentsInfoTable(client,
                researchGroup.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void setResearchGroupValues() {
        setTextValue(nameLabel, researchGroup.getName());
        setTextValue(nameShortLabel, researchGroup.getNameShort());
        setTextValue(studyLabel, researchGroup.getStudy());
        setTextValue(activityStatusLabel, researchGroup.getActivityStatus());
    }

    @Override
    public void setValues() throws Exception {
        setPartName(NLS.bind(Messages.ResearchGroupViewForm_title,
            researchGroup.getName()));
        form.setText(NLS.bind(Messages.ResearchGroupViewForm_title,
            researchGroup.getName()));
        setResearchGroupValues();
        setAddressValues(researchGroup);
        commentTable.setList(researchGroup.getCommentCollection(false));
    }

}
