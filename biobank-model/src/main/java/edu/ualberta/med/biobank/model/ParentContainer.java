package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Embeddable
public class ParentContainer implements Serializable {
    private static final long serialVersionUID = 1L;

    private Container container;
    private Integer position;

    @NotNull(message = "{ParentContainer.container.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CONTAINER_ID")
    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @NotNull(message = "{ParentContainer.position.NotNull}")
    @Min(value = 0, message = "{ParentContainer.position.Min}")
    @Column(name = "POSITION_IN_PARENT_CONTAINER")
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Transient
    public String getLabel() {
        return null; // TODO: implement this
    }
}
