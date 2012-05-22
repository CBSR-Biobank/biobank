package edu.ualberta.med.biobank.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LOG")
public class Log implements Serializable, IBiobankModel {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String username;
    private Date createdAt;
    private String center;
    private String action;
    private String patientNumber;
    private String inventoryId;
    private String locationLabel;
    private String details;
    private String type;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "USERNAME", length = 100)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "CREATED_AT")
    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "CENTER", length = 50)
    public String getCenter() {
        return this.center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    @Column(name = "ACTION", length = 100)
    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Column(name = "PATIENT_NUMBER", length = 100)
    public String getPatientNumber() {
        return this.patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    @Column(name = "INVENTORY_ID", length = 100)
    public String getInventoryId() {
        return this.inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    @Column(name = "LOCATION_LABEL")
    public String getLocationLabel() {
        return this.locationLabel;
    }

    public void setLocationLabel(String locationLabel) {
        this.locationLabel = locationLabel;
    }

    @Column(name = "DETAILS", columnDefinition="TEXT")
    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Column(name = "TYPE", length = 100)
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
