package edu.ualberta.med.biobank.forms;

import java.util.HashSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
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
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.StudyAddInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SiteEntryForm extends AddressEntryFormCommon {
    private static final I18n i18n = I18nFactory.getI18n(SiteEntryForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.SiteEntryForm";

    @SuppressWarnings("nls")
    private static final String MSG_NEW_SITE_OK =
        i18n.tr("Create a new Biobank site.");
    @SuppressWarnings("nls")
    private static final String MSG_SITE_OK =
        i18n.tr("Edit a Biobank site.");

    private SiteAdapter siteAdapter;

    private final SiteWrapper site = new SiteWrapper(
        SessionManager.getAppService());

    protected Combo session;

    private ComboViewer activityStatusComboViewer;

    private StudyAddInfoTable studiesTable;

    private SiteInfo siteInfo;

    private CommentsInfoTable commentEntryTable;

    private final CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private final BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        updateSiteInfo(adapter.getId());

        String tabName;
        if (site.isNew()) {
            tabName = i18n.tr("New repository site");
            site.setActivityStatus(ActivityStatus.ACTIVE);
        } else {
            tabName =
                i18n.tr("Repository site {0}", site.getNameShort());
        }
        setPartName(tabName);
    }

    private void updateSiteInfo(Integer id) throws Exception {
        if (id != null) {
            siteInfo =
                SessionManager.getAppService().doAction(
                    new SiteGetInfoAction(id));
            site.setWrappedObject(siteInfo.getSite());
        } else {
            siteInfo = new SiteInfo.Builder().build();
            site.setWrappedObject(new Site());
        }

        comment.setWrappedObject(new Comment());
        ((AdapterBase) adapter).setModelObject(site);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws ApplicationException {
        form.setText(i18n.tr("Repository site information"));
        page.setLayout(new GridLayout(1, false));
        createSiteSection();
        createCommentSection();
        createAddressArea(site);
        createStudySection();

        // When adding help uncomment line below
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        // IJavaHelpContextIds.XXXXX);
    }

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client, site.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            i18n.tr("Add a comment"), null, comment, "message", null);
    }

    @SuppressWarnings("nls")
    private void createSiteSection() {
        toolkit
            .createLabel(
                page,
                "Studies, clinics, containers and container types can be added after submitting this information.",
                SWT.LEFT);

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE, HasName.PropertyName.NAME.toString(), null, site,
            SitePeer.NAME.getName(),
            new NonEmptyStringValidator(
                i18n.tr("Site must have a name"))));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            HasNameShort.PropertyName.NAME_SHORT.toString(), null, site,
            SitePeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                i18n.tr("Site short name cannot be blank")));

        activityStatusComboViewer =
            createComboViewer(client,
                ActivityStatus.NAME.singular().toString(),
                ActivityStatus.valuesList(), site.getActivityStatus(),
                i18n.tr("Site must have an activity status"),
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        site.setActivityStatus((ActivityStatus) selectedObject);
                    }
                });

    }

    @SuppressWarnings("nls")
    private void createStudySection() {
        Section section = createSection(Study.NAME.plural().toString());
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
        studiesTable = new StudyAddInfoTable(section, site, superAdmin);
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

    @Override
    protected String getOkMessage() {
        if (site.getId() == null) {
            return MSG_NEW_SITE_OK;
        }
        return MSG_SITE_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        final SiteSaveAction siteSaveAction = new SiteSaveAction();
        siteSaveAction.setId(site.getId());
        siteSaveAction.setName(site.getName());
        siteSaveAction.setNameShort(site.getNameShort());
        siteSaveAction.setActivityStatus(site.getActivityStatus());
        siteSaveAction.setAddress(site.getAddress().getWrappedObject());

        HashSet<Integer> studyIds = new HashSet<Integer>();
        for (StudyWrapper study : site.getStudyCollection(false)) {
            studyIds.add(study.getId());
        }
        siteSaveAction.setStudyIds(studyIds);
        siteSaveAction.setCommentText(comment.getMessage());

        Integer id =
            SessionManager.getAppService().doAction(siteSaveAction).getId();
        updateSiteInfo(id);

        siteAdapter.getParent().performExpand();
        SessionManager.getUser().updateCurrentCenter(site);
    }

    @Override
    public String getNextOpenedFormId() {
        return SiteViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        if (site.isNew()) {
            site.setActivityStatus(ActivityStatus.ACTIVE);
        }
        GuiUtil.reset(activityStatusComboViewer, site.getActivityStatus());
        studiesTable.reload();
        commentEntryTable.setList(site.getCommentCollection(false));
    }
}
