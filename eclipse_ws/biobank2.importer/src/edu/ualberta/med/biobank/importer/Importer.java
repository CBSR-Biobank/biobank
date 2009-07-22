
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
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
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/*
 *  need to remove the password on MS Access side.
 * a call to get a column from a result set can only be made once, otherwise the
 * driver generates an exception.
 */

public class Importer {
    public static final String BBPDB_DATE_FMT = "yyyy-MM-dd HH:mm:ss";

    public static final String BIOBANK2_DATE_FMT = "yyyy-MM-dd HH:mm";

    public SimpleDateFormat bbpdbDateFmt;

    public SimpleDateFormat biobank2DateFmt;

    private WritableApplicationService appService;

    private static Importer instance = null;

    private Connection con;

    private BioBank2Db bioBank2Db;

    private ArrayList<String> tables;

    private Site cbrSite;

    public static void main(String [] args) throws Exception {
        Importer.getInstance();
    }

    private Importer() {
        tables = new ArrayList<String>();

        try {
            appService = (WritableApplicationService) ApplicationServiceProvider.getApplicationServiceFromUrl(
                "http://localhost:8080/biobank2", "testuser", "test");
            // "http://aicml-med.cs.ualberta.ca:8080/biobank2", "testuser",
            // "test");

            bbpdbDateFmt = new SimpleDateFormat(BBPDB_DATE_FMT);
            biobank2DateFmt = new SimpleDateFormat(BIOBANK2_DATE_FMT);

            bioBank2Db = BioBank2Db.getInstance();
            bioBank2Db.setAppService(appService);

            // SimpleDateFormat dfmt = new
            // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // bioBank2Db.getPatientVisit("BBP", 4,
            // dfmt.parse("2001-06-04 12:13:00"));

            // checkCabinet();
            // checkFreezer();
            // System.exit(0);

            con = getMysqlConnection();

            getTables();
            if (tables.size() == 0) throw new Exception(
                "No tables found in database");

            String [] reqdTables = {
                "clinics", "study_list", "patient", "patient_visit", "cabinet",
                "sample_list" };

            for (String table : reqdTables) {
                if (!tableExists(table)) throw new Exception("Table " + table
                    + " not found");
            }

            // the order here matters
            bioBank2Db.deleteAll(Sample.class);
            bioBank2Db.deleteAll(Container.class);
            bioBank2Db.deleteAll(ContainerType.class);
            // bioBank2Db.deleteAll(PvInfoData.class);
            // bioBank2Db.deleteAll(PvInfo.class);
            bioBank2Db.deleteAll(PatientVisit.class);
            bioBank2Db.deleteAll(Patient.class);
            bioBank2Db.deleteAll(Clinic.class);
            bioBank2Db.deleteAll(Study.class);
            bioBank2Db.deleteAll(Site.class);

            cbrSite = bioBank2Db.createSite();

            SiteContainerTypes.getInstance().insertContainerTypes(cbrSite);
            SiteContainers.getInstance().insertContainers(cbrSite);

            importStudies();
            importClinics();
            importPatients();
            importPatientVisits();
            importFreezerSamples();
            importCabinetSamples();

            System.out.println("importing complete.");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Importer getInstance() {
        if (instance != null) return instance;
        instance = new Importer();
        return instance;
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

    private Connection getMysqlConnection() throws Exception {
        // Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/bbpdb",
            "dummy", "ozzy498");
    }

    private void getTables() throws SQLException {
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

    private boolean tableExists(String name) {
        for (int i = 0; i < tables.size(); ++i) {
            if (tables.get(i).equals(name)) return true;
        }
        return false;
    }

    private void importStudies() throws Exception {
        Study study;
        String studyNameShort;

        System.out.println("importing studies ...");

        Statement s = con.createStatement();
        s.execute("select * from study_list");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                studyNameShort = rs.getString(3);
                study = new Study();
                study.setName(rs.getString(2));
                study.setNameShort(studyNameShort);
                study.setSite(cbrSite);
                study.setActivityStatus("Active");
                study = (Study) bioBank2Db.setObject(study);

                System.out.println("importing study " + study.getNameShort()
                    + " ...");

                if (studyNameShort.equals("KDCS")) {
                    StudyPvInfo.assignKdcsInfo(study);
                }
                else if (studyNameShort.equals("VAS")) {
                    StudyPvInfo.assignVasInfo(study);
                }
                else if (studyNameShort.equals("RVS")) {
                    StudyPvInfo.assignRvsInfo(study);
                }
                else if (studyNameShort.equals("NHS")) {
                    StudyPvInfo.assignNhsInfo(study);
                }
                else if (studyNameShort.equals("MPS")) {
                    StudyPvInfo.assignMpsInfo(study);
                }
                else if (studyNameShort.equals("BBP")) {
                    StudyPvInfo.assignBbpPvInfo(study);
                }
                else {
                    throw new Exception("Unknown study: " + studyNameShort);
                }
            }
        }
    }

    private void importClinics() throws Exception {
        Clinic clinic;
        Collection<Clinic> clinicCollection;

        System.out.println("importing clinics ...");

        Statement s = con.createStatement();
        s.execute("select clinics.*, study_list.study_name_short "
            + "from clinics, study_list where clinics.study_nr=study_list.study_nr");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                clinic = new Clinic();
                clinic.setName(rs.getString(1));
                clinic.setComment(rs.getString(2));
                clinic.setSite(cbrSite);
                clinic.setActivityStatus("Active");

                Address address = new Address();
                clinic.setAddress(address);
                clinic = (Clinic) bioBank2Db.setObject(clinic);

                // assign clinic to study now
                Study study = bioBank2Db.getStudy(rs.getString(5));
                clinicCollection = study.getClinicCollection();

                if (clinicCollection == null) {
                    clinicCollection = new HashSet<Clinic>();
                }

                clinicCollection.add(clinic);
                study.setClinicCollection(clinicCollection);
                study = (Study) bioBank2Db.setObject(study);
            }
        }
    }

    private void importPatients() throws Exception {
        Patient patient;
        System.out.println("importing patients ...");

        Statement s = con.createStatement();
        s.execute("select patient.*, study_list.study_name_short "
            + "from patient, study_list where patient.study_nr=study_list.study_nr");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                String studyName = rs.getString(5);
                Study study = bioBank2Db.getStudy(studyName);
                String patientNo = rs.getString(1);
                System.out.println("importing patient number " + patientNo);
                patient = new Patient();
                patient.setNumber(patientNo);
                patient.setStudy(study);
                patient = (Patient) bioBank2Db.setObject(patient);
                // Thread.sleep(150);
            }
        }
    }

    private void importPatientVisits() throws Exception {
        Study study;
        Clinic clinic;
        String clinicName;
        PatientVisit pv;
        PvInfoData pvInfoData;

        System.out.println("importing patient visits ...");

        Statement s = con.createStatement();
        s.execute("select patient_visit.*, study_list.study_name_short "
            + "from patient_visit, study_list where patient_visit.study_nr=study_list.study_nr");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                study = bioBank2Db.getStudy(rs.getString(20));
                clinicName = rs.getString(3);
                clinic = bioBank2Db.getClinic(study, clinicName);
                if (clinic == null) {
                    System.out.println("ERROR: no such clinic: " + clinicName);
                    continue;
                }

                Patient patient = bioBank2Db.getPatient(rs.getString(2));

                pv = new PatientVisit();
                pv.setDateDrawn(bbpdbDateFmt.parse(rs.getString(5)));
                pv.setPatient(patient);
                pv.setClinic(clinic);
                pv.setComments(rs.getString(4));
                pv = (PatientVisit) bioBank2Db.setObject(pv);

                System.out.println("importing patient visit: patient/"
                    + patient.getNumber() + " visit date/"
                    + biobank2DateFmt.format(pv.getDateDrawn()));

                // make sure the study is correct
                if (!patient.getStudy().getNameShort().equals(
                    study.getNameShort())) {
                    throw new Exception();
                }

                // now set corresponding patient visit info data
                for (PvInfo pvInfo : study.getPvInfoCollection()) {
                    pvInfoData = new PvInfoData();
                    pvInfoData.setPvInfo(pvInfo);
                    pvInfoData.setPatientVisit(pv);

                    if (pvInfo.getLabel().equals("Date Received")) {
                        pvInfoData.setValue(biobank2DateFmt.format(bbpdbDateFmt.parse(rs.getString(6))));
                    }
                    else if (pvInfo.getLabel().equals("PBMC Count")) {
                        pvInfoData.setValue(rs.getString(8));
                    }
                    else if (pvInfo.getLabel().equals("Consent")) {
                        ArrayList<String> consents = new ArrayList<String>();
                        if (rs.getInt(9) == 1) {
                            consents.add("Surveillance");
                        }
                        if (rs.getInt(10) == 1) {
                            consents.add("Genetic predisposition");
                        }
                        pvInfoData.setValue(StringUtils.join(consents, ";"));
                    }
                    else if (pvInfo.getLabel().equals("Worksheet")) {
                        pvInfoData.setValue(rs.getString(15));
                    }

                    pvInfoData = (PvInfoData) bioBank2Db.setObject(pvInfoData);
                }
            }
        }
    }

    private void importCabinetSamples() throws Exception {
        System.out.println("importing cabinet samples ...");

        Statement s = con.createStatement();
        s.execute("select patient_visit.visit_nr, patient_visit.date_taken, "
            + "study_list.study_name_short, sample_list.sample_name_short, cabinet.*  "
            + "from cabinet, study_list, patient_visit, sample_list "
            + "where cabinet.study_nr=study_list.study_nr "
            + "and patient_visit.study_nr=study_list.study_nr "
            + "and cabinet.visit_nr=patient_visit.visit_nr "
            + "and cabinet.patient_nr=patient_visit.patient_nr "
            + "and cabinet.sample_nr=sample_list.sample_nr");

        ResultSet rs = s.getResultSet();
        if (rs != null) {
            Container cabinet = bioBank2Db.getContainer("cabinet");
            int cabinetNum;
            Container drawer;
            Container bin;
            PatientVisit visit;
            SampleType sampleType;
            int drawerNum;
            int binNum;
            String drawerName;
            String binPos;
            String sampleTypeNameShort;

            while (rs.next()) {
                cabinetNum = rs.getInt(5);
                if (cabinetNum != 1) throw new Exception(
                    "Invalid cabinet number: " + cabinetNum);

                drawerName = rs.getString(6);
                drawerNum = NumberingScheme.pos2Int(drawerName.substring(1));

                if (drawerNum > 4) {
                    // no such drawer in real cabinet - was used only for
                    // BBPDB test data
                    continue;
                }

                binNum = rs.getInt(7);
                binPos = rs.getString(8);

                System.out.println("importing Cabinet sample at position "
                    + drawerName + String.format("%02d", binNum) + binPos);

                visit = bioBank2Db.getPatientVisit(rs.getString(3),
                    rs.getInt(9), rs.getString(2));

                if (visit == null) {
                    continue;
                }

                sampleTypeNameShort = rs.getString(4);

                drawer = bioBank2Db.getChildContainer(cabinet, drawerNum, 0);
                bin = bioBank2Db.getChildContainer(drawer, binNum, 0);

                if (sampleTypeNameShort.equals("DNA(WBC)")) {
                    sampleTypeNameShort = "DNA(Blood)";
                }

                sampleType = bioBank2Db.getSampleType(sampleTypeNameShort);
                bioBank2Db.containerCheckSampleTypeValid(bin, sampleType);

                SamplePosition spos = new SamplePosition();
                spos.setPositionDimensionOne(1);
                spos.setPositionDimensionTwo(NumberingScheme.binPos2Int(binPos));
                spos.setContainer(bin);

                Sample sample = new Sample();
                sample.setSampleType(sampleType);
                sample.setInventoryId(rs.getString(12));
                sample.setProcessDate(rs.getDate(13));
                sample.setQuantity(rs.getDouble(14));
                sample.setSamplePosition(spos);
                sample.setPatientVisit(visit);
                spos.setSample(sample);

                sample = (Sample) bioBank2Db.setObject(sample);
            }
        }
    }

    private void importFreezerSamples() throws Exception {
        System.out.println("importing freezer samples ...");

        Statement s = con.createStatement();
        s.execute("select patient_visit.date_taken, "
            + "study_list.study_name_short, sample_list.sample_name_short, freezer.*  "
            + "from freezer, study_list, patient_visit, sample_list "
            + "where freezer.study_nr=study_list.study_nr "
            + "and patient_visit.study_nr=study_list.study_nr "
            + "and freezer.visit_nr=patient_visit.visit_nr "
            + "and freezer.patient_nr=patient_visit.patient_nr "
            + "and freezer.sample_nr=sample_list.sample_nr");

        ResultSet rs = s.getResultSet();
        if (rs != null) {
            Container freezer = bioBank2Db.getContainer("FR01");
            ContainerType freezerType = freezer.getContainerType();
            int freezerNum;
            Container hotel;
            Container palette;
            PatientVisit visit;
            SampleType sampleType;
            int patientNum;
            RowColPos hotelPos;
            int paletteNum;
            String studyName;
            String dateDrawn;
            String hotelName;
            String palettePos;
            String sampleTypeNameShort;

            while (rs.next()) {
                freezerNum = rs.getInt(4);
                if (freezerNum != 1) {
                    System.out.println("Ignoring samples for freezer number "
                        + freezerNum);
                    continue;
                }

                hotelName = rs.getString(5);
                hotelPos = NumberingScheme.hotelPos2RowCol(freezerType,
                    hotelName);

                paletteNum = rs.getInt(6) - 1;
                palettePos = rs.getString(14);

                System.out.println("importing FR01 sample at position "
                    + hotelName + String.format("%02d", paletteNum)
                    + palettePos);

                studyName = rs.getString(2);
                patientNum = rs.getInt(7);
                dateDrawn = rs.getString(1);

                visit = bioBank2Db.getPatientVisit(studyName, patientNum,
                    dateDrawn);

                if (visit == null) continue;

                sampleTypeNameShort = rs.getString(3);

                hotel = bioBank2Db.getChildContainer(freezer, hotelPos.row,
                    hotelPos.col);
                palette = bioBank2Db.getChildContainer(hotel, paletteNum, 0);

                if (sampleTypeNameShort.equals("RNA Later")) {
                    sampleTypeNameShort = "RNA Biopsy";
                }

                sampleType = bioBank2Db.getSampleType(sampleTypeNameShort);
                bioBank2Db.containerCheckSampleTypeValid(palette, sampleType);

                SamplePosition spos = new SamplePosition();
                spos.setPositionDimensionOne(1);
                spos.setPositionDimensionTwo(NumberingScheme.palettePos2Int(palettePos));
                spos.setContainer(palette);

                Sample sample = new Sample();
                sample.setSampleType(sampleType);
                sample.setInventoryId(rs.getString(10));
                sample.setProcessDate(rs.getDate(11));
                sample.setQuantity(rs.getDouble(15));
                sample.setSamplePosition(spos);
                sample.setPatientVisit(visit);
                spos.setSample(sample);

                sample = (Sample) bioBank2Db.setObject(sample);
            }
        }
    }

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
                pos.getContainer().getName(),
                pos.getContainer().getContainerType().getName(),
                pos.getParentContainer().getName(),
                pos.getPositionDimensionOne(), pos.getPositionDimensionTwo(),
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
}
