package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * A {@link Center} can have many {@link Location}s, but a {@link Location} may
 * have only one {@link Center}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "CENTER_LOCATION")
@Unique(properties = "location", groups = PrePersist.class)
public class CenterLocation
    implements Serializable {
    private static final long serialVersionUID = 1L;

    private CenterLocationId id;
    private Center center;
    private Location location;

    @EmbeddedId
    public CenterLocationId getId() {
        return id;
    }

    public void setId(CenterLocationId id) {
        this.id = id;
    }

    @MapsId("centerId")
    @NotNull(message = "{CenterLocation.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @MapsId("locationId")
    @NotNull(message = "{CenterLocation.location.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false, unique = true)
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Embeddable
    public static class CenterLocationId
        implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer centerId;
        private Integer locationId;

        public Integer getCenterId() {
            return centerId;
        }

        public void setCenterId(Integer centerId) {
            this.centerId = centerId;
        }

        public Integer getLocationId() {
            return locationId;
        }

        public void setLocationId(Integer locationId) {
            this.locationId = locationId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                + ((centerId == null) ? 0 : centerId.hashCode());
            result = prime * result
                + ((locationId == null) ? 0 : locationId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CenterLocationId other = (CenterLocationId) obj;
            if (centerId == null) {
                if (other.centerId != null) return false;
            } else if (!centerId.equals(other.centerId)) return false;
            if (locationId == null) {
                if (other.locationId != null) return false;
            } else if (!locationId.equals(other.locationId)) return false;
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((center == null) ? 0 : center.hashCode());
        result = prime * result
            + ((location == null) ? 0 : location.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CenterLocation other = (CenterLocation) obj;
        if (center == null) {
            if (other.center != null) return false;
        } else if (!center.equals(other.center)) return false;
        if (location == null) {
            if (other.location != null) return false;
        } else if (!location.equals(other.location)) return false;
        return true;
    }
}
