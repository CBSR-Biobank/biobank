package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.io.Serializable;
import java.util.Date;

import edu.ualberta.med.biobank.model.Center;

/**
 * This class could be replaced with a model object and saved to the database
 * with {@link Log} object children. But for now, this object is not persisted,
 * just used for relevant data transfer.
 * 
 * @author jferland
 * 
 */
public class LogGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private Center center;
    private Date createdAt;
    private String details;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
