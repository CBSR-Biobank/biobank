package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.admin.ResearchGroupAdapter;

public class ResearchGroupViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ResearchGroupViewForm"; //$NON-NLS-1$

    private ResearchGroupAdapter researchGroupAdapter;

    private ResearchGroupWrapper researchGroup;

    // private ResearchGroupStudyInfoTable studiesTable;

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
        setPartName(NLS.bind(Messages.ResearchGroupViewForm_title,
            researchGroup.getNameShort()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.ResearchGroupViewForm_title,
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
            Messages.label_name);
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_nameShort);
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_activity);
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.label_comments);
        patientTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ResearchGroupViewForm_field_label_totalPatients);
        ceventTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ResearchGroupViewForm_field_label_totalCollectionEvents);

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
        setPartName(NLS.bind(Messages.ResearchGroupViewForm_title,
            researchGroup.getName()));
        form.setText(NLS.bind(Messages.ResearchGroupViewForm_title,
            researchGroup.getName()));
        setResearchGroupValues();
        setAddressValues(researchGroup);
    }

}
