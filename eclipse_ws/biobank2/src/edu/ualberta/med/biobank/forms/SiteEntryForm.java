package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;

public class SiteEntryForm extends AddressEntryFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.SiteEntryForm";

    private static final String MSG_NEW_SITE_OK = "Create a new BioBank site.";
    private static final String MSG_SITE_OK = "Edit a BioBank site.";
    private static final String MSG_NO_SITE_NAME = "Site must have a name";

    private SiteAdapter siteAdapter;

    private SiteWrapper siteWrapper;

    protected Combo session;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        siteWrapper = siteAdapter.getWrapper();
        try {
            siteWrapper.reload();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        addressWrapper = siteWrapper.getAddressWrapper();

        String tabName;
        if (siteWrapper.getId() == null) {
            tabName = "New Repository Site";
        } else {
            tabName = "Repository Site " + siteWrapper.getName();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() {
        form.setText("Repository Site Information");
        form.getBody().setLayout(new GridLayout(1, false));
        createSiteSection();
        createAddressArea();

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
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_SITE));

        firstControl = createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Name", null, BeansObservables.observeValue(siteWrapper, "name"),
            NonEmptyString.class, MSG_NO_SITE_NAME);

        createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
            "Activity Status", FormConstants.ACTIVITY_STATUS, BeansObservables
                .observeValue(siteWrapper, "activityStatus"), null, null);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, BeansObservables.observeValue(
                siteWrapper, "comment"), null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
    }

    @Override
    protected String getOkMessage() {
        if (siteWrapper.getId() == null) {
            return MSG_NEW_SITE_OK;
        }
        return MSG_SITE_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        if (siteAdapter.getParent() == null) {
            siteAdapter.setParent(SessionManager.getInstance().getSession());
        }
        addressWrapper.persist();
        siteWrapper.persist();
        SessionManager.getInstance().updateSites();
    }

    @Override
    public void setFocus() {
        firstControl.setFocus();
    }

    @Override
    public String getNextOpenedFormID() {
        return SiteViewForm.ID;
    }
}
