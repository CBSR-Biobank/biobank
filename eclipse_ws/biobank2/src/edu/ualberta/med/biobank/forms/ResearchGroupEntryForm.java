package edu.ualberta.med.biobank.forms;

import java.util.HashSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.AddressSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupSaveAction;
import edu.ualberta.med.biobank.common.peer.ResearchGroupPeer;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ResearchGroupAdapter;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.ResearchGroupStudyAddInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

@SuppressWarnings("unused")
public class ResearchGroupEntryForm extends AddressEntryFormCommon {
    private static final I18n i18n = I18nFactory
        .getI18n(ResearchGroupEntryForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ResearchGroupEntryForm";

    @SuppressWarnings("nls")
    private static final String MSG_NEW_RG_OK =
        i18n.tr("New Research Group information.");

    @SuppressWarnings("nls")
    private static final String MSG_RG_OK =
        i18n.tr("Research Group information.");

    @SuppressWarnings("nls")
    private static final String MSG_NO_RG_NAME =
        i18n.tr("Research Group must have a name");

    @SuppressWarnings("nls")
    private static final String MSG_NO_RG_NAME_SHORT =
        i18n.tr("Research Group must have a short name");

    @SuppressWarnings("nls")
    private static final String MSG_NO_STUDIES =
        i18n.tr(" (A Study needs to be associated with the Research Group)");

    @SuppressWarnings("nls")
    private static final String ERR_NO_STUDIES =
        i18n.tr(" A Study needs to be associated with the Research Group. Please select a Study !");

    private ResearchGroupAdapter researchGroupAdapter;


    //OHSDEV - List of Studies associated with the Research Group
    private ResearchGroupStudyAddInfoTable studiesTable;

    //OHSDEV - Data object for the Research Group
    private ResearchGroupReadInfo rgInfo;

    private boolean showStudyTitleMessage = false;

    private final ResearchGroupWrapper researchGroup =
        new ResearchGroupWrapper(
            SessionManager.getAppService());

    private BgcBaseText commentWidget;
    private final CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private final BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private ComboViewer activityStatusComboViewer;

    private ComboViewer studyComboViewer;

    private CommentsInfoTable commentEntryTable;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ResearchGroupAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        researchGroupAdapter = (ResearchGroupAdapter) adapter;

        setRgInfo(adapter.getId());

        String tabName;
        if (researchGroup.isNew()) {
            tabName = i18n.tr("New Research Group");
        } else
            tabName = i18n.tr("Research Group {0}",
                researchGroup.getNameShort());
        setPartName(tabName);

        //OHSDEV
        if(researchGroup.getStudyCollection().isEmpty())
		showStudyTitleMessage = true;
    }

    private void setRgInfo(Integer id) throws ApplicationException {
        if (id == null) {
            researchGroup.setWrappedObject(new ResearchGroup());
            researchGroup.setActivityStatus(ActivityStatus.ACTIVE);
        } else {
		//OHSDEV - Set the Research Group data object for use in the class
            rgInfo = SessionManager.getAppService().doAction(new ResearchGroupGetInfoAction(id));
            researchGroup.setWrappedObject(rgInfo.getResearchGroup());
        }
        comment.setWrappedObject(new Comment());
    }

    @Override
    protected String getOkMessage() {
        if (researchGroup.getId() == null) {
            return MSG_NEW_RG_OK;
        }
        return MSG_RG_OK;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws ApplicationException {
        form.setText(i18n.tr("Research Group Information"));
        page.setLayout(new GridLayout(1, false));
        toolkit
            .createLabel(
                page,
                i18n.tr("Research Groups can be associated with studies after submitting this initial information."),
                SWT.LEFT);
        createResearchGroupInfoSection();
        createAddressArea(researchGroup);
        createStudySection();     //OHSDEV - Create the Study table section
        createButtonsSection();

    }

    @SuppressWarnings("nls")
    private void createResearchGroupInfoSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE, HasName.PropertyName.NAME.toString(), null,
            researchGroup,
            ResearchGroupPeer.NAME.getName(), new NonEmptyStringValidator(
                MSG_NO_RG_NAME)));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            HasNameShort.PropertyName.NAME_SHORT.toString(), null,
            researchGroup,
            ResearchGroupPeer.NAME_SHORT.getName(),
            new NonEmptyStringValidator(MSG_NO_RG_NAME_SHORT));

        toolkit.paintBordersFor(client);

        activityStatusComboViewer = createComboViewer(client,
            ActivityStatus.NAME.singular().toString(),
            ActivityStatus.valuesList(), researchGroup.getActivityStatus(),
            i18n.tr("Research Group must have an activity status"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    researchGroup
                        .setActivityStatus((ActivityStatus) selectedObject);
                }
            });

        createCommentSection();

    }

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentsInfoTable(client,
            researchGroup.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        commentWidget =
            (BgcBaseText) createBoundWidgetWithLabel(client, BgcBaseText.class,
                SWT.MULTI,
                i18n.tr("Add a comment"), null, comment, "message", null);

    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
    }

    //OHSDEV - Create the Study view in the form
    @SuppressWarnings("nls")
    private void createStudySection() {

	String title = Study.NAME.plural().toString();

	if(showStudyTitleMessage)
		title = title + MSG_NO_STUDIES; //OHSDEV - Set the section header with a message in RED if a study is NOT associated

	Section section = createSection(title);

	if(showStudyTitleMessage)
		section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED)); //OHSDEV - Set the section header with a message in RED if a study is NOT associated

        boolean superAdmin = SessionManager.getUser().isSuperAdmin();
        if (superAdmin) {
            addSectionToolbar(section, i18n.tr("Add Study "),
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        studiesTable.createStudyDlg();
                    }
                }, ContactWrapper.class);
        }
        studiesTable = new ResearchGroupStudyAddInfoTable(section, researchGroup, superAdmin);
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable
            .addClickListener(new IInfoTableDoubleClickItemListener<StudyWrapper>() {

                @Override
                public void doubleClick(InfoTableEvent<StudyWrapper> event) {
                    StudyWrapper s =
                        ((StudyWrapper) ((InfoTableSelection) event
                            .getSelection()).getObject());
                    new StudyAdapter(null, s).openViewForm();

                }
            });
        studiesTable
            .addEditItemListener(new IInfoTableEditItemListener<StudyWrapper>() {
                @Override
                public void editItem(InfoTableEvent<StudyWrapper> event) {
                    StudyWrapper s =
                        ((StudyWrapper) ((InfoTableSelection) event
                            .getSelection()).getObject());
                    new StudyAdapter(null,
                        s).openEntryForm();
                }
            });
        studiesTable.addSelectionChangedListener(listener);
        section.setClient(studiesTable);
    }

    @SuppressWarnings("null")
    @Override
    public void saveForm() throws Exception
    {
        AddressSaveInfo addressInfo =
            new AddressSaveInfo(
			researchGroup.getAddress().getId(),
			researchGroup.getAddress().getStreet1(),
			researchGroup.getAddress().getStreet2(),
			researchGroup.getAddress().getCity(),
			researchGroup.getAddress().getProvince(),
			researchGroup.getAddress().getPostalCode(),
			researchGroup.getAddress().getEmailAddress(),
			researchGroup.getAddress().getPhoneNumber(),
			researchGroup.getAddress().getFaxNumber(),
			researchGroup.getAddress().getCountry());

        HashSet<Integer> studyIds = new HashSet<Integer>();
        for (StudyWrapper study : researchGroup.getStudyCollection(false)) {
            studyIds.add(study.getId());
        }

        if(studyIds != null && studyIds.size() <= 0) {
		throw new ApplicationException(ERR_NO_STUDIES);
        }

        ResearchGroupSaveInfo saveInfo = new ResearchGroupSaveInfo(
															researchGroup.getId(),
															researchGroup.getName(),
															researchGroup.getNameShort(),
															comment.getMessage() == null ? StringUtil.EMPTY_STRING : comment.getMessage(),
															addressInfo,
															researchGroup.getActivityStatus());


	ResearchGroupSaveAction researchGroupSaveAction = new ResearchGroupSaveAction(saveInfo);
        researchGroupSaveAction.setStudyIds(studyIds);

        Integer id = SessionManager.getAppService().doAction(researchGroupSaveAction).getId();
        ResearchGroupReadInfo read = SessionManager.getAppService().doAction(new ResearchGroupGetInfoAction(id));

        researchGroup.setWrappedObject(read.getResearchGroup());

        ((AdapterBase) adapter).setModelObject(researchGroup);
        researchGroupAdapter.getParent().performExpand();
    }

    @Override
    protected void doAfterSave() throws Exception {
        SessionManager.getUser().updateCurrentCenter(researchGroup);
    }

    @Override
    public String getNextOpenedFormId() {
        return ResearchGroupViewForm.ID;
    }

    @SuppressWarnings("nls")
    @Override
    public void setValues() throws Exception {
        try {
            if (researchGroup.isNew()) {
                researchGroup.setActivityStatus(ActivityStatus.ACTIVE);
            }

            GuiUtil.reset(activityStatusComboViewer, researchGroup.getActivityStatus());
            studiesTable.reload();	//Refresh the Studies table view
            commentEntryTable.setList(researchGroup.getCommentCollection(false));
        } catch (Exception e) {
            BgcPlugin.openAsyncError(i18n.tr("Unable to reload form"));
        }

    }
}