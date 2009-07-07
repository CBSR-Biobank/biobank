
package edu.ualberta.med.scanlib;

public class ScanLibWin32 extends ScanLib {

    @Override
    public int slCalibrateToPlate(long dpi, long plateNum) {
        return ScanLibWin32Wrapper.slCalibrateToPlate(dpi, plateNum);
    }

    @Override
    public int slConfigPlateFrame(long plateNum, double left, double top,
        double right, double bottom) {
        return ScanLibWin32Wrapper.slConfigPlateFrame(plateNum, left, top,
            right, bottom);
    }

    @Override
    public int slConfigScannerBrightness(int brightness) {
        return ScanLibWin32Wrapper.slConfigScannerBrightness(brightness);
    }

    @Override
    public int slConfigScannerContrast(int contrast) {
        return ScanLibWin32Wrapper.slConfigScannerContrast(contrast);
    }

    @Override
    public int slDecodeImage(long plateNum, String filename) {
        return ScanLibWin32Wrapper.slDecodeImage(plateNum, filename);
    }

    @Override
    public int slDecodePlate(long dpi, long plateNum) {
        return ScanLibWin32Wrapper.slDecodePlate(dpi, plateNum);
    }

    @Override
    public int slIsTwainAvailable() {
        return ScanLibWin32Wrapper.slIsTwainAvailable();
    }

    @Override
    public int slScanImage(long dpi, double left, double top, double right,
        double bottom, String filename) {
        return ScanLibWin32Wrapper.slScanImage(dpi, left, top, right, bottom,
            filename);
    }

    @Override
    public int slScanPlate(long dpi, long plateNum, String filename) {
        return ScanLibWin32Wrapper.slScanPlate(dpi, plateNum, filename);
    }

    @Override
    public int slSelectSourceAsDefault() {
        return ScanLibWin32Wrapper.slSelectSourceAsDefault();
    }

}
