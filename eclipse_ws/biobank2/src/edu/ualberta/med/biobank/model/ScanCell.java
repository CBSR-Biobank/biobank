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
	
	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public String getValue() {
		return value;
	}

	/**
	 * 10 digits
	 */
	private String value;
	
	private CellStatus status;
	
	public ScanCell(int row, int column, String value) {
		this.row = row;
		this.column = column;
		this.value = value;
	}

	public static ScanCell[][] getRandomScan() {
		ScanCell[][] paletteScanned = new ScanCell[ROW_MAX][COL_MAX];
		Random random = new Random();
        for (int indexRow = 0; indexRow < ROW_MAX; indexRow++) {
        	for (int indexCol = 0; indexCol < COL_MAX; indexCol++) {
        		StringBuffer digits = new StringBuffer();
        		int test = random.nextInt(5);
        		if (test > 1) { 
        			for (int i = 0; i < 10; i++) {
        				digits.append(random.nextInt(10));
        			}
        			paletteScanned[indexRow][indexCol] = new ScanCell(indexRow, indexCol, digits.toString());
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
}
