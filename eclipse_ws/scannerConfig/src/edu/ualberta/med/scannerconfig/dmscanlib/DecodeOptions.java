package edu.ualberta.med.scannerconfig.dmscanlib;

/**
 * Used to configure the DataMatrix 2D barcode decoder.
 * 
 * @author Nelson Loyola
 * 
 */
public class DecodeOptions {
    public static final double DEFAULT_MIN_EDGE_FACTOR = 0.15;

    public static final double DEFAULT_MAX_EDGE_FACTOR = 0.3;

    /**
     * The default value for the scan gap factor.
     */
    public static final double DEFAULT_SCAN_GAP_FACTOR = 0.1;

    /**
     * The default value for square deviation.
     */
    public static final long DEFAULT_SQUARE_DEV = 10;

    /**
     * The default value for edge threshold.
     */
    public static final long DEFAULT_EDGE_THRESH = 5;

    /**
     * The default value for the number of corrections.
     */
    public static final long DEFAULT_CORRECTIONS = 10;

    /**
     * The default value for shrink. Do not use any other values unless you know what you are doing.
     */
    public static final long DEFAULT_SHRINK = 1;

    private final double minEdgeFactor;
    private final double maxEdgeFactor;
    private final double scanGapFactor;
    private final long squareDev;
    private final long edgeThresh;
    private final long corrections;
    private final long shrink;

    /**
     * Used to configure the DataMatrix 2D barcode decoder.
     * 
     * @param scanGapFactor The number of pixels to use for scan grid gap. This is a libdmtx
     *            parameter. See {@link DEFAULT_SCAN_GAP}.
     * @param squareDev Maximum deviation (degrees) from squareness between adjacent barcode sides.
     *            Default value is N=40, but N=10 is recommended for flat applications like faxes
     *            and other scanned documents. Barcode regions found with corners <(90-N) or >(90+N)
     *            will be ignored by the decoder. See {@link DEFAULT_SQUARE_DEV}.
     * @param edgeThresh Set the minimum edge threshold as a percentage of maximum. For example, an
     *            edge between a pure white and pure black pixel would have an intensity of 100.
     *            Edges with intensities below the indicated threshold will be ignored by the
     *            decoding process. Lowering the threshold will increase the amount of work to be
     *            done, but may be necessary for low contrast or blurry images. See
     *            {@link DEFAULT_EDGE_THRESH}.
     * @param corrections The number of corrections to make while decoding a single region. See
     *            {@link DEFAULT_CORRECTIONS}.
     * @param shrink Internally shrink image by factor of N. Shrinking is accomplished by skipping
     *            N-1 pixels at a time, often producing significantly faster scan times. It also
     *            improves scan success rate for images taken with poor camera focus provided the
     *            image is sufficiently large. See {@link DEFAULT_SHRINK}.
     */
    public DecodeOptions(
        double minEdgeFactor,
        double maxEdgeFactor,
        double scanGapFactor,
        long squareDev,
        long edgeThresh,
        long corrections,
        long shrink) {
        this.minEdgeFactor = minEdgeFactor;
        this.maxEdgeFactor = maxEdgeFactor;
        this.scanGapFactor = scanGapFactor;
        this.squareDev = squareDev;
        this.edgeThresh = edgeThresh;
        this.corrections = corrections;
        this.shrink = shrink;
    }

    public double getMinEdgeFactor() {
        return minEdgeFactor;
    }

    public double getMaxEdgeFactor() {
        return maxEdgeFactor;
    }

    public double getScanGapFactor() {
        return scanGapFactor;
    }

    public long getSquareDev() {
        return squareDev;
    }

    public long getEdgeThresh() {
        return edgeThresh;
    }

    public long getCorrections() {
        return corrections;
    }

    public long getShrink() {
        return shrink;
    }

    /**
     * Factory method with default settings.
     * 
     * @return Returns a DecodeOptions object with default values.
     */
    public static DecodeOptions getDefaultDecodeOptions() {
        return new DecodeOptions(
            DecodeOptions.DEFAULT_MIN_EDGE_FACTOR,
            DecodeOptions.DEFAULT_MAX_EDGE_FACTOR,
            DecodeOptions.DEFAULT_SCAN_GAP_FACTOR,
            DecodeOptions.DEFAULT_SQUARE_DEV,
            DecodeOptions.DEFAULT_EDGE_THRESH,
            DecodeOptions.DEFAULT_CORRECTIONS,
            DecodeOptions.DEFAULT_SHRINK);
    }
}
