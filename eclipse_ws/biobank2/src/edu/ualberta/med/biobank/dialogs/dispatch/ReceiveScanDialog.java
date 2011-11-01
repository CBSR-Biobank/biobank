package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.export.Data;
import edu.ualberta.med.biobank.export.PrintPdfDataExporter;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.grids.cell.AbstractUICell;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;

public abstract class ReceiveScanDialog<T extends ModelWrapper<?>> extends
    AbstractScanDialog<T> {

    private boolean specimensReceived = false;

    protected List<SpecimenWrapper> extras = new ArrayList<SpecimenWrapper>();

    private boolean hasExpectedSpecimens = false;

    public ReceiveScanDialog(Shell parentShell, final T currentShipment,
        CenterWrapper<?> currentSite) {
        super(parentShell, currentShipment, currentSite);
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.ReceiveScanDialog_description;
    }

    @Override
    protected void specificScanPosProcess(PalletCell palletCell) {
        if (palletCell.getStatus() == UICellStatus.EXTRA) {
            extras.add(palletCell.getSpecimen());
            hasExpectedSpecimens = true;
        }
        if (palletCell.getStatus() == UICellStatus.IN_SHIPMENT_EXPECTED) {
            hasExpectedSpecimens = true;
        }
    }

    @Override
    protected String getProceedButtonlabel() {
        return Messages.ReceiveScanDialog_proceed_button_label;
    }

    @Override
    protected boolean canActivateProceedButton() {
        return hasExpectedSpecimens;
    }

    @Override
    protected boolean canActivateNextAndFinishButton() {
        return !hasExpectedSpecimens;
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
        extras.clear();
        hasExpectedSpecimens = false;
        setOkButtonEnabled(true);
        specimensReceived = true;
        Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
        cancelButton.setEnabled(false);

        if (BgcPlugin.openConfirm(Messages.ReceiveScanDialog_0,
            Messages.ReceiveScanDialog_1))
            print();
    }

    protected void print() {
        try {
            PalletBarcodeDialog barcodeDialog = new PalletBarcodeDialog(
                getShell());
            if (barcodeDialog.open() == Dialog.OK) {
                String productBarcode = barcodeDialog.getBarcode();
                List<Object> output = new ArrayList<Object>();
                Map<RowColPos, ? extends AbstractUICell> cells = spw.getCells();
                for (RowColPos pos : cells.keySet()) {
                    String inventoryId = ((PalletCell) cells.get(pos))
                        .getValue();
                    SpecimenWrapper specimen = SpecimenWrapper.getSpecimen(
                        SessionManager.getAppService(), inventoryId);
                    String cell[] = new String[] {
                        ContainerLabelingSchemeWrapper.rowColToSbs(pos),
                        inventoryId,
                        specimen.getCollectionEvent().getPatient().getPnumber(),
                        specimen.getSpecimenType().getNameShort(),
                        DateFormatter.formatAsDate(specimen.getTopSpecimen()
                            .getCreatedAt()) };
                    output.add(cell);
                }
                Data data = new Data();
                data.setColumnNames(Arrays.asList(Messages.ReceiveScanDialog_2,
                    Messages.ReceiveScanDialog_3,
                    Messages.ReceiveScanDialog_4, Messages.ReceiveScanDialog_5,
                    Messages.ReceiveScanDialog_6));
                data.setDescription(Arrays.asList(Messages.ReceiveScanDialog_7
                    + productBarcode));
                data.setRows(output);
                data.setTitle("Pallet Info Sheet"); //$NON-NLS-1$

                PrintPdfDataExporter pdf = new PrintPdfDataExporter();

                pdf.export(data, new BiobankLabelProvider() {
                    @Override
                    public String getColumnText(Object o, int index) {
                        return (String) ((Object[]) o)[index];
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
