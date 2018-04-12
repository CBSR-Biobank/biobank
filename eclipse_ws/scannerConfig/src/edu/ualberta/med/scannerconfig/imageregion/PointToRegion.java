package edu.ualberta.med.scannerconfig.imageregion;

/**
 * Given a point in 2D space, this enumeration returns the possible regions the point is in.
 * 
 * @author loyola
 * 
 */
public enum PointToRegion {
    OUTSIDE_REGION,
    IN_REGION,
    IN_HANDLE_NORTH_WEST,
    IN_HANDLE_NORTH,
    IN_HANDLE_NORTH_EAST,
    IN_HANDLE_EAST,
    IN_HANDLE_SOUTH_EAST,
    IN_HANDLE_SOUTH,
    IN_HANDLE_SOUTH_WEST,
    IN_HANDLE_WEST;

}
