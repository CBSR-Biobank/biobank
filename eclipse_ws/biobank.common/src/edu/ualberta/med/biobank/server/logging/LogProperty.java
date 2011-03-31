package edu.ualberta.med.biobank.server.logging;

import edu.ualberta.med.biobank.model.Log;

public enum LogProperty {
    USERNAME("username") {
        @Override
        public void setValue(Log log, String value) {
            log.setUsername(value);
        }
    },
    CREATED_AT("createdAt") {
        @Override
        public void setValue(Log log, String value) {
        }
    },
    CENTER("center") {
        @Override
        public void setValue(Log log, String value) {
            log.setCenter(value);
        }
    },
    ACTION("action") {
        @Override
        public void setValue(Log log, String value) {
            log.setAction(value);
        }
    },
    PATIENT_NUMBER("patientNumber") {
        @Override
        public void setValue(Log log, String value) {
            log.setPatientNumber(value);
        }
    },
    INVENTORY_ID("inventoryId") {
        @Override
        public void setValue(Log log, String value) {
            log.setInventoryId(value);
        }
    },
    LOCATION_LABEL("locationLabel") {
        @Override
        public void setValue(Log log, String value) {
            log.setLocationLabel(value);
        }
    },
    DETAILS("details") {
        @Override
        public void setValue(Log log, String value) {
            log.setDetails(value);
        }
    },
    TYPE("type") {
        @Override
        public void setValue(Log log, String value) {
            log.setType(value);
        }
    };

    private String propertyName;

    private LogProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getTableName() {
        return name();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean setLogValueIfInString(Log log, String attributeName,
        String value) {
        if (propertyName.equalsIgnoreCase(attributeName)) {
            setValue(log, value);
            return true;
        }
        return false;
    }

    protected abstract void setValue(Log log, String value);
}
