package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.containerType.ContainerLabelingSchemeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerLabelingSchemeGetInfoAction.ContainerLabelingSchemeInfo;
import edu.ualberta.med.biobank.common.action.search.SpecimenByMicroplateSearchAction;
import edu.ualberta.med.biobank.common.util.InventoryIdUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.scanmanually.ScanTubesManuallyWizardDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PalletScanManagement {
    private static final I18n i18n = I18nFactory.getI18n(PalletScanManagement.class);

    protected Map<RowColPos, PalletWell> wells = new HashMap<RowColPos, PalletWell>();

    public enum ScanManualOption {
        ALLOW_DUPLICATES, NO_DUPLICATES
    };

    private int scansCount = 0;
    private boolean useScanner = true;

    private ContainerType type;

    @SuppressWarnings("nls")
    public PalletScanManagement() {
        try {
            this.type = getFakePalletRowsCols(8, 12);
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Error"),
                // TR: dialog message
                i18n.tr("Unable to load pallet type 8*12"),
                e);
        }
    }

    @SuppressWarnings("nls")
    private ContainerType getFakePalletRowsCols(int rows, int cols) throws ApplicationException {
        ContainerType ct = new ContainerType();
        ct.setCapacity(new Capacity(rows, cols));

        ContainerLabelingSchemeInfo schemeInfo = SessionManager.getAppService().doAction(
            new ContainerLabelingSchemeGetInfoAction("SBS Standard"));

        if (schemeInfo == null) {
            throw new RuntimeException("SBS Standard labeling scheme not found in database");
        }

        ct.setChildLabelingScheme(schemeInfo.getLabelingScheme());
        return ct;
    }

    public PalletScanManagement(ContainerType containerType) {
        this.type = containerType;
    }

    @SuppressWarnings("nls")
    public void launchScanAndProcessResult() {
        // FIXME: scanning and decoding
        throw new RuntimeException("not implemented yet");
    }

    @SuppressWarnings("nls")
    public void scanTubesManually(MouseEvent event, ScanManualOption scanManualOption) {
        RowColPos startPos = ((ScanPalletWidget) event.widget).getPositionAtCoordinates(
            event.x, event.y);

        // if mouse click does not produce a position then there is nothing to do
        if (startPos == null) return;

        if (!canScanTubeAlone(wells.get(startPos))) return;

        Set<PalletWell> manuallyEnteredCells = new HashSet<PalletWell>();

        for (Entry<RowColPos, String> entry : scanTubesManually(startPos, scanManualOption).entrySet()) {
            RowColPos pos = entry.getKey();
            int row = pos.getRow();
            int col = pos.getCol();
            String inventoryId = entry.getValue();

            if ((inventoryId == null) || inventoryId.isEmpty()) continue;

            PalletWell cell = wells.get(pos);
            if (cell == null) {
                cell = new PalletWell(row, col, new DecodedWell(row, col, inventoryId));
                wells.put(pos, cell);
            } else {
                cell.setValue(inventoryId);
            }

            manuallyEnteredCells.add(cell);
        }

        try {
            postprocessScanTubesManually(manuallyEnteredCells);
        } catch (Exception ex) {
            BgcPlugin.openAsyncError(
                // dialog title
                i18n.tr("Scan tube error"),
                ex);
        }
    }

    protected boolean canScanTubeAlone(
        @SuppressWarnings("unused") PalletWell cell) {
        return true;
    }

    @SuppressWarnings("nls")
    private Map<RowColPos, String> scanTubesManually(RowColPos startPos,
        ScanManualOption scanManualOption) {
        Map<String, String> existingInventoryIdsByLabel = new HashMap<String, String>();

        if (scanManualOption == ScanManualOption.NO_DUPLICATES) {
            for (PalletWell well : wells.values()) {
                String inventoryId = well.getValue();
                if ((inventoryId == null) || !inventoryId.isEmpty()) {
                    existingInventoryIdsByLabel.put(well.getLabel(), well.getValue());
                }
            }
        }

        Map<String, String> inventoryIds = ScanTubesManuallyWizardDialog.getInventoryIds(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            getLabelsForMissingInventoryIds(startPos), existingInventoryIdsByLabel);

        Map<RowColPos, String> result = new HashMap<RowColPos, String>();

        for (Entry<String, String> entry : inventoryIds.entrySet()) {
            try {
                RowColPos pos = type.getRowColFromPositionString(entry.getKey());
                if (pos == null) {
                    throw new RuntimeException("label converstion to position failed: "
                        + entry.getKey());
                }
                result.put(pos, entry.getValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    /**
     * Returns the set of labels missing an inventory ID. The set is ordered, so that the set starts
     * with the label at position <code>startPos</code>, the remainder of the set is populated with
     * the missing labels after the selection, and then the missing labels from the start of the
     * container through to the one just before the selection
     * 
     * @param startPos The label at the start of the set.
     */
    private Set<String> getLabelsForMissingInventoryIds(RowColPos startPos) {

        // find the tubes that were not successfully scanned
        Capacity capacity = type.getCapacity();

        Set<String> labelsMissingInventoryId = new LinkedHashSet<String>();
        labelsMissingInventoryId.add(type.getPositionString(startPos));

        Set<String> labelsMissingInventoryIdBeforeSelection = new LinkedHashSet<String>();

        // now add the other positions missing inventory IDs
        boolean selectionFound = false;
        for (int row = 0, rows = capacity.getRowCapacity(); row < rows; ++row) {
            for (int col = 0, cols = capacity.getColCapacity(); col < cols; ++col) {
                RowColPos pos = new RowColPos(row, col);
                if (pos.equals(startPos)) {
                    selectionFound = true;
                }

                PalletWell well = wells.get(pos);

                if ((well == null) || (well.getValue() == null) || well.getValue().isEmpty()) {
                    if (selectionFound) {
                        labelsMissingInventoryId.add(type.getPositionString(pos));
                    } else {
                        labelsMissingInventoryIdBeforeSelection.add(type.getPositionString(pos));
                    }
                }
            }
        }

        labelsMissingInventoryId.addAll(labelsMissingInventoryIdBeforeSelection);

        return labelsMissingInventoryId;
    }

    protected void beforeThreadStart() {
        // default does nothing
    }

    protected void beforeScan() {
        // default does nothing
    }

    @SuppressWarnings("unused")
    protected Map<RowColPos, PalletWell> getFakeDecodedWells(String plateToScan) throws Exception {
        return null;
    }

    @SuppressWarnings("unused")
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        // default does nothing
    }

    @SuppressWarnings("unused")
    protected void postprocessScanTubesManually(Set<PalletWell> cells) throws Exception {
        // default does nothing
    }

    protected void afterScanBeforeMerge() {
        // default does nothing
    }

    protected void afterSuccessfulScan() {
        // default does nothing
    }

    protected void afterScanAndProcess() {
        // default does nothing
    }

    protected void plateError() {
        // default does nothing
    }

    protected void scanAndProcessError(
        @SuppressWarnings("unused") String errorMsg) {
        // default does nothing
    }

    public Map<RowColPos, PalletWell> getCells() {
        return wells;
    }

    public void onReset() {
        scansCount = 0;
        initCells();
    }

    public void setUseScanner(boolean useScanner) {
        this.useScanner = useScanner;
    }

    private void initCells() {
        wells = new HashMap<RowColPos, PalletWell>();
    }

    public int getScansCount() {
        return scansCount;
    }

    @SuppressWarnings("nls")
    public void initCellsWithContainer(ContainerWrapper container) {
        if (!useScanner) {
            wells.clear();
            for (Entry<RowColPos, SpecimenWrapper> entry : container
                .getSpecimens().entrySet()) {
                RowColPos pos = entry.getKey();
                PalletWell cell =
                    new PalletWell(pos.getRow(), pos.getCol(),
                        new DecodedWell(pos.getRow(), pos.getCol(), entry
                            .getValue().getInventoryId()));
                cell.setSpecimen(entry.getValue());
                cell.setStatus(UICellStatus.FILLED);
                wells.put(pos, cell);
            }
            try {
                ArrayList<String> ids = new ArrayList<String>();
                if ((container.getProductBarcode() != null)
                    && (container.getProductBarcode().length() != 0)) {
                    ids = SessionManager.getAppService().doAction(
                        new SpecimenByMicroplateSearchAction(container.getProductBarcode())).getList();
                }
                if ((container.getContainerType().getIsMicroplate()) || (!ids.isEmpty())) { // microplate
                                                                                            // with
                                                                                            // specimens
                    for (String id : ids) {
                        SpecimenWrapper sw = SpecimenWrapper.getSpecimen(
                            SessionManager.getAppService(), id);
                        RowColPos pos = container.getPositionFromLabelingScheme(InventoryIdUtil.positionPart(id));
                        PalletWell cell =
                            new PalletWell(pos.getRow(), pos.getCol(),
                                new DecodedWell(pos.getRow(), pos.getCol(), sw.getInventoryId()));
                        cell.setSpecimen(sw);
                        cell.setStatus(UICellStatus.NEW);
                        wells.put(pos, cell);
                    }
                }
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    i18n.tr("Problem with microplate specimens"), e);
            }
        }
    }

    public void setContainerType(ContainerType containerType) {
        this.type = containerType;
    }

    public ContainerType getContainerType() {
        return type;
    }
}
