
package edu.ualberta.med.biobank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

// need to remove the password on MS Access side.

public class Importer {

    private Connection con;

    BioBank2Db bioBank2Db;

    private ArrayList<String> tables;

    private Site cbrSite;

    public static void main(String [] args) throws Exception {
        new Importer();
    }

    Importer() {

        bioBank2Db = new BioBank2Db();

        tables = new ArrayList<String>();

        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            con = getFileConnection();

            getTables();
            if (tables.size() == 0) throw new Exception();
            if (!tableExists("clinics")) throw new Exception();
            if (!tableExists("study_list")) throw new Exception();
            if (!tableExists("patient")) throw new Exception();

            bioBank2Db.deleteAll(Site.class);

            cbrSite = bioBank2Db.createSite();

            importStudies();
            importClinics();
            importStudyClinicAssoc();
            importPatients();

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

        bioBank2Db.deleteAll(Study.class);
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
            }
        }
    }

    private void importClinics() throws Exception {
        Clinic clinic;

        bioBank2Db.deleteAll(Clinic.class);
        System.out.println("importing clinics ...");

        Statement s = con.createStatement();
        s.execute("select * from clinics");
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
            }
        }
    }

    private void importPatients() throws Exception {
        Patient patient;

        bioBank2Db.deleteAll(Patient.class);
        System.out.println("importing patients ...");

        Statement s = con.createStatement();
        s.execute("select * from patient");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                patient = new Patient();
                patient.setNumber(rs.getString(1));

                patient = (Patient) bioBank2Db.setObject(patient);
            }
        }
    }

    private void importStudyClinicAssoc() throws Exception {
        System.out.println("importing studies and clinic associations...");

        Collection<Clinic> clinicCollection;

        Statement s = con.createStatement();
        s.execute("select study_list.study_name_short, clinics.clinic_site from clinics, study_list where study_list.study_nr=clinics.study_nr");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                Study study = bioBank2Db.getStudy(rs.getString(1));
                Clinic clinic = bioBank2Db.getClinic(rs.getString(2));
                clinicCollection = study.getClinicCollection();

                if (clinicCollection == null) {
                    clinicCollection = new HashSet<Clinic>();
                }

                clinicCollection.add(clinic);
                study.setClinicCollection(clinicCollection);
                study = bioBank2Db.setStudy(study);
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
