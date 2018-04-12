package edu.ualberta.med.scannerconfig;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.framework.BundleContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.Swt2DUtil;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLib;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLibResult;
import edu.ualberta.med.scannerconfig.preferences.PreferenceConstants;
import edu.ualberta.med.scannerconfig.preferences.scanner.ScannerDpi;
import edu.ualberta.med.scannerconfig.sourceproviders.PlateEnabledState;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScannerConfigPlugin extends AbstractUIPlugin {

    @SuppressWarnings("unused")
    private static final I18n i18n = I18nFactory.getI18n(ScannerConfigPlugin.class);

    public static final String IMG_SCANNER = "scanner"; //$NON-NLS-1$

    // The plug-in ID
    public static final String PLUGIN_ID = "scannerConfig"; //$NON-NLS-1$

    @SuppressWarnings("nls")
    private final boolean IS_MS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

    @SuppressWarnings("nls")
    private final boolean IS_LINUX = System.getProperty("os.name").startsWith("Linux");

    @SuppressWarnings("nls")
    private final boolean IS_ARCH_64_BIT = System.getProperty("os.arch").equals("amd64");

    // The shared instance
    private static ScannerConfigPlugin plugin;

    /**
     * The constructor
     */

    @SuppressWarnings("nls")
    public ScannerConfigPlugin() {
        if (IS_MS_WINDOWS) {
            System.loadLibrary("OpenThreadsWin32");
            System.loadLibrary("opencv_core248");
            System.loadLibrary("opencv_highgui248");
            System.loadLibrary("opencv_imgproc248");
            System.loadLibrary("dmscanlib");
        } else if (IS_LINUX && IS_ARCH_64_BIT) {
            System.loadLibrary("dmscanlib64");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    @Override
    @SuppressWarnings("nls")
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().startsWith("scanner.plate.coords.enabled.")) {
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    ISourceProviderService service =
                        (ISourceProviderService) window.getService(ISourceProviderService.class);

                    PlateEnabledState plateEnabledSourceProvider =
                        (PlateEnabledState) service
                            .getSourceProvider(PlateEnabledState.PLATES_ENABLED);
                    Assert.isNotNull(plateEnabledSourceProvider);
                    plateEnabledSourceProvider.setPlateEnabled();
                }
            }
        });
    }

    @SuppressWarnings("nls")
    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        registerImage(registry, IMG_SCANNER, "selectScanner.png");
    }

    @SuppressWarnings("nls")
    private void registerImage(ImageRegistry registry, String key, String fileName) {
        try {
            IPath path = new Path("icons/" + fileName);
            URL url = FileLocator.find(getBundle(), path, null);
            if (url != null) {
                ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                registry.put(key, desc);
            }
        } catch (Exception e) {
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ScannerConfigPlugin getDefault() {
        return plugin;
    }

    public void initialize() {
    }

    public static ScanLibResult.Result scanImage(
        Rectangle2D.Double region,
        String filename,
        ScannerDpi dpi) {
        IPreferenceStore prefs = getDefault().getPreferenceStore();

        int brightness = prefs.getInt(PreferenceConstants.SCANNER_BRIGHTNESS);
        int contrast = prefs.getInt(PreferenceConstants.SCANNER_CONTRAST);
        int debugLevel = prefs.getInt(PreferenceConstants.DLL_DEBUG_LEVEL);

        ScanLibResult res = ScanLib.getInstance().scanImage(
            debugLevel,
            dpi.getValue(),
            brightness,
            contrast,
            region.x,
            region.y,
            region.width,
            region.height,
            filename);

        return res.getResultCode();
    }

    public static ScanLibResult.Result scanPlate(
        ScanPlate scanPlate,
        String filename,
        ScannerDpi dpi) {

        String[] prefsArr = PreferenceConstants.SCANNER_PALLET_CONFIG[scanPlate.getId() - 1];

        IPreferenceStore prefs = getDefault().getPreferenceStore();

        double left = prefs.getFloat(prefsArr[1]);
        double top = prefs.getFloat(prefsArr[2]);
        double right = prefs.getFloat(prefsArr[3]);
        double bottom = prefs.getFloat(prefsArr[4]);

        Rectangle2D.Double region = new Rectangle2D.Double(
            left, top, right - left, bottom - top);

        region = regionModifyIfScannerWia(region);
        return scanImage(region, filename, dpi);
    }

    // bbox here has to start at (0,0)
    public static Rectangle2D.Double getWellsBoundingBox(final Rectangle2D.Double scanBbox) {
        return new Rectangle2D.Double(
            0,
            0,
            scanBbox.width - scanBbox.x,
            scanBbox.height - scanBbox.y);
    }

    /*
     * 
     */
    public static Rectangle2D.Double getWiaBoundingBox(final Rectangle2D.Double scanBbox) {
        return new Rectangle2D.Double(
            scanBbox.x,
            scanBbox.y,
            scanBbox.width - scanBbox.x,
            scanBbox.height - scanBbox.y);
    }

    private static Rectangle2D.Double regionModifyIfScannerWia(Rectangle2D.Double region) {
        if (!ScannerConfigPlugin.getDefault().getPreferenceStore()
            .getString(PreferenceConstants.SCANNER_DRV_TYPE)
            .equals(PreferenceConstants.SCANNER_DRV_TYPE_WIA)) {
            return region;
        }

        return getWiaBoundingBox(region);
    }

    /**
     * Used to get a list of enabled plates.
     * 
     * @return A list of the currently enabled plates in the preferences.
     */
    public Set<ScanPlate> getPlatesEnabled() {
        Set<ScanPlate> platesEnabled = new LinkedHashSet<ScanPlate>();
        for (ScanPlate plate : ScanPlate.values()) {
            if (getPreferenceStore().getBoolean(PreferenceConstants.SCANNER_PALLET_ENABLED[plate.getId() - 1])) {
                platesEnabled.add(plate);
            }
        }
        return platesEnabled;
    }

    /**
     * Used to query if a plate scanning region has been defined in the preferences by the user.
     * 
     * @param plateId A value between 1 and 5. Corresponds to the preferences.
     * 
     * @return True if the user has enabled the plate in the preferences. False otherwise.
     */
    @SuppressWarnings("nls")
    public boolean getPlateEnabled(ScanPlate plateId) {
        if (ScanPlate.size != PreferenceConstants.SCANNER_PALLET_ENABLED.length) {
            throw new IllegalStateException("the size enum ScanPlate does not match the number of preferences");
        }
        return getPreferenceStore().getBoolean(
            PreferenceConstants.SCANNER_PALLET_ENABLED[plateId.getId() - 1]);
    }

    public static int getPlateCount() {
        return ScanPlate.size;
    }

    public static int getPlatesEnabledCount() {
        int count = 0;
        for (ScanPlate plateId : ScanPlate.values()) {
            if (ScannerConfigPlugin.getDefault().getPlateEnabled(plateId)) {
                count++;
            }
        }
        return count;
    }

    public static int getPlatesMax() {
        return getPlateCount();
    }

    public int getBrightness() {
        return getPreferenceStore().getInt(PreferenceConstants.SCANNER_BRIGHTNESS);
    }

    public int getContrast() {
        return getPreferenceStore().getInt(PreferenceConstants.SCANNER_CONTRAST);
    }

    public static Rectangle2D.Double rectangleToInches(final int dpi,
        final Rectangle2D.Double rectangle) {
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(dpi, dpi);
        Rectangle2D.Double rectangleInInches =
            Swt2DUtil.inverseTransformRect(scaleTransform, rectangle);
        return rectangleInInches;
    }

    public static Rectangle2D.Double rectangleToPixels(final int dpi,
        final Rectangle2D.Double rectangle) {
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(dpi, dpi);
        Rectangle2D.Double rectangleInPixels = Swt2DUtil.transformRect(scaleTransform, rectangle);
        return rectangleInPixels;
    }
}
