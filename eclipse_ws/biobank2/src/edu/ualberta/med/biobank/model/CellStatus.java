package edu.ualberta.med.biobank.model;

import org.eclipse.swt.SWT;

public enum CellStatus {
	EMPTY(SWT.COLOR_WHITE, "Empty"), FILLED(SWT.COLOR_DARK_GRAY, "Filled"), NEW(
			SWT.COLOR_DARK_GREEN, "New"), MISSING(SWT.COLOR_CYAN, "Missing"), ERROR(
			SWT.COLOR_YELLOW, "Error"), NO_TYPE(SWT.COLOR_DARK_GREEN, "No type"), TYPE(
			SWT.COLOR_DARK_GRAY, "Type");

	private int color;
	private String legend;

	private CellStatus(int color, String legend) {
		this.color = color;
		this.legend = legend;
	}

	public int getColor() {
		return color;
	}

	public String getLegend() {
		return legend;
	}
}
