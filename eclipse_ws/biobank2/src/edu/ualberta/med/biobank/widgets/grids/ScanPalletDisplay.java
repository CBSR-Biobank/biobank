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
import edu.ualberta.med.biobank.util.SbsLabeling;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;

/**
 * Specific widget to draw a pallet for scan features
 */
public class ScanPalletDisplay extends AbstractGridDisplay {

    public static final int SAMPLE_WIDTH = 50;

    /**
     * Pallets are always 8*12 by default = fixed size
     */
    public static final int PALLET_WIDTH = SAMPLE_WIDTH * SbsLabeling.COL_DEFAULT;
    public static final int PALLET_HEIGHT = SAMPLE_WIDTH * SbsLabeling.ROW_DEFAULT;

    public static final int PALLET_HEIGHT_AND_LEGEND = PALLET_HEIGHT
        + LEGEND_HEIGHT + 4;

    public ScanPalletDisplay(final ScanPalletWidget widget, int rows, int cols) {
        super();
        widget.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseHover(MouseEvent e) {
                PalletWell cell = (PalletWell) getObjectAtCoordinates(widget,
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
        setDefaultStorageSize(rows, cols);
    }


    public void setDefaultStorageSize() {
        setStorageSize(SbsLabeling.ROW_DEFAULT, SbsLabeling.COL_DEFAULT);
    }

    public void setDefaultStorageSize(int rows, int cols) {
        setStorageSize(rows, cols);
    }

    @Override
    protected void paintGrid(PaintEvent e, ContainerDisplayWidget displayWidget) {
        FontData fd = e.gc.getFont().getFontData()[0];
        FontData fd2 = new FontData(fd.getName(), 8, fd.getStyle());
        e.gc.setFont(new Font(e.display, fd2));
        super.paintGrid(e, displayWidget);
    }

    @Override
    protected String getMiddleTextForBox(
        Map<RowColPos, ? extends AbstractUIWell> cells, int indexRow,
        int indexCol) {
        if (cells != null) {
            PalletWell cell = (PalletWell) cells.get(new RowColPos(indexRow,
                indexCol));
            if (cell != null)
                return cell.getTitle();
        }
        return StringUtil.EMPTY_STRING;
    }

    @Override
    protected String getTopTextForBox(
        Map<RowColPos, ? extends AbstractUIWell> cells, int indexRow,
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
        Map<RowColPos, ? extends AbstractUIWell> cells, int indexRow,
        int indexCol) {
        if (cells != null) {
            PalletWell cell = (PalletWell) cells.get(new RowColPos(indexRow,
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
            PalletWell cell = (PalletWell) displayWidget.getCells().get(
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
