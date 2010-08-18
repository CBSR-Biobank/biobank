package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.client.config.cbsr.CbsrClinics;
import edu.ualberta.med.biobank.client.config.cbsr.CbsrContainerTypes;
import edu.ualberta.med.biobank.client.config.cbsr.CbsrContainers;
import edu.ualberta.med.biobank.client.config.cbsr.CbsrSite;
import edu.ualberta.med.biobank.client.config.cbsr.CbsrStudies;
import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ClinicShipment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
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

/*
 * a call to get a column from a result set can only be made once, otherwise the
 * driver generates an exception.
 */

public class Importer {

    private static final Logger logger = Logger.getLogger(Importer.class
        .getName());

    public static final String WAYBILL_DATE_FORMAT = "yyyyMMdd";

    private static SimpleDateFormat WAYBILL_DATE_FORMATTER = new SimpleDateFormat(
        WAYBILL_DATE_FORMAT);

    private static WritableApplicationService appService;

    private static Connection con;

    private static ArrayList<String> tables;

    public static final Map<String, String> newSampleTypeName;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("DNA(WBC)", "DNA (WBC)");
        aMap.put("PFP", "PF Plasma");
        aMap.put("Plasma LH", "Lith Hep Plasma");
        aMap.put("RNA Later", "RNA Biopsy");
        aMap.put("CDPA Plas", "CDPA Plasma");
        newSampleTypeName = Collections.unmodifiableMap(aMap);
    }

    private static final Map<String, String> newStudyShortNameMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("AHFEM", "AHFEM");
        aMap.put("BBP", "BBPSP");
        aMap.put("CCCS", "CCCS");
        aMap.put("CEGIIR", "CEGIIR");
        aMap.put("CHILD", "CHILD");
        aMap.put("ERCIN", "ERCIN");
        aMap.put("FIDS", "FIDS");
        aMap.put("HEART", "HEART");
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
        aMap.put("D", "HEART");
        aMap.put("E", "ERCIN");
        aMap.put("F", "FIDS");
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

    private static final Map<String, String> newClinicShortNameMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("CL1", "CL1-Foothills");
        aMap.put("CL2", "CL2-Children Hosp");
        aMap.put("ED1", "ED1-UofA");
        aMap.put("FM1", "FM1-King");
        aMap.put("GP1", "GP1-QE Hosp");
        aMap.put("HL1", "HL1-QE II");
        aMap.put("HL2", "HL2-IWK");
        aMap.put("HM1", "HM1-McMaster");
        aMap.put("KN1", "KN1-Cancer Ctr");
        aMap.put("LM1", "LM1-Lloyd Hosp");
        aMap.put("LN1", "LN1-St Joseph");
        aMap.put("MC1", "MC1-Moncton Hosp");
        aMap.put("MN1", "MN1-Ste-Justine");
        aMap.put("MN2", "MN2-Children Hosp");
        aMap.put("OL1", "OL1-Hingst");
        aMap.put("OT1", "OT1-Ottawa Hosp");
        aMap.put("OT2", "OT2-Children Hosp");
        aMap.put("QB1", "QB1-Enfant-Jesus");
        aMap.put("RD1", "RD1-Red Deer Hosp");
        aMap.put("SB1", "SB1-St John NB Hosp");
        aMap.put("SD1", "SD1-Sudbury Hosp");
        aMap.put("SF1", "SF1-Health NFLD");
        aMap.put("SP1", "SP1-St Therese Hosp");
        aMap.put("SS1", "SS1-Royal Hosp");
        aMap.put("TH1", "TH1-Regional Hosp");
        aMap.put("TR1", "TR1-St Mikes");
        aMap.put("VN1", "VN1-St Paul");
        aMap.put("VN2", "VN2-Childrens Hosp");
        aMap.put("WL1", "WL1-Westlock Hosp");
        aMap.put("WN1", "WN1-Cancer Care");

        newClinicShortNameMap = Collections.unmodifiableMap(aMap);
    };

    private static final Map<String, String> patientNrToClinicMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("AA", "ED1-UofA");
        aMap.put("AB", "CL1-Sunridge");
        aMap.put("AC", "VN1-St Paul");
        aMap.put("CC", "ED1-UofA");
        aMap.put("DA", "ED1-UofA");
        aMap.put("ER", "SF1-Health NFLD");
        aMap.put("EA", "ED1-UofA");
        aMap.put("FA", "ED1-UofA");
        aMap.put("GR", "ED1-UofA");
        aMap.put("HA", "ED1-UofA");
        aMap.put("KN", "KN1-Cancer Ctr");
        aMap.put("LC", "ED1-UofA");
        aMap.put("MP", "ED1-UofA");
        aMap.put("NH", "ED1-UofA");
        aMap.put("PA", "ED1-UofA");
        aMap.put("RV", "ED1-UofA");
        aMap.put("SA", "ED1-UofA");
        aMap.put("VA", "ED1-UofA");
        aMap.put("ZA", "ED1-UofA");
        aMap.put("ZB", "CL1-Sunridge");
        aMap.put("ZC", "VN1-St Paul");

        patientNrToClinicMap = Collections.unmodifiableMap(aMap);
    };

    private static SiteWrapper cbsrSite = null;

    private static Map<String, ClinicWrapper> clinicsMap = null;

    private static Map<String, StudyWrapper> studiesMap = null;

    private static Map<String, ContainerTypeWrapper> containerTypesMap = null;

    private static Map<String, ContainerWrapper> topContainersMap = null;

    private static Map<String, ShippingMethodWrapper> shippingCompanyMap = null;

    private static Map<String, SampleTypeWrapper> sampleTypeMap;

    private static Map<StudyWrapper, Map<SampleTypeWrapper, SampleStorageWrapper>> sampleStorageMap;

    private static ImportCounts importCounts;

    private static Configuration configuration;

    private static SourceVesselWrapper importSourceVessel;

    private static Date defaultDateShipped;

    public static void main(String[] args) {
        try {
            configuration = new Configuration("config.properties");
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
                    "frz_99_inv_id" };

                for (String table : reqdTables) {
                    if (!tableExists(table))
                        throw new Exception("Table " + table + " not found");
                }

                if (configuration.decodePatientNumbers()) {
                    decodePatientNumbers();
                }

                appService = ServiceConnection.getAppService(
                    "http://" + System.getProperty("server", "localhost:8080")
                        + "/biobank2", "testuser", "test");

                cbsrSite = getCbsrSite();

                if (cbsrSite == null) {
                    createCbsrConfiguration();
                }

                doImport();

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
        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }

    private static void createCbsrConfiguration() throws Exception {
        CbsrSite.deleteConfiguration(appService);
        logger.info("creating CBSR site...");
        cbsrSite = CbsrSite.addSite(appService);

        logger.info("creating clinics...");
        new CbsrClinics(cbsrSite);

        logger.info("creating studies... ");
        new CbsrStudies(cbsrSite);

        logger.info("creating container types...");
        new CbsrContainerTypes(cbsrSite);

        logger.info("creating containers...");
        new CbsrContainers(cbsrSite);
    }

    private static void doImport() throws Exception {
        initClinicsMap();
        initStudiesMap();
        initContainerTypesMap();
        initTopContainersMap();
        initShipmentCompanyMap();

        checkSourceVessels();
        checkShippingCompanies();
        checkSampleTypes();

        if (configuration.checkContainerConfig()) {
            checkContainerConfiguration();
        }

        defaultDateShipped = getDateFromStr("1900-01-01");

        if (configuration.importPatients()) {
            importPatients();
        } else if (getPatientCount() == 0) {
            logger
                .error("cannot run importer without patients in biobank2 database");
            System.exit(2);
        } else {
            logger.info("not configured for importing patients");
        }

        if (configuration.importShipments()) {
            removeAllShipments();
            importShipments();
        } else if (getShipmentCount() == 0) {
            throw new Exception(
                "cannot run importer without shipments in biobank2 database");
        } else {
            logger.info("not configured for importing shipments");
        }

        if (configuration.importPatientVisits()) {
            importPatientVisits();
        } else if (getPatientVisitCount() == 0) {
            throw new Exception(
                "cannot run importer without patient visits in biobank2 database");
        } else {
            logger.info("not configured for importing patient visits");
        }

        if (configuration.importScanLinked()) {
            ScanLinkdedImporter scanLinkdedImporter = new ScanLinkdedImporter(
                appService, con, cbsrSite);
            importCounts.samples += scanLinkdedImporter.getSamplesImported();
        } else {
            logger.info("not configured for importing scanned linked aliquots");
        }

        if (configuration.importCabinets() || configuration.importFreezers()) {
            removeAllSamples();
            importFreezerSamples();
            importCabinetSamples();

        } else {
            logger
                .info("not configured for importing aliquots from containers");
        }
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
            clinicsMap.put(clinic.getNameShort(), clinic);
        }
    }

    private static void initStudiesMap() {
        studiesMap = new HashMap<String, StudyWrapper>();
        sampleStorageMap = new HashMap<StudyWrapper, Map<SampleTypeWrapper, SampleStorageWrapper>>();
        for (StudyWrapper study : cbsrSite.getStudyCollection()) {
            studiesMap.put(study.getNameShort(), study);

            sampleStorageMap.put(study,
                new HashMap<SampleTypeWrapper, SampleStorageWrapper>());

            for (SampleStorageWrapper ss : study.getSampleStorageCollection()) {
                sampleStorageMap.get(study).put(ss.getSampleType(), ss);
            }
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

    private static void initShipmentCompanyMap() throws Exception {
        shippingCompanyMap = new HashMap<String, ShippingMethodWrapper>();
        for (ShippingMethodWrapper company : ShippingMethodWrapper
            .getShippingMethods(appService)) {
            shippingCompanyMap.put(company.getName(), company);
        }
    }

    public static SampleTypeWrapper getSampleType(String nameShort) {
        String newName = newSampleTypeName.get(nameShort);
        if (newName != null) {
            nameShort = newName;
        }
        return sampleTypeMap.get(nameShort);
    }

    public static void checkSampleTypes() throws Exception {
        Statement s = con.createStatement();
        s.execute("select sample_name, sample_name_short from sample_list");
        ResultSet rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }
        Map<String, String> bbpdbSampleTypeMap = new HashMap<String, String>();
        while (rs.next()) {
            bbpdbSampleTypeMap.put(rs.getString(2), rs.getString(1));
        }

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getAllSampleTypes(appService, true);
        if ((allSampleTypes == null) || (allSampleTypes.size() == 0)) {
            throw new Exception("no global sample types found in the database");
        }

        sampleTypeMap = new HashMap<String, SampleTypeWrapper>();
        for (SampleTypeWrapper sampleType : allSampleTypes) {
            sampleTypeMap.put(sampleType.getNameShort(), sampleType);
        }

        // report missing sample types
        //
        // "CDPA Plas" is not being used in BioBank2
        int missing = 0;
        for (String nameShort : bbpdbSampleTypeMap.keySet()) {
            if ((sampleTypeMap.get(nameShort) != null)
                || (newSampleTypeName.get(nameShort) != null)
                || nameShort.equals("CDPA Plas"))
                continue;

            logger.error("missing sample type: \""
                + bbpdbSampleTypeMap.get(nameShort) + "\" \"" + nameShort
                + "\"");
            ++missing;
        }

        // if (missing > 0) {
        // throw new Exception("There are missing sample types. "
        // + "Container sample types require adjustments.");
        // }
    }

    private static void checkContainerConfiguration() throws Exception {
        if (!checkCabinetConfiguration() || !checkFreezerConfiguration()) {
            logger.error("container configuration failed");
            System.exit(1);
        }
    }

    private static void checkShippingCompanies() {
        if (shippingCompanyMap.get("unknown") == null) {
            logger
                .error("shipping company \"unknown\" missing - configuration failed");
            System.exit(1);
        }
    }

    private static void checkSourceVessels() throws ApplicationException {
        for (SourceVesselWrapper sourceVessel : SourceVesselWrapper
            .getAllSourceVessels(appService)) {
            if (sourceVessel.getName().equals("Unknown / import")) {
                importSourceVessel = sourceVessel;
            }
        }

        if (importSourceVessel == null) {
            logger
                .error("\"import\" source vessel is missing in configuration");
            System.exit(1);
        }
    }

    private static boolean checkCabinetConfiguration() throws Exception {
        logger.info("checking cabinet configuration");
        Statement s = con.createStatement();
        s.execute("select cnum, drawer, bin from cabinet "
            + "where inventory_id is not null "
            + "group by cnum, drawer, bin order by cnum, drawer, bin");
        ResultSet rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        int errorCount = 0;
        while (rs.next()) {
            int cabinetNr = rs.getInt(1);
            String drawerNr = rs.getString(2);
            int binNr = rs.getInt(3);
            String label = String.format(
                "%02d%s%02d",
                new Object[] { Integer.valueOf(cabinetNr), drawerNr,
                    Integer.valueOf(binNr) });
            List<ContainerWrapper> binList = ContainerWrapper
                .getContainersInSite(appService, cbsrSite, label);
            if (binList.size() == 0) {
                logger.error("bin " + label
                    + " not present in biobank configuration");
                ++errorCount;
            } else if ((cabinetNr == 1) || (cabinetNr == 2)) {
                boolean binFound = false;
                for (ContainerWrapper bin : binList) {
                    if (bin.getContainerType().getName().contains("Bin")) {
                        binFound = true;
                    }
                }
                if (!binFound) {
                    logger.error("bin " + label
                        + " not present in biobank configuration");
                    ++errorCount;
                }
            } else if (binList.size() > 1) {
                logger.error("more than 1 pallet with label " + label);
                ++errorCount;
            }
        }
        return (errorCount == 0);
    }

    private static boolean checkFreezerConfiguration() throws Exception {
        logger.info("checking freezer configuration");
        Statement s = con.createStatement();
        s.execute("select fnum, rack, box from freezer "
            + "where inventory_id is not null group by fnum, rack, box "
            + "order by fnum, rack, box");
        ResultSet rs = s.getResultSet();
        if (rs == null)
            throw new Exception("Database query returned null");

        int errorCount = 0;
        while (rs.next()) {
            int freezerNr = rs.getInt(1);
            String hotelNr = rs.getString(2);
            int palletNr = rs.getInt(3);
            if ((freezerNr != 2 || !hotelNr.startsWith("C")) && freezerNr != 4) {
                String label;
                if (freezerNr == 99)
                    label = String.format("SS%s%02d", new Object[] { hotelNr,
                        Integer.valueOf(palletNr) });
                else
                    label = String.format("%02d%s%02d",
                        new Object[] { Integer.valueOf(freezerNr), hotelNr,
                            Integer.valueOf(palletNr) });
                List<ContainerWrapper> palletList = ContainerWrapper
                    .getContainersInSite(appService, cbsrSite, label);
                if (palletList.size() == 0) {
                    logger.error("pallet " + label
                        + " not present in biobank configuration");
                    ++errorCount;
                } else if ((freezerNr == 1) || (freezerNr == 2)) {
                    boolean palletFound = false;
                    for (ContainerWrapper bin : palletList) {
                        if (bin.getContainerType().getName().contains("Box")
                            || bin.getContainerType().getName()
                                .contains("Pallet")) {
                            palletFound = true;
                        }
                    }
                    if (!palletFound) {
                        logger.error("pallet " + label
                            + " not present in biobank configuration");
                        ++errorCount;
                    }
                } else if (palletList.size() > 1) {
                    logger.error("more than 1 pallet with label " + label);
                    ++errorCount;
                }
            }
        }
        return (errorCount == 0);
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

    public static StudyWrapper getStudyFromOldShortName(String shortName)
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

    public static String getStudyNameShort(String patientNr,
        String defaultStudyNameShort) throws Exception {
        String studyNameShort;
        if (patientNr.length() == 6) {
            if (patientNr.substring(0, 2).equals("CE")) {
                studyNameShort = "CEGIIR";
            } else {
                studyNameShort = getStudyShortNameFromPatientNr(patientNr);
            }
        } else {
            studyNameShort = defaultStudyNameShort;
        }
        return studyNameShort;
    }

    private static ClinicWrapper getClinic(String patientNr,
        String defaultClinicName) throws Exception {
        String clinicName;
        if (patientNr.length() == 6) {
            if (patientNr.substring(0, 2).equals("CE")) {
                clinicName = "ED1-UofA";
            } else {
                clinicName = getClinicNameFromPatientNr(patientNr);
            }

            if (clinicName == null) {
                logger.error("no clinic for patient " + patientNr);
                return null;
            }
        } else {
            clinicName = newClinicShortNameMap.get(defaultClinicName
                .toUpperCase());
        }

        ClinicWrapper clinic = clinicsMap.get(clinicName);
        if (clinic == null) {
            logger.error("no clinic \"" + clinicName + "\" ("
                + defaultClinicName + ") for patient " + patientNr);
        }
        return clinic;
    }

    private static void decodePatientNumbers() throws Exception {
        logger.info("decoding patient numbers ...");

        BlowfishCipher cipher = new BlowfishCipher();
        Statement s = con.createStatement();
        s.execute("select * from patient");
        ResultSet rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        while (rs.next()) {
            String patientNr = cipher.decode(rs.getBytes(2));
            String decChrNr = rs.getString(5);
            if (decChrNr == null) {
                PreparedStatement ps = con
                    .prepareStatement("update patient set dec_chr_nr = ? where patient_nr = ?");
                ps.setString(1, patientNr);
                ps.setInt(2, rs.getInt(1));
                ps.executeUpdate();
            }
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
            String patientNr = cipher.decode(rs.getBytes(2));

            // update BBPDB with the decoded CHR number
            String decChrNr = rs.getString(5);
            if (decChrNr == null) {
                PreparedStatement ps = con
                    .prepareStatement("update patient set dec_chr_nr = ? where patient_nr = ?");
                ps.setString(1, patientNr);
                ps.setInt(2, rs.getInt(1));
                ps.executeUpdate();
            }

            studyNameShort = getStudyNameShort(patientNr, rs.getString(6));

            if (studyNameShort == null) {
                logger.error("no study for patient " + patientNr);
                continue;
            }

            study = getStudyFromOldShortName(studyNameShort);

            if (study == null) {
                logger.debug("ERROR: study with short name \"" + studyNameShort
                    + "\" not found, patient id: " + rs.getInt(1));
                continue;
            }

            logger.debug("importing patient number " + patientNr + " (" + count
                + "/" + numPatients + ")");
            patient = new PatientWrapper(appService);
            patient.setPnumber(patientNr);
            patient.setStudy(study);
            patient.persist();
            ++importCounts.patients;
            ++count;
        }
    }

    private static void removeAllShipments() throws Exception {
        logger.info("removing old shipments ...");

        HQLCriteria criteria = new HQLCriteria("from "
            + ClinicShipment.class.getName());
        List<ClinicShipment> shipments = appService.query(criteria);
        for (ClinicShipment shipment : shipments) {
            ClinicShipmentWrapper s = new ClinicShipmentWrapper(appService,
                shipment);
            s.delete();
        }
    }

    private static void importShipments() throws Exception {
        String studyNameShort;
        StudyWrapper study;
        ClinicWrapper clinic;
        PatientWrapper patient;
        String dateReceivedStr;
        Date dateReceived;
        ClinicShipmentWrapper shipment;
        BlowfishCipher cipher = new BlowfishCipher();

        ShippingMethodWrapper unknownShippingCompany = shippingCompanyMap
            .get("unknown");

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
            String patientNr = cipher.decode(rs.getBytes(1));
            studyNameShort = getStudyNameShort(patientNr, rs.getString(2));
            clinic = getClinic(patientNr, rs.getString(3));

            if (studyNameShort == null) {
                logger.error("no study for patient " + patientNr);
                ++count;
                continue;
            }

            study = getStudyFromOldShortName(studyNameShort);
            if (clinic == null) {
                ++count;
                continue;
            }

            // make sure the clinic and study are linked via a contact
            if (!study.hasClinic(clinic.getNameShort())) {
                logger.error("study " + study.getNameShort() + " for patient "
                    + patientNr + " is not linked to clinic "
                    + clinic.getName() + " via a contact");
                ++count;
                continue;
            }

            dateReceivedStr = rs.getString(4);
            dateReceived = getDateFromStr(dateReceivedStr);

            patient = study.getPatient(patientNr);
            // make sure patient is in the study
            if (patient == null) {
                logger.error("patient not found in study: " + patientNr + ",  "
                    + studyNameShort);
                continue;
            }

            logger.trace("getting shipment");
            shipment = clinic.getShipment(dateReceived);
            if (shipment == null) {
                ++importCounts.shipments;
                logger.debug("new shipment: " + importCounts.shipments
                    + " patient/" + patient.getPnumber() + " clinic/"
                    + clinic.getName() + " shipment/"
                    + DateFormatter.formatAsDateTime(dateReceived) + " ("
                    + count + "/" + numShipments + ")");

                shipment = new ClinicShipmentWrapper(appService);
                shipment.setClinic(clinic);

                if (clinic.getSendsShipments()) {
                    shipment.setWaybill(String.format("W-CBSR-%s-%s",
                        clinic.getNameShort(), getWaybillDate(dateReceived)));
                }
                shipment.setDateReceived(dateReceived);
                shipment.setDateShipped(defaultDateShipped);
                shipment.addPatients(Arrays.asList(patient));
                shipment.setShippingMethod(unknownShippingCompany);
                shipment.persist();
            } else if (!shipment.hasPatient(patientNr)) {
                logger.debug("adding to shipment: patient/"
                    + patient.getPnumber() + " clinic/" + clinic.getName()
                    + " shipment/"
                    + DateFormatter.formatAsDateTime(dateReceived) + " ("
                    + count + "/" + numShipments + ")");
                shipment.addPatients(Arrays.asList(patient));
                shipment.persist();
            } else {
                logger.debug("already in database: patient/"
                    + patient.getPnumber() + " clinic/" + clinic.getName()
                    + " shipment/"
                    + DateFormatter.formatAsDateTime(dateReceived) + " ("
                    + count + "/" + numShipments + ")");
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
        ClinicWrapper clinic;
        String dateProcessedStr;
        Date dateProcessed;
        PatientWrapper patient;
        ClinicShipmentWrapper shipment;
        PatientVisitWrapper pv;

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
            + "patient.dec_chr_nr " + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        int count = 1;
        while (rs.next()) {
            String patientNr = rs.getString(22);
            studyNameShort = getStudyNameShort(patientNr, rs.getString(21));
            clinic = getClinic(patientNr, rs.getString(3));

            if (studyNameShort == null) {
                logger.error("no study for patient " + patientNr);
                ++count;
                continue;
            }

            study = getStudyFromOldShortName(studyNameShort);

            if (clinic == null) {
                ++count;
                continue;
            }

            dateProcessedStr = rs.getString(6);
            dateProcessed = getDateFromStr(dateProcessedStr);

            patient = study.getPatient(patientNr);
            // make sure patient is in the study
            if (patient == null) {
                logger.error("patient not found in study: " + patientNr + ",  "
                    + studyNameShort);
                continue;
            }
            patient.reload();

            shipment = clinic.getShipment(dateProcessed, patientNr);

            // check for shipment
            if (shipment == null) {
                logger.error("found 0 shipments for patientNo/" + patientNr
                    + " studyName/" + study.getNameShort() + " clinicName/"
                    + clinic.getNameShort() + " dateReceived/"
                    + DateFormatter.formatAsDateTime(dateProcessed));
                ++count;
                continue;
            }

            ++importCounts.visits;
            pv = new PatientVisitWrapper(appService);
            pv.setDateProcessed(dateProcessed);
            pv.setPatient(patient);
            pv.setShipment(shipment);
            pv.setDateDrawn(getDateFromStr(rs.getString(5)));

            PvSourceVesselWrapper sourceVessel = new PvSourceVesselWrapper(
                appService);
            sourceVessel.setSourceVessel(importSourceVessel);
            sourceVessel.setQuantity(0);
            sourceVessel.setPatientVisit(pv);

            pv.addPvSourceVessels(Arrays.asList(sourceVessel));

            logger.debug("importing patient visit: " + importCounts.visits
                + " patient/" + patient.getPnumber() + " study/"
                + study.getNameShort() + " dateProcessed/"
                + DateFormatter.formatAsDate(dateProcessed) + " (" + count
                + "/" + numPatientVisits + ")");

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
                } else if (label.equals("Phlebotomist")) {
                    if (studyNameShort.equals("BBP")) {
                        pv.setPvAttrValue(label, rs.getString(4));
                    }
                }
            }

            pv.persist();
            pv.reload();

            // update BBPDB patient_visit table with the biobank2 visit id
            PreparedStatement ps = con
                .prepareStatement("update patient_visit set bb2_pv_id = ? where visit_nr = ?");
            ps.setInt(1, pv.getId());
            ps.setInt(2, rs.getInt(1));
            ps.executeUpdate();

            ++count;
        }
    }

    private static void removeAllSamples() throws Exception {
        logger.info("removing old samples...");

        HQLCriteria criteria = new HQLCriteria("from "
            + Aliquot.class.getName());
        List<Aliquot> samples = appService.query(criteria);
        while (samples.size() > 0) {
            AliquotWrapper sw = new AliquotWrapper(appService, samples.get(0));
            sw.delete();
            samples.remove(0);
        }
    }

    private static void importCabinetSamples() throws Exception {
        Map<Integer, ContainerWrapper> cabinetsMap = new HashMap<Integer, ContainerWrapper>();

        for (ContainerWrapper container : cbsrSite.getTopContainerCollection()) {
            String label = container.getLabel();
            String typeNameShort = container.getContainerType().getNameShort();
            if (label.equals("01") && typeNameShort.equals("Cabinet 4")) {
                cabinetsMap.put(1, container);
            } else if (label.equals("02") && typeNameShort.equals("Cabinet 4")) {
                cabinetsMap.put(2, container);
            }
        }
        String dateProcessedStr;
        String dateTakenStr;
        String binPos;
        String sampleTypeNameShort;
        String inventoryId;
        String studyNameShort;
        int binNr;
        String patientNr;
        String linkDateStr;
        Double volume;
        int visitId;
        PreparedStatement ps;
        ResultSet rs;

        for (Integer cabinetNum : cabinetsMap.keySet()) {
            if (!configuration.importCabinet(cabinetNum.intValue())) {
                logger.info("not configured to import cabinet " + cabinetNum);
                continue;
            }

            if (cabinetsMap.get(cabinetNum) == null) {
                throw new Exception("Cabinet " + cabinetNum
                    + " container not found in biobank database");
            }

            ContainerWrapper cabinet = cabinetsMap.get(cabinetNum);

            for (ContainerWrapper drawer : cabinet.getChildren().values()) {
                if (!configuration.importCabinetDrawer(drawer.getLabel())) {
                    logger.debug("not configured to import drawer "
                        + drawer.getLabel());
                    continue;
                }

                String drawerLabel = drawer.getLabel();
                int len = drawerLabel.length();
                logger.info("importing samples from drawer " + drawerLabel);

                ps = con
                    .prepareStatement("select patient_visit.visit_nr, "
                        + "patient_visit.date_received, patient_visit.date_taken, "
                        + "study_list.study_name_short,  sample_list.sample_name_short, "
                        + "cabinet.*, patient.dec_chr_nr, patient_visit.bb2_pv_id "
                        + "from cabinet "
                        + "join patient_visit on patient_visit.visit_nr=cabinet.visit_nr "
                        + "join patient on patient.patient_nr=patient_visit.patient_nr "
                        + "join study_list on study_list.study_nr=patient_visit.study_nr "
                        + "join sample_list on sample_list.sample_nr=cabinet.sample_nr "
                        + "where cabinet.inventory_id is not NULL and cnum = ? and drawer = ?");
                ps.setInt(1, cabinetNum);
                ps.setString(2, drawerLabel.substring(len - 2));

                rs = ps.executeQuery();
                if (rs == null) {
                    throw new Exception("Database query returned null");
                }

                while (rs.next()) {
                    studyNameShort = rs.getString(4);
                    patientNr = rs.getString(18);
                    dateProcessedStr = rs.getString(2);
                    dateTakenStr = rs.getString(3);
                    binNr = rs.getInt(8);
                    binPos = rs.getString(9);
                    sampleTypeNameShort = rs.getString(5);
                    linkDateStr = rs.getString(14);
                    volume = null;
                    visitId = rs.getInt(19);

                    inventoryId = rs.getString(13);
                    if (inventoryId.length() == 4) {
                        inventoryId = "C" + inventoryId;
                    }

                    Importer.importSample(cbsrSite, studyNameShort, patientNr,
                        visitId, dateProcessedStr, dateTakenStr, drawer, binNr,
                        binPos, inventoryId, sampleTypeNameShort, linkDateStr,
                        volume);

                    logger.debug(String.format(
                        "importing cabinet aliquot %s%02d%s",
                        drawer.getLabel(), binNr, binPos));
                }
            }
        }
    }

    private static void importFreezerSamples() throws Exception {
        Map<Integer, ContainerWrapper> freezersMap = new HashMap<Integer, ContainerWrapper>();

        for (ContainerWrapper container : cbsrSite.getTopContainerCollection()) {
            String label = container.getLabel();
            String typeNameShort = container.getContainerType().getNameShort();
            if (label.equals("01") && typeNameShort.equals("F3x10")) {
                freezersMap.put(1, container);
            } else if (label.equals("02") && typeNameShort.equals("F4x12")) {
                freezersMap.put(2, container);
            } else if (label.equals("03") && typeNameShort.equals("F5x9")) {
                freezersMap.put(3, container);
            } else if (label.equals("05") && typeNameShort.equals("F4x17")) {
                freezersMap.put(5, container);
            } else if (label.equals("SS") && typeNameShort.equals("F4x6")) {
                freezersMap.put(99, container);
            }
        }

        Statement s = con.createStatement();
        s.execute("select fnum from freezer group by fnum order by fnum");
        ResultSet rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        while (rs.next()) {
            int freezerNum = rs.getInt(1);

            if (!configuration.importFreezer(freezerNum)) {
                logger.info("not configured to import freezer " + freezerNum);
                continue;
            }

            if (freezersMap.get(freezerNum) == null)
                continue;

            logger.info("importing samples from freezer " + freezerNum);

            if (freezerNum == 99) {
                new Freezer99Importer(appService, con, configuration, cbsrSite,
                    freezersMap.get(freezerNum), freezerNum);
            } else if (freezerNum == 2) {
                new Freezer02Importer(appService, con, configuration, cbsrSite,
                    freezersMap.get(freezerNum), freezerNum);
            } else {
                new FreezerImporter(appService, con, configuration, cbsrSite,
                    freezersMap.get(freezerNum), freezerNum);
            }
        }
    }

    public static void importSample(SiteWrapper site, String studyNameShort,
        String patientNr, int visitId, String dateProcessedStr,
        String dateTakenStr, ContainerWrapper parentContainer, int containerNr,
        String containerPos, String inventoryId, String sampleTypeNameShort,
        String linkDateStr, Double quantity) throws Exception {

        if (containerNr > parentContainer.getRowCapacity()) {
            logger.error("pallet number is invalid: " + " hotel/"
                + parentContainer.getLabel() + " pallet/" + containerNr);
            return;
        }

        ContainerWrapper container = parentContainer.getChild(containerNr - 1,
            0);

        if (container == null) {
            logger.error("container not initialized: parent/"
                + parentContainer.getLabel() + " container/" + containerNr);
            return;
        }

        String aliquotLabel = String.format("%s%02d%s",
            parentContainer.getLabel(), containerNr, containerPos);

        RowColPos pos = container.getContainerType()
            .getRowColFromPositionString(containerPos);
        AliquotWrapper aliquot = container.getAliquot(pos.row, pos.col);
        if (aliquot != null) {
            logger.debug("container already contains aliquot at "
                + aliquotLabel);
            return;
        }

        aliquot = createAliquot(site, studyNameShort, patientNr, visitId,
            dateProcessedStr, dateTakenStr, inventoryId, sampleTypeNameShort,
            linkDateStr);

        if (!container.canHoldAliquot(aliquot)) {
            logger.error("container " + container.getLabel()
                + " cannot hold aliquot with a sample of type "
                + sampleTypeNameShort);
            return;
        }
        aliquot.persist();
        ++importCounts.samples;
    }

    public static AliquotWrapper createAliquot(SiteWrapper site,
        String studyNameShort, String patientNr, int visitId,
        String dateProcessedStr, String dateTakenStr, String inventoryId,
        String sampleTypeNameShort, String linkDateStr) throws Exception {

        // make sure inventory id is unique
        if (!Importer.inventoryIdUnique(inventoryId)) {
            return null;
        }

        PatientWrapper patient = PatientWrapper.getPatient(appService,
            patientNr);

        if (patient == null) {
            logger.error("no patient with number " + patientNr);
            return null;
        }

        studyNameShort = Importer.getStudyNameShort(patientNr, studyNameShort);

        if (studyNameShort == null) {
            logger.error("no study for patient " + patientNr);
            return null;
        }

        StudyWrapper study = Importer.getStudyFromOldShortName(studyNameShort);
        if (!patient.getStudy().equals(study)) {
            logger.error("patient and study do not match: "
                + patient.getPnumber() + ",  " + studyNameShort);
            return null;
        }

        study.getSampleStorageCollection();

        Date dateProcessed = Importer.getDateFromStr(dateProcessedStr);
        Date dateTaken = Importer.getDateFromStr(dateTakenStr);

        List<PatientVisitWrapper> visits = patient.getVisits(dateProcessed,
            dateTaken);
        if (visits.size() == 0) {
            logger.error("patient/" + patientNr + " inventory_id/"
                + inventoryId + " visit not found for dateProcessed/"
                + DateFormatter.formatAsDate(dateProcessed) + " dateTaken/"
                + DateFormatter.formatAsDate(dateTaken));
            return null;
        }

        PatientVisitWrapper visit = null;

        for (PatientVisitWrapper pv : visits) {
            if (pv.getId() == visitId) {
                visit = pv;
            }
        }

        if (visit == null) {
            logger.error("visit not found: patient/" + patientNr + " visitId/"
                + visitId + "inventory_id/" + inventoryId + " dateProcessed/"
                + DateFormatter.formatAsDate(dateProcessed) + " dateTaken/"
                + DateFormatter.formatAsDate(dateTaken));
            return null;
        }

        SampleTypeWrapper sampleType = getSampleType(sampleTypeNameShort);

        if (sampleType == null) {
            logger.error("inventory_id/" + inventoryId
                + " sample type not in database: " + sampleTypeNameShort);
            return null;
        }

        SampleStorageWrapper ss = Importer.getSampleStorage(study, sampleType);
        if (ss == null) {
            logger.error("study \"" + study.getNameShort()
                + "\" has no sample storage for sample type \""
                + sampleType.getName() + "\"");
            return null;
        }

        AliquotWrapper aliquot = new AliquotWrapper(appService);
        aliquot.setSampleType(sampleType);
        aliquot.setInventoryId(inventoryId);
        aliquot.setLinkDate(Importer.getDateFromStr(linkDateStr));
        aliquot.setPatientVisit(visit);
        aliquot.setQuantity(ss.getVolume());
        aliquot.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        return aliquot;
    }

    public static boolean inventoryIdUnique(String inventoryId)
        throws Exception {
        List<AliquotWrapper> aliquots = AliquotWrapper.getAliquotsInSite(
            appService, inventoryId, cbsrSite);
        if (aliquots.size() == 0)
            return true;

        String labels = "";
        for (AliquotWrapper aliquot : aliquots) {
            String pos = aliquot.getPositionString(true, true);
            if (pos != null) {
                labels += pos + ", ";
            }
        }
        if (labels.length() > 0) {
            logger.error("an aliquot with inventory id " + inventoryId
                + " already exists at " + labels);
        }
        logger.error("an aliquot with inventory id " + inventoryId
            + " already exists for patient "
            + aliquots.get(0).getPatientVisit().getPatient().getPnumber());
        return false;
    }

    private static Long getPatientCount() throws Exception {
        HQLCriteria c = new HQLCriteria("select count(*) from "
            + Patient.class.getName());
        List<Long> result = appService.query(c);
        return result.get(0);
    }

    private static Long getShipmentCount() throws Exception {
        HQLCriteria c = new HQLCriteria("select count(*) from "
            + ClinicShipment.class.getName());
        List<Long> result = appService.query(c);
        return result.get(0);
    }

    private static Long getPatientVisitCount() throws Exception {
        HQLCriteria c = new HQLCriteria("select count(*) from "
            + PatientVisit.class.getName());
        List<Long> result = appService.query(c);
        return result.get(0);
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
                .getAliquotPositionCollection().size()));
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

    public static Date getDateFromStr(String str) throws ParseException {
        Date date = DateFormatter.parseToDateTime(str);
        if (date == null) {
            date = DateFormatter.parseToDate(str);
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static String getWaybillDate(Date dateReceived) {
        return WAYBILL_DATE_FORMATTER.format(dateReceived);
    }

    public static SampleStorageWrapper getSampleStorage(StudyWrapper study,
        SampleTypeWrapper sampleType) {
        Map<SampleTypeWrapper, SampleStorageWrapper> innerMap = sampleStorageMap
            .get(study);
        if (innerMap == null) {
            return null;
        }
        return innerMap.get(sampleType);
    }
}

class ImportCounts {
    int patients;
    int shipments;
    int visits;
    int samples;
}
