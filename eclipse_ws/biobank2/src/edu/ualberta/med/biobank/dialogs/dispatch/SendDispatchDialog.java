package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.i18n.ShipmentInfoI18n;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SendDispatchDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(SendDispatchDialog.class);

    @SuppressWarnings("nls")
    // send dispatch dialog title
    private static final String TITLE = i18n.tr("Dispatching specimens");

    private final DispatchWrapper shipment;

    public SendDispatchDialog(Shell parentShell, DispatchWrapper shipment) {
        super(parentShell);
        this.shipment = shipment;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // send dispatch dialog title area message
        return i18n.tr("Fill the following fields to complete the shipment");
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @SuppressWarnings("nls")
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
        widgetCreator.createComboViewer(contents,
            ShipmentInfoI18n.Property.SHIPPING_METHOD.toString(),
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
            // shipping waybill text box label
            ShipmentInfoI18n.Property.WAYBILL.toString(),
            null, shipInfo, ShipmentInfoPeer.WAYBILL.getName(), null);

        Date date = new Date();
        shipment.getShipmentInfo().setPackedAt(date);
        createDateTimeWidget(contents,
            ShipmentInfoI18n.Property.PACKED_AT.toString(),
            date, shipInfo,
            ShipmentInfoPeer.PACKED_AT.getName(), new NotNullValidator(
                // time packed required validation message
                i18n.tr("Time packed must be set")));
    }
}
