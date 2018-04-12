package edu.ualberta.med.scannerconfig;

import java.io.File;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLib;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLibResult;
import edu.ualberta.med.scannerconfig.preferences.PreferenceConstants;

public class FlatbedImageScan {
    public static final int PLATE_IMAGE_DPI = 300;

    @SuppressWarnings("nls")
    public static final String FAKE_PALLET_IMAGE_FILE = "fakePlatesImage.bmp";

    @SuppressWarnings("nls")
    public static final String PALLET_IMAGE_FILE = "platesImage.bmp";

    protected ListenerList listenerList = new ListenerList();

    private static final I18n i18n = I18nFactory.getI18n(FlatbedImageScan.class);
    private BarcodeImage scannedImage;

    // used for debugging in Linux
    private final boolean haveFakeFlatbedImage;

    public FlatbedImageScan() {
        // When debug is on, use a fake image if the file exits
        if (ScannerConfigPlugin.getDefault().isDebugging()) {
            File platesFile = new File(FlatbedImageScan.FAKE_PALLET_IMAGE_FILE);
            haveFakeFlatbedImage = platesFile.exists();
        } else {
            haveFakeFlatbedImage = false;
        }
        cleanAll();
    }

    public void cleanAll() {
        File platesFile = new File(FlatbedImageScan.PALLET_IMAGE_FILE);
        if (platesFile.exists()) {
            platesFile.delete();
        }

        if (scannedImage != null) {
            scannedImage.dispose();
            scannedImage = null;
        }
    }

    /**
     * Scans an image using the values stored in the preference store.
     */
    public void scan() {
        final int brightness = ScannerConfigPlugin.getDefault().getPreferenceStore()
            .getInt(PreferenceConstants.SCANNER_BRIGHTNESS);
        final int contrast = ScannerConfigPlugin.getDefault().getPreferenceStore()
            .getInt(PreferenceConstants.SCANNER_CONTRAST);
        final int debugLevel = ScannerConfigPlugin.getDefault().getPreferenceStore()
            .getInt(PreferenceConstants.DLL_DEBUG_LEVEL);
        scan(brightness, contrast, debugLevel);
    }

    /**
     * Scans the whole flatbed region.
     * 
     * On Linux, the image stored in the file "plates.bmp" is returned if it exists. This is used
     * for debugging.
     * 
     * @param brightness The brightness value to be used.
     * @param contrast The contrast value to be used.
     * @param debugLevel A non zero value generates debug information. The larger the value the more
     *            details are generated in the debug log. 9 is the maximum value allowed.
     */
    @SuppressWarnings("nls")
    public void scan(final int brightness, final int contrast, final int debugLevel) {
        if (!haveFakeFlatbedImage
            && ScannerConfigPlugin.getDefault().getPreferenceStore()
                .getString(PreferenceConstants.SCANNER_DRV_TYPE)
                .equals(PreferenceConstants.SCANNER_DRV_TYPE_NONE)) {
            BgcPlugin.openAsyncError(
                i18n.tr("Scanner Driver Not Selected"),
                i18n.tr("Please select and configure the scanner in preferences"));
            return;
        }

        cleanAll();
        notifyListeners(false);

        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                String filename;

                if (haveFakeFlatbedImage) {
                    filename = FAKE_PALLET_IMAGE_FILE;
                } else {
                    final ScanLibResult result = ScanLib.getInstance().scanFlatbed(
                        debugLevel,
                        PLATE_IMAGE_DPI,
                        brightness,
                        contrast,
                        FlatbedImageScan.PALLET_IMAGE_FILE);

                    if (result.getResultCode() != ScanLibResult.Result.SUCCESS) {
                        BgcPlugin.openAsyncError(i18n.tr("Scanner error"),
                            result.getMessage());
                        return;
                    }
                    filename = PALLET_IMAGE_FILE;
                }

                scannedImage = new BarcodeImage(filename, null);
                notifyListeners(true);
            }
        });
    }

    public void addScannedImageChangeListener(IScanImageListener listener) {
        listenerList.add(listener);
    }

    public void removeScannedImageChangeListener(IScanImageListener listener) {
        listenerList.remove(listener);
    }

    private void notifyListeners(final boolean haveNewImage) {
        Object[] listeners = listenerList.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final IScanImageListener l = (IScanImageListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    if (haveNewImage) {
                        l.imageAvailable(scannedImage);
                    } else {
                        l.imageDeleted();
                    }
                }
            });
        }
    }
}
