package edu.ualberta.med.biobank.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import edu.ualberta.med.biobank.BioBankPlugin;

public enum SampleCellStatus {
	EMPTY(SWT.COLOR_WHITE, "Empty"), FILLED(SWT.COLOR_DARK_GRAY, "Filled"), NEW(
			SWT.COLOR_DARK_GREEN, "New"), MISSING(SWT.COLOR_CYAN, "Missing"), ERROR(
			SWT.COLOR_YELLOW, "Error"), NO_TYPE(SWT.COLOR_DARK_GREEN, "No type"), TYPE(
			SWT.COLOR_DARK_GRAY, "Type");

	private Color color;
	private String legend;

	private SampleCellStatus(int color, String legend) {
		this.color = BioBankPlugin.getDefault().getWorkbench().getDisplay()
				.getSystemColor(color);
		this.legend = legend;
	}

	public Color getColor() {
		return color;
	}

	public String getLegend() {
		return legend;
	}
}
