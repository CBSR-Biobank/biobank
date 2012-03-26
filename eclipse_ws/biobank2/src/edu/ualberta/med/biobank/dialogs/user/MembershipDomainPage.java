package edu.ualberta.med.biobank.dialogs.user;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
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
    private final Membership membership;
    private final Domain domain;
    private final ManagerContext context;

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
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        centersWidget = new MultiSelectWidget<Center>(container, SWT.NONE,
            "Available Centers",
            "Selected Centers", 120) {
            @Override
            protected String getTextForObject(Center center) {
                return center.getNameShort();
            }
        };

        updateCenterSelections();

        studiesWidget = new MultiSelectWidget<Study>(container, SWT.NONE,
            "Available Studies",
            "Selected Studies", 120) {
            @Override
            protected String getTextForObject(Study study) {
                return study.getNameShort();
            }
        };

        updateStudySelections();

        centersWidget.addSelectionChangedListener(
            new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    Set<Center> domainCenters = domain.getCenters();
                    domainCenters.clear();
                    domainCenters.addAll(centersWidget.getSelected());
                    updateStudySelections();
                    updateEveryPermissionOption();
                }
            });

        studiesWidget.addSelectionChangedListener(
            new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    Set<Study> domainStudies = domain.getStudies();
                    domainStudies.clear();
                    domainStudies.addAll(studiesWidget.getSelected());
                    updateCenterSelections();
                    updateEveryPermissionOption();
                }
            });

        userManagerButton = new Button(container, SWT.CHECK);
        userManagerButton
            .setText("Can create other users with the granted roles and permissions");
        userManagerButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                boolean userManager = userManagerButton.getSelection();
                membership.setUserManager(userManager);
            }
        });

        everyPermissionButton = new Button(container, SWT.CHECK);
        everyPermissionButton
            .setText("Grant all current and future roles and permissions");
        everyPermissionButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                boolean everyPermission = everyPermissionButton.getSelection();
                membership.setEveryPermission(everyPermission);
            }
        });

        setControl(container);
    }

    private void updateEveryPermissionOption() {
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

    @Override
    public boolean canFlipToNextPage() {
        return !membership.isEveryPermission();
    }
}
