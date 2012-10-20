package edu.ualberta.med.biobank.model.center;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.Comment;

@Entity
@Table(name = "CONTAINER_COMMENT")
public class ContainerComment
    extends Comment<Container<?>> {
    private static final long serialVersionUID = 1L;

    private Container<?> container;

    @NotNull(message = "{ContainerComment.container.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_ID", nullable = false)
    public Container<?> getContainer() {
        return container;
    }

    public void setContainer(Container<?> container) {
        this.container = container;
    }

    @Override
    @Transient
    public Container<?> getOwner() {
        return getContainer();
    }
}
