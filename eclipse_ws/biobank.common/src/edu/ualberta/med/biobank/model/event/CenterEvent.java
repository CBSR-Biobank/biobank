package edu.ualberta.med.biobank.model.event;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.ualberta.med.biobank.model.Center;

@Entity
@DiscriminatorValue("1")
public abstract class CenterEvent extends Event {
    private static final long serialVersionUID = 1L;

    private Center center;

    @Column(name = "CENTER_ID")
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }
}
