package edu.ualberta.med.biobank.tools.v3120doubleentry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.tools.v3100doubleentry.V3100DoubleEntryStats;

/**
 * The databases "biobank_v3100_de" and "biobank_v3120_de" must first be populated on the host
 * machine prior to running this code. A database dump of the production database goes into
 * "biobank_v3100_de", and a database dump of the test environment database goes into
 * "biobank_v3120_de".
 *
 * @author Nelson Loyola
 *
 */
public class V3120DoubleEntryStats {

    private static Logger log = LoggerFactory.getLogger(V3100DoubleEntryStats.class);

    public static final String DB_NAME_PRODUCTION = "biobank_v3100_de";

    public static final String DB_NAME_TEST = "biobank_v3120_de";

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

    public static final String SELECT_DETAILS_CLAUSE = "SELECT ocenter.name_short origin_center,ccenter.name_short current_center,"
        + "s.name_short study,p.pnumber,ce.visit_number,pe.worksheet,spc.inventory_id,"
        + "pspc.inventory_id parent_spc_inv_id,stype.name,date(spc.created_at),"
        + "row,col,label";

    public static final String ORDER_BY_CLAUSE = " ORDER BY s.name_short,p.pnumber,spc.inventory_id,spc.created_at";

    public static final String DATE_START = "2018-08-10 09:00";

    public static final String DATE_END = "2018-08-15 09:00";

    private static class DoubleEntryData {
        int studyCount;
        int centreCount;
        int peventCount;
        int specimensCreated;
        int specimensScanAssigned;
        Set<String> patients;
        Set<String> cevents;
        Map<String, Object[]> specimens;
    }

    public static void main(String[] argv) {
        try {
            new V3120DoubleEntryStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private V3120DoubleEntryStats() throws SQLException {
        PropertyConfigurator.configure("conf/log4j.properties");
        DoubleEntryData prodData = getDbStats(DB_NAME_PRODUCTION);
        DoubleEntryData testData = getDbStats(DB_NAME_TEST);

        System.out.println("Double entry start:, " + DATE_START + " Mountain time");
        System.out.println("Double entry end:, " + DATE_END + " Mountain time");
        System.out.println();

        System.out.println(",Production,Test");
        System.out.println("studies," + prodData.studyCount + ","
            + testData.studyCount);
        System.out.println("centres," + prodData.centreCount + ","
            + testData.centreCount);
        System.out.println("patients," + prodData.patients.size() + ","
            + testData.patients.size());
        System.out.println("collection events," + prodData.cevents.size() + ","
            + testData.cevents.size());
        System.out.println("processing events," + prodData.peventCount + ","
            + testData.peventCount);
        System.out.println("specimens created," + prodData.specimensCreated
            + "," + testData.specimensCreated);
        System.out.println("scan assigned specimens,"
            + prodData.specimensScanAssigned + ","
            + testData.specimensScanAssigned);
        System.out.println();

        printPatientDelta(prodData, testData);
        printCeventDelta(prodData, testData);
        printSpecimenDelta(prodData, testData);
    }

    private void printPatientDelta(DoubleEntryData prodData,
        DoubleEntryData testData) {
        Set<String> notInTestDb = new LinkedHashSet<String>();
        Set<String> notInProdDb = new LinkedHashSet<String>();

        // check for patients in production db and not in test db
        for (String inventoryId : prodData.patients) {
            if (!testData.patients.contains(inventoryId)) {
                notInTestDb.add(inventoryId);
            }
        }

        // check for patients in production db and not in test db
        for (String inventoryId : testData.patients) {
            if (!prodData.patients.contains(inventoryId)) {
                notInProdDb.add(inventoryId);
            }
        }

        if (notInTestDb.size() > 0) {
            System.out.println("Patients not in Test DB: ("
                + notInTestDb.size() + ")");
            for (String inventoryId : notInTestDb) {
                System.out.println(inventoryId);
            }
            System.out.println();
        }

        if (notInProdDb.size() > 0) {
            System.out.println("Patients not in Production DB: ("
                + notInProdDb.size() + ")");
            for (String inventoryId : notInProdDb) {
                System.out.println(inventoryId);
            }
            System.out.println();
        }
    }

    private void printCeventDelta(DoubleEntryData prodData,
        DoubleEntryData testData) {
        Set<String> notInTestDb = new LinkedHashSet<String>();
        Set<String> notInProdDb = new LinkedHashSet<String>();

        // check for cevents in production db and not in test db
        for (String inventoryId : prodData.cevents) {
            if (!testData.cevents.contains(inventoryId)) {
                notInTestDb.add(inventoryId);
            }
        }

        // check for cevents in production db and not in test db
        for (String inventoryId : testData.cevents) {
            if (!prodData.cevents.contains(inventoryId)) {
                notInProdDb.add(inventoryId);
            }
        }

        if (notInTestDb.size() > 0) {
            System.out.println("Collection events not in Test DB: ("
                + notInTestDb.size() + ")");
            for (String inventoryId : notInTestDb) {
                System.out.println(inventoryId);
            }
            System.out.println();
        }

        if (notInProdDb.size() > 0) {
            System.out.println("Collection events not in Production DB: ("
                + notInProdDb.size() + ")");
            for (String inventoryId : notInProdDb) {
                System.out.println(inventoryId);
            }
            System.out.println();
        }

    }

    private void printSpecimenDelta(DoubleEntryData prodData,
        DoubleEntryData testData) {
        Map<String, Object[]> notInTestDb = new LinkedHashMap<String, Object[]>();
        Map<String, Object[]> notInProdDb = new LinkedHashMap<String, Object[]>();

        // check for specimens in production db and not in test db
        for (String inventoryId : prodData.specimens.keySet()) {
            if (!testData.specimens.containsKey(inventoryId)) {
                notInTestDb.put(inventoryId,
                    prodData.specimens.get(inventoryId));
            }
        }

        // check for specimens in production db and not in test db
        for (String inventoryId : testData.specimens.keySet()) {
            if (!prodData.specimens.containsKey(inventoryId)) {
                notInProdDb.put(inventoryId,
                    testData.specimens.get(inventoryId));
            }
        }

        if (notInTestDb.size() > 0) {
            System.out.println("Specimens not in Test DB: ("
                + notInTestDb.size() + ")");
            for (String inventoryId : notInTestDb.keySet()) {
                System.out.println(inventoryId);
            }
            System.out.println();
        }

        if (notInProdDb.size() > 0) {
            System.out.println("Specimens not in Production DB: ("
                + notInProdDb.size() + ")");
            for (String inventoryId : notInProdDb.keySet()) {
                System.out.println(inventoryId);
            }
            System.out.println();
        }
    }

    private DoubleEntryData getDbStats(String dbName) throws SQLException {
        Connection dbCon = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/" + dbName, "dummy", "ozzy498");

        DoubleEntryData deData = new DoubleEntryData();

        deData.studyCount = getStudiesCount(BASE_QRY, dbCon);
        deData.centreCount = getOriginCentersCount(BASE_QRY, dbCon);
        deData.patients = getPatients(BASE_QRY, dbCon);
        deData.cevents = getCevents(BASE_QRY, dbCon);
        deData.peventCount = getPeventCount(BASE_QRY, dbCon);
        deData.specimensCreated = getSpecimensCreatedCount(BASE_QRY, dbCon);
        deData.specimensScanAssigned = getSpecimensScanAssignedCount(BASE_QRY,
            dbCon);
        deData.specimens = getSpecimens(BASE_QRY, dbCon);

        return deData;
    }

    private int getStudiesCount(String baseQry, Connection dbCon)
        throws SQLException {
        PreparedStatement ps = dbCon.prepareCall("SELECT COUNT(DISTINCT s.id) "
            + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private int getOriginCentersCount(String baseQry, Connection dbCon)
        throws SQLException {
        PreparedStatement ps = dbCon
            .prepareCall("SELECT COUNT(DISTINCT ocenter.id) " + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private Set<String> getPatients(String baseQry, Connection dbCon)
        throws SQLException {
        PreparedStatement ps = dbCon.prepareCall("SELECT DISTINCT p.pnumber "
            + baseQry);
        ResultSet rs = doQuery(ps);

        Set<String> results = new LinkedHashSet<String>();
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        return results;
    }

    private Set<String> getCevents(String baseQry, Connection dbCon)
        throws SQLException {
        PreparedStatement ps = dbCon
            .prepareCall("SELECT DISTINCT ce.visit_number, p.pnumber "
                + baseQry);
        ResultSet rs = doQuery(ps);

        Set<String> results = new LinkedHashSet<String>();
        while (rs.next()) {
            results.add(rs.getString(2) + "-" + rs.getString(1));
        }
        return results;
    }

    private int getPeventCount(String baseQry, Connection dbCon)
        throws SQLException {
        PreparedStatement ps = dbCon
            .prepareCall("SELECT COUNT(DISTINCT pe.id) " + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private int getSpecimensCreatedCount(String baseQry, Connection dbCon)
        throws SQLException {
        PreparedStatement ps = dbCon
            .prepareCall("SELECT COUNT(DISTINCT spc.id) " + baseQry);
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private int getSpecimensScanAssignedCount(String baseQry, Connection dbCon)
        throws SQLException {
        PreparedStatement ps = dbCon
            .prepareCall("SELECT COUNT(DISTINCT spc.id) " + baseQry
                + " AND spos.id is not null");
        ResultSet rs = doQuery(ps);
        rs.next();
        return rs.getInt(1);
    }

    private Map<String, Object[]> getSpecimens(String baseQry, Connection dbCon)
        throws SQLException {
        PreparedStatement ps = dbCon.prepareCall(SELECT_DETAILS_CLAUSE
            + baseQry + ORDER_BY_CLAUSE);
        ResultSet rs = doQuery(ps);

        Map<String, Object[]> results = new LinkedHashMap<String, Object[]>();
        while (rs.next()) {
            results.put(
                rs.getString(7),
                new Object[] { rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5),
                    rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), rs.getString(10),
                    rs.getString(11), rs.getString(12),
                    rs.getString(13), });
        }
        return results;
    }

    private ResultSet doQuery(PreparedStatement ps) throws SQLException {
        ps.setString(1, DATE_START);
        ps.setString(2, DATE_END);
        log.trace("doQuery: {}", ps);
        return ps.executeQuery();
    }

}
