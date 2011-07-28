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
import edu.ualberta.med.biobank.common.peer.ResearchGroupPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.treeview.admin.ResearchGroupAdapter;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

@SuppressWarnings("unused")
public class ResearchGroupEntryForm extends AddressEntryFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ResearchGroupEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_RG_OK = Messages.ResearchGroupEntryForm_creation_msg;

    private static final String MSG_RG_OK = Messages.ResearchGroupEntryForm_msg_ok;

    private static final String MSG_NO_RG_NAME = Messages.ResearchGroupEntryForm_msg_noResearchGroupName;

    private static final String MSG_NO_RG_NAME_SHORT = Messages.ResearchGroupEntryForm_msg_noResearchGroupNameShort;

    private ResearchGroupAdapter researchGroupAdapter;

    private ResearchGroupWrapper researchGroup;

    private BgcEntryFormWidgetListener listener = new BgcEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private ComboViewer activityStatusComboViewer;

    private ComboViewer studyComboViewer;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ResearchGroupAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        researchGroupAdapter = (ResearchGroupAdapter) adapter;
        researchGroup = (ResearchGroupWrapper) adapter.getModelObject();

        String tabName;
        if (researchGroup.isNew()) {
            tabName = Messages.ResearchGroupEntryForm_title_new;
            researchGroup.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else
            tabName = NLS.bind(Messages.ResearchGroupEntryForm_title_edit,
                researchGroup.getNameShort());
        setPartName(tabName);
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
            .getAvailStudies(appService);
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

        activityStatusComboViewer = createComboViewer(client,
            Messages.label_activity,
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            researchGroup.getActivityStatus(),
            Messages.ResearchGroupEntryForm_activity_validator_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    researchGroup
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            Messages.label_comments, null, researchGroup,
            ResearchGroupPeer.COMMENT.getName(), null);
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
        researchGroup.persist();
        SessionManager.updateAllSimilarNodes(researchGroupAdapter, true);
    }

    @Override
    public String getNextOpenedFormID() {
        return ResearchGroupViewForm.ID;
    }

    @Override
    protected void onReset() throws Exception {
        researchGroup.reset();

        if (researchGroup.isNew()) {
            researchGroup.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
            researchGroup.setStudy(null);
        }

        GuiUtil.reset(activityStatusComboViewer,
            researchGroup.getActivityStatus());
        GuiUtil.reset(studyComboViewer, researchGroup.getStudy());

    }
}
