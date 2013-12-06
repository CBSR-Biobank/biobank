package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.ShipmentReceiveProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

public class RequestReceiveScanDialog extends ReceiveScanDialog<RequestWrapper> {
    private static final I18n i18n = I18nFactory
        .getI18n(RequestReceiveScanDialog.class);

    private final List<SpecimenWrapper> dispatchSpecimens;

    public RequestReceiveScanDialog(Shell parentShell,
        final RequestWrapper currentShipment, CenterWrapper<?> centerWrapper) {
        super(parentShell, currentShipment, centerWrapper);
        dispatchSpecimens = new ArrayList<SpecimenWrapper>();
    }

    @Override
    protected void addExtraCells() {
        if (extras != null && extras.size() > 0) {
            Display.getDefault().asyncExec(new Runnable() {
                @SuppressWarnings("nls")
                @Override
                public void run() {
                    BgcPlugin.openInformation(
                        // information dialog title
                        i18n.tr("Extra specimens"),
                        // information dialog message
                        i18n.tr("Some of the specimens in this pallet were not supposed to be in this shipment."));
                }
            });
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected void receiveSpecimens(List<SpecimenWrapper> specimens) {
        try {
            dispatchSpecimens.addAll(specimens);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                // error dialog title
                i18n.tr("Error receiving request"), e);
        }
    }

    @Override
    protected List<UICellStatus> getPalletCellStatus() {
        return UICellStatus.REQUEST_PALLET_STATUS_LIST;
    }

    public List<SpecimenWrapper> getSpecimens() {
        return dispatchSpecimens;
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

    private ShipmentProcessInfo getProcessData() {
        return new ShipmentProcessInfo(null, currentShipment, false);
    }

}
