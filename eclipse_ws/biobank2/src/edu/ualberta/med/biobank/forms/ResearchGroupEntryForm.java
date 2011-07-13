package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
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

public class ResearchGroupEntryForm extends AddressEntryFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ResearchGroupEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_CLINIC_OK = Messages
        .getString("ResearchGroupEntryForm.creation.msg"); //$NON-NLS-1$

    private static final String MSG_CLINIC_OK = Messages
        .getString("ResearchGroupEntryForm.msg.ok"); //$NON-NLS-1$

    private static final String MSG_NO_CLINIC_NAME = Messages
        .getString("ResearchGroupEntryForm.msg.noResearchGroupName"); //$NON-NLS-1$

    private static final String MSG_NO_CLINIC_NAME_SHORT = Messages
        .getString("ResearchGroupEntryForm.msg.noResearchGroupNameShort"); //$NON-NLS-1$

    private ResearchGroupAdapter researchGroupAdapter;

    private ResearchGroupWrapper researchGroup;

    protected Combo session;

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
        researchGroup = (ResearchGroupWrapper) getModelObject();

        String tabName;
        if (researchGroup.isNew()) {
            tabName = Messages.getString("ResearchGroupEntryForm.title.new"); //$NON-NLS-1$
            researchGroup.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else
            tabName = Messages.getString("ResearchGroupEntryForm.title.edit", //$NON-NLS-1$
                researchGroup.getNameShort());
        setPartName(tabName);
    }

    @Override
    protected String getOkMessage() {
        if (researchGroup.getId() == null) {
            return MSG_NEW_CLINIC_OK;
        }
        return MSG_CLINIC_OK;
    }

    @Override
    protected void createFormContent() throws ApplicationException {
        form.setText(Messages.getString("ResearchGroupEntryForm.main.title")); //$NON-NLS-1$
        page.setLayout(new GridLayout(1, false));
        toolkit
            .createLabel(
                page,
                Messages.getString("ResearchGroupEntryForm.main.description"), SWT.LEFT); //$NON-NLS-1$
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
            SWT.NONE, Messages.getString("label.name"), null, researchGroup, //$NON-NLS-1$
            ResearchGroupPeer.NAME.getName(), new NonEmptyStringValidator(
                MSG_NO_CLINIC_NAME)));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.getString("label.nameShort"), null, researchGroup, //$NON-NLS-1$
            ResearchGroupPeer.NAME_SHORT.getName(),
            new NonEmptyStringValidator(MSG_NO_CLINIC_NAME_SHORT));

        toolkit.paintBordersFor(client);

        studyComboViewer = createComboViewer(client,
            Messages.getString("label.study"),
            ResearchGroupWrapper.getAvailStudies(appService),
            researchGroup.getStudy(),
            Messages.getString("ResearchGroupEntryForm.study.validator.msg"), //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    researchGroup.setStudy((StudyWrapper) selectedObject);
                }
            });

        activityStatusComboViewer = createComboViewer(
            client,
            Messages.getString("label.activity"), //$NON-NLS-1$
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            researchGroup.getActivityStatus(),
            Messages.getString("ResearchGroupEntryForm.activity.validator.msg"), //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    researchGroup
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            Messages.getString("label.comments"), null, researchGroup, //$NON-NLS-1$
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
