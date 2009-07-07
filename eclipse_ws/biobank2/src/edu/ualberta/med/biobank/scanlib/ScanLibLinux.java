
package edu.ualberta.med.biobank.scanlib;

public class ScanLibLinux extends ScanLib {

    @Override
    public int slCalibrateToPlate(long dpi, long plateNum) {
        // TODO Auto-generated method stub
        return -1;
    }

    @Override
    public int slConfigPlateFrame(long plateNum, double left, double top,
        double right, double bottom) {
        return -1;
    }

    @Override
    public int slConfigScannerBrightness(int brightness) {
        return -1;
    }

    @Override
    public int slConfigScannerContrast(int contrast) {
        return -1;
    }

    @Override
    public int slDecodeImage(long plateNum, String filename) {
        return -1;
    }

    @Override
    public int slDecodePlate(long dpi, long plateNum) {
        return -1;
    }

    @Override
    public int slIsTwainAvailable() {
        return -1;
    }

    @Override
    public int slScanImage(long dpi, double left, double top, double right,
        double bottom, String filename) {
        return -1;
    }

    @Override
    public int slScanPlate(long dpi, long plateNum, String filename) {
        return -1;
    }

    @Override
    public int slSelectSourceAsDefault() {
        return -1;
    }

}
