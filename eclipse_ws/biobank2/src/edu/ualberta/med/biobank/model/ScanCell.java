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

	private CellStatus status;

	private String information;

	private String title;

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
				if (indexRow == 0 && indexCol == 0) {
					paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
						indexCol, "4990741101"); // sample existant - deja lie
				} else if (indexRow == 1 && indexCol == 0) {
					paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
						indexCol, "1853075889"); // sample appartenant a un
					// autre patient mais meme study
				} else if (indexRow == 2 && indexCol == 0) {
					paletteScanned[indexRow][indexCol] = new ScanCell(indexRow,
						indexCol, "8934954760"); // sample appartenant a un
					// autre patientVisit et differente etude
				} else if (indexRow == 7 && (indexCol > 5)) {
					if (indexCol < 10) {
						paletteScanned[indexRow][indexCol] = new ScanCell(
							indexRow, indexCol, ""); // test pb scan
					} else {
						paletteScanned[indexRow][indexCol] = null;
					}
				} else {
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

	public CellStatus getStatus() {
		return status;
	}

	public void setStatus(CellStatus status) {
		this.status = status;
	}

	public String getTitle() {
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
}
