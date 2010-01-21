package edu.ualberta.med.biobank.importer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Shipment;
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
        aMap.put("A", "KDCS");
        aMap.put("C", "CCCS");
        aMap.put("E", "ERCIN");
        aMap.put("G", "CEGIIR");
        aMap.put("H", "AHFEM");
        aMap.put("K", "KMS");
        aMap.put("L", "LCS");
        aMap.put("M", "MPS");
        aMap.put("N", "NHS");
        aMap.put("P", "CHILD");
        aMap.put("R", "RVS");
        aMap.put("S", "SPARK");
        aMap.put("V", "VAS");
        aMap.put("Z", "TCKS");
        newStudyShortNameMap = Collections.unmodifiableMap(aMap);
    };

    private static final Map<String, String> patientNrToClinicMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("AA", "ED1");
        aMap.put("AB", "CL1-KDCS");
        aMap.put("AC", "VN1");
        aMap.put("CC", "ED1");
        aMap.put("ER", "SF1");
        aMap.put("EA", "ED1");
        aMap.put("GR", "ED1");
        aMap.put("HA", "ED1");
        aMap.put("KN", "KN1");
        aMap.put("LC", "ED1");
        aMap.put("MP", "ED1");
        aMap.put("NH", "ED1");
        aMap.put("PA", "ED1");
        aMap.put("RV", "ED1");
        aMap.put("SA", "ED1");
        aMap.put("VA", "ED1");
        aMap.put("ZA", "ED1");
        aMap.put("ZB", "CL1-KDCS");
        aMap.put("ZC", "VN1");

        patientNrToClinicMap = Collections.unmodifiableMap(aMap);
    };

    private static SiteWrapper cbsrSite = null;

    private static Map<String, ClinicWrapper> clinicsMap = null;

    private static Map<String, StudyWrapper> studiesMap = null;

    private static Map<String, ContainerTypeWrapper> containerTypesMap = null;

    private static Map<String, ContainerWrapper> topContainersMap = null;

    private static Map<String, SampleTypeWrapper> sampleTypeMap;

    private static ImportCounts importCounts;

    public static void main(String[] args) throws Exception {
        dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        tables = new ArrayList<String>();
        PropertyConfigurator.configure("conf/log4j.properties");

        importCounts = new ImportCounts();
        importCounts.patients = 0;
        importCounts.shipments = 0;
        importCounts.visits = 0;
        importCounts.samples = 0;

        try {
            con = getMysqlConnection();

            getTables();
            if (tables.size() == 0) {
                throw new Exception("No tables found in export database");
            }

            String[] reqdTables = { "clinics", "study_list", "patient",
                "patient_visit", "cabinet", "freezer", "sample_list",
                "frz_inv_id_cnt" };

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

                // importPatients();
                // removeAllShipments();
                // importShipments();
                importPatientVisits();
                // removeAllSamples();
                // importCabinetSamples();
                // importFreezerSamples();
            }

            logger.info("import complete");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
        }

        logger.info("patients imported: " + importCounts.patients);
        logger.info("shipments imported: " + importCounts.shipments);
        logger.info("visits imported: " + importCounts.visits);
        logger.info("samples imported: " + importCounts.samples);
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

        // importPatientVisits();
        // importCabinetSamples();
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

    private static void removeAllPatients() throws Exception {
        logger.info("removing old patients ...");

        HQLCriteria criteria = new HQLCriteria("from "
            + Patient.class.getName());
        List<Patient> patients = appService.query(criteria);
        for (Patient patient : patients) {
            PatientWrapper p = new PatientWrapper(appService, patient);
            p.delete();
        }
    }

    private static void importPatients() throws Exception {
        BlowfishCipher cipher = new BlowfishCipher();
        StudyWrapper study;
        String studyNameShort;
        PatientWrapper patient;
        removeAllPatients();
        logger.info("importing patients ...");

        String qryPart = "from patient join study_list on patient.study_nr=study_list.study_nr";

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
            String patientNo = cipher.decode(rs.getBytes(2));

            if (patientNo.length() == 6) {
                studyNameShort = getStudyShortNameFromPatientNr(patientNo);
            } else {
                studyNameShort = rs.getString(6);
            }

            study = getStudyFromOldShortName(studyNameShort);

            if (study == null) {
                logger.debug("ERROR: study with short name \"" + studyNameShort
                    + "\" not found, patient id: " + rs.getInt(1));
                continue;
            }

            logger.debug("importing patient number " + patientNo + " (" + count
                + "/" + numPatients + ")");
            patient = new PatientWrapper(appService);
            patient.setPnumber(patientNo);
            patient.setStudy(study);
            patient.persist();
            ++importCounts.patients;
            ++count;

            // update the BBPDB with the decoded CHR number
            String decChrNr = rs.getString(5);
            if (decChrNr == null) {
                PreparedStatement ps = con
                    .prepareStatement("update patient set dec_chr_nr = ? where patient_nr = ?");
                ps.setString(1, patientNo);
                ps.setInt(2, rs.getInt(1));
                ps.executeUpdate();
            }
        }
    }

    private static void removeAllShipments() throws Exception {
        logger.info("removing old shipments ...");

        HQLCriteria criteria = new HQLCriteria("from "
            + Shipment.class.getName());
        List<Shipment> shipments = appService.query(criteria);
        for (Shipment shipment : shipments) {
            ShipmentWrapper s = new ShipmentWrapper(appService, shipment);
            s.delete();
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

        logger.info("importing shipments ...");

        String qryPart = "from patient_visit, study_list, patient "
            + "where patient_visit.study_nr=study_list.study_nr "
            + "and patient_visit.patient_nr=patient.patient_nr "
            + "order by patient_visit.date_received desc";

        Statement s = con.createStatement();
        s.execute("select count(*) " + qryPart);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numShipments = rs.getInt(1);

        s.execute("select patient.chr_nr, "
            + "study_list.study_name_short, patient_visit.clinic_site, "
            + "patient_visit.date_received " + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        int count = 1;
        while (rs.next()) {
            String patientNo = cipher.decode(rs.getBytes(1));
            if (patientNo.length() == 6) {
                if (patientNo.substring(0, 2).equals("CE")) {
                    studyNameShort = "CEGIIR";
                    clinicName = "ED1";
                } else {
                    studyNameShort = getStudyShortNameFromPatientNr(patientNo);
                    clinicName = getClinicNameFromPatientNr(patientNo);
                }
            } else {
                studyNameShort = rs.getString(2);
                clinicName = rs.getString(3);
            }

            if (clinicName == null) {
                logger.error("no clinic for patient " + patientNo);
                ++count;
                continue;
            }

            study = getStudyFromOldShortName(studyNameShort);
            clinicName = clinicName.toUpperCase();
            clinic = clinicsMap.get(clinicName);
            if (clinic == null) {
                logger.error("no clinic \"" + clinicName + "\" for patient "
                    + patientNo);
                ++count;
                continue;
            }

            // make sure the clinic and study are linked via a contact
            if (!study.hasClinic(clinicName)) {
                logger.error("study " + study.getNameShort() + " for patient "
                    + patientNo + " is not linked to clinic "
                    + clinic.getName() + " via a contact");
                ++count;
                continue;
            }

            dateReceivedStr = rs.getString(4);
            dateReceived = dateTimeFormatter.parse(dateReceivedStr);

            Calendar cal = new GregorianCalendar();
            cal.setTime(dateReceived);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            dateReceived = cal.getTime();

            patient = study.getPatient(patientNo);
            // make sure patient is in the study
            if (patient == null) {
                throw new Exception("patient not found in study: " + patientNo
                    + ",  " + studyNameShort);
            }

            shipment = clinic.getShipment(dateReceived);
            if (shipment == null) {
                ++importCounts.shipments;
                logger.debug("new shipment: " + importCounts.shipments
                    + " patient/" + patient.getPnumber() + " clinic/"
                    + clinic.getName() + " shipment/" + dateReceivedStr + " ("
                    + count + "/" + numShipments + ")");

                shipment = new ShipmentWrapper(appService);
                shipment.setClinic(clinic);
                shipment.setWaybill(dateReceivedStr);
                shipment.setDateReceived(dateReceived);
                shipment.setPatientCollection(Arrays.asList(patient));
                shipment.persist();
            } else if (shipment.getPatient(patientNo) == null) {
                logger.debug("adding to shipment: patient/"
                    + patient.getPnumber() + " clinic/" + clinic.getName()
                    + " shipment/" + dateReceivedStr + " (" + count + "/"
                    + numShipments + ")");

                List<PatientWrapper> patients = shipment.getPatientCollection();
                if (patients == null) {
                    patients = new ArrayList<PatientWrapper>();
                }
                patients.add(patient);
                shipment.setPatientCollection(patients);
                shipment.persist();
            } else {
                logger.debug("already in database: patient/"
                    + patient.getPnumber() + " clinic/" + clinic.getName()
                    + " shipment/" + dateReceivedStr + " (" + count + "/"
                    + numShipments + ")");
            }

            ++count;
        }
    }

    private static void removeAllPatientVisits() throws Exception {
        logger.info("removing old patient visits ...");

        HQLCriteria criteria = new HQLCriteria("from "
            + PatientVisit.class.getName());
        List<PatientVisit> visits = appService.query(criteria);
        for (PatientVisit visit : visits) {
            PatientVisitWrapper v = new PatientVisitWrapper(appService, visit);
            v.delete();
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

        logger.info("importing patient visits ...");

        String qryPart = "from patient_visit, study_list, patient "
            + "where patient_visit.study_nr=study_list.study_nr "
            + "and patient_visit.patient_nr=patient.patient_nr "
            + "order by patient_visit.date_received";

        Statement s = con.createStatement();
        s.execute("select count(*) " + qryPart);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numPatientVisits = rs.getInt(1);

        s.execute("select patient_visit.*, study_list.study_name_short, "
            + "patient.chr_nr " + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        int count = 1;
        while (rs.next()) {
            String patientNo = cipher.decode(rs.getBytes(21));
            if (patientNo.length() == 6) {
                if (patientNo.substring(0, 2).equals("CE")) {
                    studyNameShort = "CEGIIR";
                    clinicName = "ED1";
                } else {
                    studyNameShort = getStudyShortNameFromPatientNr(patientNo);
                    clinicName = getClinicNameFromPatientNr(patientNo);
                }
            } else {
                studyNameShort = rs.getString(20);
                clinicName = rs.getString(3);
            }

            clinicName = clinicName.toUpperCase();

            if (clinicName == null) {
                logger.error("no for patient " + patientNo);
                ++count;
                continue;
            }

            study = getStudyFromOldShortName(studyNameShort);
            clinic = clinicsMap.get(clinicName);

            if (clinic == null) {
                logger.error("no clinic \"" + clinicName + "\" for patient "
                    + patientNo);
                ++count;
                continue;
            }

            dateProcessedStr = rs.getString(6);
            dateProcessed = dateTimeFormatter.parse(dateProcessedStr);

            patient = study.getPatient(patientNo);
            // make sure patient is in the study
            if (patient == null) {
                throw new Exception("patient not found in study: " + patientNo
                    + ",  " + studyNameShort);
            }
            patient.reload();

            Calendar cal = new GregorianCalendar();
            cal.setTime(dateProcessed);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            dateProcessed = cal.getTime();

            shipment = clinic.getShipment(dateProcessed, patientNo);

            // check for shipment
            if (shipment == null) {
                logger.error("found 0 shipments for patientNo/" + patientNo
                    + " studyName/" + study.getNameShort() + " clinicName/"
                    + clinicName + " dateReceived/" + dateProcessed);
                ++count;
                continue;
            }

            ++importCounts.visits;
            pv = new PatientVisitWrapper(appService);
            pv.setDateProcessed(dateProcessed);
            pv.setPatient(patient);
            pv.setShipment(shipment);
            pv.setComment(rs.getString(4));

            logger.debug("importing patient visit: " + importCounts.visits
                + " patient/" + patient.getPnumber() + " study/"
                + study.getNameShort() + " dateProcessed/" + dateProcessed
                + " (" + count + "/" + numPatientVisits + ")");

            // now set corresponding patient visit info data
            for (String label : study.getStudyPvAttrLabels()) {
                if (label.equals("PBMC Count")) {
                    pv.setPvAttrValue(label, rs.getString(8));
                } else if (label.equals("Consent")) {
                    ArrayList<String> consents = new ArrayList<String>();
                    if (studyNameShort.equals("BBP")) {
                        if (rs.getInt(9) == 1) {
                            consents.add("Surveillance");
                        }
                        if (rs.getInt(10) == 1) {
                            consents.add("Genetic Predisposition");
                        }
                    } else if (studyNameShort.equals("KDCS")) {
                        if (rs.getInt(10) == 1) {
                            consents.add("Genetic");
                        }
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

    private static void removeAllSamples() throws Exception {
        logger.info("removing old samples...");

        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName());
        List<Sample> samples = appService.query(criteria);
        while (samples.size() > 0) {
            SampleWrapper sw = new SampleWrapper(appService, samples.get(0));
            sw.delete();
            samples.remove(0);
        }
    }

    private static void importCabinetSamples() throws Exception {
        logger.info("importing cabinet samples ...");

        String qryPart = "from cabinet, study_list, patient_visit, sample_list, patient "
            + "where cabinet.study_nr=study_list.study_nr "
            + "and patient_visit.study_nr=study_list.study_nr "
            + "and cabinet.visit_nr=patient_visit.visit_nr "
            + "and cabinet.patient_nr=patient_visit.patient_nr "
            + "and cabinet.sample_nr=sample_list.sample_nr "
            + "and patient_visit.patient_nr=patient.patient_nr "
            + "and cabinet.inventory_id is not NULL";

        Statement s = con.createStatement();
        s.execute("select count(*) " + qryPart);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numSamples = rs.getInt(1);

        s.execute("select patient_visit.visit_nr, "
            + "patient_visit.date_received, patient_visit.date_taken, "
            + "study_list.study_name_short,  sample_list.sample_name_short, "
            + "cabinet.*, patient.chr_nr " + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        ContainerWrapper[] cabinets = new ContainerWrapper[] { null, null, null };

        for (ContainerWrapper container : cbsrSite.getTopContainerCollection()) {
            String label = container.getLabel();
            String typeNameShort = container.getContainerType().getNameShort();
            if (label.equals("01") && typeNameShort.equals("Cabinet 4")) {
                cabinets[1] = container;
            } else if (label.equals("02") && typeNameShort.equals("Cabinet 4")) {
                cabinets[2] = container;
            }
        }

        if ((cabinets[1] == null) || (cabinets[2] == null)) {
            throw new Exception(
                "Cabinet containers not found in biobank database");
        }

        ContainerTypeWrapper cabinetType = cabinets[1].getContainerType();

        int cabinetNum;
        StudyWrapper study;
        ContainerWrapper drawer;
        ContainerWrapper bin;
        String studyNameShort;
        PatientWrapper patient;
        PatientVisitWrapper visit;
        String dateProcessedStr;
        Date dateProcessed;
        SampleTypeWrapper sampleType;
        int drawerNum;
        int binNum;
        String drawerName;
        RowColPos binPos;
        String sampleTypeNameShort;
        String inventoryId;
        int visitNr;
        BlowfishCipher cipher = new BlowfishCipher();

        int count = 0;
        while (rs.next()) {
            ++count;
            visitNr = rs.getInt(1);
            cabinetNum = rs.getInt(6);
            if ((cabinetNum != 1) && (cabinetNum != 2)) {
                logger.error("cabinet number " + cabinetNum
                    + " is invalid for visit number " + visitNr);
                continue;
            }

            inventoryId = rs.getString(13);
            if (inventoryId == null) {
                continue;
            }

            if (inventoryId.length() == 4) {
                inventoryId = "C" + inventoryId;
            }

            // make sure inventory id is unique
            List<SampleWrapper> samples = SampleWrapper.getSamplesInSite(
                appService, inventoryId, cbsrSite);
            if (samples.size() > 0) {
                String labels = "";
                for (SampleWrapper sample : samples) {
                    labels += sample.getPositionString(true, true) + ", ";
                }
                logger.error("a sample with inventory id " + inventoryId
                    + " already exisits at " + labels);
                continue;
            }

            drawerName = rs.getString(7);
            Integer rowCap = cabinets[cabinetNum].getRowCapacity();
            Integer colCap = cabinets[cabinetNum].getColCapacity();
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

            String patientNo = cipher.decode(rs.getBytes(18));
            patient = PatientWrapper.getPatientInSite(appService, patientNo,
                cbsrSite);

            studyNameShort = rs.getString(4);
            study = getStudyFromOldShortName(studyNameShort);
            if (!patient.getStudy().equals(study)) {
                throw new Exception("patient and study do not match: "
                    + patient.getPnumber() + ",  " + studyNameShort);
            }

            dateProcessedStr = rs.getString(2);
            dateProcessed = dateTimeFormatter.parse(dateProcessedStr);
            Calendar cal = new GregorianCalendar();
            cal.setTime(dateProcessed);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            dateProcessed = cal.getTime();

            visit = patient.getVisit(dateProcessed);

            if (visit == null) {
                logger.error("patient " + patientNo
                    + " visit not found for date: " + dateProcessed.toString());
                continue;
            }

            sampleTypeNameShort = rs.getString(5);
            drawer = cabinets[cabinetNum].getChild(pos.row, 0);
            bin = drawer.getChild(binNum, 0);

            if (bin == null) {
                logger.error("invalid bin number \"" + binNum
                    + "\" for visit cabinet " + cabinetNum + " and drawer "
                    + drawerName);
                continue;
            }

            sampleType = sampleTypeMap.get(sampleTypeNameShort);

            SampleWrapper sample = new SampleWrapper(appService);
            sample.setParent(bin);
            sample.setSampleType(sampleType);
            sample.setInventoryId(inventoryId);
            sample.setLinkDate(rs.getDate(14));
            sample.setQuantityUsed(rs.getDouble(15));
            sample.setPosition(binPos.row, 0);
            sample.setPatientVisit(visit);

            if (!bin.canHoldSample(sample)) {
                logger.error("bin " + bin.getLabel()
                    + " cannot hold sample of type" + sampleType.getName());
                continue;
            }

            logger.debug("importing Cabinet sample at position "
                + bin.getLabel() + binPosStr + " (" + count + "/" + numSamples
                + ")");
            ++importCounts.samples;
            sample.persist();
        }
    }

    private static void importFreezerSamples() throws Exception {
        logger.debug("importing freezer samples ...");

        ContainerWrapper[] freezers = new ContainerWrapper[] { null, null,
            null, null, null, null };

        for (ContainerWrapper container : cbsrSite.getTopContainerCollection()) {
            String label = container.getLabel();
            String typeNameShort = container.getContainerType().getNameShort();
            if (label.equals("01") && typeNameShort.equals("F3x10")) {
                freezers[1] = container;
            } else if (label.equals("03") && typeNameShort.equals("F5x9")) {
                freezers[3] = container;
            } else if (label.equals("04") && typeNameShort.equals("F3x6")) {
                freezers[4] = container;
            } else if (label.equals("05") && typeNameShort.equals("F6x12")) {
                freezers[5] = container;
            }
        }

        int freezerNum;
        ContainerWrapper freezer;
        ContainerTypeWrapper freezerType;
        ContainerWrapper hotel;
        ContainerWrapper pallet;
        PatientWrapper patient;
        PatientVisitWrapper visit;
        SampleTypeWrapper sampleType;
        RowColPos hotelPos;
        int palletNum;
        String dateProcessedStr;
        Date dateProcessed;
        String hotelLabel;
        String palletPos;
        String sampleTypeNameShort;
        String inventoryId;
        BlowfishCipher cipher = new BlowfishCipher();
        SampleWrapper sample;
        RowColPos pos;

        Statement s = con.createStatement();
        s.execute("select fnum, rack from freezer group by fnum, rack "
            + "order by fnum, rack");
        ResultSet rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        class FreezerHotelLabel {
            int freerzerId;
            String hotelLabel;

            FreezerHotelLabel(int fId, String hLabel) {
                this.freerzerId = fId;
                this.hotelLabel = hLabel;
            }
        }
        ;

        Set<FreezerHotelLabel> hotelLabels = new LinkedHashSet<FreezerHotelLabel>();
        while (rs.next()) {
            int fId = rs.getInt(1);
            if ((fId < freezers.length) && (freezers[fId] != null)) {
                hotelLabels.add(new FreezerHotelLabel(fId, rs.getString(2)));
            }
        }

        for (FreezerHotelLabel bbpdbHotelLabel : hotelLabels) {
            PreparedStatement ps = con
                .prepareStatement("select patient_visit.date_received, "
                    + "patient_visit.date_taken, study_list.study_name_short, "
                    + "sample_list.sample_name_short, freezer.*, patient.chr_nr  "
                    + "from freezer "
                    + "join frz_inv_id_cnt on freezer.inventory_id=frz_inv_id_cnt.inventory_id "
                    + "join study_list on freezer.study_nr=study_list.study_nr "
                    + "join patient on patient.patient_nr=freezer.patient_nr "
                    + "join patient_visit on patient_visit.study_nr=study_list.study_nr "
                    + "and freezer.visit_nr=patient_visit.visit_nr "
                    + "and freezer.patient_nr=patient_visit.patient_nr "
                    + "join sample_list on freezer.sample_nr=sample_list.sample_nr "
                    + "where freezer.fnum = ? and freezer.rack= ?"
                    + "order by freezer.box, freezer.cell");
            ps.setInt(1, bbpdbHotelLabel.freerzerId);
            ps.setString(2, bbpdbHotelLabel.hotelLabel);

            rs = ps.executeQuery();
            if (rs == null) {
                throw new Exception("Database query returned null");
            }

            int count = 0;
            while (rs.next()) {
                ++count;
                inventoryId = rs.getString(11);

                freezerNum = rs.getInt(5);
                hotelLabel = rs.getString(6);

                if (freezers[freezerNum] == null) {
                    logger.debug("Ignoring samples for freezer number "
                        + freezerNum);
                    continue;
                }

                freezer = freezers[freezerNum];
                freezerType = freezer.getContainerType();
                hotelPos = LabelingScheme.cbsrTwoCharToRowCol(hotelLabel,
                    freezer.getRowCapacity(), freezer.getColCapacity(),
                    freezerType.getName());
                hotel = freezer.getChild(hotelPos.row, hotelPos.col);

                if (hotel == null) {
                    logger.error("hotel not initialized: " + " freezer/"
                        + freezer.getLabel() + " hotel/" + hotelLabel);
                    continue;
                }

                palletNum = rs.getInt(7) - 1;
                palletPos = rs.getString(15);
                pallet = hotel.getChild(palletNum, 0);

                if (pallet == null) {
                    logger.error("pallet not initialized: " + " hotel/"
                        + hotel.getLabel() + " pallet/" + palletNum + 1);
                    continue;
                }

                // make sure inventory id is unique
                List<SampleWrapper> samples = SampleWrapper.getSamplesInSite(
                    appService, inventoryId, cbsrSite);
                if (samples.size() > 0) {
                    String labels = "";
                    for (SampleWrapper samp : samples) {
                        labels += samp.getPositionString(true, true) + ", ";
                    }
                    logger.error("a sample with inventory id " + inventoryId
                        + " already exisits at " + labels);
                    continue;
                }

                String patientNo = cipher.decode(rs.getBytes(17));
                patient = PatientWrapper.getPatientInSite(appService,
                    patientNo, cbsrSite);
                dateProcessedStr = rs.getString(1);
                dateProcessed = dateTimeFormatter.parse(dateProcessedStr);
                Calendar cal = new GregorianCalendar();
                cal.setTime(dateProcessed);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                dateProcessed = cal.getTime();

                visit = patient.getVisit(dateProcessed);

                if (visit == null) {
                    logger.error("patient visit not found for date: "
                        + dateProcessed.toString());
                    continue;
                }

                sampleTypeNameShort = rs.getString(4);
                if (sampleTypeNameShort.equals("RNA Later")) {
                    sampleTypeNameShort = "RNA Biopsy";
                }
                sampleType = sampleTypeMap.get(sampleTypeNameShort);

                pos = LabelingScheme.sbsToRowCol(palletPos);
                sample = pallet.getSample(pos.row, pos.col);
                if ((sample != null)
                    && sample.getSampleType().getNameShort().equals(
                        sampleTypeNameShort)
                    && sample.getInventoryId().equals(inventoryId)) {
                    logger.debug("freezer already contains sample "
                        + pallet.getLabel() + palletPos);
                    continue;
                }

                sample = new SampleWrapper(appService);
                sample.setParent(pallet);
                sample.setSampleType(sampleType);
                sample.setInventoryId(inventoryId);
                sample.setLinkDate(rs.getDate(12));
                sample.setQuantity(rs.getDouble(16));
                sample.setPosition(pos);
                sample.setPatientVisit(visit);

                if (!pallet.canHoldSample(sample)) {
                    logger.error("bin " + pallet.getLabel()
                        + " cannot hold sample of type" + sampleType.getName());
                    continue;
                }
                sample.persist();

                logger.debug("importing freezer sample " + pallet.getLabel()
                    + palletPos);
                ++importCounts.samples;
            }
        }
    }

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

    /*
     * Selects the study based on the first letter of a patient number
     */
    private static String getStudyShortNameFromPatientNr(String patientNr)
        throws Exception {
        String studyLetter = patientNr.substring(0, 1);
        String studyName = newStudyShortNameMap.get(studyLetter);
        if (studyName == null) {
            throw new Exception("no study name associated for patient number "
                + patientNr);
        }
        return studyName;
    }

    /*
     * Selects the clinic based on the first two letters of a patient number.
     */
    private static String getClinicNameFromPatientNr(String patientNr)
        throws Exception {
        String prefix = patientNr.substring(0, 2);
        String clinicName = patientNrToClinicMap.get(prefix);
        return clinicName;
    }
}

class ImportCounts {
    int patients;
    int shipments;
    int visits;
    int samples;
};