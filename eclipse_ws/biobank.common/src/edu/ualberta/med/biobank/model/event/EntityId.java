package edu.ualberta.med.biobank.model.event;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import edu.ualberta.med.biobank.model.HasId;

@Embeddable
public class EntityId {
    private String label;
    private Integer entityId;

    public EntityId() {
    }

    public EntityId(String label, Integer entityId) {
        this.label = label;
        this.entityId = entityId;
    }

    public EntityId(String label, HasId<Integer> hasId) {
        this(label, hasId.getId());
    }

    @Column(name = "LABEL")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(name = "ENTITY_ID")
    public Integer getEntityId() {
        return entityId;
    }

    public void setId(Integer id) {
        this.entityId = id;
    }
}
