package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.ShipmentReadInfo;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable.ColumnsShown;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentViewForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory
        .getI18n(ShipmentViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ShipmentViewForm";

    private final OriginInfoWrapper originInfo = new OriginInfoWrapper(
        SessionManager.getAppService());

    private BgcBaseText senderLabel;

    private BgcBaseText receiverLabel;

    private BgcBaseText waybillLabel;

    private BgcBaseText departedLabel;

    private BgcBaseText dateReceivedLabel;

    private BgcBaseText shippingMethodLabel;

    private BgcBaseText boxNumberLabel;

    private NewSpecimenInfoTable specimenTable;

    private CommentsInfoTable commentEntryTable;

    private final ShipmentInfoWrapper shipmentInfo = new ShipmentInfoWrapper(
        SessionManager.getAppService());

    private List<SpecimenInfo> specimens;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ShipmentAdapter),
            "Invalid editor input: object of type "
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
            specimens = new ArrayList<SpecimenInfo>();
        } else {
            ShipmentReadInfo read =
                SessionManager.getAppService().doAction(
                    new ShipmentGetInfoAction(id));
            originInfo.setWrappedObject(read.originInfo);
            shipmentInfo.setWrappedObject(read.originInfo.getShipmentInfo());
            specimens = read.specimens;
            SessionManager.logLookup(read.originInfo);
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
            createSectionWithClient(Specimen.NAME.plural().toString());
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        specimenTable =
            new NewSpecimenInfoTable(client, specimens,
                ColumnsShown.PEVENT_SOURCE_SPECIMENS, 10);
        specimenTable.adaptToToolkit(toolkit, true);
        specimenTable
            .addClickListener(new IInfoTableDoubleClickItemListener<SpecimenInfo>() {

                @Override
                public void doubleClick(InfoTableEvent<SpecimenInfo> event) {
                    Specimen s =
                        ((SpecimenInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).specimen;
                    AdapterBase.openForm(
                        new FormInput(
                            new SpecimenAdapter(null,
                                new SpecimenWrapper(SessionManager
                                    .getAppService(), s))),
                        SpecimenViewForm.ID);
                }
            });
        specimenTable
            .addEditItemListener(new IInfoTableEditItemListener<SpecimenInfo>() {
                @Override
                public void editItem(InfoTableEvent<SpecimenInfo> event) {
                    Specimen s =
                        ((SpecimenInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).specimen;
                    AdapterBase.openForm(
                        new FormInput(
                            new SpecimenAdapter(null,
                                new SpecimenWrapper(SessionManager
                                    .getAppService(), s))),
                        SpecimenEntryForm.ID);
                }
            });
    }

    @SuppressWarnings("nls")
    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        senderLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Dispatch.PropertyName.SENDER_CENTER.toString());
        receiverLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Dispatch.PropertyName.RECEIVER_CENTER.toString());
        waybillLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                ShipmentInfo.PropertyName.WAYBILL.toString());
        shippingMethodLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                ShippingMethod.NAME.singular().toString());
        if (originInfo.getShipmentInfo().getShippingMethod().needDate()) {
            departedLabel =
                createReadOnlyLabelledField(client, SWT.NONE,
                    i18n.tr("Packed"));
        }
        boxNumberLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                i18n.tr("Box number"));
        dateReceivedLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                i18n.tr("Received"));

        createCommentSection();

        setShipmentValues();
    }

    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client,
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

        CenterWrapper<?> rcvCenter = originInfo.getReceiverCenter();
        setTextValue(receiverLabel, rcvCenter != null ? rcvCenter.getName()
            : StringUtil.EMPTY_STRING);

        setTextValue(waybillLabel, originInfo.getShipmentInfo().getWaybill());
        if (departedLabel != null) {
            setTextValue(departedLabel, shipInfo.getFormattedDatePacked());
        }
        setTextValue(shippingMethodLabel,
            shipMethod == null ? StringUtil.EMPTY_STRING : shipMethod.getName());

        setTextValue(boxNumberLabel, shipInfo.getBoxNumber());
        setTextValue(dateReceivedLabel, shipInfo.getFormattedDateReceived());
    }

    @Override
    public void setValues() throws Exception {
        setPartName();
        setFormText();
        setShipmentValues();

        commentEntryTable.setList(originInfo.getCommentCollection(false));
        specimenTable.setList(specimens);
    }

    @SuppressWarnings("nls")
    private void setPartName() {
        setPartName(i18n.tr("Shipment {0}", originInfo
            .getShipmentInfo().getFormattedDateReceived()));
    }

    @SuppressWarnings("nls")
    private void setFormText() {
        if (!form.isDisposed()) {
            form.setText(i18n.tr("Shipment received on {0} from {1}",
                originInfo.getShipmentInfo().getFormattedDateReceived(),
                originInfo.getCenter().getNameShort()));
        }
    }

}
