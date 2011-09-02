package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.widgets.infotables.PermissionInfoTable;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class MembershipAddDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;
    private PrincipalWrapper<?> principal;
    private ComboViewer centersViewer;
    private ComboViewer studiesViewer;
    private MultiSelectWidget<RoleWrapper> rolesWidget;
    private PermissionInfoTable permissionsInfoTable;
    private Section rpSection;
    private MembershipWrapper ms;
    private Composite contents;
    private List<PermissionWrapper> addedPermissions = new ArrayList<PermissionWrapper>();

    public MembershipAddDialog(Shell parent, PrincipalWrapper<?> principal) {
        super(parent);
        Assert.isNotNull(principal);
        this.principal = principal;
        currentTitle = Messages.MembershipAddDialog_title;
        titleAreaMessage = Messages.MembershipAddDialog_description;
        ms = new MembershipWrapper(SessionManager.getAppService());
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createCentersCombo(contents);

        List studies = new ArrayList();
        String noStudySelection = new String(
            Messages.MembershipAddDialog_all_studies_label);
        studies.add(noStudySelection);
        studies.addAll(StudyWrapper.getAllStudies(SessionManager
            .getAppService()));
        studiesViewer = createComboViewer(contents,
            Messages.MembershipAddDialog_study_label, studies, null, null,
            null, new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof String)
                        return (String) element;
                    return ((StudyWrapper) element).getNameShort();
                }
            });
        studiesViewer.setSelection(new StructuredSelection(noStudySelection));

        new Label(contents, SWT.NONE);

        createRolesWidget(contents);

        createRightsWidgets(contents);
    }

    private void createCentersCombo(Composite contents)
        throws ApplicationException {
        Label label = widgetCreator.createLabel(contents,
            Messages.MembershipAddDialog_center_label);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;

        Composite compCenters = new Composite(contents, SWT.NONE);
        compCenters.setLayout(new GridLayout(5, false));
        compCenters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        gd = new GridData();
        gd.horizontalSpan = 2;
        compCenters.setLayoutData(gd);

        Label filterlabel = widgetCreator.createLabel(compCenters,
            "Filter list by");
        gd = new GridData();
        gd.verticalSpan = 2;
        gd.verticalAlignment = SWT.TOP;
        filterlabel.setLayoutData(gd);
        final Button allCentersRadio = new Button(compCenters, SWT.RADIO);
        allCentersRadio.setSelection(true);
        allCentersRadio.setText(Messages.MembershipAddDialog_none_label);
        final Button sitesRadio = new Button(compCenters, SWT.RADIO);
        sitesRadio.setText(Messages.MembershipAddDialog_sitesOnly_label);
        final Button clinicsRadio = new Button(compCenters, SWT.RADIO);
        clinicsRadio.setText(Messages.MembershipAddDialog_clinicsOnly_label);
        final Button rgRadio = new Button(compCenters, SWT.RADIO);
        rgRadio.setText(Messages.MembershipAddDialog_rgOnly_label);

        List<Object> centers = new ArrayList<Object>();
        final String noCenterSelection = new String(
            Messages.MembershipAddDialog_all_centers_label);
        centers.add(noCenterSelection);
        centers
            .addAll(CenterWrapper.getCenters(SessionManager.getAppService()));
        centersViewer = widgetCreator.createComboViewer(compCenters, label,
            centers, null, null, true, null, null, new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof String)
                        return (String) element;
                    return ((CenterWrapper<?>) element).getNameShort();
                }
            });
        gd = (GridData) centersViewer.getControl().getLayoutData();
        gd.horizontalSpan = 4;
        centersViewer.getControl().setLayoutData(gd);
        centersViewer.setSelection(new StructuredSelection(noCenterSelection));
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

    private void createRolesWidget(Composite contents)
        throws ApplicationException {
        GridData gd;
        rolesWidget = new MultiSelectWidget<RoleWrapper>(contents, SWT.NONE,
            Messages.MembershipAddDialog_roles_available_label,
            Messages.MembershipAddDialog_roles_selected_label, 110) {
            @Override
            protected String getTextForObject(RoleWrapper nodeObject) {
                return nodeObject.getName();
            }
        };
        rolesWidget.setSelections(
            RoleWrapper.getAllRoles(SessionManager.getAppService()),
            new ArrayList<RoleWrapper>());
        gd = (GridData) rolesWidget.getLayoutData();
        gd.horizontalSpan = 2;
        // rolesWidget
        // .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
        // @Override
        // public void selectionChanged(MultiSelectEvent event) {
        // if (rolesWidget.getSelected().isEmpty())
        // setErrorMessage(Messages.MembershipAddDialog_role_error_msg);
        // else
        // setErrorMessage(null);
        // }
        // });
    }

    private void createRightsWidgets(Composite contents) {
        rpSection = createSection(contents,
            Messages.MembershipAddDialog_rp_assoc_section,
            Messages.MembershipAddDialog_rp_assoc_add_label,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addPermission();
                }
            });

        // FIXME if add only, empty list is ok. If edit, need to get the
        // current membership
        permissionsInfoTable = new PermissionInfoTable(rpSection,
            new ArrayList<PermissionWrapper>()) {
            @Override
            protected List<BbRightWrapper> getAlreadyUsedRights() {
                List<BbRightWrapper> rights = new ArrayList<BbRightWrapper>();
                for (PermissionWrapper rp : permissionsInfoTable
                    .getCollection()) {
                    rights.add(rp.getRight());
                }
                return rights;
            }

            @Override
            protected void removeFromPermissionCollection(
                List<PermissionWrapper> rpList) {
            }
        };
        rpSection.setClient(permissionsInfoTable);
        // permissionsInfoTable
        // .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
        // @Override
        // public void selectionChanged(MultiSelectEvent event) {
        // if (permissionsInfoTable.getCollection().isEmpty())
        // setErrorMessage(Messages.MembershipAddDialog_rights_error_msg);
        // else
        // setErrorMessage(null);
        // }
        // });
    }

    protected void addPermission() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                PermissionAddDialog dlg = new PermissionAddDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    new ArrayList<BbRightWrapper>());
                int res = dlg.open();
                if (res == Status.OK) {
                    addedPermissions.addAll(dlg.getNewPermissionList());
                    permissionsInfoTable.getCollection().addAll(
                        dlg.getNewPermissionList());
                    permissionsInfoTable.reloadCollection(
                        permissionsInfoTable.getCollection(), null);
                }
            }
        });
    }

    @Override
    protected void okPressed() {
        // FIXME test at least one role or one permission added
        // if (rolesWidget.getSelected().isEmpty()) {
        // rolesWidget.notifyListeners(new MultiSelectEvent(rolesWidget));
        // return;
        // }
        // if (rightPrivilegeInfoTable.getCollection().isEmpty()) {
        // rightPrivilegeInfoTable.notifyListeners(new MultiSelectEvent(
        // rightPrivilegeInfoTable));
        // return;
        // }

        ms.addToRoleCollection(rolesWidget.getAddedToSelection());
        ms.addToPermissionCollection(addedPermissions);
        ms.setCenter(getCenterSelection());
        ms.setStudy(getStudySelection());
        ms.setPrincipal(principal);
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

    @SuppressWarnings("rawtypes")
    private CenterWrapper<?> getCenterSelection() {
        ISelection sel = centersViewer.getSelection();
        if (sel != null) {
            IStructuredSelection structSel = (IStructuredSelection) sel;
            if (structSel.size() > 0) {
                Object selection = structSel.getFirstElement();
                if (selection instanceof String)
                    return null;
                return (CenterWrapper) selection;
            }
        }
        return null;
    }

    public MembershipWrapper getMembership() {
        return ms;
    }

}