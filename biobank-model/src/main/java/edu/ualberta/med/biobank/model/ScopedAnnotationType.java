package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public abstract class ScopedAnnotationType
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private AnnotationType type;
    private Boolean enabled;
    private Boolean required;

    /**
     * @return true if this {@link AnnotationType} is still being used and if
     *         values can still be entered, otherwise false when values cannot
     *         be entered but this {@link AnnotationType} must be kept for
     *         historical and record-keeping purposes.
     */
    @NotNull(message = "{AnnotationType.enabled.NotNull}")
    @Column(name = "IS_ENABLED", nullable = false)
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return true if this {@link AnnotationType} <em>must</em> be assigned a
     *         value.
     */
    @NotNull(message = "{AnnotationType.required.NotNull}")
    @Column(name = "IS_REQUIRED", nullable = false)
    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public enum Scope implements Serializable {
        COLLECTION_EVENT("CE"),
        PATIENT("PT"),
        SPECIMEN("SP");

        private final String id;

        private Scope(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
