
package edu.ualberta.med.biobank.importer;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/*
 *  need to remove the password on MS Access side.
 * a call to get a column from a result set can only be made once, otherwise the
 * driver generates an exception.
 */

public class Importer {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
        DATE_TIME_FORMAT);

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

    public static void main(String [] args) throws Exception {
        tables = new ArrayList<String>();

        try {
            appService = ServiceConnection.getAppService("https://"
                + System.getProperty("server", "localhost:8443") + "/biobank2",
                "testuser", "test");

            // checkCabinet();
            // checkFreezer();
            // System.exit(0);

            CbsrSite.deleteConfiguration(appService);
            System.out.println("creating CBSR site...");
            SiteWrapper cbsrSite = CbsrSite.addSite(appService);

            System.out.println("creating clinics...");
            CbsrClinics.createClinics(cbsrSite);

            System.out.println("creating studies... ");
            CbsrStudies.createStudies(cbsrSite);

            System.out.println("creating container types...");
            CbsrContainerTypes.createContainerTypes(cbsrSite);

            System.out.println("creating containers...");
            CbsrContainers.createContainers(cbsrSite);

            con = getMysqlConnection();

            getTables();
            if (tables.size() == 0) {
                throw new Exception("No tables found in database");
            }

            String [] reqdTables = {
                "clinics", "study_list", "patient", "patient_visit", "cabinet",
                "freezer", "sample_list" };

            for (String table : reqdTables) {
                if (!tableExists(table)) throw new Exception("Table " + table
                    + " not found");
            }

            importPatients();
            importShipments();
            importPatientVisits();

            importCabinetSamples();
            // importFreezerSamples();

            System.out.println("importing complete.");

        }
        catch (Exception e) {
            e.printStackTrace();
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
            new String [] { "TABLE" });
        while (res.next()) {
            tables.add(res.getString("TABLE_NAME"));

            // System.out.println("   " + res.getString("TABLE_CAT") + ", "
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
        try {
            return CbsrStudies.getStudy(newShortName);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static boolean tableExists(String name) {
        for (int i = 0; i < tables.size(); ++i) {
            if (tables.get(i).equals(name)) return true;
        }
        return false;
    }

    private static void importPatients() throws Exception {
        BlowfishCipher cipher = new BlowfishCipher();
        StudyWrapper study;
        PatientWrapper patient;
        System.out.println("importing patients ...");

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
                System.out.println("ERROR: study with short name \""
                    + studyNameShort + "\" not found. Patient id: "
                    + rs.getInt(1));
                continue;
            }

            System.out.println("importing patient number " + patientNo + " ("
                + count + "/" + numPatients + ")");
            patient = new PatientWrapper(appService);
            patient.setNumber(patientNo);
            patient.setStudy(study);
            patient.persist();
            ++count;
        }
    }

    private static void importShipments() throws Exception {
        String studyNameShort;
        StudyWrapper study;
        String clinicName;
        ClinicWrapper clinic;
        PatientWrapper patient;
        String dateReceived;
        ShipmentWrapper shipment;
        BlowfishCipher cipher = new BlowfishCipher();

        System.out.println("importing shipments ...");

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
            study = getStudyFromOldShortName(studyNameShort);
            clinicName = rs.getString(3);
            clinic = CbsrClinics.getClinic(clinicName);
            dateReceived = rs.getString(4);

            String patientNo = cipher.decode(rs.getBytes(2));
            patient = PatientWrapper.getPatientInSite(appService, patientNo,
                CbsrSite.cbsrSite);

            // make sure the study is correct
            if (!patient.getStudy().getNameShort().equals(study.getNameShort())) {
                throw new Exception("patient and study do not match: "
                    + patient.getNumber() + ",  " + studyNameShort);
            }

            shipment = ShipmentWrapper.getShipmentInSite(appService,
                dateTimeFormatter.parse(dateReceived), CbsrSite.cbsrSite);

            // make sure the clinic is correct
            if ((shipment != null) && !shipment.getClinic().equals(clinic)) {
                throw new Exception("shipment and clinic do not match: "
                    + dateReceived + ",  " + clinicName);
            }

            if (shipment == null) {
                shipment = new ShipmentWrapper(appService);
                shipment.setClinic(clinic);
                shipment.setWaybill(dateReceived);
                shipment.setDateReceived(dateTimeFormatter.parse(dateReceived));
                shipment.persist();

                System.out.println("importing shipment: patient/"
                    + patient.getNumber() + " shipment_date_received/"
                    + dateReceived + " (" + count + "/" + numShipments + ")");
            }
            ++count;
        }
    }

    private static void importPatientVisits() throws Exception {
        String studyNameShort;
        StudyWrapper study;
        String clinicName;
        ClinicWrapper clinic;
        String dateReceived;
        PatientWrapper patient;
        ShipmentWrapper shipment;
        PatientVisitWrapper pv;
        BlowfishCipher cipher = new BlowfishCipher();

        System.out.println("importing patient visits ...");

        String qryPart = "from patient_visit, study_list, patient "
            + "where patient_visit.study_nr=study_list.study_nr "
            + "and patient_visit.patient_nr=patient.patient_nr";

        Statement s = con.createStatement();
        s.execute("select count(*) " + qryPart);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numPatientVisits = rs.getInt(1);

        s.execute("select patient_visit.*, study_list.study_name_short, patient.chr_nr "
            + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        int count = 1;
        while (rs.next()) {
            studyNameShort = rs.getString(1);
            study = getStudyFromOldShortName(studyNameShort);
            clinicName = rs.getString(3);
            clinic = CbsrClinics.getClinic(clinicName);
            dateReceived = rs.getString(4);

            String patientNo = cipher.decode(rs.getBytes(21));
            patient = PatientWrapper.getPatientInSite(appService, patientNo,
                CbsrSite.cbsrSite);

            shipment = ShipmentWrapper.getShipmentInSite(appService,
                dateTimeFormatter.parse(dateReceived), CbsrSite.cbsrSite);

            // check for shipment
            if (shipment == null) {
                throw new Exception("found 0 shipments for studyName/"
                    + study.getNameShort() + " clinicName/" + clinicName
                    + " dateReceived/" + dateReceived);
            }

            // make sure the clinic is correct
            if ((shipment != null) && !shipment.getClinic().equals(clinic)) {
                throw new Exception("shipment and clinic do not match: "
                    + dateReceived + ",  " + clinicName);
            }

            // make sure the study is correct
            if (!patient.getStudy().equals(study)) {
                throw new Exception(
                    "patient study does not match patient visit study");
            }

            pv = new PatientVisitWrapper(appService);
            pv.setDateProcessed(dateTimeFormatter.parse(dateReceived));
            pv.setPatient(patient);
            pv.setShipment(shipment);
            pv.setComment(rs.getString(4));

            System.out.println("importing patient visit: patient/"
                + patient.getNumber() + " visit date/" + dateReceived + " ("
                + count + "/" + numPatientVisits + ")");

            // now set corresponding patient visit info data
            for (String label : study.getStudyPvAttrLabels()) {
                if (label.equals("Date Received")) {
                    pv.setPvAttrValue(label, rs.getString(6));
                }
                else if (label.equals("PBMC Count")) {
                    pv.setPvAttrValue(label, rs.getString(8));
                }
                else if (label.equals("Consent")) {
                    ArrayList<String> consents = new ArrayList<String>();
                    if (rs.getInt(9) == 1) {
                        consents.add("Surveillance");
                    }
                    if (rs.getInt(10) == 1) {
                        consents.add("Genetic predisposition");
                    }
                    pv.setPvAttrValue(label, StringUtils.join(consents, ";"));
                }
                else if (label.equals("Worksheet")) {
                    pv.setPvAttrValue(label, rs.getString(15));
                }
            }

            pv.persist();
            ++count;
        }
    }

    private static void importCabinetSamples() throws Exception {
        System.out.println("importing cabinet samples ...");

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

        s.execute("select patient_visit.visit_nr, patient_visit.date_received, patient_visit.date_taken, "
            + "study_list.study_name_short, sample_list.sample_name_short, cabinet.*, patient.chr_nr "
            + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        ContainerWrapper cabinet = null;

        for (ContainerWrapper container : ContainerWrapper.getContainersInSite(
            appService, CbsrSite.getSite(), "01")) {
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
            if (cabinetNum != 1) throw new Exception("Invalid cabinet number: "
                + cabinetNum);

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

            System.out.println("importing Cabinet sample at position "
                + drawerName + String.format("%02d", binNum) + binPosStr + " ("
                + count + "/" + numSamples + ")");

            String patientNo = cipher.decode(rs.getBytes(18));
            patient = PatientWrapper.getPatientInSite(appService, patientNo,
                CbsrSite.cbsrSite);

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
    // System.out.println("importing freezer samples ...");
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
    // System.out.println("Ignoring samples for freezer number "
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
    // System.out.println("importing freezer sample at position "
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
        c.setParameters(Arrays.asList(new Object [] { "Cabinet" }));

        System.out.println("CName        Type         CParent      pos1 pos2 SampleSize");
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
        c.setParameters(Arrays.asList(new Object [] { "FR01" }));

        System.out.println("CName        Type         CParent      pos1 pos2 SampleSize");
        List<Container> containers = appService.query(c);
        for (Container sc : containers) {
            printContainerPositions(sc.getChildPositionCollection());
        }
    }

    private void printContainerPositions(Collection<ContainerPosition> positions) {
        if (positions == null) return;

        for (ContainerPosition pos : positions) {
            System.out.println(String.format(
                "%-12s %-12s %-12s %2d  %2d    %3d",
                pos.getContainer().getLabel(),
                pos.getContainer().getContainerType().getName(),
                pos.getParentContainer().getLabel(), pos.getRow(),
                pos.getCol(),
                pos.getContainer().getSamplePositionCollection().size()));
        }

        for (ContainerPosition pos : positions) {
            printContainerPositions(pos.getContainer().getChildPositionCollection());
        }
    }

    @SuppressWarnings("unused")
    private String [] getRowData(ResultSet rs) throws SQLException {
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numCols = rsMetaData.getColumnCount();
        String [] result = new String [numCols];

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
