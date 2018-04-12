package edu.ualberta.med.scannerconfig.dmscanlib;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.scannerconfig.BarcodePosition;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.PalletOrientation;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

@SuppressWarnings("nls")
public class TestDmScanLibWindows extends RequiresJniLibraryTest {

    private static Logger log = LoggerFactory
        .getLogger(TestDmScanLibWindows.class);

    @Before
    public void beforeMethod() {
        // these tests are valid only when not running on windows
        Assume.assumeTrue(LibraryLoader.getInstance().runningMsWindows());
    }

    @Test
    public void scanImage() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();
        ScanLibResult r = scanLib.selectSourceAsDefault();
        Assert.assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());

        Rectangle2D.Double scanRegion = new Rectangle2D.Double(1, 1, 2, 3);
        Rectangle2D.Double scanBbox = ScannerConfigPlugin.getWiaBoundingBox(scanRegion);

        final int dpi = 300;
        String filename = "tempscan.png";
        File file = new File(filename);
        file.delete(); // dont care if file doesn't exist

        r = scanLib.scanImage(
            2,
            dpi,
            0,
            0,
            scanBbox.x,
            scanBbox.y,
            scanBbox.width,
            scanBbox.height,
            filename);

        Assert.assertNotNull(r);
        Assert.assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());

        BufferedImage image = ImageIO.read(new File(filename));
        Assert.assertEquals(new Double(scanRegion.getWidth() * dpi).intValue(), image.getWidth());
        Assert.assertEquals(new Double(scanRegion.getHeight() * dpi).intValue(), image.getHeight());
    }

    @Test
    public void scanImageBadParams() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();
        Rectangle2D.Double scanBox = new Rectangle2D.Double(0, 0, 4, 4);

        ScanLibResult r = scanLib.scanImage(
            0, 300, 0, 0, scanBox.x, scanBox.y, scanBox.width, scanBox.height, null);
        Assert.assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());

        r = scanLib.scanImage(
            0, 175, 0, 0, scanBox.x, scanBox.y, scanBox.width, scanBox.height, "tempscan.bmp");
        Assert.assertEquals(ScanLibResult.Result.INVALID_DPI, r.getResultCode());
    }

    @Test
    public void scanFlatbed() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();

        final int dpi = 300;
        String filename = "flatbed.bmp";
        File file = new File(filename);
        file.delete(); // dont care if file doesn't exist

        ScanLibResult r = scanLib.scanFlatbed(0, dpi, 0, 0, filename);

        Assert.assertNotNull(r);
        Assert.assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());
    }

    @Test
    public void scanFlatbedBadParams() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();

        ScanLibResult r = scanLib.scanFlatbed(0, 300, 0, 0, null);
        Assert.assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());

        r = scanLib.scanFlatbed(0, 0, 0, 0, "tempscan.bmp");
        Assert.assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());
    }

    @Test
    public void scanAndDecode() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();

        double x = 0.400;
        double y = 0.265;
        double width = 4.566 - x;
        double height = 3.020 - y;
        Rectangle2D.Double scanRegion = new Rectangle2D.Double(x, y, width, height);
        Rectangle2D.Double scanBbox = ScannerConfigPlugin.getWiaBoundingBox(scanRegion);
        final int dpi = 300;

        Rectangle2D.Double wellsBbox = new Rectangle2D.Double(
            0,
            0,
            Math.floor(dpi * scanRegion.getWidth()),
            Math.floor(dpi * scanRegion.getHeight()));

        Set<CellRectangle> wells = CellRectangle.getCellsForBoundingBox(
            wellsBbox,
            PalletOrientation.LANDSCAPE,
            PalletDimensions.DIM_ROWS_8_COLS_12,
            BarcodePosition.BOTTOM);

        DecodeResult dr = scanLib.scanAndDecode(
            3, dpi,
            0,
            0,
            scanBbox.x,
            scanBbox.y,
            scanBbox.width,
            scanBbox.height,
            DecodeOptions.getDefaultDecodeOptions(),
            wells.toArray(new CellRectangle[] {}));

        Assert.assertNotNull(dr);
        Assert.assertFalse(dr.getDecodedWells().isEmpty());

        for (DecodedWell decodedWell : dr.getDecodedWells()) {
            log.debug("decoded well: {}", decodedWell);
        }

        log.debug("wells decoded: {}", dr.getDecodedWells().size());
    }
}
