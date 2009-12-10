
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.common.cbsr.CbsrSite;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
 *  need to remove the password on MS Access side.
 * a call to get a column from a result set can only be made once, otherwise the
 * driver generates an exception.
 */

public class Importer {
    private static WritableApplicationService appService;

    private static Connection con;

    private static ArrayList<String> tables;

    public static void main(String [] args) throws Exception {
        tables = new ArrayList<String>();

        try {
            appService = (WritableApplicationService) ApplicationServiceProvider.getApplicationServiceFromUrl(
                "http://localhost:8080/biobank2", "testuser", "test");

            // checkCabinet();
            // checkFreezer();
            // System.exit(0);

            CbsrSite.createConfiguration(appService);

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

            // importStudies();
            // importClinics();
            // importPatients();
            // importShipments();
            // importPatientVisits();
            // importCabinetSamples();
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

    private static boolean tableExists(String name) {
        for (int i = 0; i < tables.size(); ++i) {
            if (tables.get(i).equals(name)) return true;
        }
        return false;
    }

    // private void importStudies() throws Exception {
    // StudyWrapper study;
    // String studyNameShort;
    //
    // System.out.println("importing studies ...");
    //
    // Statement s = con.createStatement();
    // s.execute("select * from study_list");
    // ResultSet rs = s.getResultSet();
    // if (rs != null) {
    // while (rs.next()) {
    // studyNameShort = rs.getString(3);
    // study = new StudyWrapper(appService);
    // study.setName(rs.getString(2));
    // study.setNameShort(studyNameShort);
    // study.setSite(cbsrSite);
    // study.setActivityStatus("Active");
    // study.persist();
    // study.reload();
    //
    // System.out.println("importing study " + study.getNameShort()
    // + " ...");
    // }
    // }
    // }
    //
    // private void importClinics() throws Exception {
    // Clinic clinic;
    // Collection<Contact> contactCollection;
    //
    // System.out.println("importing clinics ...");
    //
    // Statement s = con.createStatement();
    // s.execute("select clinics.*, study_list.study_name_short "
    // + "from clinics, study_list where clinics.study_nr=study_list.study_nr");
    // ResultSet rs = s.getResultSet();
    // if (rs != null) {
    // while (rs.next()) {
    // clinic = new Clinic();
    // clinic.setName(rs.getString(1));
    // clinic.setComment(rs.getString(2));
    // clinic.setActivityStatus("Active");
    //
    // Address address = new Address();
    // clinic.setAddress(address);
    // //clinic = (Clinic) bioBank2Db.setObject(clinic);
    //
    // Contact contact = new Contact();
    // contact.setName("assign me");
    // contact.setClinic(clinic);
    // //contact = (Contact) bioBank2Db.setObject(contact);
    //
    // // assign contact to study now
    // //Study study = bioBank2Db.getStudy(rs.getString(5));
    // contactCollection = study.getContactCollection();
    //
    // if (contactCollection == null) {
    // contactCollection = new HashSet<Contact>();
    // }
    //
    // contactCollection.add(contact);
    // study.setContactCollection(contactCollection);
    // study = (Study) bioBank2Db.setObject(study);
    // }
    // }
    // }
    //
    // private void importPatients() throws Exception {
    // BlowfishCipher cipher = new BlowfishCipher();
    // Patient patient;
    // System.out.println("importing patients ...");
    //
    // String qryPart =
    // "from patient, study_list where patient.study_nr=study_list.study_nr";
    //
    // Statement s = con.createStatement();
    // s.execute("select count(*) " + qryPart);
    // ResultSet rs = s.getResultSet();
    // rs.next();
    // int numPatients = rs.getInt(1);
    //
    // s.execute("select patient.*, study_list.study_name_short " + qryPart);
    // rs = s.getResultSet();
    // int count = 1;
    // if (rs != null) {
    // while (rs.next()) {
    // String studyName = rs.getString(5);
    // Study study = bioBank2Db.getStudy(studyName);
    // String patientNo = cipher.decode(rs.getBytes(2));
    // System.out.println("importing patient number " + patientNo
    // + " (" + count + "/" + numPatients + ")");
    // patient = new Patient();
    // patient.setNumber(patientNo);
    // patient.setStudy(study);
    // patient = (Patient) bioBank2Db.setObject(patient);
    // ++count;
    // // Thread.sleep(150);
    // }
    // }
    // }
    //
    // private void importShipments() throws Exception {
    // Study study;
    // Clinic clinic;
    // String clinicName;
    // String dateReceived;
    // Shipment shipment;
    // BlowfishCipher cipher = new BlowfishCipher();
    //
    // System.out.println("importing shipments ...");
    //
    // String qryPart = "from patient_visit, study_list, patient "
    // + "where patient_visit.study_nr=study_list.study_nr "
    // + "and patient_visit.patient_nr=patient.patient_nr";
    //
    // Statement s = con.createStatement();
    // s.execute("select count(*) " + qryPart);
    // ResultSet rs = s.getResultSet();
    // rs.next();
    // int numShipments = rs.getInt(1);
    //
    // s.execute("select study_list.study_name_short, patient.chr_nr, "
    // + "patient_visit.clinic_site, patient_visit.date_received "
    // + qryPart);
    //
    // rs = s.getResultSet();
    // int count = 1;
    // if (rs != null) {
    // while (rs.next()) {
    // study = bioBank2Db.getStudy(rs.getString(1));
    // clinicName = rs.getString(3);
    // clinic = bioBank2Db.getClinic(study, clinicName);
    // dateReceived = rs.getString(4);
    // if (clinic == null) {
    // System.out.println("ERROR: no such clinic: " + clinicName);
    // continue;
    // }
    //
    // String patientNo = cipher.decode(rs.getBytes(2));
    // Patient patient = bioBank2Db.getPatient(patientNo);
    //
    // // make sure the study is correct
    // if (!patient.getStudy().getNameShort().equals(
    // study.getNameShort())) {
    // throw new Exception();
    // }
    //
    // shipment = bioBank2Db.getShipment(study.getNameShort(),
    // clinicName, dateReceived);
    //
    // if (shipment == null) {
    // shipment = new Shipment();
    // shipment.setClinic(clinic);
    // shipment.setWaybill(dateReceived);
    // shipment.setDateReceived(bbpdbDateFmt.parse(dateReceived));
    // shipment = (Shipment) bioBank2Db.setObject(shipment);
    //
    // System.out.println("importing shipment: patient/"
    // + patient.getNumber() + " shipment_date_received/"
    // + dateReceived + " (" + count + "/" + numShipments
    // + ")");
    // }
    //
    // Collection<Patient> patients = shipment.getPatientCollection();
    // if (patients == null) {
    // patients = new HashSet<Patient>();
    // }
    // patients.add(patient);
    //
    // ++count;
    //
    // }
    // }
    // }
    //
    // private void importPatientVisits() throws Exception {
    // Study study;
    // Clinic clinic;
    // String clinicName;
    // String dateReceived;
    // PatientVisit pv;
    // PvAttr pvAttr;
    // BlowfishCipher cipher = new BlowfishCipher();
    //
    // System.out.println("importing patient visits ...");
    //
    // String qryPart = "from patient_visit, study_list, patient "
    // + "where patient_visit.study_nr=study_list.study_nr "
    // + "and patient_visit.patient_nr=patient.patient_nr";
    //
    // Statement s = con.createStatement();
    // s.execute("select count(*) " + qryPart);
    // ResultSet rs = s.getResultSet();
    // rs.next();
    // int numPatientVisits = rs.getInt(1);
    //
    // s.execute("select patient_visit.*, study_list.study_name_short, patient.chr_nr "
    // + qryPart);
    //
    // rs = s.getResultSet();
    // int count = 1;
    // if (rs != null) {
    // while (rs.next()) {
    // study = bioBank2Db.getStudy(rs.getString(20));
    // clinicName = rs.getString(3);
    // clinic = bioBank2Db.getClinic(study, clinicName);
    // dateReceived = rs.getString(6);
    // if (clinic == null) {
    // System.out.println("ERROR: no such clinic: " + clinicName);
    // continue;
    // }
    //
    // String patientNo = cipher.decode(rs.getBytes(21));
    // Patient patient = bioBank2Db.getPatient(patientNo);
    // Shipment shipment = bioBank2Db.getShipment(
    // study.getNameShort(), clinicName, dateReceived);
    //
    // // check for shipment
    // if (shipment == null) {
    // throw new Exception("found 0 shipments for studyName/"
    // + study.getNameShort() + " clinicName/" + clinicName
    // + " dateReceived/" + dateReceived);
    // }
    //
    // pv = new PatientVisit();
    // pv.setDateProcessed(bbpdbDateFmt.parse(dateReceived));
    // pv.setPatient(patient);
    // pv.setShipment(shipment);
    // pv.setComment(rs.getString(4));
    // pv = (PatientVisit) bioBank2Db.setObject(pv);
    //
    // System.out.println("importing patient visit: patient/"
    // + patient.getNumber() + " visit date/" + dateReceived
    // + " (" + count + "/" + numPatientVisits + ")");
    //
    // // make sure the study is correct
    // if (!patient.getStudy().getNameShort().equals(
    // study.getNameShort())) {
    // throw new Exception();
    // }
    //
    // // now set corresponding patient visit info data
    // for (StudyPvAttr studyPvAttr : study.getStudyPvAttrCollection()) {
    // pvAttr = new PvAttr();
    // pvAttr.setStudyPvAttr(studyPvAttr);
    // pvAttr.setPatientVisit(pv);
    //
    // if (studyPvAttr.getLabel().equals("Date Received")) {
    // pvAttr.setValue(biobank2DateFmt.format(bbpdbDateFmt.parse(rs.getString(6))));
    // }
    // else if (studyPvAttr.getLabel().equals("PBMC Count")) {
    // pvAttr.setValue(rs.getString(8));
    // }
    // else if (studyPvAttr.getLabel().equals("Consent")) {
    // ArrayList<String> consents = new ArrayList<String>();
    // if (rs.getInt(9) == 1) {
    // consents.add("Surveillance");
    // }
    // if (rs.getInt(10) == 1) {
    // consents.add("Genetic predisposition");
    // }
    // pvAttr.setValue(StringUtils.join(consents, ";"));
    // }
    // else if (studyPvAttr.getLabel().equals("Worksheet")) {
    // pvAttr.setValue(rs.getString(15));
    // }
    //
    // pvAttr = (PvAttr) bioBank2Db.setObject(pvAttr);
    // }
    //
    // ++count;
    // }
    // }
    // }
    //
    // private void importCabinetSamples() throws Exception {
    // System.out.println("importing cabinet samples ...");
    //
    // String qryPart =
    // "from cabinet, study_list, patient_visit, sample_list, patient "
    // + "where cabinet.study_nr=study_list.study_nr "
    // + "and patient_visit.study_nr=study_list.study_nr "
    // + "and cabinet.visit_nr=patient_visit.visit_nr "
    // + "and cabinet.patient_nr=patient_visit.patient_nr "
    // + "and cabinet.sample_nr=sample_list.sample_nr "
    // + "and patient_visit.patient_nr=patient.patient_nr";
    //
    // Statement s = con.createStatement();
    // s.execute("select count(*) " + qryPart);
    // ResultSet rs = s.getResultSet();
    // rs.next();
    // int numSamples = rs.getInt(1);
    //
    // s.execute("select patient_visit.visit_nr, patient_visit.date_received, patient_visit.date_taken, "
    // +
    // "study_list.study_name_short, sample_list.sample_name_short, cabinet.*, patient.chr_nr "
    // + qryPart);
    //
    // rs = s.getResultSet();
    // if (rs != null) {
    // Container cabinet = bioBank2Db.getContainer("01", "cabinet");
    // ContainerType cabinetType = cabinet.getContainerType();
    //
    // int cabinetNum;
    // Container drawer;
    // Container bin;
    // PatientVisit visit;
    // SampleType sampleType;
    // int drawerNum;
    // int binNum;
    // String drawerName;
    // RowColPos binPos;
    // String sampleTypeNameShort;
    // BlowfishCipher cipher = new BlowfishCipher();
    //
    // int count = 0;
    // while (rs.next()) {
    // ++count;
    // cabinetNum = rs.getInt(6);
    // if (cabinetNum != 1) throw new Exception(
    // "Invalid cabinet number: " + cabinetNum);
    //
    // drawerName = rs.getString(7);
    // Capacity capacity = cabinetType.getCapacity();
    // Integer rowCap = capacity.getRowCapacity();
    // Integer colCap = capacity.getColCapacity();
    // RowColPos pos = LabelingScheme.cbsrTwoCharToRowCol(drawerName,
    // rowCap, colCap, cabinetType.getName());
    // drawerNum = pos.row;
    //
    // if (drawerNum > 4) {
    // // no such drawer in real cabinet - was used only for
    // // BBPDB test data
    // continue;
    // }
    //
    // binNum = rs.getInt(8) - 1;
    // String binPosStr = rs.getString(9);
    // binPos = LabelingScheme.cbsrTwoCharToRowCol(binPosStr, 120, 1,
    // "bin");
    //
    // System.out.println("importing Cabinet sample at position "
    // + drawerName + String.format("%02d", binNum) + binPosStr
    // + " (" + count + "/" + numSamples + ")");
    //
    // String patientNo = cipher.decode(rs.getBytes(18));
    //
    // visit = bioBank2Db.getPatientVisit(rs.getString(4), patientNo,
    // rs.getString(2));
    //
    // if (visit == null) {
    // continue;
    // }
    //
    // sampleTypeNameShort = rs.getString(5);
    //
    // drawer = bioBank2Db.getChildContainer(cabinet, drawerNum, 0);
    // bin = bioBank2Db.getChildContainer(drawer, binNum, 0);
    //
    // if (sampleTypeNameShort.equals("DNA(WBC)")) {
    // sampleTypeNameShort = "DNA(Blood)";
    // }
    //
    // sampleType = bioBank2Db.getSampleType(sampleTypeNameShort);
    // bioBank2Db.containerCheckSampleTypeValid(bin, sampleType);
    //
    // SamplePosition spos = new SamplePosition();
    // spos.setRow(binPos.row);
    // spos.setCol(0);
    // spos.setContainer(bin);
    //
    // Sample sample = new Sample();
    // sample.setSampleType(sampleType);
    // sample.setInventoryId(rs.getString(13));
    // sample.setLinkDate(rs.getDate(14));
    // sample.setQuantity(rs.getDouble(15));
    // sample.setSamplePosition(spos);
    // sample.setPatientVisit(visit);
    // spos.setSample(sample);
    //
    // sample = (Sample) bioBank2Db.setObject(sample);
    // }
    // }
    // }
    //
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
    // sampleType = bioBank2Db.getSampleType(sampleTypeNameShort);
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
    //
    // @SuppressWarnings("unused")
    // private void checkCabinet() throws Exception {
    //
    // HQLCriteria c = new HQLCriteria("select sc"
    // + " from edu.ualberta.med.biobank.model.Container as sc"
    // + " where sc.name=?");
    // c.setParameters(Arrays.asList(new Object [] { "Cabinet" }));
    //
    // System.out.println("CName        Type         CParent      pos1 pos2 SampleSize");
    // List<Container> containers = appService.query(c);
    // for (Container sc : containers) {
    // printContainerPositions(sc.getChildPositionCollection());
    // }
    // }
    //
    // @SuppressWarnings("unused")
    // private void checkFreezer() throws Exception {
    //
    // HQLCriteria c = new HQLCriteria("select sc"
    // + " from edu.ualberta.med.biobank.model.Container as sc"
    // + " where sc.name=?");
    // c.setParameters(Arrays.asList(new Object [] { "FR01" }));
    //
    // System.out.println("CName        Type         CParent      pos1 pos2 SampleSize");
    // List<Container> containers = appService.query(c);
    // for (Container sc : containers) {
    // printContainerPositions(sc.getChildPositionCollection());
    // }
    // }
    //
    // private void printContainerPositions(Collection<ContainerPosition>
    // positions) {
    // if (positions == null) return;
    //
    // for (ContainerPosition pos : positions) {
    // System.out.println(String.format(
    // "%-12s %-12s %-12s %2d  %2d    %3d",
    // pos.getContainer().getLabel(),
    // pos.getContainer().getContainerType().getName(),
    // pos.getParentContainer().getLabel(), pos.getRow(),
    // pos.getCol(),
    // pos.getContainer().getSamplePositionCollection().size()));
    // }
    //
    // for (ContainerPosition pos : positions) {
    // printContainerPositions(pos.getContainer().getChildPositionCollection());
    // }
    // }
    //
    // @SuppressWarnings("unused")
    // private String [] getRowData(ResultSet rs) throws SQLException {
    // ResultSetMetaData rsMetaData = rs.getMetaData();
    // int numCols = rsMetaData.getColumnCount();
    // String [] result = new String [numCols];
    //
    // for (int i = 1; i <= numCols; ++i) {
    // result[i - 1] = rs.getString(i);
    // }
    // return result;
    // }

    public static WritableApplicationService getAppService() throws Exception {
        if (appService == null) {
            throw new Exception("appService has not been initialized");
        }
        return appService;
    }

}
