package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.AddressSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupAdapterInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupSaveAction;
import edu.ualberta.med.biobank.common.peer.ResearchGroupPeer;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.treeview.admin.ResearchGroupAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

@SuppressWarnings("unused")
public class ResearchGroupEntryForm extends AddressEntryFormCommon {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ResearchGroupEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_RG_OK =
        Messages.ResearchGroupEntryForm_creation_msg;

    private static final String MSG_RG_OK =
        Messages.ResearchGroupEntryForm_msg_ok;

    private static final String MSG_NO_RG_NAME =
        Messages.ResearchGroupEntryForm_msg_noResearchGroupName;

    private static final String MSG_NO_RG_NAME_SHORT =
        Messages.ResearchGroupEntryForm_msg_noResearchGroupNameShort;

    private ResearchGroupAdapter researchGroupAdapter;

    private ResearchGroupWrapper researchGroup = new ResearchGroupWrapper(
        SessionManager.getAppService());

    private BgcBaseText commentWidget;
    private CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private ComboViewer activityStatusComboViewer;

    private ComboViewer studyComboViewer;

    private CommentCollectionInfoTable commentEntryTable;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ResearchGroupAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        researchGroupAdapter = (ResearchGroupAdapter) adapter;

        setRgInfo(adapter.getId());

        String tabName;
        if (researchGroup.isNew()) {
            tabName = Messages.ResearchGroupEntryForm_title_new;
        } else
            tabName = NLS.bind(Messages.ResearchGroupEntryForm_title_edit,
                researchGroup.getNameShort());
        setPartName(tabName);
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
    protected String getOkMessage() {
        if (researchGroup.getId() == null) {
            return MSG_NEW_RG_OK;
        }
        return MSG_RG_OK;
    }

    @Override
    protected void createFormContent() throws ApplicationException {
        form.setText(Messages.ResearchGroupEntryForm_main_title);
        page.setLayout(new GridLayout(1, false));
        toolkit.createLabel(page,
            Messages.ResearchGroupEntryForm_main_description, SWT.LEFT);
        createResearchGroupInfoSection();
        createAddressArea(researchGroup);
        createButtonsSection();

    }

    private void createResearchGroupInfoSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE, Messages.label_name, null, researchGroup,
            ResearchGroupPeer.NAME.getName(), new NonEmptyStringValidator(
                MSG_NO_RG_NAME)));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.label_nameShort, null, researchGroup,
            ResearchGroupPeer.NAME_SHORT.getName(),
            new NonEmptyStringValidator(MSG_NO_RG_NAME_SHORT));

        toolkit.paintBordersFor(client);

        List<StudyWrapper> availableStudies = ResearchGroupWrapper
            .getAvailStudies(SessionManager.getAppService());
        if (!researchGroup.isNew())
            availableStudies.add(researchGroup.getStudy());

        studyComboViewer = createComboViewer(client,
            Messages.ResearchGroupEntryForm_study_label, availableStudies,
            researchGroup.getStudy(),
            Messages.ResearchGroupEntryForm_study_validator_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    researchGroup.setStudy((StudyWrapper) selectedObject);
                }
            });
        studyComboViewer.getControl().setEnabled(
            SessionManager.getUser().isSuperAdmin());

        activityStatusComboViewer = createComboViewer(client,
            Messages.label_activity,
            ActivityStatus.valuesList(), researchGroup.getActivityStatus(),
            Messages.ResearchGroupEntryForm_activity_validator_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    researchGroup
                        .setActivityStatus((ActivityStatus) selectedObject);
                }
            });

        createCommentSection();

    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentCollectionInfoTable(client,
            researchGroup.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        commentWidget =
            (BgcBaseText) createBoundWidgetWithLabel(client, BgcBaseText.class,
                SWT.MULTI,
                Messages.Comments_add, null, comment, "message", null);

    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
    }

    @Override
    public void saveForm() throws Exception {
        AddressSaveInfo addressInfo =
            new AddressSaveInfo(researchGroup.getAddress().getId(),
                researchGroup.getAddress().getStreet1(), researchGroup
                    .getAddress().getStreet2(), researchGroup.getAddress()
                    .getCity(), researchGroup.getAddress().getProvince(),
                researchGroup.getAddress().getPostalCode(), researchGroup
                    .getAddress().getEmailAddress(),
                researchGroup.getAddress().getPhoneNumber(), researchGroup
                    .getAddress().getFaxNumber(), researchGroup.getAddress()
                    .getCountry());
        ResearchGroupSaveInfo info =
            new ResearchGroupSaveInfo(researchGroup.getId(),
                researchGroup.getName(), researchGroup.getNameShort(),
                researchGroup.getStudy().getId(),
                comment.getMessage() == null ? ""
                    : comment.getMessage(), addressInfo, researchGroup
                    .getActivityStatus());
        ResearchGroupSaveAction save = new ResearchGroupSaveAction(info);
        Integer id = SessionManager.getAppService().doAction(save)
            .getId();
        ResearchGroupReadInfo read =
            SessionManager.getAppService().doAction(
                new ResearchGroupGetInfoAction(id));
        researchGroup.setWrappedObject(read.researchGroup);
        adapter.setValue(new ResearchGroupAdapterInfo(read.researchGroup.getId(), read.researchGroup
            .getName()));
    }

    @Override
    protected void doAfterSave() throws Exception {
        SessionManager.getUser().updateCurrentCenter(researchGroup);
    }

    @Override
    public String getNextOpenedFormId() {
        return ResearchGroupViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        try {
            if (researchGroup.isNew()) {
                researchGroup.setActivityStatus(ActivityStatus.ACTIVE);
                researchGroup.setStudy(null);
            }

            GuiUtil.reset(activityStatusComboViewer,
                researchGroup.getActivityStatus());
            GuiUtil.reset(studyComboViewer, researchGroup.getStudy());
        } catch (Exception e) {
            BgcPlugin.openAsyncError("Error", "Unable to reload form");
        }

    }
}
