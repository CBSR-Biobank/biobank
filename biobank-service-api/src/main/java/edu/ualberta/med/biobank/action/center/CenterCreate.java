package edu.ualberta.med.biobank.action.center;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.IdResult;

public class CenterCreate
    implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
