package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.ShipmentReadInfo;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetInfoAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentViewForm extends BiobankViewForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.ShipmentViewForm"; //$NON-NLS-1$

    private OriginInfoWrapper originInfo = new OriginInfoWrapper(
        SessionManager.getAppService());

    private BgcBaseText senderLabel;

    private BgcBaseText receiverLabel;

    private BgcBaseText waybillLabel;

    private BgcBaseText departedLabel;

    private BgcBaseText dateReceivedLabel;

    private BgcBaseText shippingMethodLabel;

    private BgcBaseText boxNumberLabel;

    private SpecimenEntryWidget specimenWidget;

    private CommentCollectionInfoTable commentEntryTable;

    private ShipmentInfoWrapper shipmentInfo = new ShipmentInfoWrapper(
        SessionManager.getAppService());

    private List<SpecimenWrapper> specimens;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ShipmentAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        setOiInfo(adapter.getId());
        setPartName();
    }

    private void setOiInfo(Integer id) throws ApplicationException {
        if (id == null) {
            OriginInfo oi = new OriginInfo();
            oi.setShipmentInfo(new ShipmentInfo());
            originInfo.setWrappedObject(oi);
            shipmentInfo.setWrappedObject(oi.getShipmentInfo());
            specimens = new ArrayList<SpecimenWrapper>();
        } else {
            ShipmentReadInfo read =
                SessionManager.getAppService().doAction(
                    new ShipmentGetInfoAction(id));
            originInfo.setWrappedObject(read.originInfo);
            shipmentInfo.setWrappedObject(read.originInfo.getShipmentInfo());
            specimens =
                ModelWrapper.wrapModelCollection(
                    SessionManager.getAppService(), read.specimens,
                    SpecimenWrapper.class);
        }

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
        Composite client =
            createSectionWithClient(Messages.ShipmentViewForm_specimens_title);
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        specimenWidget =
            new SpecimenEntryWidget(client, SWT.NONE, toolkit,
                SessionManager.getAppService(), false);
        specimenWidget.setSpecimens(specimens);
        specimenWidget.addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        senderLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ShipmentViewForm_sender_label);
        receiverLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ShipmentViewForm_receiver_label);
        waybillLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ShipmentViewForm_waybill_label);
        shippingMethodLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ShipmentViewForm_shipmethod_label);
        if (originInfo.getShipmentInfo().getShippingMethod().needDate()) {
            departedLabel =
                createReadOnlyLabelledField(client, SWT.NONE,
                    Messages.ShipmentViewForm_packed_label);
        }
        boxNumberLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ShipmentViewForm_boxNber_label);
        dateReceivedLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ShipmentViewForm_received_label);

        createCommentSection();

        setShipmentValues();
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentCollectionInfoTable(client,
                originInfo.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);

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
    }

    @Override
    public void setValues() throws Exception {
        setPartName();
        setFormText();
        setShipmentValues();

        commentEntryTable.setList(originInfo.getCommentCollection(false));
        specimenWidget.setSpecimens(specimens);
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
