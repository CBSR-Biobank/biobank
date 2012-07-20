package edu.ualberta.med.biobank.model.event;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import edu.ualberta.med.biobank.model.Center;

@MappedSuperclass
public abstract class CenterEvent<T extends Enum<T> & EventType>
    extends Event<T> {
    private static final long serialVersionUID = 1L;

    private Center center;

    public CenterEvent() {
    }

    public CenterEvent(Center center) {
        setCenter(center);
    }

    @Column(name = CENTER_COLUMN_NAME)
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }
}
