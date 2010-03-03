package edu.ualberta.med.biobank.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.debug.DebugUtil;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.scanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PalletCell extends Cell {

    private SampleCellStatus status;

    private String information;

    private String title;

    private SampleTypeWrapper type;

    private AliquotWrapper sample;

    private ScanCell scanCell;

    private AliquotWrapper expectedSample;

    public PalletCell(ScanCell scanCell) {
        this.scanCell = scanCell;
    }

    public static Map<RowColPos, PalletCell> convertArray(ScanCell[][] scancells) {
        Map<RowColPos, PalletCell> palletScanned = new TreeMap<RowColPos, PalletCell>();
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            for (int j = 0; j < ScanCell.COL_MAX; j++) {
                palletScanned.put(new RowColPos(i, j), new PalletCell(
                    scancells[i][j]));
            }
        }
        return palletScanned;
    }

    public static Map<RowColPos, PalletCell> getRandomScanLink() {
        return convertArray(ScanCell.getRandom());
    }

    public static Map<RowColPos, PalletCell> getRandomScanLinkWithSamplesAlreadyLinked(
        WritableApplicationService appService, Integer siteId) throws Exception {
        Map<RowColPos, PalletCell> cells = convertArray(ScanCell.getRandom());
        List<AliquotWrapper> samples = DebugUtil
            .getRandomSamplesAlreadyLinked(appService, siteId);
        if (samples.size() > 1) {
            int row = 2;
            int col = 3;
            ScanCell scanCell = new ScanCell(row, col, samples.get(0)
                .getInventoryId());
            cells.put(new RowColPos(row, col), new PalletCell(scanCell));
            row = 3;
            col = 1;
            scanCell = new ScanCell(row, col, samples.get(1).getInventoryId());
            cells.put(new RowColPos(row, col), new PalletCell(scanCell));
        }
        return cells;
    }

    public static Map<RowColPos, PalletCell> getRandomSamplesAlreadyAssigned(
        WritableApplicationService appService, Integer siteId) throws Exception {
        Map<RowColPos, PalletCell> palletScanned = initArray();
        List<AliquotWrapper> randomSamples = DebugUtil
            .getRandomSamplesAlreadyAssigned(appService, siteId);
        if (randomSamples.size() > 0) {
            palletScanned.put(new RowColPos(0, 0), new PalletCell(new ScanCell(
                0, 0, randomSamples.get(0).getInventoryId())));
        }
        if (randomSamples.size() > 1) {
            palletScanned.put(new RowColPos(2, 4), new PalletCell(new ScanCell(
                2, 4, randomSamples.get(1).getInventoryId())));
        }
        return palletScanned;
    }

    public static Map<RowColPos, PalletCell> getRandomSamplesNotAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        Map<RowColPos, PalletCell> palletScanned = initArray();
        List<AliquotWrapper> randomSamples = DebugUtil
            .getRandomSamplesNotAssigned(appService, siteId);
        if (randomSamples.size() > 1) {
            // Random r = new Random();
            // int sample1 = r.nextInt(samples.size());
            // int sample2 = r.nextInt(samples.size());
            palletScanned.put(new RowColPos(0, 0), new PalletCell(new ScanCell(
                0, 0, randomSamples.get(0).getInventoryId())));
            // palletScanned[2][4] = new PalletCell(new ScanCell(2, 4, samples
            // .get(1).getInventoryId()));
        }
        return palletScanned;
    }

    private static Map<RowColPos, PalletCell> initArray() {
        Map<RowColPos, PalletCell> palletScanned = new TreeMap<RowColPos, PalletCell>();
        for (int indexRow = 0; indexRow < ScanCell.ROW_MAX; indexRow++) {
            for (int indexCol = 0; indexCol < ScanCell.COL_MAX; indexCol++) {
                palletScanned.put(new RowColPos(indexRow, indexCol),
                    new PalletCell(new ScanCell(indexRow, indexCol, null)));
            }
        }
        return palletScanned;
    }

    public SampleCellStatus getStatus() {
        return status;
    }

    public void setStatus(SampleCellStatus status) {
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

    public void setSample(AliquotWrapper sample) {
        this.sample = sample;
    }

    public AliquotWrapper getSample() {
        return sample;
    }

    public String getValue() {
        if (scanCell != null) {
            return scanCell.getValue();
        }
        return null;
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

    public void setExpectedSample(AliquotWrapper expectedSample) {
        this.expectedSample = expectedSample;
    }

    public AliquotWrapper getExpectedSample() {
        return expectedSample;
    }

}
