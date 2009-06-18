package edu.ualberta.med.biobank.model;

import org.eclipse.swt.SWT;

public enum ContainerStatus {
	EMPTY(SWT.COLOR_WHITE, "Empty"), FILLED(SWT.COLOR_DARK_GRAY, "Filled"), FREE_POSITIONS(
			SWT.COLOR_DARK_MAGENTA, "Free positions"), NOT_INITIALIZED(
			SWT.COLOR_DARK_YELLOW, "Not initialized");

	private int color;
	private String legend;

	private ContainerStatus(int color, String legend) {
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
