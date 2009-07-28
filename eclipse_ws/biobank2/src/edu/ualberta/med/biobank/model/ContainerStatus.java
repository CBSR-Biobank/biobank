package edu.ualberta.med.biobank.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import edu.ualberta.med.biobank.BioBankPlugin;

public enum ContainerStatus {
    EMPTY(SWT.COLOR_WHITE, "Empty"), FILLED(SWT.COLOR_DARK_GRAY, "Filled"), FULL(
        SWT.COLOR_DARK_GRAY, "Full"), NOT_INITIALIZED(SWT.COLOR_DARK_GREEN,
        "Not Initialized"), FREE_LOCATIONS(SWT.COLOR_WHITE, "Free locations");

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
