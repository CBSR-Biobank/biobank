package edu.ualberta.med.biobank.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class FreezerImporter {

    protected static final Logger logger = Logger
        .getLogger(FreezerImporter.class.getName());

    protected static String DEFAULT_QUERY = "select patient_visit.date_received, "
        + "patient_visit.date_taken, study_list.study_name_short, "
        + "sample_list.sample_name_short, freezer.*, patient.dec_chr_nr, "
        + "patient_visit.bb2_pv_id from freezer "
        + "left join frz_99_inv_id on frz_99_inv_id.inventory_id=freezer.inventory_id "
        + "join patient_visit on patient_visit.visit_nr=freezer.visit_nr "
        + "join patient on patient.patient_nr=patient_visit.patient_nr "
        + "join study_list on study_list.study_nr=patient_visit.study_nr "
        + "join sample_list on freezer.sample_nr=sample_list.sample_nr "
        + "where freezer.fnum = ? and freezer.rack= ? "
        + "and frz_99_inv_id.inventory_id is null "
        + "order by freezer.box, freezer.cell";

    protected WritableApplicationService appService;
    protected Connection con;
    protected final SiteWrapper site;
    protected ContainerWrapper freezer;
    protected int bbpdbFreezerNum;
    protected int sampleImportCount;
    protected String query;
    protected int currentPalletNr;
    protected Configuration configuration;

    public FreezerImporter(WritableApplicationService appService,
        Connection con, Configuration configuration, final SiteWrapper site,
        ContainerWrapper container, int bbpdbFreezerNum) throws Exception {
        this(appService, con, configuration, site, container, bbpdbFreezerNum,
            DEFAULT_QUERY);
    }

    protected FreezerImporter(WritableApplicationService appService,
        Connection con, Configuration configuration, final SiteWrapper site,
        ContainerWrapper container, int bbpdbFreezerNum, String query)
        throws Exception {
        this.appService = appService;
        this.con = con;
        this.configuration = configuration;
        this.site = site;
        this.freezer = container;
        this.bbpdbFreezerNum = bbpdbFreezerNum;
        this.query = query;
        sampleImportCount = 0;
        currentPalletNr = 0;

        doImport();
    }

    protected void doImport() throws Exception {
        ResultSet rs;
        PreparedStatement ps;

        for (ContainerWrapper hotel : freezer.getChildren().values()) {
            if (!configuration.importFreezerHotel(hotel.getLabel())) {
                logger.debug("not configured to import hotel "
                    + hotel.getLabel());
                continue;
            }

            ps = con.prepareStatement(query);
            ps.setInt(1, bbpdbFreezerNum);
            String hotelLabel = hotel.getLabel();
            int len = hotelLabel.length();
            ps.setString(2, hotelLabel.substring(len - 2));

            rs = ps.executeQuery();
            if (rs == null) {
                throw new Exception("Database query returned null");
            }

            processResultSet(rs, hotel);
        }
    }

    protected void processResultSet(ResultSet rs, ContainerWrapper hotel)
        throws Exception {
        String dateProcessedStr;
        String dateTakenStr;
        String palletPos;
        String sampleTypeNameShort;
        String inventoryId;
        String studyNameShort;
        int palletNr;
        String patientNr;
        String linkDateStr;
        double volume;
        int visitId;

        while (rs.next()) {
            studyNameShort = rs.getString(3);
            patientNr = rs.getString(17);
            dateProcessedStr = rs.getString(1);
            dateTakenStr = rs.getString(2);
            palletNr = rs.getInt(7);
            palletPos = rs.getString(15);
            inventoryId = rs.getString(11);
            sampleTypeNameShort = rs.getString(4);
            linkDateStr = rs.getString(12);
            volume = rs.getDouble(16);
            visitId = rs.getInt(18);

            Importer.importSample(site, studyNameShort, patientNr, visitId,
                dateProcessedStr, dateTakenStr, hotel, palletNr, palletPos,
                inventoryId, sampleTypeNameShort, linkDateStr, volume);

            if (currentPalletNr != palletNr) {
                logger.debug(String.format(
                    "importing freezer samples into pallet %s%02d", hotel
                        .getLabel(), palletNr));
                currentPalletNr = palletNr;
            }

            logger.trace(String.format("importing freezer aliquot %s%02d%s",
                hotel.getLabel(), palletNr, palletPos));
            ++sampleImportCount;
        }
    }

    public int getSamplesImported() {
        return sampleImportCount;
    }
}
