package edu.ualberta.med.biobank.model;

import java.util.List;
import java.util.Random;

import edu.ualberta.med.scanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PalletCell {

    private SampleCellStatus status;

    private String information;

    private String title;

    private boolean selected = false;

    private SampleType type;

    private Sample sample;

    private ScanCell scanCell;

    public PalletCell(ScanCell scanCell) {
        this.scanCell = scanCell;
    }

    public static PalletCell[][] getScanLibResults() throws Exception {
        return convertArray(ScanCell.getScanLibResults());
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
    }

    @SuppressWarnings("unused")
    public static PalletCell[][] getRandomScanProcess() {
        PalletCell[][] palletScanned = new PalletCell[ScanCell.ROW_MAX][ScanCell.COL_MAX];
        Random random = new Random();
        for (int indexRow = 0; indexRow < ScanCell.ROW_MAX; indexRow++) {
            for (int indexCol = 0; indexCol < ScanCell.COL_MAX; indexCol++) {
                if (indexRow == 0 && indexCol == 0) {
                    palletScanned[indexRow][indexCol] = new PalletCell(
                        new ScanCell(indexRow, indexCol, "123"));
                } else {
                    palletScanned[indexRow][indexCol] = new PalletCell(
                        new ScanCell(indexRow, indexCol, null));
                }
            }
        }
        return palletScanned;
    }

    public static PalletCell[][] getRandomScanProcessAlreadyInPallet(
        WritableApplicationService appService) {
        // FIXME check uml pour positionSample/sample comme pour
        // container/containerposition
        PalletCell[][] palletScanned = initArray();
        try {
            HQLCriteria criteria = new HQLCriteria("from "
                + Sample.class.getName()
                + " as s where s in (select sp.sample from "
                + SamplePosition.class.getName() + " as sp)");
            List<Sample> samples = appService.query(criteria);
            if (samples.size() > 0) {
                palletScanned[0][0] = new PalletCell(new ScanCell(0, 0,
                    samples.get(0).getInventoryId()));
            }
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return palletScanned;
    }

    public static PalletCell[][] getRandomScanProcessNotInPallet(
        WritableApplicationService appService) {
        // FIXME check uml pour positionSample/sample comme pour
        // container/containerposition
        PalletCell[][] palletScanned = initArray();
        try {
            HQLCriteria criteria = new HQLCriteria("from "
                + Sample.class.getName()
                + " as s where s not in (select sp.sample from "
                + SamplePosition.class.getName() + " as sp) "
                + "and s.inventoryId <> '123'");
            List<Sample> samples = appService.query(criteria);
            if (samples.size() > 1) {
                palletScanned[0][0] = new PalletCell(new ScanCell(0, 0,
                    samples.get(0).getInventoryId()));
                palletScanned[2][4] = new PalletCell(new ScanCell(2, 4,
                    samples.get(1).getInventoryId()));
            }
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    public SampleType getType() {
        return type;
    }

    public void setType(SampleType type) {
        this.type = type;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Sample getSample() {
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

    public Integer getColumn() {
        if (scanCell != null) {
            return scanCell.getColumn();
        }
        return null;
    }

    public static boolean hasValue(PalletCell cell) {
        return cell != null && cell.getValue() != null;
    }
}
