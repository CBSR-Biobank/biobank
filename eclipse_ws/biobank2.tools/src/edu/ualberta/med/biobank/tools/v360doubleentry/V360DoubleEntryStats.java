package edu.ualberta.med.biobank.tools.v360doubleentry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The databases "biobank_v320_de" and "biobank_v360_de" must first be populated on the host machine
 * prior to running this code. A database dump of the production database goes into
 * "biobank_v320_de" and, a databse dump of the test environment database goes into
 * "biobank_v360_de".
 * 
 * @author Nelson Loyola
 * 
 */
public class V360DoubleEntryStats {

    public static final String DB_NAME_PRODUCTION = "biobank_v320_de";

    public static final String DB_NAME_TEST = "biobank_v360_de";

    public static final String BASE_QRY = " FROM specimen spc"
        + " LEFT JOIN specimen pspc on pspc.id=spc.parent_specimen_id"
        + " JOIN origin_info oi on oi.id=spc.origin_info_id"
        + " JOIN center ocenter on ocenter.id=oi.center_id"
        + " JOIN center ccenter on ccenter.id=spc.current_center_id"
        + " JOIN specimen_type stype on stype.id=spc.specimen_type_id"
        + " JOIN collection_event ce on ce.id=spc.collection_event_id"
        + " LEFT JOIN processing_event pe on pe.id=spc.processing_event_id"
        + " JOIN patient p on p.id=ce.patient_id"
        + " JOIN study s on s.id=p.study_id"
        + " LEFT JOIN specimen_position spos on spos.specimen_id=spc.id"
        + " LEFT JOIN container c on c.id=spos.container_id"
        + " WHERE spc.created_at >= convert_tz(?,'Canada/Mountain','GMT') AND spc.created_at <= convert_tz(?,'Canada/Mountain','GMT')"
        + " AND (ocenter.name_short='CBSR' or ccenter.name_short='CBSR')";

    public static final String SELECT_DETAILS_CLAUSE =
        "SELECT ocenter.name_short origin_center,ccenter.name_short current_center,"
            + "s.name_short study,p.pnumber,ce.visit_number,pe.worksheet,spc.inventory_id,"
            + "pspc.inventory_id parent_spc_inv_id,stype.name,date(spc.created_at),"
            + "row,col,label";

    public static final String ORDER_BY_CLAUSE =
        " ORDER BY s.name_short,p.pnumber,spc.inventory_id,spc.created_at";

    public static final String DATE_START = "2013-03-07 09:00";

    public static final String DATE_END = "2013-03-27 22:00";

    private static class DoubleEntryData {
        int studies;
        int centres;
        int patients;
        int cevents;
        int pevents;
        int specimensCreated;
        int specimensScanAssigned;
        Map<String, Object[]> specimens;
    }

    public static void main(String[] argv) {
        try {
            new V360DoubleEntryStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private V360DoubleEntryStats() throws SQLException {
        // PropertyConfigurator.configure("conf/log4j.properties");
        DoubleEntryData prodData = getDbStats(DB_NAME_PRODUCTION);
        DoubleEntryData testData = getDbStats(DB_NAME_TEST);

        System.out.println("Double entry start:, " + DATE_START);
        System.out.println("Double entry end:, " + DATE_END);
        System.out.println();

        System.out.println(",Production,Test");
        System.out.println("studies," + prodData.studies + "," + testData.studies);
        System.out.println("centres," + prodData.centres + "," + testData.centres);
        System.out.println("patients," + prodData.patients + "," + testData.patients);
        System.out.println("collection events," + prodData.cevents + "," + testData.cevents);
        System.out.println("processing events," + prodData.pevents + "," + testData.pevents);
        System.out.println("specimens created," + prodData.specimensCreated + ","
            + testData.specimensCreated);
        System.out.println("scan assigned specimens," + prodData.specimensScanAssigned + ","
            + testData.specimensScanAssigned);
        System.out.println();

        Map<String, Object[]> specimensNotInTestDb = new LinkedHashMap<String, Object[]>();
        Map<String, Object[]> specimensNotInProdDb = new LinkedHashMap<String, Object[]>();

        // check for specimens in production db and not in test db
        for (String inventoryId : prodData.specimens.keySet()) {
            if (!testData.specimens.containsKey(inventoryId)) {
                specimensNotInTestDb.put(inventoryId, prodData.specimens.get(inventoryId));
            }
        }

        // check for specimens in production db and not in test db
        for (String inventoryId : testData.specimens.keySet()) {
            if (!prodData.specimens.containsKey(inventoryId)) {
                specimensNotInProdDb.put(inventoryId, testData.specimens.get(inventoryId));
            }
        }

        if (specimensNotInTestDb.size() > 0) {
            System.out.println("Specimens not in Test DB: (" + specimensNotInTestDb.size() + ")");
            for (String inventoryId : specimensNotInTestDb.keySet()) {
                System.out.println(inventoryId);
            }
            System.out.println();
        }

        if (specimensNotInProdDb.size() > 0) {
            System.out.println("Specimens not in Production DB: (" + specimensNotInProdDb.size()
                + ")");
            for (String inventoryId : specimensNotInProdDb.keySet()) {
                System.out.println(inventoryId);
            }
            System.out.println();
        }
    }

    private DoubleEntryData getDbStats(String dbName) throws SQLException {
        Connection dbCon = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/" + dbName, "dummy", "ozzy498");

        DoubleEntryData deData = new DoubleEntryData();

        deData.studies = getStudiesCount(BASE_QRY, dbCon);
        deData.centres = getOriginCentersCount(BASE_QRY, dbCon);
        deData.patients = getPatientsCount(BASE_QRY, dbCon);
        deData.cevents = getCeventCount(BASE_QRY, dbCon);
        deData.pevents = getPeventCount(BASE_QRY, dbCon);
        deData.specimensCreated = getSpecimensCreatedCount(BASE_QRY, dbCon);
        deData.specimensScanAssigned = getSpecimensScanAssignedCount(BASE_QRY, dbCon);
        deData.specimens = getSpecimens(BASE_QRY, dbCon);

        return deData;
    }

    private int getStudiesCount(String baseQry, Connection dbCon) throws SQLException {
        PreparedStatement ps = dbCon.prepareCall("SELECT COUNT(DISTINCT s.id) " + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private int getOriginCentersCount(String baseQry, Connection dbCon) throws SQLException {
        PreparedStatement ps = dbCon.prepareCall("SELECT COUNT(DISTINCT ocenter.id) " + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private int getPatientsCount(String baseQry, Connection dbCon) throws SQLException {
        PreparedStatement ps = dbCon.prepareCall("SELECT COUNT(DISTINCT p.id) " + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private int getCeventCount(String baseQry, Connection dbCon) throws SQLException {
        PreparedStatement ps = dbCon.prepareCall("SELECT COUNT(DISTINCT ce.id) " + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private int getPeventCount(String baseQry, Connection dbCon) throws SQLException {
        PreparedStatement ps = dbCon.prepareCall("SELECT COUNT(DISTINCT pe.id) " + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private int getSpecimensCreatedCount(String baseQry, Connection dbCon) throws SQLException {
        PreparedStatement ps = dbCon.prepareCall("SELECT COUNT(DISTINCT spc.id) " + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private int getSpecimensScanAssignedCount(String baseQry, Connection dbCon) throws SQLException {
        PreparedStatement ps =
            dbCon.prepareCall("SELECT COUNT(DISTINCT spc.id) " + baseQry
                + " AND spos.id is not null");
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private Map<String, Object[]> getSpecimens(String baseQry, Connection dbCon)
        throws SQLException {
        PreparedStatement ps = dbCon.prepareCall(SELECT_DETAILS_CLAUSE + baseQry + ORDER_BY_CLAUSE);
        ResultSet rs = doQuery(ps);

        Map<String, Object[]> results = new LinkedHashMap<String, Object[]>();
        while (rs.next()) {
            results.put(
                rs.getString(7),
                new Object[] { rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),
                    rs.getString(13), });
        }
        return results;
    }

    private ResultSet doQuery(PreparedStatement ps) throws SQLException {
        ps.setString(1, DATE_START);
        ps.setString(2, DATE_END);
        return ps.executeQuery();
    }

}
