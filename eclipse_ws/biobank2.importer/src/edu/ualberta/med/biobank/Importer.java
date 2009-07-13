
package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

import java.lang.reflect.Constructor;
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
import java.util.List;

// need to remove the password on MS Access side.

public class Importer {

    private static WritableApplicationService appService;

    private Connection con;

    private ArrayList<String> tables;

    private Site cbrSite;

    public static void main(String [] args) throws Exception {
        new Importer();
    }

    Importer() {
        tables = new ArrayList<String>();

        try {
            appService = (WritableApplicationService) ApplicationServiceProvider.getApplicationServiceFromUrl(
                "http://localhost:8080/biobank2", "testuser", "test");

            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

            con = getFileConnection();

            getTables();
            if (tables.size() == 0) throw new Exception();
            if (!tableExists("clinics")) throw new Exception();
            if (!tableExists("study_list")) throw new Exception();

            deleteAll(Site.class);

            cbrSite = createSite();

            importClinics();
            importStudies();
            importStudyClinicAssoc();

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

    private Site createSite() throws ApplicationException {
        Site site = new Site();
        site.setName("CBR");
        Address address = new Address();
        site.setAddress(address);

        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            site));
        site = (Site) res.getObjectResult();

        return site;
    }

    private void importClinics() throws Exception {
        Clinic clinic;
        SDKQueryResult res;

        deleteAll(Clinic.class);
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

                res = appService.executeQuery(new InsertExampleQuery(clinic));
                clinic = (Clinic) res.getObjectResult();
            }
        }
    }

    private void importStudies() throws Exception {
        Study study;
        SDKQueryResult res;

        deleteAll(Study.class);
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

                res = appService.executeQuery(new InsertExampleQuery(study));
                study = (Study) res.getObjectResult();
            }
        }
    }

    private void importStudyClinicAssoc() throws Exception {
        System.out.println("importing studies and clinic associations...");

        Collection<Clinic> clinicCollection;
        SDKQueryResult res;

        Statement s = con.createStatement();
        s.execute("select study_list.study_name_short, clinics.clinic_site from clinics, study_list where study_list.study_nr=clinics.study_nr");
        ResultSet rs = s.getResultSet();
        if (rs != null) {
            while (rs.next()) {
                Study study = localGetStudy(rs.getString(1));
                Clinic clinic = localGetClinic(rs.getString(2));
                clinicCollection = study.getClinicCollection();

                if (clinicCollection == null) {
                    clinicCollection = new HashSet<Clinic>();
                }

                clinicCollection.add(clinic);

                study.setClinicCollection(clinicCollection);

                res = appService.executeQuery(new UpdateExampleQuery(study));
                study = (Study) res.getObjectResult();
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

    private void deleteAll(Class<?> classType) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        List<?> list = appService.search(classType, instance);
        for (Object o : list) {
            appService.executeQuery(new DeleteExampleQuery(o));
        }
    }

    private Clinic localGetClinic(String name) throws Exception {
        Clinic clinic = new Clinic();
        clinic.setName(name);

        List<Clinic> list = appService.search(Clinic.class, clinic);
        if (list.size() == 0) throw new Exception();
        return list.get(0);
    }

    private Study localGetStudy(String shortName) throws Exception {
        Study study = new Study();
        study.setNameShort(shortName);

        List<Study> list = appService.search(Study.class, study);
        if (list.size() == 0) throw new Exception();
        return list.get(0);
    }
}
