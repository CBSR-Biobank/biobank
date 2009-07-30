package edu.ualberta.med.biobank.scannerconfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PlateImageDialog extends Dialog {

    private Shell dialogShell;
    private Canvas canvas;

    public static final String alignFile = "align100.bmp";
    public static final double alignDpi = 100;
    private static final double imgW = 425;
    private static final double imgH = 585;

    private boolean pointTopLeft;

    // pointTopLeft: Used to determine which point the user is currently
    // adjusting.The point is either top-left or bottom-right.

    public PlateImageDialog(Shell parent, int style) {
        super(parent, style);
    }

    public double[] open(final double plate[], final boolean isTwain,
        final Color mycolor) {
        try {
            Shell parent = getParent();

            dialogShell = new Shell(parent, SWT.DIALOG_TRIM
                | SWT.APPLICATION_MODAL);
            dialogShell.setLayout(new FormLayout());
            dialogShell
                .setText("Plate Setup Coordinates     Click the top-left and bottom-right offsets");
            pointTopLeft = true;
            {
                FormData canvasLData = new FormData();
                canvasLData.width = (int) imgW;
                canvasLData.height = (int) imgH;
                canvasLData.top = new FormAttachment(0, 1000, 0);
                canvasLData.left = new FormAttachment(0, 1000, 0);
                canvas = new Canvas(dialogShell, SWT.NONE);
                canvas.setLayoutData(canvasLData);

                canvas.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseDoubleClick(MouseEvent e) {
                    }

                    @Override
                    public void mouseDown(MouseEvent e) {
                        Image img = new Image(Display.getDefault(), alignFile);
                        Rectangle bounds = img.getBounds();
                        if (pointTopLeft) {
                            pointTopLeft = false;
                            double x1 = (e.x / alignDpi / (imgW / bounds.width));
                            double y1 = (e.y / alignDpi / (imgH / bounds.height));
                            plate[0] = x1;
                            plate[1] = y1;

                        } else {
                            double x2 = (e.x / alignDpi / (imgW / bounds.width));
                            double y2 = (e.y / alignDpi / (imgH / bounds.height));
                            if (isTwain) {
                                if (x2 > plate[0] && y2 > plate[0]) {
                                    plate[2] = x2;
                                    plate[3] = y2;
                                    pointTopLeft = true;
                                } else {
                                    pointTopLeft = false;
                                    double x1 = (e.x / alignDpi / (imgW / bounds.width));
                                    double y1 = (e.y / alignDpi / (imgH / bounds.height));
                                    plate[0] = x1;
                                    plate[1] = y1;
                                }
                            } else {// WIA
                                if (x2 - plate[0] > 0 && y2 - plate[1] > 0) {
                                    plate[2] = x2 - plate[0];
                                    plate[3] = y2 - plate[1];
                                    pointTopLeft = true;
                                } else {
                                    pointTopLeft = false;

                                    double x1 = (e.x / alignDpi / (imgW / bounds.width));
                                    double y1 = (e.y / alignDpi / (imgH / bounds.height));
                                    plate[0] = x1;
                                    plate[1] = y1;
                                }
                            }

                        }
                        canvas.redraw();
                        canvas.update();
                    }

                    @Override
                    public void mouseUp(MouseEvent e) {
                    }
                });

                canvas.addPaintListener(new PaintListener() {
                    public void paintControl(PaintEvent e) {
                        Image img = new Image(Display.getDefault(), alignFile);
                        Rectangle bounds = img.getBounds();
                        GC gc = new GC(canvas);
                        gc.drawImage(img, 0, 0, bounds.width, bounds.height, 0,
                            0, (int) imgW, (int) imgH);
                        gc.setForeground(mycolor);
                        double x1 = plate[0] * alignDpi * (imgW / bounds.width);
                        double y1 = plate[1] * alignDpi
                            * (imgH / bounds.height);
                        double x2 = plate[2] * alignDpi * (imgW / bounds.width);
                        double y2 = plate[3] * alignDpi
                            * (imgH / bounds.height);

                        if (isTwain) {
                            gc.drawRectangle((int) x1, (int) y1,
                                (int) (x2 - x1), (int) (y2 - y1));
                        } else {// WIA x,y,w,h
                            gc.drawRectangle((int) x1, (int) y1, (int) x2,
                                (int) y2);
                        }

                        gc.dispose();
                        img.dispose();
                    }
                });

            }
            canvas.update();
            dialogShell.layout();
            dialogShell.pack();
            dialogShell.setLocation(getParent().toDisplay(0, 0));
            dialogShell.open();
            Display display = dialogShell.getDisplay();
            while (!dialogShell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plate;
    }
}
