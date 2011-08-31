package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RightPrivilegeWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNode;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class RightPrivilegeAddDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;
    private List<BbRightWrapper> alreadyUsedRights;
    private MultiSelectWidget<PrivilegeWrapper> privilegesWidget;
    private MultiSelectWidget<BbRightWrapper> rightsWidget;
    private List<RightPrivilegeWrapper> newRpList = new ArrayList<RightPrivilegeWrapper>();
    private RightPrivilegeWrapper editRp;

    public RightPrivilegeAddDialog(Shell parent,
        List<BbRightWrapper> alreadyUsedRights) {
        super(parent);
        this.alreadyUsedRights = alreadyUsedRights;
        currentTitle = Messages.RightPrivilegeAddDialog_title;
        titleAreaMessage = Messages.RightPrivilegeAddDialog_description;
    }

    public int edit(RightPrivilegeWrapper rp) {
        this.editRp = rp;
        return open();
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

        if (editRp == null) {
            Composite compCenters = new Composite(contents, SWT.NONE);
            compCenters.setLayout(new GridLayout(4, false));
            compCenters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                true));
            final Button allCentersRadio = new Button(compCenters, SWT.RADIO);
            allCentersRadio.setSelection(true);
            allCentersRadio
                .setText(Messages.RightPrivilegeAddDialog_allCenters_label);
            final Button sitesRadio = new Button(compCenters, SWT.RADIO);
            sitesRadio
                .setText(Messages.RightPrivilegeAddDialog_sitesOnly_label);
            final Button clinicsRadio = new Button(compCenters, SWT.RADIO);
            clinicsRadio
                .setText(Messages.RightPrivilegeAddDialog_clinicsOnly_label);
            final Button rgRadio = new Button(compCenters, SWT.RADIO);
            rgRadio.setText(Messages.RightPrivilegeAddDialog_rgOnly_label);
            GridData gd = new GridData();
            gd.horizontalSpan = 2;
            compCenters.setLayoutData(gd);

            rightsWidget = new MultiSelectWidget<BbRightWrapper>(contents,
                SWT.NONE,
                Messages.RightPrivilegeAddDialog_rights_available_label,
                Messages.RightPrivilegeAddDialog_rights_selected_label, 110) {
                @Override
                protected String getTextForObject(BbRightWrapper nodeObject) {
                    return nodeObject.getName();
                }
            };
            List<BbRightWrapper> allRights = BbRightWrapper
                .getAllRights(SessionManager.getAppService());
            allRights.removeAll(alreadyUsedRights);
            rightsWidget.setSelections(allRights,
                new ArrayList<BbRightWrapper>());
            rightsWidget.setFilter(new ViewerFilter() {
                @SuppressWarnings("unchecked")
                @Override
                public boolean select(Viewer viewer, Object parentElement,
                    Object element) {
                    MultiSelectNode<BbRightWrapper> node = (MultiSelectNode<BbRightWrapper>) element;
                    BbRightWrapper right = node.getNodeObject();
                    if (sitesRadio.getSelection())
                        return right.isForSite();
                    if (clinicsRadio.getSelection())
                        return right.isForClinic();
                    if (rgRadio.getSelection())
                        return right.isForResearchGroup();
                    return true;
                }
            });

            SelectionListener selListener = new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    rightsWidget.refreshLists();
                }
            };
            allCentersRadio.addSelectionListener(selListener);
            sitesRadio.addSelectionListener(selListener);
            clinicsRadio.addSelectionListener(selListener);
            rgRadio.addSelectionListener(selListener);
        } else {
            Label label = new Label(contents, SWT.NONE);
            label.setText(NLS.bind(
                Messages.RightPrivilegeAddDialog_edit_privileges_label, editRp
                    .getRight().getName()));
        }

        privilegesWidget = new MultiSelectWidget<PrivilegeWrapper>(contents,
            SWT.NONE,
            Messages.RightPrivilegeAddDialog_privileges_available_label,
            Messages.RightPrivilegeAddDialog_privileges_selected_label, 110) {
            @Override
            protected String getTextForObject(PrivilegeWrapper nodeObject) {
                return nodeObject.getName();
            }
        };
        List<PrivilegeWrapper> selection = new ArrayList<PrivilegeWrapper>();
        if (editRp != null)
            selection = editRp.getPrivilegeCollection(true);
        privilegesWidget.setSelections(
            PrivilegeWrapper.getAllPrivileges(SessionManager.getAppService()),
            selection);
    }

    @Override
    protected void okPressed() {
        if (editRp == null) {
            for (BbRightWrapper right : rightsWidget.getAddedToSelection()) {
                RightPrivilegeWrapper rp = new RightPrivilegeWrapper(
                    SessionManager.getAppService());
                rp.setRight(right);
                rp.addToPrivilegeCollection(privilegesWidget
                    .getAddedToSelection());
                newRpList.add(rp);
            }
        } else {
            editRp.addToPrivilegeCollection(privilegesWidget
                .getAddedToSelection());
            editRp.removeFromPrivilegeCollection(privilegesWidget
                .getRemovedFromSelection());
        }
        super.okPressed();
    }

    public List<RightPrivilegeWrapper> getNewRightPrivilegeList() {
        return newRpList;
    }

}