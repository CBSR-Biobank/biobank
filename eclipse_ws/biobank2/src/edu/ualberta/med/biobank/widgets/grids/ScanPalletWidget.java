package edu.ualberta.med.biobank.widgets.grids;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.model.AliquotCellStatus;
import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.scannerconfig.scanlib.ScanCell;

/**
 * Specific widget to draw a 8*12 pallet for scan features
 */
public class ScanPalletWidget extends AbstractGridWidget {

    public static final int SAMPLE_WIDTH = 50;

    /**
     * Pallets are always 8*12 = fixed size
     */
    public static final int PALLET_WIDTH = SAMPLE_WIDTH * ScanCell.COL_MAX;
    public static final int PALLET_HEIGHT = SAMPLE_WIDTH * ScanCell.ROW_MAX;

    protected List<AliquotCellStatus> statusAvailable;

    public static final int PALLET_HEIGHT_AND_LEGEND = PALLET_HEIGHT
        + LEGEND_HEIGHT + 4;

    public ScanPalletWidget(Composite parent) {
        this(parent, true);
    }

    public ScanPalletWidget(Composite parent, boolean hasLegend) {
        super(parent);
        addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseHover(MouseEvent e) {
                PalletCell cell = (PalletCell) getObjectAtCoordinates(e.x, e.y);
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
                    setToolTipText(msg);
                } else {
                    setToolTipText(null);
                }
            }
        });
        setCellWidth(SAMPLE_WIDTH);
        setCellHeight(SAMPLE_WIDTH);
        setStorageSize(ScanCell.ROW_MAX, ScanCell.COL_MAX);
        if (hasLegend) {
            initLegend();
        }
    }

    @Override
    public void initLegend() {
        hasLegend = true;
        statusAvailable = new ArrayList<AliquotCellStatus>();
        statusAvailable.add(AliquotCellStatus.EMPTY);
        statusAvailable.add(AliquotCellStatus.NEW);
        statusAvailable.add(AliquotCellStatus.MOVED);
        statusAvailable.add(AliquotCellStatus.FILLED);
        statusAvailable.add(AliquotCellStatus.MISSING);
        statusAvailable.add(AliquotCellStatus.ERROR);
        legendWidth = PALLET_WIDTH / statusAvailable.size();
    }

    @Override
    protected void paintGrid(PaintEvent e) {
        FontData fd = e.gc.getFont().getFontData()[0];
        FontData fd2 = new FontData(fd.getName(), 8, fd.getStyle());
        e.gc.setFont(new Font(e.display, fd2));
        super.paintGrid(e);
        if (hasLegend) {
            for (int i = 0; i < statusAvailable.size(); i++) {
                AliquotCellStatus status = statusAvailable.get(i);
                drawLegend(e, status.getColor(), i, status.getLegend());
            }
        }
    }

    @Override
    protected String getMiddleTextForBox(int indexRow, int indexCol) {
        if (cells != null) {
            PalletCell cell = (PalletCell) cells.get(new RowColPos(indexRow,
                indexCol));
            if (cell != null) {
                String title = cell.getTitle();
                if (title != null) {
                    return title;
                }
            }
        }
        return "";
    }

    @Override
    protected String getTopTextForBox(int indexRow, int indexCol) {
        String row = new Character((char) (indexRow + 'A')).toString();
        String col = new Integer(indexCol + 1).toString();
        return row + col;
    }

    @Override
    protected String getBottomTextForBox(int indexRow, int indexCol) {
        if (cells != null) {
            PalletCell cell = (PalletCell) cells.get(new RowColPos(indexRow,
                indexCol));
            if (cell != null) {
                AliquotWrapper aliquot = cell.getAliquot();
                if (aliquot != null) {
                    return aliquot.getSampleType().getNameShort();
                }
            }
        }
        return "";
    }

    @Override
    protected void drawRectangle(PaintEvent e, Rectangle rectangle,
        int indexRow, int indexCol) {
        if (cells != null) {
            PalletCell cell = (PalletCell) cells.get(new RowColPos(indexRow,
                indexCol));
            if (cell != null && cell.getStatus() != null) {
                Color color = cell.getStatus().getColor();
                e.gc.setBackground(color);
                e.gc.fillRectangle(rectangle);
            }
        }
        e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
        e.gc.drawRectangle(rectangle);

    }

    @Override
    public Cell getObjectAtCoordinates(int xPosition, int yPosition) {
        if (cells == null) {
            return null;
        }
        RowColPos rcp = getPositionAtCoordinates(xPosition, yPosition);
        if (rcp != null) {
            return cells.get(rcp);
        }
        return null;
    }

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
