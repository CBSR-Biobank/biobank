package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;

public abstract class ReceiveScanDialog<T extends ModelWrapper<?>> extends
    AbstractScanDialog<T> {

    private int pendingSpecimensNumber = 0;

    private boolean specimensReceived = false;

    protected List<SpecimenWrapper> extras = new ArrayList<SpecimenWrapper>();

    public ReceiveScanDialog(Shell parentShell, final T currentShipment,
        CenterWrapper<?> currentSite) {
        super(parentShell, currentShipment, currentSite);
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.getString("DispatchReceiveScanDialog.description"); //$NON-NLS-1$
    }

    @Override
    protected abstract ProcessData getProcessData();

    @Override
    protected void specificScanPosProcess(PalletCell palletCell) {
        if (palletCell.getStatus() == UICellStatus.EXTRA)
            extras.add(palletCell.getSpecimen());
    }

    @Override
    protected String getProceedButtonlabel() {
        return Messages
            .getString("DispatchReceiveScanDialog.proceed.button.label"); //$NON-NLS-1$
    }

    @Override
    protected boolean canActivateProceedButton() {
        return pendingSpecimensNumber != 0;
    }

    @Override
    protected boolean canActivateNextAndFinishButton() {
        return pendingSpecimensNumber == 0;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.PROCEED_ID == buttonId
            || IDialogConstants.FINISH_ID == buttonId
            || IDialogConstants.NEXT_ID == buttonId) {
            addExtraCells();
        }
        if (IDialogConstants.NEXT_ID == buttonId)
            extras.clear();
        super.buttonPressed(buttonId);
    }

    protected abstract void addExtraCells();

    @Override
    protected void doProceed() {
        List<SpecimenWrapper> specimens = new ArrayList<SpecimenWrapper>();
        for (PalletCell cell : getCells().values()) {
            if (cell.getStatus() == UICellStatus.IN_SHIPMENT_EXPECTED) {
                specimens.add(cell.getSpecimen());
                cell.setStatus(UICellStatus.IN_SHIPMENT_RECEIVED);
            }
        }
        receiveSpecimens(specimens);
        redrawPallet();
        pendingSpecimensNumber = 0;
        setOkButtonEnabled(true);
        specimensReceived = true;
        Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
        cancelButton.setEnabled(false);
    }

    protected abstract void receiveSpecimens(List<SpecimenWrapper> specimens);

    @Override
    protected abstract List<UICellStatus> getPalletCellStatus();

    @Override
    protected abstract Map<RowColPos, PalletCell> getFakeScanCells();

    public boolean hasReceivedSpecimens() {
        return specimensReceived;
    }

}
