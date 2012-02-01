/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.LogPeer;
import java.util.Date;

public class LogBaseWrapper extends ModelWrapper<Log> {

    public LogBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public LogBaseWrapper(WritableApplicationService appService,
        Log wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Log> getWrappedClass() {
        return Log.class;
    }

    @Override
    public Property<Integer, ? super Log> getIdProperty() {
        return LogPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Log>> getProperties() {
        return LogPeer.PROPERTIES;
    }

    public String getCenter() {
        return getProperty(LogPeer.CENTER);
    }

    public void setCenter(String center) {
        String trimmed = center == null ? null : center.trim();
        setProperty(LogPeer.CENTER, trimmed);
    }

    public String getUsername() {
        return getProperty(LogPeer.USERNAME);
    }

    public void setUsername(String username) {
        String trimmed = username == null ? null : username.trim();
        setProperty(LogPeer.USERNAME, trimmed);
    }

    public String getDetails() {
        return getProperty(LogPeer.DETAILS);
    }

    public void setDetails(String details) {
        String trimmed = details == null ? null : details.trim();
        setProperty(LogPeer.DETAILS, trimmed);
    }

    public Date getCreatedAt() {
        return getProperty(LogPeer.CREATED_AT);
    }

    public void setCreatedAt(Date createdAt) {
        setProperty(LogPeer.CREATED_AT, createdAt);
    }

    public String getLocationLabel() {
        return getProperty(LogPeer.LOCATION_LABEL);
    }

    public void setLocationLabel(String locationLabel) {
        String trimmed = locationLabel == null ? null : locationLabel.trim();
        setProperty(LogPeer.LOCATION_LABEL, trimmed);
    }

    public String getPatientNumber() {
        return getProperty(LogPeer.PATIENT_NUMBER);
    }

    public void setPatientNumber(String patientNumber) {
        String trimmed = patientNumber == null ? null : patientNumber.trim();
        setProperty(LogPeer.PATIENT_NUMBER, trimmed);
    }

    public String getAction() {
        return getProperty(LogPeer.ACTION);
    }

    public void setAction(String action) {
        String trimmed = action == null ? null : action.trim();
        setProperty(LogPeer.ACTION, trimmed);
    }

    public String getInventoryId() {
        return getProperty(LogPeer.INVENTORY_ID);
    }

    public void setInventoryId(String inventoryId) {
        String trimmed = inventoryId == null ? null : inventoryId.trim();
        setProperty(LogPeer.INVENTORY_ID, trimmed);
    }

    public String getType() {
        return getProperty(LogPeer.TYPE);
    }

    public void setType(String type) {
        String trimmed = type == null ? null : type.trim();
        setProperty(LogPeer.TYPE, trimmed);
    }

}
