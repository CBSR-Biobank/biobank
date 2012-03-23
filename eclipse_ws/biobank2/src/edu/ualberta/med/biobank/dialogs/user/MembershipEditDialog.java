package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget.PermissionTreeRes;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class MembershipEditDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;
    private final ManagerContext context;
    private ComboViewer centersViewer;
    private ComboViewer studiesViewer;
    private ComboViewer ranksViewer;
    private MultiSelectWidget<Role> rolesWidget;
    private MembershipWrapper ms;
    private PermissionCheckTreeWidget permissionsTree;

    public MembershipEditDialog(Shell parent, MembershipWrapper ms,
        ManagerContext context) {
        super(parent);
        Assert.isNotNull(ms);
        this.ms = ms;
        this.context = context;
        currentTitle = Messages.MembershipAddDialog_title;
        titleAreaMessage = Messages.MembershipAddDialog_description;
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return titleAreaMessage;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TabFolder tb = new TabFolder(contents, SWT.TOP);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        tb.setLayoutData(gd);

        Composite centerStudyComp = createTabItem(tb,
            Messages.MembershipAddDialog_centerStudy_tab_title, 1);
        createRankAndLevel(centerStudyComp);
        createCentersCombo(centerStudyComp);
        createStudysCombo(centerStudyComp);

        createRolesWidget(createTabItem(tb,
            Messages.MembershipAddDialog_roles_tab_title, 1));

        createPermissionWidgets(createTabItem(tb,
            Messages.MembershipAddDialog_permissions_tab_title, 1));

        updateRoleAndPermissionOptions();
    }

    protected void createRankAndLevel(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));

        ranksViewer =
            createComboViewer(contents,
                "Rank",
                Rank.valuesList(),
                ms.getWrappedObject().getRank(), null,
                null, new LabelProvider() {
                    @Override
                    public String getText(Object element) {
                        return ((Rank) element).getName();
                    }
                });
        ranksViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    ms.getWrappedObject().setRank(getRankSelection());
                    updateRoleAndPermissionOptions();
                }
            });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void createStudysCombo(Composite parent)
        throws ApplicationException {
        Group groupComp = new Group(parent, SWT.SHADOW_IN);
        groupComp.setText(Messages.MembershipAddDialog_study_label);
        groupComp.setLayout(new GridLayout(2, false));
        groupComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        List studies = new ArrayList();
        String noStudySelection = new String(
            Messages.MembershipAddDialog_all_studies_label);
        studies.add(0, noStudySelection);
        studies.addAll(StudyWrapper.getAllStudies(SessionManager
            .getAppService()));
        studiesViewer = createComboViewer(groupComp,
            Messages.MembershipAddDialog_selected_study_label, studies,
            ms.getStudy() == null ? noStudySelection : ms.getStudy(), null,
            null, new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof String)
                        return (String) element;
                    return ((StudyWrapper) element).getNameShort();
                }
            });
        studiesViewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                Object element) {
                StudyWrapper study = null;
                if (element instanceof StudyWrapper)
                    study = (StudyWrapper) element;
                return !ms
                    .isCenterStudyAlreadyUsed(getCenterSelection(), study);
            }
        });
        studiesViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    ms.setStudy(getStudySelection());
                    updateRoleAndPermissionOptions();
                }
            });
    }

    private void createCentersCombo(Composite contents)
        throws ApplicationException {
        Group groupComp = new Group(contents, SWT.SHADOW_IN);
        groupComp.setText(Messages.MembershipAddDialog_center_label);
        groupComp.setLayout(new GridLayout(5, false));
        groupComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        widgetCreator.createLabel(groupComp,
            Messages.MembershipAddDialog_filter_center_label);
        final Button allCentersRadio = new Button(groupComp, SWT.RADIO);
        allCentersRadio.setSelection(true);
        allCentersRadio.setText(Messages.MembershipAddDialog_none_label);
        final Button sitesRadio = new Button(groupComp, SWT.RADIO);
        sitesRadio.setText(Messages.MembershipAddDialog_sitesOnly_label);
        final Button clinicsRadio = new Button(groupComp, SWT.RADIO);
        clinicsRadio.setText(Messages.MembershipAddDialog_clinicsOnly_label);
        final Button rgRadio = new Button(groupComp, SWT.RADIO);
        rgRadio.setText(Messages.MembershipAddDialog_rgOnly_label);

        List<Object> centers = new ArrayList<Object>();
        final String noCenterSelection = new String(
            Messages.MembershipAddDialog_all_centers_label);
        centers
            .addAll(getCenterOptions());
        centersViewer = createComboViewer(groupComp,
            Messages.MembershipAddDialog_selected_center_label, centers,
            ms.getCenter() == null ? noCenterSelection : ms.getCenter(), null,
            null, new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof String)
                        return (String) element;
                    return ((CenterWrapper<?>) element).getNameShort();
                }
            });

        centersViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    ms.setCenter(getCenterSelection());
                    updateStudyOptions();
                }
            });
        GridData gd = (GridData) centersViewer.getControl().getLayoutData();
        gd.horizontalSpan = 4;

        centersViewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                Object element) {
                if (element instanceof String)
                    return true;
                if (element instanceof SiteWrapper)
                    return allCentersRadio.getSelection()
                        || sitesRadio.getSelection();
                if (element instanceof ClinicWrapper)
                    return allCentersRadio.getSelection()
                        || clinicsRadio.getSelection();
                if (element instanceof ResearchGroupWrapper)
                    return allCentersRadio.getSelection()
                        || rgRadio.getSelection();
                return false;
            }
        });
        SelectionListener selListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                centersViewer.refresh();
                if (((IStructuredSelection) centersViewer.getSelection())
                    .isEmpty()) {
                    centersViewer.setSelection(new StructuredSelection(
                        noCenterSelection));
                }
            }
        };
        allCentersRadio.addSelectionListener(selListener);
        sitesRadio.addSelectionListener(selListener);
        clinicsRadio.addSelectionListener(selListener);
        rgRadio.addSelectionListener(selListener);
    }

    private Composite createTabItem(TabFolder tb, String title, int columns) {
        TabItem item = new TabItem(tb, SWT.NONE);
        item.setText(title);
        Composite contents = new Composite(tb, SWT.NONE);
        contents.setLayout(new GridLayout(columns, false));
        item.setControl(contents);
        return contents;
    }

    private void createRolesWidget(Composite contents)
        throws ApplicationException {
        GridData gd;
        rolesWidget = new MultiSelectWidget<Role>(contents, SWT.NONE,
            Messages.MembershipAddDialog_roles_available_label,
            Messages.MembershipAddDialog_roles_selected_label, 300) {
            @Override
            protected String getTextForObject(Role nodeObject) {
                return nodeObject.getName();
            }
        };
        rolesWidget.setSelections(
            new ArrayList<Role>(context.getRoles()),
            new ArrayList<Role>(ms.getWrappedObject().getRoles()));
        gd = (GridData) rolesWidget.getLayoutData();
        gd.horizontalSpan = 2;
        rolesWidget
            .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    checkedRolesOrPermissionsSelected();
                    updatePermissions();
                }
            });
    }

    protected void checkedRolesOrPermissionsSelected() {
        if (rolesWidget.getSelected().isEmpty()
            && !permissionsTree.hasCheckedItems())
            setErrorMessage(Messages.MembershipAddDialog_role_error_msg);
        else
            setErrorMessage(null);
    }

    private void createPermissionWidgets(Composite contents) {
        permissionsTree = new PermissionCheckTreeWidget(contents, false,
            PermissionEnum.valuesList());
        permissionsTree.setSelections(ms.getPermissionCollection());

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        permissionsTree.setLayoutData(gd);

        permissionsTree.addCheckedListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (!event.getChecked()) {
                    checkedRolesOrPermissionsSelected();
                }
            }
        });
    }

    @Override
    protected void okPressed() {
        if (rolesWidget.getSelected().isEmpty()
            && !permissionsTree.hasCheckedItems()) {
            checkedRolesOrPermissionsSelected();
            return;
        }
        ms.getWrappedObject().getRoles()
            .removeAll(rolesWidget.getRemovedFromSelection());
        ms.getWrappedObject().getRoles()
            .addAll(rolesWidget.getAddedToSelection());
        PermissionTreeRes res = permissionsTree.getAddedAndRemovedNodes();
        ms.addToPermissionCollection(res.addedPermissions);
        ms.removeFromPermissionCollection(res.removedPermissions);
        super.okPressed();
    }

    private StudyWrapper getStudySelection() {
        ISelection sel = studiesViewer.getSelection();
        if (sel != null) {
            IStructuredSelection structSel = (IStructuredSelection) sel;
            if (structSel.size() > 0) {
                Object selection = structSel.getFirstElement();
                if (selection instanceof String)
                    return null;
                return (StudyWrapper) selection;
            }
        }
        return null;
    }

    private Rank getRankSelection() {
        ISelection sel = ranksViewer.getSelection();
        if (sel != null) {
            IStructuredSelection structSel = (IStructuredSelection) sel;
            if (structSel.size() > 0) {
                Object selection = structSel.getFirstElement();
                return (Rank) selection;
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private CenterWrapper<?> getCenterSelection() {
        ISelection sel = centersViewer.getSelection();
        if (sel != null) {
            IStructuredSelection structSel = (IStructuredSelection) sel;
            if (structSel.size() > 0) {
                Object selection = structSel.getFirstElement();
                CenterWrapper center =
                    (selection instanceof String) ? null
                        : (CenterWrapper) selection;
                return center;
            }
        }
        return null;
    }

    public MembershipWrapper getMembership() {
        return ms;
    }

    @Override
    protected void cancelPressed() {
        if (ms.isNew())
            ms.setPrincipal(null);
        super.cancelPressed();
    }

    private List getCenterOptions() {
        Set<Center> options = new HashSet<Center>();

        boolean hasAllCenters = false;
        User manager = context.getManager();
        for (Membership m : manager.getAllMemberships()) {
            if (m.getCenter() == null) {
                hasAllCenters = true;

                HQLCriteria criteria =
                    new HQLCriteria("from " + Center.class.getName(),
                        new ArrayList<Object>());

                try {
                    List<Center> centers =
                        SessionManager.getAppService().query(criteria);
                    options.addAll(centers);
                } catch (ApplicationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            } else {
                options.add(m.getCenter());
            }
        }

        List wrappedOptions =
            ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
                options, null);

        if (hasAllCenters) {
            final String noCenterSelection = new String(
                Messages.MembershipAddDialog_all_centers_label);
            wrappedOptions.add(0, noCenterSelection);
        }

        return wrappedOptions;
    }

    public void updateStudyOptions() {
        CenterWrapper<?> selectedCenter = getCenterSelection();
        Center modelCenter =
            selectedCenter != null ? selectedCenter.getWrappedObject() : null;

        Set<Study> studies = new HashSet<Study>();
        boolean hasAllStudies = false;

        for (Membership m : context.getManager().getAllMemberships()) {
            Center mCenter = m.getCenter();
            if (mCenter == null || mCenter.equals(modelCenter)) {
                Study study = m.getStudy();
                if (study == null) {
                    hasAllStudies = true;
                    HQLCriteria criteria =
                        new HQLCriteria("from " + Study.class.getName(),
                            new ArrayList<Object>());

                    try {
                        List<Study> tmp =
                            SessionManager.getAppService().query(criteria);
                        studies.addAll(tmp);
                    } catch (ApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
                } else {
                    studies.add(study);
                }
            }
        }

        @SuppressWarnings("rawtypes")
        List wrappedStudies =
            ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
                studies, StudyWrapper.class);

        String noStudySelection = new String(
            Messages.MembershipAddDialog_all_studies_label);
        if (hasAllStudies) {
            wrappedStudies.add(0, noStudySelection);
        }

        StudyWrapper oldSelection = getStudySelection();
        studiesViewer.setInput(wrappedStudies);
        studiesViewer.setSelection(new StructuredSelection(oldSelection));
        studiesViewer.refresh();
    }

    private void updateRoleAndPermissionOptions() {
        User user = context.getManager();
        Membership m = ms.getWrappedObject();

        Set<Role> mRoles = m.getManageableRoles(user, context.getRoles());

        List<Role> mRolesList = new ArrayList<Role>(mRoles);

        rolesWidget.setSelections(mRolesList, rolesWidget.getSelected());

        updatePermissions();
    }

    private void updatePermissions() {
        User user = context.getManager();
        Membership m = ms.getWrappedObject();
        Set<PermissionEnum> mPerms = m.getManageablePermissions(user);

        // only show permissions not already encompased by roles.
        List<PermissionEnum> mPermsList = new ArrayList<PermissionEnum>(mPerms);
        for (Role role : rolesWidget.getSelected()) {
            mPermsList.removeAll(role.getPermissions());
        }

        Set<PermissionEnum> selPerms = permissionsTree.getCheckedElements();
        selPerms.retainAll(mPermsList);

        permissionsTree.setInput(mPermsList);
        permissionsTree.setSelections(selPerms);
    }
}