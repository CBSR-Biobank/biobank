package edu.ualberta.med.biobank.importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {
    private boolean decodePatientNumbers;
    private boolean importPatients;
    private boolean importShipments;
    private boolean importPatientVisits;
    private Map<Integer, String> importCabinets;
    private Map<Integer, String> importFreezers;

    Configuration(String configFilename) throws IOException {
        importPatients = false;
        importShipments = false;
        importPatientVisits = false;
        importCabinets = new HashMap<Integer, String>();
        importFreezers = new HashMap<Integer, String>();

        Properties configProps = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(configFilename);
        configProps.load(in);

        String property;

        property = configProps.getProperty("cbsr.decode.patient_numbers");
        if (property != null) {
            decodePatientNumbers = property.equals("yes");
        }

        property = configProps.getProperty("cbsr.import.patients");
        if (property != null) {
            importPatients = property.equals("yes");
        }

        property = configProps.getProperty("cbsr.import.shipments");
        if (property != null) {
            importShipments = property.equals("yes");
        }

        property = configProps.getProperty("cbsr.import.patient_visits");
        if (property != null) {
            importPatientVisits = property.equals("yes");
        }

        for (int i = 1; i <= 2; ++i) {
            property = configProps.getProperty("cbsr.import.cabinet.0" + i);
            if (property != null) {
                importCabinets.put(i, property);
            }
        }

        String[] freezerNrs = new String[] { "01", "02", "03", "05", "99" };
        for (String nr : freezerNrs) {
            property = configProps.getProperty("cbsr.import.freezer." + nr);
            if (property != null) {
                importFreezers.put(Integer.valueOf(nr), property);
            }
        }
    }

    public boolean decodePatientNumbers() {
        return decodePatientNumbers;
    }

    public boolean importPatients() {
        return importPatients;
    }

    public boolean importShipments() {
        return importShipments;
    }

    public boolean importPatientVisits() {
        return importPatientVisits;
    }

    public boolean importCabinets() {
        boolean result = false;
        for (String configValue : importCabinets.values()) {
            if (!configValue.equals("no")) {
                result = true;
            }
        }
        return result;
    }

    public boolean importCabinet(int cabinetId) {
        String configValue = importCabinets.get(cabinetId);
        if (configValue == null)
            return false;
        return !configValue.equals("no");
    }

    public boolean importCabinetDrawer(String drawerLabel) throws Exception {
        if (drawerLabel.length() < 4) {
            throw new Exception(
                "invalid length for drawer label. should be 4 characters: "
                    + drawerLabel);
        }

        String configValue;
        if (drawerLabel.length() == 4) {
            configValue = importCabinets.get(Integer.valueOf(drawerLabel
                .substring(0, 2)));
        } else {
            throw new Exception("invalid hotel label: " + drawerLabel);
        }

        return (configValue.equals("yes") || configValue.contains(drawerLabel
            .subSequence(2, 4)));
    }

    public boolean importFreezers() {
        boolean result = false;
        for (String configValue : importFreezers.values()) {
            if (!configValue.equals("no")) {
                result = true;
            }
        }
        return result;
    }

    public boolean importFreezer(int freezerId) {
        String configValue = importFreezers.get(freezerId);
        if (configValue == null)
            return false;
        return !configValue.equals("no");
    }

    public boolean importFreezerHotel(String hotelLabel) throws Exception {
        if (hotelLabel.length() < 4) {
            throw new Exception(
                "invalid length for hotel label. should be 4 characters: "
                    + hotelLabel);
        }

        String configValue;
        if (hotelLabel.startsWith("SS")) {
            configValue = importFreezers.get(99);
        } else if (hotelLabel.length() == 4) {
            configValue = importFreezers.get(Integer.valueOf(hotelLabel
                .substring(0, 2)));
        } else {
            throw new Exception("invalid hotel label: " + hotelLabel);
        }

        return (configValue.equals("yes") || configValue.contains(hotelLabel
            .subSequence(2, 4)));
    }

}
