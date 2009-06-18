package edu.ualberta.med.biobank.model;

import java.util.Random;

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

	public static ScanCell[][] getRandomScanLink() {
		ScanCell[][] paletteScanned = new ScanCell[ROW_MAX][COL_MAX];
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

	public static ScanCell[][] getRandomScanProcess() {
		ScanCell[][] paletteScanned = new ScanCell[ROW_MAX][COL_MAX];
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
						indexCol, "6982157916");
				} else {
					paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
						indexCol, null);
				}
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
