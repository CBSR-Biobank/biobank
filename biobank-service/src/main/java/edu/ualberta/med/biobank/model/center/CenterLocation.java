package edu.ualberta.med.biobank.model.center;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.type.Location;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * In case a {@link Center} has more than one {@link Location}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "CENTER_LOCATION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CENTER_ID", "NAME" })
    })
@NotUsed.List({
    @NotUsed(by = ContainerTree.class, property = "location", groups = PreDelete.class),
    @NotUsed(by = Specimen.class, property = "originLocation", groups = PreDelete.class),
    @NotUsed(by = Specimen.class, property = "location", groups = PreDelete.class),
    @NotUsed(by = Shipment.class, property = "fromLocation", groups = PreDelete.class),
    @NotUsed(by = Shipment.class, property = "toLocation", groups = PreDelete.class)
})
@Unique(properties = { "center", "location.name" }, groups = PrePersist.class)
public class CenterLocation
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Center center;
    private Location location;

    @NotNull(message = "{CenterLocation.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @NotNull(message = "{CenterLocation.location.NotNull}")
    @Valid
    @Embedded
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
