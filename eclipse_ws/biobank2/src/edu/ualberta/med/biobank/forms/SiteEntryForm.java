package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteEntryForm extends AddressEntryFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.SiteEntryForm";

    private static final String MSG_NEW_SITE_OK = "Create a new BioBank site.";
    private static final String MSG_SITE_OK = "Edit a BioBank site.";
    private static final String MSG_NO_SITE_NAME = "Site must have a name";

    private SiteAdapter siteAdapter;

    private Site site;

    protected Combo session;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        site = siteAdapter.getSite();

        String tabName;
        if (site.getId() == null) {
            tabName = "New Repository Site";
        } else {
            tabName = "Repository Site " + site.getName();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() {
        form.setText("Repository Site Information");
        address = site.getAddress();
        form.getBody().setLayout(new GridLayout(1, false));
        createSiteSection();
        createAddressArea();
        createButtonsSection();

        // When adding help uncomment line below
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        // IJavaHelpContextIds.XXXXX);
    }

    private void createSiteSection() {
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

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Name", null,
            PojoObservables.observeValue(site, "name"), NonEmptyString.class,
            MSG_NO_SITE_NAME);

        createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
            "Activity Status", FormConstants.ACTIVITY_STATUS, PojoObservables
                .observeValue(site, "activityStatus"), null, null);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, PojoObservables.observeValue(site,
                "comment"), null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.numColumns = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);

        initCancelConfirmWidget(client);
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

        SDKQuery query;
        SDKQueryResult result;

        if ((site.getName() == null) && !checkSiteNameUnique()) {
            setDirty(true);
            return;
        }

        site.setAddress(address);
        if ((site.getId() == null) || (site.getId() == 0)) {
            Assert.isTrue(site.getAddress().getId() == null,
                "insert invoked on address already in database");

            query = new InsertExampleQuery(site.getAddress());
            result = appService.executeQuery(query);
            site.setAddress((Address) result.getObjectResult());
            query = new InsertExampleQuery(site);
        } else {
            Assert.isNotNull(site.getAddress().getId(),
                "update invoked on address not in database");

            query = new UpdateExampleQuery(site.getAddress());
            result = appService.executeQuery(query);
            site.setAddress((Address) result.getObjectResult());
            query = new UpdateExampleQuery(site);
        }

        result = appService.executeQuery(query);
        site = (Site) result.getObjectResult();
        siteAdapter.setSite(site);
        SessionManager.getInstance().getSiteCombo().addChild(site);
        siteAdapter.getParent().performExpand();
    }

    private boolean checkSiteNameUnique() throws ApplicationException {
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Site where name = '"
                + site.getName() + "'");

        List<Object> results = appService.query(c);
        if (results.size() == 0)
            return true;

        BioBankPlugin.openAsyncError("Site Name Problem", "A site with name \""
            + site.getName() + "\" already exists.");
        return false;
    }

    @Override
    public void setFocus() {
        form.setFocus();
    }

    @Override
    public void cancelForm() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getNextOpenedFormID() {
        return SiteViewForm.ID;
    }
}
