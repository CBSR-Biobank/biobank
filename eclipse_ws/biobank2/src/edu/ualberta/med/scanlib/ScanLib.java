
package edu.ualberta.med.scanlib;

public abstract class ScanLib {

    public static final int DPI_300 = 300;

    public static final int DPI_400 = 400;

    public static final int DPI_600 = 600;

    public abstract int slIsTwainAvailable();

    public abstract int slSelectSourceAsDefault();

    public abstract int slConfigScannerBrightness(int brightness);

    public abstract int slConfigScannerContrast(int contrast);

    public abstract int slConfigPlateFrame(long plateNum, double left,
        double top, double right, double bottom);

    public abstract int slScanImage(long dpi, double left, double top,
        double right, double bottom, String filename);

    public abstract int slScanPlate(long dpi, long plateNum, String filename);

    public abstract int slCalibrateToPlate(long dpi, long plateNum);

    public abstract int slDecodePlate(long dpi, long plateNum);

    public abstract int slDecodeImage(long plateNum, String filename);

}
