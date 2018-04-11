package edu.ualberta.med.biobank.forms;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.RequestSubmitAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcFileBrowser;
import edu.ualberta.med.biobank.gui.common.widgets.IBgcFileBrowserListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.admin.ResearchGroupAdapter;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ResearchGroupStudyInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ResearchGroupViewForm extends AddressViewFormCommon implements
    IBgcFileBrowserListener {
    private static final I18n i18n = I18nFactory
        .getI18n(ResearchGroupViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ResearchGroupViewForm";

    private final ResearchGroupWrapper researchGroup =
        new ResearchGroupWrapper(
            SessionManager.getAppService());

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private BgcBaseText activityStatusLabel;

    private BgcFileBrowser csvSelector;

    private Button uploadButton;

    private CommentsInfoTable commentTable;

    //OHSDEV - Data object for the Research Group
    private ResearchGroupReadInfo rgInfo;
    //OHSDEV - List of Studies associated with the Research Group
    private ResearchGroupStudyInfoTable studiesTable;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ResearchGroupAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        setRgInfo(adapter.getId());
        setPartName(i18n.tr("Research Group {0}",
            researchGroup.getNameShort()));
    }

    private void setRgInfo(Integer id) throws ApplicationException {
        if (id == null) {
            ResearchGroup rg = new ResearchGroup();
            researchGroup.setWrappedObject(rg);
            researchGroup.setActivityStatus(ActivityStatus.ACTIVE);
        } else {
		//OHSDEV - Set the Research Group data object for use in the class
		rgInfo = SessionManager.getAppService().doAction(new ResearchGroupGetInfoAction(id));
            researchGroup.setWrappedObject(rgInfo.getResearchGroup());
            Assert.isNotNull(rgInfo.getStudies());
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Research Group {0}",
            researchGroup.getName()));

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createResearchGroupSection();
        createAddressSection(researchGroup);
        createStudySection();		//OHSDEV - Create the Study table section
        createUploadSection();
    }

	//OHSDEV - Modify the view and remove the row for Study as it will have it's own section in the form
    private void createResearchGroupSection() {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE, HasName.PropertyName.NAME.toString());
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE, HasNameShort.PropertyName.NAME_SHORT.toString());
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE, ActivityStatus.NAME.singular().toString());

        createCommentsSection();
        setResearchGroupValues();
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Comment.NAME.plural().toString());
        commentTable = new CommentsInfoTable(client,
        researchGroup.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    @SuppressWarnings("nls")
    private void createUploadSection() {
        Composite client =
            createSectionWithClient(i18n.tr("Request Upload"));
        client.setLayout(new GridLayout(3, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        toolkit.createLabel(client,
            i18n.tr("Submit a request on behalf of this research group:"));
        csvSelector =
            new BgcFileBrowser(client,
                i18n.tr("CSV File"), SWT.NONE,
                new String[] { "*.csv" });
        csvSelector.addFileSelectedListener(this);
        csvSelector.adaptToToolkit(toolkit, true);
        uploadButton = new Button(client, SWT.PUSH);
        uploadButton.setText("Upload Request");
        uploadButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    saveRequest();
                } catch (Exception e1) {
                    BgcPlugin.openAsyncError(
                        i18n.tr("Error Uploading"),
                        //OHSDEV - provide message from server side
                        i18n.tr("There was an error creating the request.\n"+e1.getLocalizedMessage()));
                }
            }
        });
        uploadButton.setEnabled(false);
    }

	//OHSDEV - Create the Study view in the form
    private void createStudySection() {
        Section section = createSection(Study.NAME.plural().toString());
        studiesTable = new ResearchGroupStudyInfoTable(section, rgInfo.getStudies());
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable.addClickListener(new IInfoTableDoubleClickItemListener<StudyCountInfo>() {
            @Override
            public void doubleClick(InfoTableEvent<StudyCountInfo> event) {
                Study s = ((StudyCountInfo) ((InfoTableSelection)event.getSelection()).getObject()).getStudy();
                new StudyAdapter(null, new StudyWrapper(SessionManager.getAppService(), s)).openViewForm();
            }
        });
        studiesTable.addEditItemListener(new IInfoTableEditItemListener<StudyCountInfo>() {
            @Override
            public void editItem(InfoTableEvent<StudyCountInfo> event) {
                Study s = ((StudyCountInfo) ((InfoTableSelection)event.getSelection()).getObject()).getStudy();
                new StudyAdapter(null, new StudyWrapper(SessionManager.getAppService(), s)).openEntryForm();
            }
        });

        section.setClient(studiesTable);
    }

    @Override
    public void fileSelected(String filename) {
        uploadButton.setEnabled(true);
    }

    @SuppressWarnings({ "nls" })
    public void saveRequest() throws Exception {
        FileReader f = new FileReader(csvSelector.getFilePath());

        //OHSDEV - Commenting out code that serves no purpose
        //OHSDEV - Causes the EOF to be reached so not further processing happens
        //int newLines = 0;
        //while (f.ready() && newLines < 4) {
        //    char c = (char) f.read();
        //    if (c == '\n') newLines++;
        //}

        ICsvBeanReader reader =
            new CsvBeanReader(f, CsvPreference.STANDARD_PREFERENCE);

        final CellProcessor[] processors =
            new CellProcessor[] { null, null,
                new ParseDate("yyyy-MM-dd"), null, null, null };

        List<RequestInput> requests = new ArrayList<RequestInput>();

        try {
            // Peer class not used because this refers to RequestInput fields
            String[] header = new String[] { "pnumber", "inventoryID",
                "dateDrawn", "specimenTypeNameShort", "location",
                "activityStatus" };
            RequestInput srequest;
            while ((srequest =
                reader.read(RequestInput.class, header, processors)) != null) {
                if (!srequest.getInventoryID().isEmpty())
                    requests.add(srequest);
            }
        } catch (SuperCSVException e) {
            throw new Exception(i18n.tr("Parse error at line {0}",
                reader.getLineNumber())
                + "\n" + e.getCsvContext());
        } finally {
            reader.close();
        }

        List<String> specs = new ArrayList<String>();
        for (RequestInput ob : requests) {
            specs.add(ob.getInventoryID());
        }

        List<String> studies = new ArrayList<String>();
        for (StudyCountInfo study : rgInfo.getStudies()) {
            studies.add(study.getStudy().getId().toString());
        }

        RequestSubmitAction action =
            new RequestSubmitAction(researchGroup.getId(), specs, studies, SessionManager.getUser().getCurrentWorkingCenter().getId());
        SessionManager.getAppService().doAction(action);

        BgcPlugin.openMessage(
            // dialog title.
            i18n.tr("Success"),
            i18n.tr("Request successfully uploaded"));
        SpecimenTransitView.reloadCurrent();
    }

    private void setResearchGroupValues() {
        setTextValue(nameLabel, researchGroup.getName());
        setTextValue(nameShortLabel, researchGroup.getNameShort());
        setTextValue(activityStatusLabel, researchGroup.getActivityStatus());
    }

    @SuppressWarnings("nls")
    @Override
    public void setValues() throws Exception {
        setPartName(i18n.tr("Research Group {0}",
            researchGroup.getName()));
        form.setText(i18n.tr("Research Group {0}",
            researchGroup.getName()));
        setResearchGroupValues();
        setAddressValues(researchGroup);
        studiesTable.setList(rgInfo.getStudies());		//Set the value of the Studies table to the list read from the database
        commentTable.setList(researchGroup.getCommentCollection(false));
    }

}