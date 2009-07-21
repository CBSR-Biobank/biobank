package edu.ualberta.med.biobank.model;

import java.util.List;
import java.util.Random;

import edu.ualberta.med.scanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PaletteCell {

    private SampleCellStatus status;

    private String information;

    private String title;

    private boolean selected = false;

    private SampleType type;

    private Sample sample;

    private ScanCell scanCell;

    public PaletteCell(ScanCell scanCell) {
        this.scanCell = scanCell;
    }

    public static PaletteCell[][] getScanLibResults() throws Exception {
        return convertArray(ScanCell.getScanLibResults());
    }

    public static PaletteCell[][] convertArray(ScanCell[][] scancells) {
        PaletteCell[][] paletteScanned = new PaletteCell[ScanCell.ROW_MAX][ScanCell.COL_MAX];
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            for (int j = 0; j < ScanCell.COL_MAX; j++) {
                paletteScanned[i][j] = new PaletteCell(scancells[i][j]);
            }
        }
        return paletteScanned;
    }

    public static PaletteCell[][] getRandomScanLink() {
        return convertArray(ScanCell.getRandom());
    }

    @SuppressWarnings("unused")
    public static PaletteCell[][] getRandomScanProcess() {
        PaletteCell[][] paletteScanned = new PaletteCell[ScanCell.ROW_MAX][ScanCell.COL_MAX];
        Random random = new Random();
        for (int indexRow = 0; indexRow < ScanCell.ROW_MAX; indexRow++) {
            for (int indexCol = 0; indexCol < ScanCell.COL_MAX; indexCol++) {
                if (indexRow == 0 && indexCol == 0) {
                    paletteScanned[indexRow][indexCol] = new PaletteCell(
                        new ScanCell(indexRow, indexCol, "123"));
                } else {
                    paletteScanned[indexRow][indexCol] = new PaletteCell(
                        new ScanCell(indexRow, indexCol, null));
                }
            }
        }
        return paletteScanned;
    }

    public static PaletteCell[][] getRandomScanProcessAlreadyInPalette(
        WritableApplicationService appService) {
        // FIXME check uml pour positionSample/sample comme pour
        // container/containerposition
        PaletteCell[][] paletteScanned = initArray();
        List<Sample> samples;
        try {
            samples = appService.search(Sample.class, new Sample());
            for (Sample sample : samples) {
                if (sample.getSamplePosition() != null
                    && sample.getSamplePosition().getContainer() != null) {
                    paletteScanned[0][0] = new PaletteCell(new ScanCell(0, 0,
                        sample.getInventoryId()));
                    break;
                }
            }
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paletteScanned;
    }

    public static PaletteCell[][] getRandomScanProcessNotInPalette(
        WritableApplicationService appService) {
        // FIXME check uml pour positionSample/sample comme pour
        // container/containerposition
        PaletteCell[][] paletteScanned = initArray();
        List<Sample> samples;
        try {
            samples = appService.search(Sample.class, new Sample());
            for (Sample sample : samples) {
                if ((sample.getSamplePosition() == null || sample
                    .getSamplePosition().getContainer() == null)
                    && !sample.getInventoryId().equals("123")) {
                    paletteScanned[0][0] = new PaletteCell(new ScanCell(0, 0,
                        sample.getInventoryId()));
                    break;
                }
            }
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paletteScanned;
    }

    private static PaletteCell[][] initArray() {
        PaletteCell[][] paletteScanned = new PaletteCell[ScanCell.ROW_MAX][ScanCell.COL_MAX];
        for (int indexRow = 0; indexRow < ScanCell.ROW_MAX; indexRow++) {
            for (int indexCol = 0; indexCol < ScanCell.COL_MAX; indexCol++) {
                paletteScanned[indexRow][indexCol] = new PaletteCell(
                    new ScanCell(indexRow, indexCol, null));
            }
        }
        return paletteScanned;
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

    public static boolean hasValue(PaletteCell cell) {
        return cell != null && cell.getValue() != null;
    }
}
