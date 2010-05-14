package edu.ualberta.med.biobank.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.debug.DebugUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.scannerconfig.scanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PalletCell extends Cell {

    private AliquotCellStatus status;

    private String information;

    private String title;

    private SampleTypeWrapper type;

    private AliquotWrapper aliquot;

    private ScanCell scanCell;

    private AliquotWrapper expectedAliquot;

    public PalletCell(ScanCell scanCell) {
        this.scanCell = scanCell;
    }

    public static Map<RowColPos, PalletCell> convertArray(ScanCell[][] scancells) {
        Map<RowColPos, PalletCell> palletScanned = new TreeMap<RowColPos, PalletCell>();
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            for (int j = 0; j < ScanCell.COL_MAX; j++) {
                ScanCell scanCell = scancells[i][j];
                if (scanCell != null && scanCell.getValue() != null) {
                    palletScanned.put(new RowColPos(i, j), new PalletCell(
                        scanCell));
                }
            }
        }
        return palletScanned;
    }

    public static Map<RowColPos, PalletCell> getRandomScanLink() {
        return convertArray(ScanCell.getRandom());
    }

    public static Map<RowColPos, PalletCell> getRandomScanLinkWithAliquotsAlreadyLinked(
        WritableApplicationService appService, Integer siteId) throws Exception {
        Map<RowColPos, PalletCell> cells = convertArray(ScanCell.getRandom());
        List<AliquotWrapper> aliquots = DebugUtil
            .getRandomAliquotsAlreadyLinked(appService, siteId);
        if (aliquots.size() > 1) {
            int row = 2;
            int col = 3;
            ScanCell scanCell = new ScanCell(row, col, aliquots.get(0)
                .getInventoryId());
            cells.put(new RowColPos(row, col), new PalletCell(scanCell));
            row = 3;
            col = 1;
            scanCell = new ScanCell(row, col, aliquots.get(1).getInventoryId());
            cells.put(new RowColPos(row, col), new PalletCell(scanCell));
        }
        return cells;
    }

    public static Map<RowColPos, PalletCell> getRandomAliquotsAlreadyAssigned(
        WritableApplicationService appService, Integer siteId) throws Exception {
        Map<RowColPos, PalletCell> palletScanned = new HashMap<RowColPos, PalletCell>();
        List<AliquotWrapper> randomAliquots = DebugUtil
            .getRandomAliquotsAlreadyAssigned(appService, siteId);
        if (randomAliquots.size() > 0) {
            palletScanned.put(new RowColPos(0, 0), new PalletCell(new ScanCell(
                0, 0, randomAliquots.get(0).getInventoryId())));
        }
        if (randomAliquots.size() > 1) {
            palletScanned.put(new RowColPos(2, 4), new PalletCell(new ScanCell(
                2, 4, randomAliquots.get(1).getInventoryId())));
        }
        return palletScanned;
    }

    public static Map<RowColPos, PalletCell> getRandomAliquotsNotAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        Map<RowColPos, PalletCell> palletScanned = new HashMap<RowColPos, PalletCell>();
        List<AliquotWrapper> randomAliquots = DebugUtil
            .getRandomAliquotsNotAssigned(appService, siteId);
        // if (randomAliquots.size() > 1) {
        // // Random r = new Random();
        // // int sample1 = r.nextInt(samples.size());
        // // int sample2 = r.nextInt(samples.size());
        // palletScanned.put(new RowColPos(0, 0), new PalletCell(new ScanCell(
        // 0, 0, randomAliquots.get(0).getInventoryId())));
        // // palletScanned[2][4] = new PalletCell(new ScanCell(2, 4, samples
        // // .get(1).getInventoryId()));
        // }
        return palletScanned;
    }

    public AliquotCellStatus getStatus() {
        return status;
    }

    public void setStatus(AliquotCellStatus status) {
        this.status = status;
    }

    public String getTitle() {
        if (type != null) {
            if (type.getNameShort() != null) {
                return type.getNameShort();
            }
            return type.getName();
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public SampleTypeWrapper getType() {
        return type;
    }

    public void setType(SampleTypeWrapper type) {
        this.type = type;
    }

    public void setAliquot(AliquotWrapper aliquot) {
        this.aliquot = aliquot;
    }

    public AliquotWrapper getAliquot() {
        return aliquot;
    }

    public String getValue() {
        if (scanCell != null) {
            return scanCell.getValue();
        }
        return null;
    }

    public void setValue(String value) {
        if (scanCell != null) {
            // scanCell.setValue(value);
        }
    }

    @Override
    public Integer getRow() {
        if (scanCell != null) {
            return scanCell.getRow();
        }
        return null;
    }

    @Override
    public Integer getCol() {
        if (scanCell != null) {
            return scanCell.getColumn();
        }
        return null;
    }

    public static boolean hasValue(PalletCell cell) {
        return cell != null && cell.getValue() != null;
    }

    public void setExpectedAliquot(AliquotWrapper expectedAliquot) {
        this.expectedAliquot = expectedAliquot;
    }

    public AliquotWrapper getExpectedAliquot() {
        return expectedAliquot;
    }

}
