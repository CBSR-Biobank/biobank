package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.containerType.ContainerLabelingSchemeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerLabelingSchemeGetInfoAction.ContainerLabelingSchemeInfo;
import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.search.SpecimenByMicroplateSearchAction;
import edu.ualberta.med.biobank.common.util.InventoryIdUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.scanmanually.IManualScan;
import edu.ualberta.med.biobank.forms.linkassign.IDecodePalletManagement;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.mvp.view.DialogView.Dialog;
import edu.ualberta.med.biobank.widgets.grids.PalletWidget;
import edu.ualberta.med.biobank.widgets.grids.well.SpecimenCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.dialogs.DecodeImageDialog;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PalletScanManagement {

    // private static Logger LOG = LoggerFactory.getLogger(PalletScanManagement.class.getName());

    private static final I18n i18n = I18nFactory.getI18n(PalletScanManagement.class);

    protected Map<RowColPos, SpecimenCell> wells = new HashMap<RowColPos, SpecimenCell>();

    public enum ScanManualOption {
        ALLOW_DUPLICATES, NO_DUPLICATES
    };

    private int scansCount = 0;

    private ContainerType selectedContainerType;

    private final IDecodePalletManagement parent;

    private final IManualScan scanManually;

    public PalletScanManagement(
        IDecodePalletManagement parent,
        ContainerType containerType,
        IManualScan scanManually) {
        this.parent = parent;
        this.selectedContainerType = containerType;
        this.scanManually = scanManually;
    }

    public PalletScanManagement(
        IDecodePalletManagement parent,
        IManualScan scanManually) {
        this.parent = parent;
        this.selectedContainerType = getFakePalletRowsCols(8, 12);
        this.scanManually = scanManually;
    }

    @SuppressWarnings("nls")
    private ContainerType getFakePalletRowsCols(int rows, int cols) {
        try {

            ContainerType ct = new ContainerType();
            ct.setCapacity(new Capacity(rows, cols));

            ContainerLabelingSchemeInfo schemeInfo;
            schemeInfo = SessionManager.getAppService().doAction(
                new ContainerLabelingSchemeGetInfoAction("SBS Standard"));

            if (schemeInfo == null) {
                throw new RuntimeException("SBS Standard labeling scheme not found in database");
            }

            ct.setChildLabelingScheme(schemeInfo.getLabelingScheme());
            return ct;
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("nls")
    public void decodeAndProcessResult() {
        Set<PalletDimensions> validPlateDimensions;
        DecodeImageDialog dialog;

        if (selectedContainerType != null) {
            validPlateDimensions = getValidPlateDimensions(selectedContainerType);
            dialog = new DecodeImageDialog(
                Display.getDefault().getActiveShell(), validPlateDimensions);
        } else {
            dialog = new DecodeImageDialog(Display.getDefault().getActiveShell());
        }

        if (dialog.open() == Dialog.OK) {
            scansCount++;
            initCells();
            Set<DecodedWell> decodeResult = dialog.getDecodeResult();
            PalletDimensions plateDimensions = dialog.getPlateDimensions();
            setFakeContainerType(plateDimensions.getRows(), plateDimensions.getCols());
            wells = SpecimenCell.convertArray(decodeResult);

            parent.beforeProcessingThreadStart();

            IRunnableWithProgress op = new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) {
                    monitor.beginTask(
                        // progress monitor message
                        i18n.tr("Processing specimens..."),
                        IProgressMonitor.UNKNOWN);

                    try {
                        parent.processDecodeResult();
                    } catch (RemoteConnectFailureException exp) {
                        BgcPlugin.openRemoteConnectErrorMessage(exp);
                        parent.decodeAndProcessError(null);
                    } catch (AccessDeniedException e) {
                        BgcPlugin.openAsyncError(
                            // dialog title
                            i18n.tr("Scan result error"),
                            e.getLocalizedMessage());
                        parent.decodeAndProcessError(e.getLocalizedMessage());
                    } catch (Exception ex) {
                        BgcPlugin.openAsyncError(
                            // dialog title
                            i18n.tr("Processing error"),
                            ex,
                            // dialog message
                            i18n.tr("Barcodes can still be entered with the handheld 2D scanner."));
                    }
                    monitor.done();
                }
            };

            try {
                new ProgressMonitorDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()).run(
                    true, false, op);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("nls")
    public void scanTubesManually(MouseEvent event, ScanManualOption scanManualOption) {
        // LOG.trace("scanTubesManually: event [ x: {}, y {} ]", event.x, event.y);

        RowColPos startPos = ((PalletWidget) event.widget).getPositionAtCoordinates(
            event.x, event.y);

        // if mouse click does not produce a position then there is nothing to do
        if (startPos == null) return;

        SpecimenCell specimenCell = wells.get(startPos);

        if ((specimenCell != null) && !specimenCell.getValue().isEmpty()) {
            // this cell already has an inventory id
            return;
        }

        if (!canScanTubeAlone(specimenCell)) return;

        Set<SpecimenCell> manuallyEnteredCells = new HashSet<SpecimenCell>();

        for (Entry<RowColPos, String> entry : scanTubesManually(startPos, scanManualOption).entrySet()) {
            RowColPos pos = entry.getKey();
            int row = pos.getRow();
            int col = pos.getCol();
            String inventoryId = entry.getValue();

            if ((inventoryId == null) || inventoryId.isEmpty()) continue;

            String label = selectedContainerType.getPositionString(pos);
            SpecimenCell cell = new SpecimenCell(row, col, new DecodedWell(label, inventoryId));
            wells.put(pos, cell);

            manuallyEnteredCells.add(cell);
        }

        if (!manuallyEnteredCells.isEmpty()) {
            try {
                parent.postProcessDecodeTubesManually(manuallyEnteredCells);
            } catch (Exception ex) {
                BgcPlugin.openAsyncError(
                    // dialog title
                    i18n.tr("Decode pallet error"),
                    ex);
            }
        }
    }

    protected boolean canScanTubeAlone(
        @SuppressWarnings("unused") SpecimenCell cell) {
        return true;
    }

    @SuppressWarnings("nls")
    private Map<RowColPos, String> scanTubesManually(RowColPos startPos,
        ScanManualOption scanManualOption) {
        Map<String, String> existingInventoryIdsByLabel = new HashMap<String, String>();

        if (scanManualOption == ScanManualOption.NO_DUPLICATES) {
            for (SpecimenCell well : wells.values()) {
                String inventoryId = well.getValue();
                if ((inventoryId == null) || !inventoryId.isEmpty()) {
                    existingInventoryIdsByLabel.put(well.getLabel(), well.getValue());
                }
            }
        }

        Map<String, String> inventoryIds = scanManually.getInventoryIds(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            getLabelsForMissingInventoryIds(startPos), existingInventoryIdsByLabel);

        Map<RowColPos, String> result = new HashMap<RowColPos, String>();

        for (Entry<String, String> entry : inventoryIds.entrySet()) {
            try {
                RowColPos pos = selectedContainerType.getRowColFromPositionString(entry.getKey());
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
        Capacity capacity = selectedContainerType.getCapacity();

        Set<String> labelsMissingInventoryId = new LinkedHashSet<String>();
        labelsMissingInventoryId.add(selectedContainerType.getPositionString(startPos));

        Set<String> labelsMissingInventoryIdBeforeSelection = new LinkedHashSet<String>();

        // now add the other positions missing inventory IDs
        boolean selectionFound = false;
        for (int row = 0, rows = capacity.getRowCapacity(); row < rows; ++row) {
            for (int col = 0, cols = capacity.getColCapacity(); col < cols; ++col) {
                RowColPos pos = new RowColPos(row, col);
                if (pos.equals(startPos)) {
                    selectionFound = true;
                }

                SpecimenCell well = wells.get(pos);

                if ((well == null) || (well.getValue() == null) || well.getValue().isEmpty()) {
                    if (selectionFound) {
                        labelsMissingInventoryId.add(selectedContainerType.getPositionString(pos));
                    } else {
                        labelsMissingInventoryIdBeforeSelection.add(selectedContainerType.getPositionString(pos));
                    }
                }
            }
        }

        labelsMissingInventoryId.addAll(labelsMissingInventoryIdBeforeSelection);

        return labelsMissingInventoryId;
    }

    public Map<RowColPos, SpecimenCell> getCells() {
        return wells;
    }

    public void onReset() {
        scansCount = 0;
        initCells();
    }

    private void initCells() {
        wells = new HashMap<RowColPos, SpecimenCell>();
    }

    public int getScansCount() {
        return scansCount;
    }

    @SuppressWarnings("nls")
    public void initCellsWithContainer(ContainerWrapper container) {
        wells.clear();
        for (Entry<RowColPos, SpecimenWrapper> entry : container.getSpecimens().entrySet()) {
            RowColPos pos = entry.getKey();
            SpecimenCell cell = new SpecimenCell(
                pos.getRow(),
                pos.getCol(),
                new DecodedWell(container.getLabel(), entry.getValue().getInventoryId()));
            cell.setSpecimen(entry.getValue());
            cell.setStatus(UICellStatus.FILLED);
            cell.setExpectedSpecimen(entry.getValue());
            wells.put(pos, cell);
        }
        try {
            ArrayList<String> ids = new ArrayList<String>();
            if ((container.getProductBarcode() != null)
                && (container.getProductBarcode().length() != 0)) {
                ids = SessionManager.getAppService().doAction(
                    new SpecimenByMicroplateSearchAction(container.getProductBarcode())).getList();
            }
            ContainerTypeWrapper containerType = container.getContainerType();
            if (((containerType != null) && containerType.getIsMicroplate())
                || !ids.isEmpty()) {
                // microplate with specimens
                for (String id : ids) {
                    SpecimenWrapper sw = SpecimenWrapper.getSpecimen(
                        SessionManager.getAppService(), id);
                    RowColPos pos = container.getPositionFromLabelingScheme(
                        InventoryIdUtil.positionPart(id));
                    SpecimenCell cell = new SpecimenCell(
                        pos.getRow(),
                        pos.getCol(),
                        new DecodedWell(container.getLabel(), sw.getInventoryId()));
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

    public void setFakeContainerType(int rows, int cols) {
        this.selectedContainerType = getFakePalletRowsCols(rows, cols);
    }

    public void setContainerType(ContainerType containerType) {
        this.selectedContainerType = containerType;
    }

    public ContainerType getContainerType() {
        return selectedContainerType;
    }

    @SuppressWarnings("nls")
    public static PalletDimensions capacityToPlateDimensions(Capacity capacity) {
        int rows = capacity.getRowCapacity();
        int cols = capacity.getColCapacity();

        for (PalletDimensions dim : PalletDimensions.values()) {
            if ((dim.getRows() == rows) && (dim.getCols() == cols)) {
                return dim;
            }
        }

        throw new IllegalStateException("capacity does not match a valid plate dimension");
    }

    /*
     * This is the default implementation for scan assign and dispatch. For scan link this method is
     * overriden.
     */
    public static Set<PalletDimensions> getValidPlateDimensions(ContainerType containerType) {
        Set<PalletDimensions> dimensions = new HashSet<PalletDimensions>(1);
        dimensions.add(capacityToPlateDimensions(containerType.getCapacity()));
        return dimensions;
    }
}
