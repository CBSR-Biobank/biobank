package edu.ualberta.med.biobank.importer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.ServiceConnection;
import edu.ualberta.med.biobank.common.cbsr.CbsrClinics;
import edu.ualberta.med.biobank.common.cbsr.CbsrContainerTypes;
import edu.ualberta.med.biobank.common.cbsr.CbsrContainers;
import edu.ualberta.med.biobank.common.cbsr.CbsrSite;
import edu.ualberta.med.biobank.common.cbsr.CbsrStudies;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/*
 * a call to get a column from a result set can only be made once, otherwise the
 * driver generates an exception.
 */

public class Importer {

    private static final Logger logger = Logger.getLogger(Importer.class
        .getName());

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static SimpleDateFormat dateTimeFormatter;

    private static WritableApplicationService appService;

    private static Connection con;

    private static ArrayList<String> tables;

    private static final Map<String, String> newStudyShortNameMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("AHFEM", "AHFEM");
        aMap.put("BBP", "BBPSP");
        aMap.put("CCCS", "CCCS");
        aMap.put("CEGIIR", "CEGIIR");
        aMap.put("CHILD", "CHILD");
        aMap.put("ERCIN", "ERCIN");
        aMap.put("KDCS", "KDCS");
        aMap.put("KMS", "KMS");
        aMap.put("LCS", "LCS");
        aMap.put("MPS", "MPS");
        aMap.put("NHS", "NHS");
        aMap.put("RVS", "RVS");
        aMap.put("SPARK", "SPARK");
        aMap.put("TCKS", "TCKS");
        aMap.put("VAS", "VAS");
        newStudyShortNameMap = Collections.unmodifiableMap(aMap);
    };

    private static SiteWrapper cbsrSite = null;

    private static Map<String, ClinicWrapper> clinicsMap = null;

    private static Map<String, StudyWrapper> studiesMap = null;

    private static Map<String, ContainerTypeWrapper> containerTypesMap = null;

    private static Map<String, ContainerWrapper> topContainersMap = null;

    private static Map<String, SampleTypeWrapper> sampleTypeMap;

    public static void main(String[] args) throws Exception {
        dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        tables = new ArrayList<String>();
        PropertyConfigurator.configure("conf/log4j.properties");

        try {
            con = getMysqlConnection();

            getTables();
            if (tables.size() == 0) {
                throw new Exception("No tables found in export database");
            }

            String[] reqdTables = { "clinics", "study_list", "patient",
                "patient_visit", "cabinet", "freezer", "sample_list" };

            for (String table : reqdTables) {
                if (!tableExists(table))
                    throw new Exception("Table " + table + " not found");
            }

            appService = ServiceConnection.getAppService("http://"
                + System.getProperty("server", "localhost:8080") + "/biobank2",
                "testuser", "test");

            cbsrSite = getCbsrSite();

            if (cbsrSite == null) {
                importAll();
            } else {
                initClinicsMap();
                initStudiesMap();
                initContainerTypesMap();
                initTopContainersMap();
                getSampleTypeMap();

                // removeAllPatientVisits();
                // importShipments();
                importPatientVisits();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void importAll() throws Exception {
        // checkCabinet();
        // checkFreezer();
        // System.exit(0);

        CbsrSite.deleteConfiguration(appService);
        logger.info("creating CBSR site...");
        cbsrSite = CbsrSite.addSite(appService);

        logger.info("creating clinics...");
        CbsrClinics.createClinics(cbsrSite);

        logger.info("creating studies... ");
        CbsrStudies.createStudies(cbsrSite);

        logger.info("creating container types...");
        CbsrContainerTypes.createContainerTypes(cbsrSite);

        logger.info("creating containers...");
        CbsrContainers.createContainers(cbsrSite);

        initClinicsMap();
        initStudiesMap();
        initContainerTypesMap();
        initTopContainersMap();
        getSampleTypeMap();

        importPatients();
        importShipments();
        importPatientVisits();

        importCabinetSamples();
        // importFreezerSamples();

        logger.info("importing complete.");
    }

    private static SiteWrapper getCbsrSite() throws Exception {
        for (SiteWrapper site : SiteWrapper.getSites(appService)) {
            if (site.getName().equals("Canadian BioSample Repository")) {
                return site;
            }
        }
        return null;
    }

    private static void initClinicsMap() {
        clinicsMap = new HashMap<String, ClinicWrapper>();
        for (ClinicWrapper clinic : cbsrSite.getClinicCollection()) {
            clinicsMap.put(clinic.getName(), clinic);
        }
    }

    private static void initStudiesMap() {
        studiesMap = new HashMap<String, StudyWrapper>();
        for (StudyWrapper study : cbsrSite.getStudyCollection()) {
            studiesMap.put(study.getNameShort(), study);
        }
    }

    private static void initContainerTypesMap() {
        containerTypesMap = new HashMap<String, ContainerTypeWrapper>();
        for (ContainerTypeWrapper type : cbsrSite.getContainerTypeCollection()) {
            containerTypesMap.put(type.getName(), type);
        }
    }

    private static void initTopContainersMap() throws Exception {
        topContainersMap = new HashMap<String, ContainerWrapper>();
        for (ContainerWrapper container : cbsrSite.getTopContainerCollection()) {
            topContainersMap.put(container.getLabel(), container);
        }
    }

    public static void getSampleTypeMap() throws Exception {
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        if ((allSampleTypes == null) || (allSampleTypes.size() == 0)) {
            throw new Exception("no global sample types found in the database");
        }

        sampleTypeMap = new HashMap<String, SampleTypeWrapper>();
        for (SampleTypeWrapper sampleType : allSampleTypes) {
            sampleTypeMap.put(sampleType.getNameShort(), sampleType);
        }
    }

    @SuppressWarnings("unused")
    private Connection getDsnConnection() throws Exception {
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        String dbUrl = "jdbc:odbc:bbp_db";
        return DriverManager.getConnection(dbUrl, "", "");
    }

    @SuppressWarnings("unused")
    private Connection getFileConnection() throws Exception {
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        String filename = "bbp_db.mdb";
        String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
        database += filename.trim() + ";DriverID=22;READONLY=true}";
        return DriverManager.getConnection(database, "", "");
    }

    public static Connection getMysqlConnection() throws Exception {
        // Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/bbpdb",
            "dummy", "ozzy498");
    }

    private static void getTables() throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        ResultSet res = meta.getTables(null, null, null,
            new String[] { "TABLE" });
        while (res.next()) {
            tables.add(res.getString("TABLE_NAME"));

            // logger.debug("   " + res.getString("TABLE_CAT") + ", "
            // + res.getString("TABLE_SCHEM") + ", "
            // + res.getString("TABLE_NAME") + ", "
            // + res.getString("TABLE_TYPE") + ", " + res.getString("REMARKS"));
        }
        res.close();
    }

    private static StudyWrapper getStudyFromOldShortName(String shortName)
        throws Exception {
        String newShortName = newStudyShortNameMap.get(shortName);
        if (newShortName == null) {
            throw new Exception("no study mapped with old name: " + shortName);
        }
        return studiesMap.get(newShortName);
    }

    private static boolean tableExists(String name) {
        for (int i = 0; i < tables.size(); ++i) {
            if (tables.get(i).equals(name))
                return true;
        }
        return false;
    }

    private static void importPatients() throws Exception {
        BlowfishCipher cipher = new BlowfishCipher();
        StudyWrapper study;
        PatientWrapper patient;
        logger.debug("importing patients ...");

        String qryPart = "from patient, study_list where patient.study_nr=study_list.study_nr";

        Statement s = con.createStatement();
        s.execute("select count(*) " + qryPart);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numPatients = rs.getInt(1);

        s.execute("select patient.*, study_list.study_name_short " + qryPart);
        rs = s.getResultSet();
        int count = 1;
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        while (rs.next()) {
            String studyNameShort = rs.getString(5);
            String patientNo = cipher.decode(rs.getBytes(2));
            study = getStudyFromOldShortName(studyNameShort);

            if (study == null) {
                logger.debug("ERROR: study with short name \"" + studyNameShort
                    + "\" not found, patient id: " + rs.getInt(1));
                continue;
            }

            logger.debug("importing patient number " + patientNo + " (" + count
                + "/" + numPatients + ")");
            patient = new PatientWrapper(appService);
            patient.setNumber(patientNo);
            patient.setStudy(study);
            patient.persist();
            ++count;
        }
    }

    private static void removeAllShipments() throws Exception {
        logger.debug("removing old shipments ...");

        for (ClinicWrapper clinic : clinicsMap.values()) {
            List<ShipmentWrapper> shipments = clinic.getShipmentCollection();
            if (shipments == null)
                continue;
            for (ShipmentWrapper shipment : shipments) {
                shipment.delete();
            }
            clinic.reload();
        }
    }

    private static void importShipments() throws Exception {
        String studyNameShort;
        StudyWrapper study;
        String clinicName;
        ClinicWrapper clinic;
        PatientWrapper patient;
        String dateReceivedStr;
        Date dateReceived;
        ShipmentWrapper shipment;
        BlowfishCipher cipher = new BlowfishCipher();

        removeAllShipments();

        logger.debug("importing shipments ...");

        String qryPart = "from patient_visit, study_list, patient "
            + "where patient_visit.study_nr=study_list.study_nr "
            + "and patient_visit.patient_nr=patient.patient_nr";

        Statement s = con.createStatement();
        s.execute("select count(*) " + qryPart);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numShipments = rs.getInt(1);

        s.execute("select study_list.study_name_short, patient.chr_nr, "
            + "patient_visit.clinic_site, patient_visit.date_received "
            + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        int count = 1;
        while (rs.next()) {
            studyNameShort = rs.getString(1);
            String patientNo = cipher.decode(rs.getBytes(2));
            clinicName = rs.getString(3);

            study = getStudyFromOldShortName(studyNameShort);
            clinic = clinicsMap.get(clinicName);

            if (clinic == null) {
                logger.error("no clinic \"" + clinicName + "\"for patient "
                    + patientNo);
                continue;
            }

            clinic.reload();
            dateReceivedStr = rs.getString(4);
            dateReceived = dateTimeFormatter.parse(dateReceivedStr);

            Calendar cal = new GregorianCalendar();
            cal.setTime(dateReceived);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            dateReceived = cal.getTime();

            patient = PatientWrapper.getPatientInSite(appService, patientNo,
                cbsrSite);

            // make sure the study is correct
            if (!patient.getStudy().getNameShort().equals(study.getNameShort())) {
                throw new Exception("patient and study do not match: "
                    + patient.getNumber() + ",  " + studyNameShort);
            }

            shipment = null;
            List<ShipmentWrapper> clinicShipments = clinic
                .getShipmentCollection();
            if (clinicShipments != null) {
                for (ShipmentWrapper cs : clinicShipments) {
                    // Date.equals() checks for milliseconds and for some reason
                    // they are not the same, instead have to convert to string
                    // and then compare strings
                    if (dateTimeFormatter.format(cs.getDateReceived()).equals(
                        dateTimeFormatter.format(dateReceived))) {
                        shipment = cs;
                    }
                }
            }

            // make sure the clinic and study are linked via a contact
            if (!study.getClinicCollection().contains(clinic)) {
                logger.debug("ERROR: study " + study.getNameShort()
                    + " for patient " + patientNo + " is not linked to clinic "
                    + clinic.getName() + " via a contact");
                continue;
            }

            if (shipment == null) {
                shipment = new ShipmentWrapper(appService);
                shipment.setClinic(clinic);
                shipment.setWaybill(dateReceivedStr);
                shipment.setDateReceived(dateReceived);
                shipment.setPatientCollection(Arrays.asList(patient));
                shipment.persist();

                logger.debug("importing shipment: patient/"
                    + patient.getNumber() + " clinic/" + clinic.getName()
                    + " shipment/" + dateReceivedStr + " (" + count + "/"
                    + numShipments + ")");
            } else {
                List<PatientWrapper> patients = shipment.getPatientCollection();
                if (patients == null) {
                    patients = new ArrayList<PatientWrapper>();
                }
                patients.add(patient);
                shipment.setPatientCollection(patients);
                shipment.persist();

                logger.debug("adding to shipment: patient/"
                    + patient.getNumber() + " clinic/" + clinic.getName()
                    + " shipment/" + dateReceivedStr + " (" + count + "/"
                    + numShipments + ")");
            }

            ++count;
        }
    }

    private static void removeAllPatientVisits() throws Exception {
        logger.debug("removing old patient visits ...");

        for (StudyWrapper study : studiesMap.values()) {
            if (study.getPatientVisitCount() == 0)
                continue;

            List<PatientWrapper> patients = study.getPatientCollection();
            if (patients == null)
                continue;
            for (PatientWrapper patient : patients) {
                List<PatientVisitWrapper> visits = patient
                    .getPatientVisitCollection();
                if (visits == null)
                    continue;
                for (PatientVisitWrapper visit : visits) {
                    visit.delete();
                }
            }
            study.reload();
        }
    }

    private static void importPatientVisits() throws Exception {
        removeAllPatientVisits();

        String studyNameShort;
        StudyWrapper study;
        String clinicName;
        ClinicWrapper clinic;
        String dateProcessedStr;
        Date dateProcessed;
        PatientWrapper patient;
        ShipmentWrapper shipment;
        PatientVisitWrapper pv;
        BlowfishCipher cipher = new BlowfishCipher();

        logger.debug("importing patient visits ...");

        String qryPart = "from patient_visit, study_list, patient "
            + "where patient_visit.study_nr=study_list.study_nr "
            + "and patient_visit.patient_nr=patient.patient_nr";

        Statement s = con.createStatement();
        s.execute("select count(*) " + qryPart);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numPatientVisits = rs.getInt(1);

        s
            .execute("select patient_visit.*, study_list.study_name_short, patient.chr_nr "
                + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        int count = 1;
        while (rs.next()) {
            studyNameShort = rs.getString(20);
            clinicName = rs.getString(3);
            String patientNo = cipher.decode(rs.getBytes(21));

            study = getStudyFromOldShortName(studyNameShort);
            clinic = clinicsMap.get(clinicName);

            if (clinic == null) {
                logger.error("no clinic \"" + clinicName + "\"for patient "
                    + patientNo);
                continue;
            }

            dateProcessedStr = rs.getString(6);
            dateProcessed = dateTimeFormatter.parse(dateProcessedStr);

            patient = PatientWrapper.getPatientInSite(appService, patientNo,
                cbsrSite);

            Calendar cal = new GregorianCalendar();
            cal.setTime(dateProcessed);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            dateProcessed = cal.getTime();

            shipment = clinic.getShipment(dateProcessed);

            // make sure the clinic is correct
            if ((shipment != null) && !shipment.getClinic().equals(clinic)) {
                throw new Exception("shipment and clinic do not match: "
                    + dateProcessed + ",  " + clinicName);
            }

            // make sure the study is correct
            if (!patient.getStudy().equals(study)) {
                throw new Exception(
                    "patient study does not match patient visit study");
            }

            // check for shipment
            if (shipment == null) {
                logger.error("found 0 shipments for studyName/"
                    + study.getNameShort() + " clinicName/" + clinicName
                    + " dateReceived/" + dateProcessed);
                continue;
            }

            // check if there is a visit for this date
            if (patient.getVisit(dateProcessed) != null) {
                logger.error("patient " + patientNo
                    + " already has a visit on " + dateProcessed);
                continue;
            }

            pv = new PatientVisitWrapper(appService);
            pv.setDateProcessed(dateProcessed);
            pv.setPatient(patient);
            pv.setShipment(shipment);
            pv.setComment(rs.getString(4));

            logger.debug("importing patient visit: patient/"
                + patient.getNumber() + " visit date/" + dateProcessed + " ("
                + count + "/" + numPatientVisits + ")");

            // now set corresponding patient visit info data
            for (String label : study.getStudyPvAttrLabels()) {
                if (label.equals("PBMC Count")) {
                    pv.setPvAttrValue(label, rs.getString(8));
                } else if (label.equals("Consent")) {
                    ArrayList<String> consents = new ArrayList<String>();
                    if (rs.getInt(9) == 1) {
                        consents.add("Surveillance");
                    }
                    if (rs.getInt(10) == 1) {
                        consents.add("Genetic Predisposition");
                    }
                    pv.setPvAttrValue(label, StringUtils.join(consents, ";"));
                } else if (label.equals("Worksheet")) {
                    pv.setPvAttrValue(label, rs.getString(15));
                }
            }

            pv.persist();
            ++count;
        }
    }

    private static void importCabinetSamples() throws Exception {
        logger.debug("importing cabinet samples ...");

        String qryPart = "from cabinet, study_list, patient_visit, sample_list, patient "
            + "where cabinet.study_nr=study_list.study_nr "
            + "and patient_visit.study_nr=study_list.study_nr "
            + "and cabinet.visit_nr=patient_visit.visit_nr "
            + "and cabinet.patient_nr=patient_visit.patient_nr "
            + "and cabinet.sample_nr=sample_list.sample_nr "
            + "and patient_visit.patient_nr=patient.patient_nr";

        Statement s = con.createStatement();
        s.execute("select count(*) " + qryPart);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numSamples = rs.getInt(1);

        s
            .execute("select patient_visit.visit_nr, patient_visit.date_received, patient_visit.date_taken, "
                + "study_list.study_name_short, sample_list.sample_name_short, cabinet.*, patient.chr_nr "
                + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        ContainerWrapper cabinet = null;

        for (ContainerWrapper container : ContainerWrapper.getContainersInSite(
            appService, cbsrSite, "01")) {
            if (container.getContainerType().getName().equals(
                "Cabinet 4 drawer")) {
                cabinet = container;
                break;
            }
        }

        if (cabinet == null) {
            throw new Exception(
                "Cabinet container not found in biobank database");
        }

        ContainerTypeWrapper cabinetType = cabinet.getContainerType();

        int cabinetNum;
        ContainerWrapper drawer;
        ContainerWrapper bin;
        String studyNameShort;
        PatientWrapper patient;
        PatientVisitWrapper visit;
        Date dateProcessed;
        SampleTypeWrapper sampleType;
        int drawerNum;
        int binNum;
        String drawerName;
        RowColPos binPos;
        String sampleTypeNameShort;
        BlowfishCipher cipher = new BlowfishCipher();

        int count = 0;
        while (rs.next()) {
            ++count;
            cabinetNum = rs.getInt(6);
            if (cabinetNum != 1)
                throw new Exception("Invalid cabinet number: " + cabinetNum);

            drawerName = rs.getString(7);
            Integer rowCap = cabinet.getRowCapacity();
            Integer colCap = cabinet.getColCapacity();
            RowColPos pos = LabelingScheme.cbsrTwoCharToRowCol(drawerName,
                rowCap, colCap, cabinetType.getName());
            drawerNum = pos.row;

            if (drawerNum > 4) {
                throw new Exception("invalid drawer number \"" + drawerNum
                    + "\" for visit number " + rs.getInt(1));
            }

            binNum = rs.getInt(8) - 1;
            String binPosStr = rs.getString(9);
            binPos = LabelingScheme.cbsrTwoCharToRowCol(binPosStr, 120, 1,
                "bin");

            logger.debug("importing Cabinet sample at position " + drawerName
                + String.format("%02d", binNum) + binPosStr + " (" + count
                + "/" + numSamples + ")");

            String patientNo = cipher.decode(rs.getBytes(18));
            patient = PatientWrapper.getPatientInSite(appService, patientNo,
                cbsrSite);

            studyNameShort = rs.getString(4);
            if (!patient.getStudy().getNameShort().equals(studyNameShort)) {
                throw new Exception("patient and study do not match: "
                    + patient.getNumber() + ",  " + studyNameShort);
            }

            visit = null;
            dateProcessed = dateTimeFormatter.parse(rs.getString(2));
            for (PatientVisitWrapper v : patient.getPatientVisitCollection()) {
                if (v.getDateProcessed().equals(dateProcessed)) {
                    visit = v;
                }
            }

            if (visit == null) {
                throw new Exception("patient visit not found for date: "
                    + dateProcessed.toString());
            }

            sampleTypeNameShort = rs.getString(5);
            drawer = cabinet.getChild(pos.row, 0);
            bin = drawer.getChild(binNum, 0);
            sampleType = CbsrSite.getSampleType(sampleTypeNameShort);

            SampleWrapper sample = new SampleWrapper(appService);
            sample.setSampleType(sampleType);
            sample.setInventoryId(rs.getString(13));
            sample.setLinkDate(rs.getDate(14));
            sample.setQuantity(rs.getDouble(15));
            sample.setPosition(binPos.row, 0);
            sample.setPatientVisit(visit);

            if (!bin.canHoldSample(sample)) {
                throw new Exception("bin cannot hold sample");
            }
            sample.persist();
        }
    }

    // private void importFreezerSamples() throws Exception {
    // logger.debug("importing freezer samples ...");
    //
    // String qryPart =
    // "from freezer, study_list, patient_visit, sample_list,patient "
    // + "where freezer.study_nr=study_list.study_nr "
    // + "and patient_visit.study_nr=study_list.study_nr "
    // + "and freezer.visit_nr=patient_visit.visit_nr "
    // + "and freezer.patient_nr=patient_visit.patient_nr "
    // + "and freezer.sample_nr=sample_list.sample_nr "
    // + "and patient_visit.patient_nr=patient.patient_nr";
    //
    // Statement s = con.createStatement();
    // s.execute("select count(*) " + qryPart);
    // ResultSet rs = s.getResultSet();
    // rs.next();
    // int numSamples = rs.getInt(1);
    //
    // s.execute("select patient_visit.date_received, patient_visit.date_taken, "
    // +
    // "study_list.study_name_short, sample_list.sample_name_short, freezer.*, patient.chr_nr "
    // + qryPart);
    //
    // rs = s.getResultSet();
    // if (rs != null) {
    // Container freezer01 = bioBank2Db.getContainer("01", "Freezer-3x10");
    // Container freezer03 = bioBank2Db.getContainer("01", "Freezer-3x10");
    // Container freezer;
    // ContainerType freezerType;
    //
    // int freezerNum;
    // Container hotel;
    // Container pallet;
    // PatientVisit visit;
    // SampleType sampleType;
    // RowColPos hotelPos;
    // int palletNum;
    // String studyName;
    // String dateProcessed;
    // String hotelName;
    // String palletPos;
    // String sampleTypeNameShort;
    // BlowfishCipher cipher = new BlowfishCipher();
    //
    // int count = 0;
    // while (rs.next()) {
    // ++count;
    // freezerNum = rs.getInt(5);
    // hotelName = rs.getString(6);
    //
    // if (freezerNum == 1) {
    // freezer = freezer01;
    // }
    // else if (freezerNum == 3) {
    // freezer = freezer03;
    // }
    // else {
    // logger.debug("Ignoring samples for freezer number "
    // + freezerNum);
    // continue;
    // }
    //
    // freezerType = freezer.getContainerType();
    // Capacity freezerCapacity = freezerType.getCapacity();
    // hotelPos = LabelingScheme.cbsrTwoCharToRowCol(hotelName,
    // freezerCapacity.getRowCapacity(),
    // freezerCapacity.getColCapacity(), freezerType.getName());
    //
    // palletNum = rs.getInt(7) - 1;
    // palletPos = rs.getString(15);
    //
    // logger.debug("importing freezer sample at position "
    // + String.format("%02d", freezerNum) + hotelName
    // + String.format("%02d", palletNum + 1) + palletPos + " ("
    // + count + "/" + numSamples + ")");
    //
    // studyName = rs.getString(3);
    // String patientNo = cipher.decode(rs.getBytes(17));
    // dateProcessed = rs.getString(1);
    //
    // visit = bioBank2Db.getPatientVisit(studyName, patientNo,
    // dateProcessed);
    //
    // if (visit == null) continue;
    //
    // sampleTypeNameShort = rs.getString(4);
    //
    // hotel = bioBank2Db.getChildContainer(freezer, hotelPos.row,
    // hotelPos.col);
    // pallet = bioBank2Db.getChildContainer(hotel, palletNum, 0);
    //
    // if (sampleTypeNameShort.equals("RNA Later")) {
    // sampleTypeNameShort = "RNA Biopsy";
    // }
    //
    // sampleType = bioBank2Db.getSampleTypeByName(sampleTypeNameShort);
    // bioBank2Db.containerCheckSampleTypeValid(pallet, sampleType);
    //
    // RowColPos rowColPos = LabelingScheme.sbsToRowCol(palletPos);
    // SamplePosition spos = new SamplePosition();
    // spos.setRow(rowColPos.row);
    // spos.setCol(rowColPos.col);
    // spos.setContainer(pallet);
    //
    // Sample sample = new Sample();
    // sample.setSampleType(sampleType);
    // sample.setInventoryId(rs.getString(11));
    // sample.setLinkDate(rs.getDate(12));
    // sample.setQuantity(rs.getDouble(16));
    // sample.setSamplePosition(spos);
    // sample.setPatientVisit(visit);
    // spos.setSample(sample);
    //
    // sample = (Sample) bioBank2Db.setObject(sample);
    // }
    // }
    // }

    @SuppressWarnings("unused")
    private void checkCabinet() throws Exception {
        HQLCriteria c = new HQLCriteria("select sc"
            + " from edu.ualberta.med.biobank.model.Container as sc"
            + " where sc.name=?");
        c.setParameters(Arrays.asList(new Object[] { "Cabinet" }));

        System.out
            .println("CName        Type         CParent      pos1 pos2 SampleSize");
        List<Container> containers = appService.query(c);
        for (Container sc : containers) {
            printContainerPositions(sc.getChildPositionCollection());
        }
    }

    @SuppressWarnings("unused")
    private void checkFreezer() throws Exception {

        HQLCriteria c = new HQLCriteria("select sc"
            + " from edu.ualberta.med.biobank.model.Container as sc"
            + " where sc.name=?");
        c.setParameters(Arrays.asList(new Object[] { "FR01" }));

        System.out
            .println("CName        Type         CParent      pos1 pos2 SampleSize");
        List<Container> containers = appService.query(c);
        for (Container sc : containers) {
            printContainerPositions(sc.getChildPositionCollection());
        }
    }

    private void printContainerPositions(Collection<ContainerPosition> positions) {
        if (positions == null)
            return;

        for (ContainerPosition pos : positions) {
            logger.debug(String.format("%-12s %-12s %-12s %2d  %2d    %3d", pos
                .getContainer().getLabel(), pos.getContainer()
                .getContainerType().getName(), pos.getParentContainer()
                .getLabel(), pos.getRow(), pos.getCol(), pos.getContainer()
                .getSamplePositionCollection().size()));
        }

        for (ContainerPosition pos : positions) {
            printContainerPositions(pos.getContainer()
                .getChildPositionCollection());
        }
    }

    @SuppressWarnings("unused")
    private String[] getRowData(ResultSet rs) throws SQLException {
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numCols = rsMetaData.getColumnCount();
        String[] result = new String[numCols];

        for (int i = 1; i <= numCols; ++i) {
            result[i - 1] = rs.getString(i);
        }
        return result;
    }

    public static WritableApplicationService getAppService() throws Exception {
        if (appService == null) {
            throw new Exception("appService has not been initialized");
        }
        return appService;
    }

}
