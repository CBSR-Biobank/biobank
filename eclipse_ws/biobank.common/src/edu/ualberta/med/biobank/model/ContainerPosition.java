package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

@Entity
@Table(name="CONTAINER_POSITION")
public class ContainerPosition extends AbstractPosition {
    private static final long serialVersionUID = 1L;
    
    private Container parentContainer;
    private Container container;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CONTAINER_ID", nullable = false)
    public Container getParentContainer() {
        return this.parentContainer;
    }

    public void setParentContainer(Container parentContainer) {
        this.parentContainer = parentContainer;
    }

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "position")
    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }
}
