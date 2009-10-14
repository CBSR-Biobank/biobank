package edu.ualberta.med.biobank.model;

import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.scanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PalletCell {

    private SampleCellStatus status;

    private String information;

    private String title;

    private boolean selected = false;

    private SampleTypeWrapper type;

    private SampleWrapper sample;

    private ScanCell scanCell;

    private SampleWrapper expectedSample;

    public PalletCell(ScanCell scanCell) {
        this.scanCell = scanCell;
    }

    public static PalletCell[][] convertArray(ScanCell[][] scancells) {
        PalletCell[][] palletScanned = new PalletCell[ScanCell.ROW_MAX][ScanCell.COL_MAX];
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            for (int j = 0; j < ScanCell.COL_MAX; j++) {
                palletScanned[i][j] = new PalletCell(scancells[i][j]);
            }
        }
        return palletScanned;
    }

    public static PalletCell[][] getRandomScanLink() {
        return convertArray(ScanCell.getRandom());
        // ScanCell[][] palletScanned = new ScanCell[8][12];
        // palletScanned[0][0] = new ScanCell(0, 0, "titi");
        // palletScanned[1][3] = new ScanCell(1, 3, "toto");
        // return convertArray(palletScanned);
    }

    public static PalletCell[][] getRandomSamplesAlreadyAssigned(
        WritableApplicationService appService, Integer siteId) throws Exception {
        PalletCell[][] palletScanned = initArray();
        List<SampleWrapper> randomSamples = SampleWrapper
            .getRandomSamplesAlreadyAssigned(appService, siteId);
        if (randomSamples.size() > 0) {
            palletScanned[0][0] = new PalletCell(new ScanCell(0, 0,
                randomSamples.get(0).getInventoryId()));
        }
        if (randomSamples.size() > 1) {
            palletScanned[2][4] = new PalletCell(new ScanCell(2, 4,
                randomSamples.get(1).getInventoryId()));
        }
        return palletScanned;
    }

    public static PalletCell[][] getRandomSamplesNotAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        PalletCell[][] palletScanned = initArray();
        List<SampleWrapper> randomSamples = SampleWrapper
            .getRandomSamplesNotAssigned(appService, siteId);
        if (randomSamples.size() > 1) {
            // Random r = new Random();
            // int sample1 = r.nextInt(samples.size());
            // int sample2 = r.nextInt(samples.size());
            palletScanned[0][0] = new PalletCell(new ScanCell(0, 0,
                randomSamples.get(0).getInventoryId()));
            // palletScanned[2][4] = new PalletCell(new ScanCell(2, 4, samples
            // .get(1).getInventoryId()));
        }
        return palletScanned;
    }

    private static PalletCell[][] initArray() {
        PalletCell[][] palletScanned = new PalletCell[ScanCell.ROW_MAX][ScanCell.COL_MAX];
        for (int indexRow = 0; indexRow < ScanCell.ROW_MAX; indexRow++) {
            for (int indexCol = 0; indexCol < ScanCell.COL_MAX; indexCol++) {
                palletScanned[indexRow][indexCol] = new PalletCell(
                    new ScanCell(indexRow, indexCol, null));
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
            return type.getNameShort();
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public SampleTypeWrapper getType() {
        return type;
    }

    public void setType(SampleTypeWrapper type) {
        this.type = type;
    }

    public void setSample(SampleWrapper sample) {
        this.sample = sample;
    }

    public SampleWrapper getSample() {
        return sample;
    }

    public String getValue() {
        if (scanCell != null) {
            return scanCell.getValue();
        }
        return null;
    }

    public Integer getRow() {
        if (scanCell != null) {
            return scanCell.getRow();
        }
        return null;
    }

    public Integer getCol() {
        if (scanCell != null) {
            return scanCell.getColumn();
        }
        return null;
    }

    public static boolean hasValue(PalletCell cell) {
        return cell != null && cell.getValue() != null;
    }

    public static PalletCell[][] getEmptyCells() {
        return new PalletCell[ScanCell.ROW_MAX][ScanCell.COL_MAX];
    }

    public void setExpectedSample(SampleWrapper expectedSample) {
        this.expectedSample = expectedSample;
    }

    public SampleWrapper getExpectedSample() {
        return expectedSample;
    }
}
