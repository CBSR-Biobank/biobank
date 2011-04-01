package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm.AliquotInfo;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchReceiveScanDialog extends
    AbstractScanDialog<DispatchWrapper> {

    private static final String TITLE = Messages
        .getString("DispatchReceiveScanDialog.title"); //$NON-NLS-1$

    private int pendingAliquotsNumber = 0;

    private boolean aliquotsReceived = false;

    private int errors;

    public DispatchReceiveScanDialog(Shell parentShell,
        final DispatchWrapper currentShipment, CenterWrapper<?> currentSite) {
        super(parentShell, currentShipment, currentSite);
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.getString("DispatchReceiveScanDialog.description"); //$NON-NLS-1$
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    /**
     * set the status of the cell. return the aliquot if it is an extra one.
     */
    protected void processCellStatus(PalletCell cell) {
        AliquotInfo info = DispatchReceivingEntryForm.getInfoForInventoryId(
            currentShipment, cell.getValue());
        if (info.aliquot != null) {
            cell.setSpecimen(info.aliquot);
            cell.setTitle(info.aliquot.getCollectionEvent().getPatient()
                .getPnumber());
        }
        switch (info.type) {
        case RECEIVED:
            cell.setStatus(CellStatus.IN_SHIPMENT_RECEIVED);
            break;
        case DUPLICATE:
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation(Messages
                .getString("DispatchReceiveScanDialog.cell.duplicate.msg") //$NON-NLS-1$
                + cell.getValue());
            cell.setTitle("!"); //$NON-NLS-1$
            errors++;
            break;
        case NOT_IN_DB:
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation(Messages.getString(
                "DispatchReceiveScanDialog.cell.notInDb.msg", cell.getValue())); //$NON-NLS-1$
            cell.setTitle("!"); //$NON-NLS-1$
            errors++;
            break;
        case NOT_IN_SHIPMENT:
            cell.setStatus(CellStatus.EXTRA);
            cell.setInformation(Messages
                .getString("DispatchReceiveScanDialog.cell.notInShipment.msg")); //$NON-NLS-1$
            pendingAliquotsNumber++;
            break;
        case OK:
            cell.setStatus(CellStatus.IN_SHIPMENT_EXPECTED);
            pendingAliquotsNumber++;
            break;
        case EXTRA:
            cell.setStatus(CellStatus.EXTRA);
            pendingAliquotsNumber++;
            break;
        }
    }

    @Override
    protected void processScanResult(IProgressMonitor monitor,
        CenterWrapper<?> site) throws Exception {
        Map<RowColPos, PalletCell> cells = getCells();
        if (cells != null) {
            processCells(cells.keySet(), monitor);
        }
    }

    private void processCells(Collection<RowColPos> rcps,
        IProgressMonitor monitor) {
        pendingAliquotsNumber = 0;
        errors = 0;
        Map<RowColPos, PalletCell> cells = getCells();
        if (cells != null) {
            setScanOkValue(false);
            List<SpecimenWrapper> newExtraAliquots = new ArrayList<SpecimenWrapper>();
            for (RowColPos rcp : rcps) {
                if (monitor != null) {
                    monitor
                        .subTask(Messages
                            .getString("DispatchReceiveScanDialog.processCell.task.position") //$NON-NLS-1$
                            + ContainerLabelingSchemeWrapper.rowColToSbs(rcp));
                }
                PalletCell cell = cells.get(rcp);
                processCellStatus(cell);
                if (cell.getStatus() == CellStatus.EXTRA) {
                    newExtraAliquots.add(cell.getSpecimen());
                }
            }
            addExtraCells(newExtraAliquots);
            setScanOkValue(errors == 0);
        }
    }

    private void addExtraCells(final List<SpecimenWrapper> extraAliquots) {
        if (extraAliquots.size() > 0) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    BiobankPlugin.openInformation(
                        Messages
                            .getString("DispatchReceiveScanDialog.notInDispatch.error.title"), //$NON-NLS-1$
                        Messages
                            .getString("DispatchReceiveScanDialog.notInDispatch.error.msg")); //$NON-NLS-1$
                    try {
                        (currentShipment).addExtraAliquots(extraAliquots);
                    } catch (Exception e) {
                        BiobankPlugin.openAsyncError(
                            Messages
                                .getString("DispatchReceiveScanDialog.flagging.error.title"), //$NON-NLS-1$
                            e);
                    }
                }
            });
        }
    }

    @Override
    protected String getProceedButtonlabel() {
        return Messages
            .getString("DispatchReceiveScanDialog.proceed.button.label"); //$NON-NLS-1$
    }

    @Override
    protected boolean canActivateProceedButton() {
        return pendingAliquotsNumber != 0;
    }

    @Override
    protected boolean canActivateNextAndFinishButton() {
        return pendingAliquotsNumber == 0;
    }

    @Override
    protected void doProceed() {
        List<SpecimenWrapper> aliquots = new ArrayList<SpecimenWrapper>();
        for (PalletCell cell : getCells().values()) {
            if (cell.getStatus() == CellStatus.IN_SHIPMENT_EXPECTED) {
                aliquots.add(cell.getSpecimen());
                cell.setStatus(CellStatus.IN_SHIPMENT_RECEIVED);
            }
        }
        try {
            (currentShipment).receiveSpecimens(aliquots);
            redrawPallet();
            pendingAliquotsNumber = 0;
            setOkButtonEnabled(true);
            aliquotsReceived = true;
        } catch (Exception e) {
            BiobankPlugin
                .openAsyncError(
                    Messages
                        .getString("DispatchReceiveScanDialog.receiveing.error.title"), e); //$NON-NLS-1$
        }
        Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
        cancelButton.setEnabled(false);
    }

    @Override
    protected void startNewPallet() {
        setRescanMode(false);
        super.startNewPallet();
    }

    @Override
    protected List<CellStatus> getPalletCellStatus() {
        return CellStatus.DEFAULT_PALLET_DISPATCH_RECEIVE_STATUS_LIST;
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() {
        Map<RowColPos, PalletCell> palletScanned = new TreeMap<RowColPos, PalletCell>();
        if ((currentShipment).getSpecimenCollection().size() > 0) {
            int i = 0;
            for (DispatchSpecimenWrapper dsa : (currentShipment)
                .getDispatchSpecimenCollection(false)) {
                int row = i / 12;
                int col = i % 12;
                if (!DispatchSpecimenState.MISSING.isEquals(dsa.getState()))
                    palletScanned.put(new RowColPos(row, col), new PalletCell(
                        new ScanCell(row, col, dsa.getSpecimen()
                            .getInventoryId())));
                i++;
            }
        }
        return palletScanned;
    }

    public boolean hasReceivedAliquots() {
        return aliquotsReceived;
    }

    @Override
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
        processCells(Arrays.asList(cell.getRowColPos()), null);
        super.postprocessScanTubeAlone(cell);
    }
}
