package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;

public class ShipmentViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentViewForm"; //$NON-NLS-1$

    private OriginInfoWrapper originInfo;

    private BgcBaseText senderLabel;

    private BgcBaseText receiverLabel;

    private BgcBaseText waybillLabel;

    private BgcBaseText departedLabel;

    private BgcBaseText dateReceivedLabel;

    private BgcBaseText shippingMethodLabel;

    private BgcBaseText boxNumberLabel;

    private BgcBaseText commentLabel;

    private SpecimenEntryWidget specimenWidget;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ShipmentAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        originInfo = (OriginInfoWrapper) getModelObject();
        setPartName();
    }

    @Override
    protected void createFormContent() throws Exception {
        setFormText();
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createMainSection();
        createSpecimensSection();
    }

    private void createSpecimensSection() {
        Composite client = createSectionWithClient(Messages.ShipmentViewForm_specimens_title);
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        specimenWidget = new SpecimenEntryWidget(client, SWT.NONE, toolkit,
            appService, false);
        specimenWidget.setSpecimens(originInfo.getSpecimenCollection());
        specimenWidget.addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        senderLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ShipmentViewForm_sender_label);
        receiverLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ShipmentViewForm_receiver_label);
        waybillLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ShipmentViewForm_waybill_label);
        shippingMethodLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ShipmentViewForm_shipmethod_label);
        if (originInfo.getShipmentInfo().getShippingMethod().needDate()) {
            departedLabel = createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ShipmentViewForm_packed_label);
        }
        boxNumberLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ShipmentViewForm_boxNber_label);
        dateReceivedLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ShipmentViewForm_received_label);
        commentLabel = createReadOnlyLabelledField(client,
            SWT.WRAP | SWT.MULTI, Messages.ShipmentViewForm_comment_label);

        setShipmentValues();
    }

    private void setShipmentValues() {
        ShipmentInfoWrapper shipInfo = originInfo.getShipmentInfo();
        ShippingMethodWrapper shipMethod = shipInfo.getShippingMethod();

        setTextValue(senderLabel, originInfo.getCenter().getName());

        SiteWrapper rcvSite = originInfo.getReceiverSite();
        setTextValue(receiverLabel, rcvSite != null ? rcvSite.getName() : ""); //$NON-NLS-1$

        setTextValue(waybillLabel, originInfo.getShipmentInfo().getWaybill());
        if (departedLabel != null) {
            setTextValue(departedLabel, shipInfo.getFormattedDatePacked());
        }
        setTextValue(shippingMethodLabel,
            shipMethod == null ? "" : shipMethod.getName()); //$NON-NLS-1$

        setTextValue(boxNumberLabel, shipInfo.getBoxNumber());
        setTextValue(dateReceivedLabel, shipInfo.getFormattedDateReceived());
        setTextValue(commentLabel, shipInfo.getComment());
    }

    @Override
    public void reload() throws Exception {
        originInfo.reload();
        setPartName();
        setFormText();
        setShipmentValues();

        specimenWidget.setSpecimens(originInfo.getSpecimenCollection());
    }

    private void setPartName() {
        setPartName(NLS.bind(Messages.ShipmentViewForm_title, originInfo
            .getShipmentInfo().getFormattedDateReceived()));
    }

    private void setFormText() {
        if (!form.isDisposed()) {
            form.setText(NLS.bind(Messages.ShipmentViewForm_form_title,
                originInfo.getShipmentInfo().getFormattedDateReceived(),
                originInfo.getCenter().getNameShort()));
        }
    }

}
