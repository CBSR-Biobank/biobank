package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipRoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.MembershipRoleBaseWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class MembershipAddDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;
    private PrincipalWrapper<?> principal;
    private ComboViewer centersViewer;
    private ComboViewer studiesViewer;
    private MultiSelectWidget<RoleWrapper> rolesWidget;
    private MembershipWrapper<?> ms;

    public MembershipAddDialog(Shell parent, PrincipalWrapper<?> principal) {
        super(parent);
        Assert.isNotNull(principal);
        this.principal = principal;
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite compCenters = new Composite(contents, SWT.NONE);
        compCenters.setLayout(new GridLayout(4, false));
        compCenters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final Button allCentersRadio = new Button(compCenters, SWT.RADIO);
        allCentersRadio.setSelection(true);
        allCentersRadio.setText(Messages.MembershipAddDialog_allCenters_label);
        final Button sitesRadio = new Button(compCenters, SWT.RADIO);
        sitesRadio.setText(Messages.MembershipAddDialog_sitesOnly_label);
        final Button clinicsRadio = new Button(compCenters, SWT.RADIO);
        clinicsRadio.setText(Messages.MembershipAddDialog_clinicsOnly_label);
        final Button rgRadio = new Button(compCenters, SWT.RADIO);
        rgRadio.setText(Messages.MembershipAddDialog_rgOnly_label);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        compCenters.setLayoutData(gd);

        List centers = new ArrayList();
        final String noCenterSelection = new String(Messages.MembershipAddDialog_nospecific_center_label);
        centers.add(noCenterSelection);
        centers
            .addAll(CenterWrapper.getCenters(SessionManager.getAppService()));
        centersViewer = createComboViewer(contents, Messages.MembershipAddDialog_center_label, centers, null,
            null, null, new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof String)
                        return (String) element;
                    return ((CenterWrapper<?>) element).getNameShort();
                }
            });
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

        List studies = new ArrayList();
        String noStudySelection = new String(Messages.MembershipAddDialog_nospecific_study_label);
        studies.add(noStudySelection);
        studies.addAll(StudyWrapper.getAllStudies(SessionManager
            .getAppService()));
        studiesViewer = createComboViewer(contents, Messages.MembershipAddDialog_study_label, studies, null,
            null, null, new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof String)
                        return (String) element;
                    return ((StudyWrapper) element).getNameShort();
                }
            });
        studiesViewer.setSelection(new StructuredSelection(noStudySelection));

        rolesWidget = new MultiSelectWidget<RoleWrapper>(contents, SWT.NONE,
            Messages.MembershipAddDialog_roles_available_label, Messages.MembershipAddDialog_roles_selected_label, 110) {
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
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void okPressed() {
        ms = new MembershipRoleWrapper(SessionManager.getAppService());
        ms.setCenter(getCenterSelection());
        ms.setStudy(getStudySelection());
        ((MembershipRoleBaseWrapper) ms).addToRoleCollection(rolesWidget
            .getAddedToSelection());
        ms.setPrincipal(principal);
        principal.addToMembershipCollection(Arrays.asList(ms));
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

    public MembershipWrapper<?> getMembership() {
        return ms;
    }

}