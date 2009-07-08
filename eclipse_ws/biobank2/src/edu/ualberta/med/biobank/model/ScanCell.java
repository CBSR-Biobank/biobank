
package edu.ualberta.med.biobank.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Random;

import org.springframework.util.Assert;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ScanCell {

    public static int ROW_MAX = 8;

    public static int COL_MAX = 12;

    /**
     * 1 <= row <=8
     */
    private int row;

    /**
     * 1<= column <= 12
     */
    private int column;

    /**
     * 10 digits
     */
    private String value;

    private SampleCellStatus status;

    private String information;

    private String title;

    private boolean selected = false;

    private SampleType type;

    private Sample sample;

    public ScanCell(int row, int column, String value) {
        this.row = row;
        this.column = column;
        this.value = value;
    }

    public static ScanCell [][] getScanLibResults() {
        ScanCell [][] paletteScanned = new ScanCell [ROW_MAX] [COL_MAX];

        try {
            BufferedReader in = new BufferedReader(
                new FileReader("scanlib.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                if (str.charAt(0) == '#') continue;
                String [] fields = str.split(",");
                Assert.isTrue(fields.length == 4);
                int row = (int) (fields[1].charAt(0) - 'A');
                int col = Integer.parseInt(fields[2]) - 1;

                paletteScanned[row][col] = new ScanCell(row, col, fields[3]);
            }
            in.close();

            for (int row = 0; row < ROW_MAX; ++row) {
                for (int col = 0; col < COL_MAX; ++col) {
                    if (paletteScanned[row][col] == null) {
                        paletteScanned[row][col] = new ScanCell(row, col, null);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return paletteScanned;
    }

    public static ScanCell [][] getRandomScanLink() {
        ScanCell [][] paletteScanned = new ScanCell [ROW_MAX] [COL_MAX];
        Random random = new Random();
        for (int indexRow = 0; indexRow < ROW_MAX; indexRow++) {
            if (indexRow % 2 == 0) {
                for (int indexCol = 0; indexCol < COL_MAX; indexCol++) {
                    StringBuffer digits = new StringBuffer();
                    for (int i = 0; i < 10; i++) {
                        digits.append(random.nextInt(10));
                    }
                    paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
                        indexCol, digits.toString());
                }
            }
        }
        return paletteScanned;
    }

    @SuppressWarnings("unused")
    public static ScanCell [][] getRandomScanProcess() {
        ScanCell [][] paletteScanned = new ScanCell [ROW_MAX] [COL_MAX];
        Random random = new Random();
        for (int indexRow = 0; indexRow < ROW_MAX; indexRow++) {
            for (int indexCol = 0; indexCol < COL_MAX; indexCol++) {
                // if (indexRow == 0 && indexCol == 0) {
                // paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
                // indexCol, "9925338946"); // sample existing - already
                // // linked
                // } else if (indexRow == 1 && indexCol == 0) {
                // paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
                // indexCol, "3533775882"); // sample from another patient,
                // // same study
                // }
                // else if (indexRow == 2 && indexCol == 0) {
                // paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
                // indexCol, "7901081731"); // sample from another patient,
                // // another study
                // }
                if (indexRow == 0 && indexCol == 0) {
                    paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
                        indexCol, "123");
                }
                else {
                    paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
                        indexCol, null);
                }
            }
        }
        return paletteScanned;
    }

    public static ScanCell [][] getRandomScanProcessAlreadyInPalette(
        WritableApplicationService appService) {
        // FIXME check uml pour positionSample/sample comme pour
        // container/containerposition
        ScanCell [][] paletteScanned = initArray();
        List<Sample> samples;
        try {
            samples = appService.search(Sample.class, new Sample());
            for (Sample sample : samples) {
                if (sample.getSamplePosition() != null
                    && sample.getSamplePosition().getStorageContainer() != null) {
                    paletteScanned[0][0] = new ScanCell(0, 0,
                        sample.getInventoryId());
                    break;
                }
            }
        }
        catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paletteScanned;
    }

    public static ScanCell [][] getRandomScanProcessNotInPalette(
        WritableApplicationService appService) {
        // FIXME check uml pour positionSample/sample comme pour
        // container/containerposition
        ScanCell [][] paletteScanned = initArray();
        List<Sample> samples;
        try {
            samples = appService.search(Sample.class, new Sample());
            for (Sample sample : samples) {
                if ((sample.getSamplePosition() == null || sample.getSamplePosition().getStorageContainer() == null)
                    && !sample.getInventoryId().equals("123")) {
                    paletteScanned[0][0] = new ScanCell(0, 0,
                        sample.getInventoryId());
                    break;
                }
            }
        }
        catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paletteScanned;
    }

    private static ScanCell [][] initArray() {
        ScanCell [][] paletteScanned = new ScanCell [ROW_MAX] [COL_MAX];
        for (int indexRow = 0; indexRow < ROW_MAX; indexRow++) {
            for (int indexCol = 0; indexCol < COL_MAX; indexCol++) {
                paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
                    indexCol, null);
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

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getValue() {
        return value;
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
}
