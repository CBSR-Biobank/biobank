package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.export.Data;
import edu.ualberta.med.biobank.export.PrintPdfDataExporter;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

public abstract class ReceiveScanDialog<T extends ModelWrapper<?>> extends
    AbstractScanDialog<T> {
    private static final I18n i18n = I18nFactory
        .getI18n(ReceiveScanDialog.class);

    private boolean specimensReceived = false;

    protected List<SpecimenWrapper> extras = new ArrayList<SpecimenWrapper>();

    private boolean hasExpectedSpecimens = false;

    public ReceiveScanDialog(Shell parentShell, final T currentShipment,
        CenterWrapper<?> currentSite) {
        super(parentShell, currentShipment, currentSite);
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // title area message
        return i18n.tr("Scan one pallet received in the shipment.");
    }

    @Override
    protected void specificScanPosProcess(PalletWell palletCell) {
        if (palletCell.getStatus() == UICellStatus.EXTRA) {
            extras.add(palletCell.getSpecimen());
            hasExpectedSpecimens = true;
        }
        if (palletCell.getStatus() == UICellStatus.IN_SHIPMENT_EXPECTED) {
            hasExpectedSpecimens = true;
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected String getProceedButtonlabel() {
        // proceed button label
        return i18n.tr("Accept specimens");
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

    @SuppressWarnings("nls")
    @Override
    protected void doProceed() {
        List<SpecimenWrapper> specimens = new ArrayList<SpecimenWrapper>();
        for (PalletWell cell : getCells().values()) {
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

        if (BgcPlugin
            .openConfirm(
                // confirmation dialog title
                i18n.tr("Print"),
                // confirmation dialog message
                i18n.tr("Do you wish to print a location sheet for the recipient?")))
            print();
    }

    @SuppressWarnings("nls")
    protected void print() {
        try {
            PalletBarcodeDialog barcodeDialog = new PalletBarcodeDialog(
                getShell());
            if (barcodeDialog.open() == Dialog.OK) {
                String productBarcode = barcodeDialog.getBarcode();
                List<Object> output = new ArrayList<Object>();
                Map<RowColPos, ? extends AbstractUIWell> cells = spw.getCells();
                for (RowColPos pos : cells.keySet()) {
                    String inventoryId = ((PalletWell) cells.get(pos))
                        .getValue();
                    SpecimenWrapper specimen = SpecimenWrapper.getSpecimen(
                        SessionManager.getAppService(), inventoryId);
                    String cell[] =
                        new String[] {
                            ContainerLabelingSchemeWrapper.rowColToSbs(pos),
                            inventoryId,
                            specimen.getCollectionEvent().getPatient()
                                .getPnumber(),
                            specimen.getSpecimenType().getNameShort(),
                            DateFormatter.formatAsDate(specimen
                                .getTopSpecimen()
                                .getCreatedAt()) };
                    output.add(cell);
                }
                Data data = new Data();
                data.setColumnNames(Arrays.asList(
                    // exported report column name, for positions of specimens
                    // according to a labeling scheme
                    i18n.tr("Location"),
                    Specimen.PropertyName.INVENTORY_ID.toString(),
                    Patient.NAME.format(1).toString(),
                    // exported report column name, for specimen's specimen type
                    i18n.tr("Sample Type"),
                    // exported report column name, for specimen's date drawn
                    i18n.tr("Date Drawn")));

                // exported report description
                String description = i18n.tr("Barcode: {0}", productBarcode);

                data.setDescription(Arrays.asList(description));
                data.setRows(output);

                // exported report title
                data.setTitle(i18n.tr("Pallet Info Sheet"));

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
    protected abstract Map<RowColPos, PalletWell> getFakeDecodedWells();

    public boolean hasReceivedSpecimens() {
        return specimensReceived;
    }

}
