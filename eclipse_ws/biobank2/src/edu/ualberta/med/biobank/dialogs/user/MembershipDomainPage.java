package edu.ualberta.med.biobank.dialogs.user;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizardPage;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Domain;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

public class MembershipDomainPage extends BgcWizardPage {
    private final StudiesSelectionHandler studiesSelectionHandler =
        new StudiesSelectionHandler();
    private final CentersSelectionHandler centersSelectionHandler =
        new CentersSelectionHandler();
    private final Membership membership;
    private final Domain domain;
    private final ManagerContext context;

    private Button allCentersButton;
    private Button allStudiesButton;
    private MultiSelectWidget<Center> centersWidget;
    private MultiSelectWidget<Study> studiesWidget;
    private Button userManagerButton;
    private Button everyPermissionButton;

    MembershipDomainPage(Membership membership, ManagerContext context) {
        super("Domain", "Select centers and studies for this membership", null);

        this.membership = membership;
        this.domain = membership.getDomain();

        this.context = context;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group centersGroup = createGroup(container, "Centers");
        createAllCentersButton(centersGroup);
        createCentersWidget(centersGroup);

        Group studiesGroup = createGroup(container, "Studies");
        createAllStudiesButton(studiesGroup);
        createStudiesWidget(studiesGroup);

        centersWidget.addSelectionChangedListener(centersSelectionHandler);
        studiesWidget.addSelectionChangedListener(studiesSelectionHandler);

        createUserManagerButton(container);
        createEveryPermissionButton(container);

        updatePageComplete();

        setControl(container);
    }

    private Group createGroup(Composite parent, String title) {
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(title);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return group;
    }

    private void createAllCentersButton(Composite parent) {
        allCentersButton = new Button(parent, SWT.CHECK);
        allCentersButton.setText("All Centers");
        allCentersButton.setSelection(domain.isAllCenters());
        allCentersButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                boolean allCenters = allCentersButton.getSelection();
                domain.setAllCenters(allCenters);
                centersWidget.setEnabled(!allCenters);
                updatePageComplete();
            }
        });
    }

    private void createCentersWidget(Composite parent) {
        centersWidget = new MultiSelectWidget<Center>(parent, SWT.NONE,
            "Available Centers",
            "Selected Centers", 80) {
            @Override
            protected String getTextForObject(Center center) {
                return center.getNameShort();
            }
        };

        centersWidget.setEnabled(!domain.isAllCenters());
        updateCenterSelections();
    }

    private void createAllStudiesButton(Composite parent) {
        allStudiesButton = new Button(parent, SWT.CHECK);
        allStudiesButton.setText("All Studies");
        allStudiesButton.setSelection(domain.isAllCenters());
        allStudiesButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                boolean allStudies = allStudiesButton.getSelection();
                domain.setAllStudies(allStudies);
                studiesWidget.setEnabled(!allStudies);
                updatePageComplete();
            }
        });
    }

    private void createStudiesWidget(Composite parent) {
        studiesWidget = new MultiSelectWidget<Study>(parent, SWT.NONE,
            "Available Studies",
            "Selected Studies", 80) {
            @Override
            protected String getTextForObject(Study study) {
                return study.getNameShort();
            }
        };

        studiesWidget.setEnabled(!domain.isAllStudies());
        updateStudySelections();
    }

    private void createUserManagerButton(Composite parent) {
        userManagerButton = new Button(parent, SWT.CHECK);
        userManagerButton.setText("User manager");
        userManagerButton
            .setToolTipText("Can create, edit, and delete users and groups.");
        userManagerButton.setSelection(membership.isUserManager());
        userManagerButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                boolean userManager = userManagerButton.getSelection();
                membership.setUserManager(userManager);
            }
        });
    }

    private void createEveryPermissionButton(Composite parent) {
        everyPermissionButton = new Button(parent, SWT.CHECK);
        everyPermissionButton.setText("Grant all permissions and roles");
        everyPermissionButton
            .setToolTipText("Grant all current and future roles and permissions");
        everyPermissionButton.setSelection(membership.isEveryPermission());
        everyPermissionButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                boolean everyPermission = everyPermissionButton.getSelection();
                membership.setEveryPermission(everyPermission);
            }
        });
    }

    private void updatePageComplete() {
        boolean complete = true;
        complete &= domain.isAllCenters() || !domain.getCenters().isEmpty();
        complete &= domain.isAllStudies() || !domain.getStudies().isEmpty();
        setPageComplete(complete);
    }

    private void updateEveryPermissionButton() {
        boolean canGrantEveryPermission = false;
        for (Domain d : context.getManager().getManageableDomains()) {
            if (d.isSuperset(domain)) {
                canGrantEveryPermission = true;
                break;
            }
        }
        if (!canGrantEveryPermission) {
            everyPermissionButton.setSelection(false);
        }
        everyPermissionButton.setEnabled(canGrantEveryPermission);
    }

    private void updateCenterSelections() {
        centersWidget.setSelections(
            getCenterOptions(),
            membership.getDomain().getCenters());
    }

    private void updateStudySelections() {
        studiesWidget.setSelections(
            getStudyOptions(),
            membership.getDomain().getStudies());
    }

    private Set<Center> getCenterOptions() {
        Set<Center> options = new HashSet<Center>();
        for (Domain d : context.getManager().getManageableDomains()) {
            if (d.containsAllStudies(domain.getStudies())) {
                if (d.isAllCenters()) {
                    options.addAll(context.getCenters());
                    break;
                } else {
                    options.addAll(d.getCenters());
                }
            }
        }
        return options;
    }

    private Set<Study> getStudyOptions() {
        Set<Study> options = new HashSet<Study>();
        for (Domain d : context.getManager().getManageableDomains()) {
            if (d.containsAllCenters(domain.getCenters())) {
                if (d.isAllStudies()) {
                    options.addAll(context.getStudies());
                    break;
                } else {
                    options.addAll(d.getStudies());
                }
            }
        }
        return options;
    }

    private class CentersSelectionHandler implements BgcEntryFormWidgetListener {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            Set<Center> domainCenters = domain.getCenters();
            domainCenters.clear();
            domainCenters.addAll(centersWidget.getSelected());
            updateStudySelections();
            updateEveryPermissionButton();
            updatePageComplete();
        }
    }

    private class StudiesSelectionHandler implements BgcEntryFormWidgetListener {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            Set<Study> domainStudies = domain.getStudies();
            domainStudies.clear();
            domainStudies.addAll(studiesWidget.getSelected());
            updateCenterSelections();
            updateEveryPermissionButton();
            updatePageComplete();
        }
    }
}
