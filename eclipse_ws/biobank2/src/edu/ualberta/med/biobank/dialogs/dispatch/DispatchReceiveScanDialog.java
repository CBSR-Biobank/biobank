package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.ShipmentReceiveProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

public class DispatchReceiveScanDialog extends
    ReceiveScanDialog<DispatchWrapper> {
    private static final I18n i18n = I18nFactory
        .getI18n(DispatchReceiveScanDialog.class);

    public DispatchReceiveScanDialog(Shell parentShell,
        final DispatchWrapper currentShipment, CenterWrapper<?> currentSite) {
        super(parentShell, currentShipment, currentSite);
    }

    @Override
    protected Action<ProcessResult> getCellProcessAction(Integer centerId,
        CellInfo cell, Locale locale) {
        return new ShipmentReceiveProcessAction(getProcessData(), centerId,
            cell,
            locale);
    }

    @Override
    protected Action<ProcessResult> getPalletProcessAction(
        Integer centerId,
        Map<RowColPos, CellInfo> cells,
        Locale locale) {
        return new ShipmentReceiveProcessAction(getProcessData(), centerId, cells, locale);
    }

    protected ShipmentProcessInfo getProcessData() {
        return new ShipmentProcessInfo(null, currentShipment, false);
    }

    @SuppressWarnings("nls")
    @Override
    protected void addExtraCells() {
        if (extras != null && extras.size() > 0) {
            BgcPlugin
                .openAsyncInformation(
                    // alert error title
                    i18n.tr("Specimens not in dispatch"),
                    // alert error message
                    i18n.tr("Some of the specimens in this pallet were not supposed  to be in this shipment. They will be added to the extra-pending list."));
            try {
                currentShipment.addSpecimens(extras,
                    DispatchSpecimenState.EXTRA);
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    // alert error title
                    i18n.tr("Error flagging specimens"), e);
            }
        }
    }

    @Override
    protected void receiveSpecimens(List<SpecimenWrapper> specimens) {
        currentShipment.receiveSpecimens(specimens);
    }

    @Override
    protected List<UICellStatus> getPalletCellStatus() {
        return UICellStatus.DEFAULT_PALLET_DISPATCH_RECEIVE_STATUS_LIST;
    }
}
