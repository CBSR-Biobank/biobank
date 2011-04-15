package edu.ualberta.med.biobank.widgets.grids;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.cell.AbstractUICell;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileSettings;

/**
 * Specific widget to draw a 8*12 pallet for scan features
 */
public class ScanPalletDisplay extends AbstractGridDisplay {

    public static final int SAMPLE_WIDTH = 50;

    /**
     * Pallets are always 8*12 = fixed size
     */
    public static final int PALLET_WIDTH = SAMPLE_WIDTH * ScanCell.COL_MAX;
    public static final int PALLET_HEIGHT = SAMPLE_WIDTH * ScanCell.ROW_MAX;

    public static final int PALLET_HEIGHT_AND_LEGEND = PALLET_HEIGHT
        + LEGEND_HEIGHT + 4;

    private ProfileSettings loadedProfile;

    public ScanPalletDisplay(final ScanPalletWidget widget) {
        super();
        widget.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseHover(MouseEvent e) {
                PalletCell cell = (PalletCell) getObjectAtCoordinates(widget,
                    e.x, e.y);
                if (cell != null) {
                    String msg = cell.getValue();
                    if (cell.getInformation() != null) {
                        if (msg == null) {
                            msg = "";
                        } else {
                            msg += ": ";
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
        setStorageSize(ScanCell.ROW_MAX, ScanCell.COL_MAX);
    }

    @Override
    public void initLegend(List<UICellStatus> status) {
        super.initLegend(status);
        hasLegend = true;
        legendWidth = PALLET_WIDTH / legendStatus.size();
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
        if (hasLegend) {
            for (int i = 0; i < legendStatus.size(); i++) {
                UICellStatus status = legendStatus.get(i);
                drawLegend(e, status.getColor(), i, status.getLegend());
            }
        }
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
        return "";
    }

    @Override
    protected String getTopTextForBox(
        Map<RowColPos, ? extends AbstractUICell> cells, int indexRow,
        int indexCol) {
        String row = Character.valueOf((char) (indexRow + 'A')).toString();
        String col = Integer.valueOf(indexCol + 1).toString();
        return row + col;
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
        return "";
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

    @Override
    public RowColPos getPositionAtCoordinates(int xPosition, int yPosition) {
        int col = xPosition / getCellWidth();
        int row = yPosition / getCellHeight();
        if (col >= 0 && col < ScanCell.COL_MAX && row >= 0
            && row < ScanCell.ROW_MAX) {
            return new RowColPos(row, col);
        }
        return null;
    }
}
