package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CENTER_COMMENT")
public class CenterComment
    extends Comment {
    private static final long serialVersionUID = 1L;

    private Center center;

    @NotNull(message = "{CenterComment.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }
}
