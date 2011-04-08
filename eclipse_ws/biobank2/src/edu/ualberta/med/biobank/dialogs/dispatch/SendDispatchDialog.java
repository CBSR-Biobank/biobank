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
import edu.ualberta.med.biobank.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class SendDispatchDialog extends BiobankDialog {

    private static final String TITLE = "Dispatching aliquots";
    private DispatchWrapper shipment;

    public SendDispatchDialog(Shell parentShell, DispatchWrapper shipment) {
        super(parentShell);
        this.shipment = shipment;
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Fill the following fields to complete the shipment";
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
        widgetCreator.createComboViewer(contents, "Shipping Method",
            ShippingMethodWrapper.getShippingMethods(SessionManager
                .getAppService()), selectedShippingMethod, null,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    shipment.getShipmentInfo().setShippingMethod(
                        (ShippingMethodWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.NONE,
            "Waybill", null, shipInfo, ShipmentInfoPeer.WAYBILL.getName(), null);

        Date date = new Date();
        shipment.getShipmentInfo().setPackedAt(date);
        createDateTimeWidget(contents, "Packed At", date, shipInfo,
            ShipmentInfoPeer.PACKED_AT.getName(), new NotNullValidator(
                "Packed should be set"));
    }

}
