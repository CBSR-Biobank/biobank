
package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/*
 *  need to remove the password on MS Access side.
 * a call to get a column from a result set can only be made once, otherwise the
 * driver generates an exception.
 */

public class Importer {
    private WritableApplicationService appService;

    private Connection con;

    private BioBank2Db bioBank2Db;

    private ArrayList<String> tables;

    private Site cbrSite;

    public static void main(String [] args) throws Exception {
        new Importer();
    }

    Importer() {
        tables = new ArrayList<String>();

        try {
            appService = (WritableApplicationService) ApplicationServiceProvider.getApplicationServiceFromUrl(
                // "http://localhost:8080/biobank2", "testuser", "test");
                "http://aicml-med.cs.ualberta.ca:8080/biobank2", "testuser",
                "test");

            bioBank2Db = BioBank2Db.getInstance();
            bioBank2Db.setAppService(appService);

            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            con = getFileConnection();

            getTables();
            if (tables.size() == 0) throw new Exception();
            if (!tableExists("clinics")) throw new Exception();
            if (!tableExists("study_list")) throw new Exception();
            if (!tableExists("patient")) throw new Exception();
            if (!tableExists("patient_visit")) throw new Exception();

            // the order here matters
            bioBank2Db.deleteAll(Patient.class);
            bioBank2Db.deleteAll(Clinic.class);
            bioBank2Db.deleteAll(Study.class);
            bioBank2Db.deleteAll(Site.class);

            cbrSite = bioBank2Db.createSite();

            importStudies();
            importClinics();
            importPatients();
            // importPatientVisits();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private Connection getDsnConnection() throws SQLException {
        String dbUrl = "jdbc:odbc:bbp_db";
        return DriverManager.getConnection(dbUrl, "", "");
    }

    private Connection getFileConnection() throws SQLException {
        String filename = "bbp_db.mdb";
        String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
        database += filename.trim() + ";DriverID=22;READONLY=true}";
        return DriverManager.getConnection(database, "", "");
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

        System.out.println("importing studies ...");

        Statement s = con.createStatement();
        s.execute("select * from study_list");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                study = new Study();
                study.setName(rs.getString(2));
                study.setNameShort(rs.getString(3));
                study.setSite(cbrSite);
                study = (Study) bioBank2Db.setObject(study);

                if (study.getNameShort().equals("KDCS")) {
                    StudyPvInfo.assignKdcsInfo(study);
                }
                else if (study.getNameShort().equals("VAS")) {
                    StudyPvInfo.assignVasInfo(study);
                }
                else if (study.getNameShort().equals("RVS")) {
                    StudyPvInfo.assignRvsInfo(study);
                }
                else if (study.getNameShort().equals("NHS")) {
                    StudyPvInfo.assignNhsInfo(study);
                }
                else if (study.getNameShort().equals("MPS")) {
                    StudyPvInfo.assignMpsInfo(study);
                }
                else if (study.getNameShort().equals("BBP")) {
                    StudyPvInfo.assignBbpPvInfo(study);
                }
            }
        }
    }

    private void importClinics() throws Exception {
        Clinic clinic;
        Collection<Clinic> clinicCollection;

        System.out.println("importing clinics ...");

        Statement s = con.createStatement();
        s.execute("select clinics.*, study_list.study_name_short from clinics, study_list where study_list.study_nr=clinics.study_nr");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                clinic = new Clinic();
                clinic.setName(rs.getString(1));
                clinic.setComment(rs.getString(2));
                clinic.setSite(cbrSite);

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
        s.execute("select patient.*, study_list.study_name_short from patient, study_list where study_list.study_nr=patient.study_nr");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                Study study = bioBank2Db.getStudy(rs.getString(5));
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
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        PatientVisit pv;
        Date date;

        System.out.println("importing patients ...");

        Statement s = con.createStatement();
        s.execute("select * from patient_visit");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                pv = new PatientVisit();
                date = dateFmt.parse(rs.getString(1));
                pv.setDateDrawn(date);
                pv = (PatientVisit) bioBank2Db.setObject(pv);
            }
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
