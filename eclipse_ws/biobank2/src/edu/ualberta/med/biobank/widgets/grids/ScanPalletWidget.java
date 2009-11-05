package edu.ualberta.med.biobank.widgets.grids;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.widgets.listeners.ScanPalletModificationEvent;
import edu.ualberta.med.biobank.widgets.listeners.ScanPalletModificationListener;
import edu.ualberta.med.scanlib.ScanCell;

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

    protected List<SampleCellStatus> statusAvailable;

    public static final int PALLET_HEIGHT_AND_LEGEND = PALLET_HEIGHT
        + LEGEND_HEIGHT + 4;

    protected PalletCell[][] scannedElements;

    List<ScanPalletModificationListener> listeners;

    public ScanPalletWidget(Composite parent) {
        this(parent, true);
    }

    public ScanPalletWidget(Composite parent, boolean hasLegend) {
        super(parent);
        listeners = new ArrayList<ScanPalletModificationListener>();
        addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseHover(MouseEvent e) {
                PalletCell cell = (PalletCell) getObjectAtCoordinates(e.x, e.y);
                if (cell != null) {
                    String msg = cell.getValue();
                    if (cell.getInformation() != null) {
                        msg += " : " + cell.getInformation();
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
        statusAvailable = new ArrayList<SampleCellStatus>();
        statusAvailable.add(SampleCellStatus.EMPTY);
        statusAvailable.add(SampleCellStatus.NEW_MOVED);
        statusAvailable.add(SampleCellStatus.FILLED);
        statusAvailable.add(SampleCellStatus.MISSING);
        statusAvailable.add(SampleCellStatus.ERROR);
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
                SampleCellStatus status = statusAvailable.get(i);
                drawLegend(e, status.getColor(), i, status.getLegend());
            }
        }
    }

    @Override
    protected String getMiddleTextForBox(int indexRow, int indexCol) {
        if (scannedElements != null
            && scannedElements[indexRow][indexCol] != null) {
            String title = scannedElements[indexRow][indexCol].getTitle();
            if (title != null) {
                return title;
            }
        }
        return "-";
    }

    @Override
    protected String getTopTextForBox(int indexRow, int indexCol) {
        return getDefaultTextForBox(indexRow, indexCol);
    }

    @Override
    protected String getBottomTextForBox(int indexRow, int indexCol) {
        if (scannedElements != null
            && scannedElements[indexRow][indexCol] != null) {
            SampleWrapper sample = scannedElements[indexRow][indexCol]
                .getExpectedSample();
            if (sample != null) {
                PatientVisitWrapper pv = sample.getPatientVisit();
                if (pv != null) {
                    String number = pv.getPatient().getNumber();
                    if (number != null) {
                        return number;
                    }
                }
            }
        }
        return "-";
    }

    @Override
    protected void drawRectangle(PaintEvent e, Rectangle rectangle,
        int indexRow, int indexCol) {
        if (scannedElements != null
            && scannedElements[indexRow][indexCol] != null
            && scannedElements[indexRow][indexCol].getStatus() != null) {
            Color color = scannedElements[indexRow][indexCol].getStatus()
                .getColor();
            e.gc.setBackground(color);
            e.gc.fillRectangle(rectangle);
        }
        e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
        e.gc.drawRectangle(rectangle);
    }

    public void setScannedElements(PalletCell[][] elements) {
        this.scannedElements = elements;
        redraw();
    }

    @Override
    public void setInput(Object object) {
        Assert.isNotNull(object);
        Assert.isTrue(object.getClass().isArray());
        PalletCell[][] cells = (PalletCell[][]) object;
        setScannedElements(cells);
    }

    public PalletCell[][] getScannedElements() {
        return scannedElements;
    }

    @Override
    public Object getObjectAtCoordinates(int xPosition, int yPosition) {
        if (scannedElements == null) {
            return null;
        }
        int col = xPosition / getCellWidth();
        int row = yPosition / getCellHeight();
        if (col >= 0 && col < ScanCell.COL_MAX && row >= 0
            && row < ScanCell.ROW_MAX) {
            return scannedElements[row][col];
        }
        return null;
    }

    public void addModificationListener(ScanPalletModificationListener listener) {
        listeners.add(listener);
    }

    public void removeModificationListener(
        ScanPalletModificationListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(ScanPalletModificationEvent event) {
        for (ScanPalletModificationListener listener : listeners) {
            listener.modification(event);
        }
    }

    @Override
    public void setSelection(RowColPos selection) {
    }
}
