package edu.ualberta.med.biobank.dialogs.user;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizardPage;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Study;

public class MembershipDomainPage extends BgcWizardPage {
    private final Membership membership;
    private final ManagerContext context;

    private Button allCentersCheckbox;
    private Button allStudiesCheckbox;
    private ComboViewer centersViewer;
    private ComboViewer studiesViewer;

    MembershipDomainPage(Membership membership, ManagerContext context) {
        super("Domain", "Select centers and studies for this membership", null);

        this.membership = membership;
        this.context = context;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        centersViewer = getWidgetCreator().createComboViewer(
            container,
            "Centers",
            getCenterOptions(),
            membership.getCenter(),
            null,
            null,
            new LabelProvider() {
                @Override
                public String getText(Object element) {
                    return ((Center) element).getNameShort();
                }
            });

        centersViewer.addSelectionChangedListener(
            new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection =
                        (IStructuredSelection) event.getSelection();
                    membership.setCenter((Center) selection.getFirstElement());
                    updateStudyOptions();
                }
            });

        studiesViewer = createComboViewer(container,
            "Studies",
            getStudyOptions(membership.getCenter()),
            membership.getStudy(),
            null,
            null,
            new LabelProvider() {
                @Override
                public String getText(Object element) {
                    return ((Study) element).getNameShort();
                }
            });

        studiesViewer.addSelectionChangedListener(
            new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection =
                        (IStructuredSelection) event.getSelection();
                    membership.setStudy((Study) selection.getFirstElement());
                    updateRoleAndPermissionOptions();
                }
            });

        setControl(container);
    }

    private Set<Center> getCenterOptions() {
        Set<Center> options = new HashSet<Center>();
        if (isAllCentersOption()) {
            options.addAll(context.getCenters());
        } else {
            for (Membership m : context.getManager().getAllMemberships()) {
                options.add(m.getCenter());
            }
        }
        return options;
    }

    private void updateStudyOptions() {
        studiesViewer.setInput(getStudyOptions(membership.getCenter()));
    }

    public Set<Study> getStudyOptions(Center selectedCenter) {
        Set<Study> options = new HashSet<Study>();
        if (isAllStudiesOption()) {
            options.addAll(context.getStudies());
        } else {
            for (Membership m : context.getManager().getAllMemberships()) {
                if (m.getCenter().equals(selectedCenter)) {
                    options.add(m.getStudy());
                }
            }
        }
        return options;
    }

    public boolean isAllCentersOption() {
        for (Membership m : context.getManager().getAllMemberships())
            if (m.getCenter() == null) return true;
        return false;
    }

    public boolean isAllStudiesOption() {
        for (Membership m : context.getManager().getAllMemberships())
            if (m.getStudy() == null) return true;
        return false;
    }

    @Override
    public boolean canFlipToNextPage() {
        return !membership.isEveryPermission();
    }
}
