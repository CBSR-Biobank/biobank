package edu.ualberta.med.biobank.widgets.grids;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.cell.AbstractUICell;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileSettings;

/**
 * Specific widget to draw a 8*12 pallet for scan features
 */
public class ScanPalletDisplay extends AbstractGridDisplay {

    public static final int SAMPLE_WIDTH = 50;

    public final int rows;
    public final int cols;
    public final int palletWidth;
    public final int palletHeight;
    public final int palletHeightAndLegend;

    private ProfileSettings loadedProfile;

    public ScanPalletDisplay(final ScanPalletWidget widget, int rows, int cols) {
        super();

        this.rows = rows;
        this.cols = cols;
        palletHeight = SAMPLE_WIDTH * rows;
        palletWidth = SAMPLE_WIDTH * cols;
        palletHeightAndLegend = palletHeight + LEGEND_HEIGHT + 4;

        widget.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseHover(MouseEvent e) {
                PalletCell cell = (PalletCell) getObjectAtCoordinates(widget,
                    e.x, e.y);
                if (cell != null) {
                    String msg = cell.getValue();
                    if (cell.getInformation() != null) {
                        if (msg == null) {
                            msg = StringUtil.EMPTY_STRING;
                        } else {
                            msg += ": "; //$NON-NLS-1$
                        }
                        msg += cell.getInformation();
                    }
                    widget.setToolTipText(msg);
                } else {
                    widget.setToolTipText(null);
                }
            }
        });
        setCellWidth(SAMPLE_WIDTH);
        setCellHeight(SAMPLE_WIDTH);
        setDefaultStorageSize();
    }

    public void setDefaultStorageSize() {
        setStorageSize(rows, cols);
    }

    protected void setProfile(ProfileSettings profile) {
        this.loadedProfile = profile;
    }

    @Override
    protected void paintGrid(PaintEvent e, ContainerDisplayWidget displayWidget) {
        FontData fd = e.gc.getFont().getFontData()[0];
        FontData fd2 = new FontData(fd.getName(), 8, fd.getStyle());
        e.gc.setFont(new Font(e.display, fd2));
        super.paintGrid(e, displayWidget);
    }

    @Override
    protected Color getDefaultBackgroundColor(PaintEvent e,
        ContainerDisplayWidget displayWidget, Rectangle rectangle,
        int indexRow, int indexCol) {
        if (this.loadedProfile != null) {
            if (this.loadedProfile.get(indexCol + indexRow * 12)) {
                return new Color(e.display, 185, 211, 238);
            }
        }
        return super.getDefaultBackgroundColor(e, displayWidget, rectangle,
            indexRow, indexCol);
    }

    @Override
    protected String getMiddleTextForBox(
        Map<RowColPos, ? extends AbstractUICell> cells, int indexRow,
        int indexCol) {
        if (cells != null) {
            PalletCell cell = (PalletCell) cells.get(new RowColPos(indexRow,
                indexCol));
            if (cell != null)
                return cell.getTitle();
        }
        return StringUtil.EMPTY_STRING;
    }

    @Override
    protected String getTopTextForBox(
        Map<RowColPos, ? extends AbstractUICell> cells, int indexRow,
        int indexCol) {
        if (containerType == null) {
            String row = Character.valueOf((char) (indexRow + 'A')).toString();
            String col = Integer.valueOf(indexCol + 1).toString();
            return row + col;
        }
        return getDefaultTextForBox(cells, indexRow, indexCol);
    }

    @Override
    protected String getBottomTextForBox(
        Map<RowColPos, ? extends AbstractUICell> cells, int indexRow,
        int indexCol) {
        if (cells != null) {
            PalletCell cell = (PalletCell) cells.get(new RowColPos(indexRow,
                indexCol));
            if (cell != null)
                return cell.getTypeString();
        }
        return StringUtil.EMPTY_STRING;
    }

    @Override
    protected void drawRectangle(PaintEvent e,
        ContainerDisplayWidget displayWidget, Rectangle rectangle,
        int indexRow, int indexCol, Color defaultBackgroundColor) {
        Color backgroundColor = defaultBackgroundColor;
        if (displayWidget.getCells() != null) {
            PalletCell cell = (PalletCell) displayWidget.getCells().get(
                new RowColPos(indexRow, indexCol));
            if (cell != null && cell.getStatus() != null) {
                backgroundColor = cell.getStatus().getColor();
            }
        }
        e.gc.setBackground(backgroundColor);
        e.gc.fillRectangle(rectangle);
        e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
        e.gc.drawRectangle(rectangle);
    }

}
