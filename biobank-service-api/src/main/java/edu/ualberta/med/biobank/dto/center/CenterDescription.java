package edu.ualberta.med.biobank.dto.center;

import org.hibernate.validator.constraints.NotEmpty;

public interface CenterDescription {
    @NotEmpty(message = "{Center.name.NotEmpty}")
    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);
}
