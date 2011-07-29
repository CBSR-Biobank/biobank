package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SendDispatchDialog extends BgcBaseDialog {

    private static final String TITLE = Messages.SendDispatchDialog_title;
    private DispatchWrapper shipment;

    public SendDispatchDialog(Shell parentShell, DispatchWrapper shipment) {
        super(parentShell);
        this.shipment = shipment;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.SendDispatchDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        ShipmentInfoWrapper shipInfo = new ShipmentInfoWrapper(
            SessionManager.getAppService());
        shipment.setShipmentInfo(shipInfo);

        ShippingMethodWrapper selectedShippingMethod = shipInfo
            .getShippingMethod();
        widgetCreator.createComboViewer(contents, Messages.SendDispatchDialog_shippingMethod_label,
            ShippingMethodWrapper.getShippingMethods(SessionManager
                .getAppService()), selectedShippingMethod, null,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    shipment.getShipmentInfo().setShippingMethod(
                        (ShippingMethodWrapper) selectedObject);
                }
            }, new BiobankLabelProvider());

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.NONE,
            Messages.SendDispatchDialog_waybill_label, null, shipInfo, ShipmentInfoPeer.WAYBILL.getName(), null);

        Date date = new Date();
        shipment.getShipmentInfo().setPackedAt(date);
        createDateTimeWidget(contents, Messages.SendDispatchDialog_timePacked_label, date, shipInfo,
            ShipmentInfoPeer.PACKED_AT.getName(), new NotNullValidator(
                Messages.SendDispatchDialog_timePacked_validator_msg));
    }

}
