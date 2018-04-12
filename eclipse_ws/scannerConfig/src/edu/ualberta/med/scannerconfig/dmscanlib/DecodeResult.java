package edu.ualberta.med.scannerconfig.dmscanlib;

import java.util.Set;
import java.util.TreeSet;

/**
 * Contains the results of an attempt to scan and decode an image with the flatbed scanner.
 * 
 * The method {@link #getDecodedWells()} returns the set of wells that were successfully decoded. If
 * an image was not scanned successfully then this set is empty.
 * 
 * @author Nelson Loyola
 *
 */
public class DecodeResult extends ScanLibResult {

    private final Set<DecodedWell> wells = new TreeSet<DecodedWell>();

    public DecodeResult(int resultCode, int value, String message) {
        super(resultCode, value, message);
    }

    public void addWell(String label, String message) {
        wells.add(new DecodedWell(label, message));
    }

    /**
     * Gets the set of wells that were successfully decoded.
     * 
     * @return the set of wells that were successfully decoded.
     */
    public Set<DecodedWell> getDecodedWells() {
        return wells;
    }
}
