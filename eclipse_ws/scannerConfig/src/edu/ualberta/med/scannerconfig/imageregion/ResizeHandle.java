package edu.ualberta.med.scannerconfig.imageregion;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A package private enumerated type that defines the possible resize handles for a region.
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
enum ResizeHandle {
    NORTH_WEST(PointToRegion.IN_HANDLE_NORTH_WEST),
    NORTH(PointToRegion.IN_HANDLE_NORTH),
    NORTH_EAST(PointToRegion.IN_HANDLE_NORTH_EAST),
    EAST(PointToRegion.IN_HANDLE_EAST),
    SOUTH_EAST(PointToRegion.IN_HANDLE_SOUTH_EAST),
    SOUTH(PointToRegion.IN_HANDLE_SOUTH),
    SOUTH_WEST(PointToRegion.IN_HANDLE_SOUTH_WEST),
    WEST(PointToRegion.IN_HANDLE_WEST);

    private final PointToRegion pointRegion;

    public static final int size = values().length;

    private static final Map<PointToRegion, ResizeHandle> POINT_REGION_MAP;

    static {
        Map<PointToRegion, ResizeHandle> map = new LinkedHashMap<PointToRegion, ResizeHandle>();

        for (ResizeHandle enumValue : values()) {
            PointToRegion pointRegion = enumValue.getPointRegion();
            ResizeHandle check = map.get(pointRegion);
            if (check != null) {
                throw new IllegalStateException("point region value "
                    + pointRegion + " used multiple times");
            }

            map.put(pointRegion, enumValue);
        }

        POINT_REGION_MAP = Collections.unmodifiableMap(map);
    }

    private ResizeHandle(PointToRegion pointRegion) {
        this.pointRegion = pointRegion;
    }

    PointToRegion getPointRegion() {
        return pointRegion;
    }

    static Map<PointToRegion, ResizeHandle> pointRegionMap() {
        return POINT_REGION_MAP;
    }

    static ResizeHandle getFromPointRegion(PointToRegion pointRegion) {
        ResizeHandle result = pointRegionMap().get(pointRegion);
        if (result == null) {
            throw new IllegalStateException("invalid point region: " + pointRegion);
        }
        return result;
    }

}
