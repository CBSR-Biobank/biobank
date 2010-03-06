package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SiteEntryForm extends AddressEntryFormCommon {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SiteEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.SiteEntryForm";

    private static final String MSG_NEW_SITE_OK = "Create a new BioBank site.";
    private static final String MSG_SITE_OK = "Edit a BioBank site.";
    private static final String MSG_NO_SITE_NAME = "Site must have a name";

    private SiteAdapter siteAdapter;

    private SiteWrapper site;

    protected Combo session;

    private ComboViewer activityStatusComboViewer;

    private ActivityStatusWrapper currentActivityStatus;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        site = siteAdapter.getWrapper();
        try {
            site.reload();
        } catch (Exception e) {
            logger.error("Can't reload site", e);
        }

        String tabName;
        if (site.getId() == null) {
            tabName = "New Repository Site";
        } else {
            tabName = "Repository Site " + site.getNameShort();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws ApplicationException {
        form.setText("Repository Site Information");
        form.getBody().setLayout(new GridLayout(1, false));
        currentActivityStatus = site.getActivityStatus();
        createSiteSection();
        createAddressArea(site);

        // When adding help uncomment line below
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        // IJavaHelpContextIds.XXXXX);
    }

    private void createSiteSection() throws ApplicationException {
        toolkit
            .createLabel(
                form.getBody(),
                "Studies, Clinics, and Container Types can be added after submitting this information.",
                SWT.LEFT);

        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_SITE));

        firstControl = createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Name", null, BeansObservables.observeValue(site, "name"),
            new NonEmptyStringValidator(MSG_NO_SITE_NAME));

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Name Short",
            null, BeansObservables.observeValue(site, "nameShort"),
            new NonEmptyStringValidator("Site short name cannot be blank"));

        activityStatusComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Activity Status", ActivityStatusWrapper
                .getAllActivityStatuses(appService), site.getActivityStatus(),
            "Site must have an activity status", true);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, BeansObservables.observeValue(site,
                "comment"), null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
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
        if (siteAdapter.getParent() == null) {
            siteAdapter.setParent(SessionManager.getInstance().getSession());
        }
        site
            .setActivityStatus((ActivityStatusWrapper) ((StructuredSelection) activityStatusComboViewer
                .getSelection()).getFirstElement());
        site.persist();
        SessionManager.getInstance().updateSites();
    }

    @Override
    public String getNextOpenedFormID() {
        return SiteViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        currentActivityStatus = site.getActivityStatus();
        if (currentActivityStatus != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                currentActivityStatus));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }

    }
}
