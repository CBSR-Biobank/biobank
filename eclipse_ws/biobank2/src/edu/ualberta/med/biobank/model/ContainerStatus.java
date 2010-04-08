package edu.ualberta.med.biobank.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import edu.ualberta.med.biobank.BioBankPlugin;

public enum ContainerStatus {
    NOT_INITIALIZED(SWT.COLOR_WHITE, "Not Initialized"), INITIALIZED(
        SWT.COLOR_DARK_GRAY, "Initialized"), FULL(SWT.COLOR_DARK_GRAY, "Full"), FREE_LOCATIONS(
        SWT.COLOR_WHITE, "Free locations");

    private Color color;
    private String legend;

    private ContainerStatus(int color, String legend) {
        this.color = BioBankPlugin.getDefault().getWorkbench().getDisplay()
            .getSystemColor(color);
        this.legend = legend;
    }

    private ContainerStatus(int red, int green, int blue, String legend) {
        this.color = new Color(BioBankPlugin.getDefault().getWorkbench()
            .getDisplay(), red, green, blue);
        this.legend = legend;
    }

    public Color getColor() {
        return color;
    }

    public String getLegend() {
        return legend;
    }
}
