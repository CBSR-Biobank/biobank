package edu.ualberta.med.biobank.tools.upgradetest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;

public class UpgradeV131V140 {

    private static final String v131filename = "biobank2_v131.csv";

    private static final String v140filename = "biobank2_v140.csv";

    private Connection connectionV131;

    private Connection connectionV140;

    private UpgradeV131V140() throws Exception {
        try {
            connectionV131 = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/biobank2_v131",
                    "dummy", "ozzy498");

            connectionV140 = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/biobank2_pre140", "dummy",
                "ozzy498");

            File f = new File(v131filename);
            f.delete();

            f = new File(v140filename);
            f.delete();

            BufferedWriter bwV131 = new BufferedWriter(new FileWriter(
                v131filename, true));

            // getV131Aliquots(bwV131);
            getV131PvSourceVessels(bwV131);

            BufferedWriter bwV140 = new BufferedWriter(new FileWriter(
                v140filename, true));

            // getV140AliquotedSpecimens(bwV140);
            getV140SourceSpecimens(bwV140);

            bwV131.close();
            bwV140.close();
        } catch (IOException ioe) {
            throw new Exception(ioe);
        }
    }

    public static void main(String[] args) throws Exception {
        new UpgradeV131V140();
    }

    private void getV131Aliquots(BufferedWriter bw) throws SQLException,
        IOException {
        String qry = "select study.name_short,clinic.name_short,pnumber,"
            + "inventory_id,link_date,quantity,st.name "
            + "from aliquot "
            + "join sample_type as st on st.id=aliquot.sample_type_id "
            + "join patient_visit as pv on pv.id=aliquot.patient_visit_id "
            + "join clinic_shipment_patient as csp on csp.id=pv.clinic_shipment_patient_id "
            + "join abstract_shipment as aship on aship.id=csp.clinic_shipment_id  "
            + "join clinic on clinic.id=aship.clinic_id  "
            + "join patient on patient.id=csp.patient_id  "
            + "join study on study.id=patient.study_id  "
            + "where aship.discriminator='ClinicShipment' "
            + "order by study.name_short,clinic.name_short,pnumber,inventory_id,link_date";
        PreparedStatement ps = connectionV131.prepareStatement(qry);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2))
                .append(",").append(rs.getString(3)).append(",")
                .append(rs.getString(4)).append(",").append(rs.getDate(5))
                .append(",").append(rs.getDouble(6)).append(",")
                .append(rs.getString(7));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    private void getV131PvSourceVessels(BufferedWriter bw) throws SQLException,
        IOException, ParseException {
        String qry = "select study.name_short,clinic.name_short,pnumber,"
            + "if(psv.time_drawn is null,pv.date_drawn, "
            + "   addtime(timestamp(date(pv.date_drawn)), time(psv.time_drawn))) as date_drawn,"
            + "date_processed,sv.name "
            + "from pv_source_vessel as psv "
            + "join patient_visit as pv on pv.id=psv.patient_visit_id "
            + "join clinic_shipment_patient as csp on csp.id=pv.clinic_shipment_patient_id "
            + "join patient on patient.id=csp.patient_id "
            + "join study on study.id=patient.study_id "
            + "join abstract_shipment as aship on aship.id=csp.clinic_shipment_id "
            + "join clinic on clinic.id=aship.clinic_id "
            + "join source_vessel as sv on sv.id=psv.source_vessel_id "
            + "order by study.name_short,clinic.name_short,pnumber,"
            + "date_drawn,date_processed,sv.name";
        PreparedStatement ps = connectionV131.prepareStatement(qry);

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

    private void getV140AliquotedSpecimens(BufferedWriter bw)
        throws SQLException, IOException {
        String qry = "select study.name_short,center.name_short,pnumber,"
            + "inventory_id,spc.created_at,quantity,st.name "
            + "from specimen as spc "
            + "inner join specimen_type as st on st.id=spc.specimen_type_id "
            + "join collection_event as ce on ce.id=spc.collection_event_id "
            + "join origin_info as oi on oi.id=spc.origin_info_id "
            + "join center on center.id=oi.center_id "
            + "join patient on patient.id=ce.patient_id "
            + "join study on study.id=patient.study_id "
            + "where center.discriminator='Clinic' and inventory_id not like 'sw upgrade%' "
            + "order by study.name_short,center.name_short,pnumber,inventory_id,spc.created_at";

        PreparedStatement ps = connectionV140.prepareStatement(qry);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            row.append(rs.getString(1)).append(",").append(rs.getString(2))
                .append(",").append(rs.getString(3)).append(",")
                .append(rs.getString(4)).append(",").append(rs.getDate(5))
                .append(",").append(rs.getDouble(6)).append(",")
                .append(rs.getString(7));
            bw.write(row.toString());
            bw.newLine();
        }
        bw.newLine();
        bw.flush();
    }

    private void getV140SourceSpecimens(BufferedWriter bw) throws SQLException,
        IOException, ParseException {
        String qry = "select study.name_short,center.name_short,pnumber,"
            + "spc.created_at,pe.created_at,st.name "
            + "from specimen as spc "
            + "inner join specimen_type as st on st.id=spc.specimen_type_id "
            + "join collection_event as ce on ce.id=spc.collection_event_id "
            + "join origin_info as oi on oi.id=spc.origin_info_id "
            + "join center on center.id=oi.center_id "
            + "join patient on patient.id=ce.patient_id "
            + "join study on study.id=patient.study_id "
            + "join processing_event as pe on pe.id=spc.processing_event_id "
            + "where center.discriminator='Clinic' and spc.parent_specimen_id is null "
            + "order by study.name_short,center.name_short,pnumber,"
            + "spc.created_at,pe.created_at,st.name";

        PreparedStatement ps = connectionV140.prepareStatement(qry);

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

}
