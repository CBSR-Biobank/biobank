package edu.ualberta.med.biobank.tools.upgradetest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

@SuppressWarnings("nls")
public class DataDumpV131 extends DataDump {

    public DataDumpV131(Connection dbconnection, Properties queryProps,
        String filename) throws IOException {
        super(dbconnection, queryProps, filename);
    }

    public void getAliquots() throws SQLException, IOException {
        PreparedStatement ps = dbconnection.prepareStatement(queryProps
            .getProperty("v131.getAliquots"));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2))
                .append(",").append(rs.getString(3)).append(",")
                .append(rs.getString(4)).append(",").append(rs.getString(5))
                .append(",").append(rs.getDate(6)).append(",")
                .append(rs.getDouble(7)).append(",").append(rs.getString(8));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    public void getPvSourceVessels() throws SQLException, IOException {
        PreparedStatement ps = dbconnection.prepareStatement(queryProps
            .getProperty("v131.getPvSourceVessels"));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2))
                .append(",").append(rs.getString(3)).append(",")
                .append(getDateFromStr(rs.getString(4))).append(",")
                .append(getDateFromStr(rs.getString(5))).append(",")
                .append(rs.getString(6));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    public void getAliquotStorageSite() throws SQLException, IOException {
        PreparedStatement ps = dbconnection.prepareStatement(queryProps
            .getProperty("v131.getAliquotStorageSite"));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    public void getSiteStudies() throws SQLException, IOException {
        PreparedStatement ps = dbconnection.prepareStatement(queryProps
            .getProperty("v131.getSiteStudies"));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    public void getStudyContacts() throws SQLException, IOException {
        PreparedStatement ps = dbconnection.prepareStatement(queryProps
            .getProperty("v131.getStudyContacts"));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2))
                .append(",").append(rs.getString(3));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    public void getSiteContainers() throws SQLException, IOException {
        PreparedStatement ps = dbconnection.prepareStatement(queryProps
            .getProperty("v131.getSiteContainers"));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    public void getClinicShipments() throws SQLException, IOException {
        PreparedStatement ps = dbconnection.prepareStatement(queryProps
            .getProperty("v131.getClinicShipments"));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getInt(2))
                .append(",").append(rs.getString(3)).append(",")
                .append(getDateFromStr(rs.getString(4))).append(",")
                .append(getDateFromStr(rs.getString(5))).append(",")
                .append(rs.getString(6)).append(",").append(rs.getString(7));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    public void getDispatchAliquots() throws SQLException, IOException {
        PreparedStatement ps = dbconnection.prepareStatement(queryProps
            .getProperty("v131.getDispatchAliquots"));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2))
                .append(",").append(rs.getString(3)).append(",")
                .append(getDateFromStr(rs.getString(4))).append(",")
                .append(getDateFromStr(rs.getString(5)))
                .append(rs.getString(6));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    public void getPatientVisits() throws SQLException, IOException {
        PreparedStatement ps = dbconnection.prepareStatement(queryProps
            .getProperty("v131.getPatientVisits"));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2))
                .append(",").append(getDateFromStr(rs.getString(3)))
                .append(",").append(rs.getInt(4));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

}
