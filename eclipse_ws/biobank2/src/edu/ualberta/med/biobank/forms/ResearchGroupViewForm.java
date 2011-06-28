package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.admin.ResearchGroupAdapter;

public class ResearchGroupViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ResearchGroupViewForm"; //$NON-NLS-1$

    private ResearchGroupAdapter researchGroupAdapter;

    private ResearchGroupWrapper researchGroup;

    private ResearchGroupStudyInfoTable studiesTable;

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private Button hasShipmentsButton;

    private BgcBaseText activityStatusLabel;

    private BgcBaseText commentLabel;

    private BgcBaseText patientTotal;

    private BgcBaseText ceventTotal;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ResearchGroupAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        researchGroupAdapter = (ResearchGroupAdapter) adapter;
        researchGroup = researchGroupAdapter.getWrapper();
        researchGroup.reload();
        setPartName(Messages.getString("ResearchGroupViewForm.title", //$NON-NLS-1$
            researchGroup.getNameShort()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ResearchGroupViewForm.title", //$NON-NLS-1$
            researchGroup.getName()));

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createResearchGroupSection();
        createAddressSection(researchGroup);
    }

    private void createResearchGroupSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.name")); //$NON-NLS-1$
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.nameShort")); //$NON-NLS-1$
        hasShipmentsButton = (Button) createLabelledWidget(client,
            Button.class, SWT.NONE,
            Messages.getString("researchGroup.field.label.sendsShipments")); //$NON-NLS-1$
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.activity")); //$NON-NLS-1$
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments")); //$NON-NLS-1$
        patientTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages
                .getString("ResearchGroupViewForm.field.label.totalPatients")); //$NON-NLS-1$
        ceventTotal = createReadOnlyLabelledField(
            client,
            SWT.NONE,
            Messages
                .getString("ResearchGroupViewForm.field.label.totalCollectionEvents")); //$NON-NLS-1$

        setResearchGroupValues();
    }

    private void setResearchGroupValues() throws Exception {
        setTextValue(nameLabel, researchGroup.getName());
        setTextValue(nameShortLabel, researchGroup.getNameShort());
        setCheckBoxValue(hasShipmentsButton, researchGroup.getSendsShipments());
        setTextValue(activityStatusLabel, researchGroup.getActivityStatus());
        setTextValue(commentLabel, researchGroup.getComment());
        setTextValue(patientTotal, researchGroup.getPatientCount());
        setTextValue(ceventTotal, researchGroup.getCollectionEventCount());
    }

    @Override
    public void reload() throws Exception {
        researchGroup.reload();
        setPartName(Messages.getString(
            "ResearchGroupViewForm.title", researchGroup.getName())); //$NON-NLS-1$
        form.setText(Messages.getString("ResearchGroupViewForm.title", //$NON-NLS-1$
            researchGroup.getName()));
        setResearchGroupValues();
        setAddressValues(researchGroup);
    }

}
