package edu.ualberta.med.scannerconfig.widgets.imageregion;

import java.awt.geom.Rectangle2D;


/**
 * Used by {@link ScanRegionCanvas} to retrieve or assign scan region dimensions.
 * 
 * @author loyola
 * 
 */
public interface IScanRegionWidget {

    /**
     * Called on the parent widget when the user has changed the dimensions of the scan region
     * either by using the mouse or the keyboard.
     * 
     * @param scanRegionInInches the new dimensions in inches.
     */
    public void scanRegionChanged(Rectangle2D.Double scanRegionInInches);

}
