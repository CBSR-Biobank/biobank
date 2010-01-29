package edu.ualberta.med.biobank.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class FreezerImporter {

    protected static final Logger logger = Logger
        .getLogger(FreezerImporter.class.getName());

    protected static String DEFAULT_QUERY = "select patient_visit.date_received, "
        + "patient_visit.date_taken, study_list.study_name_short, "
        + "sample_list.sample_name_short, freezer.*, patient.chr_nr  "
        + "from freezer "
        + "left join frz_99_inv_id on frz_99_inv_id.inventory_id=freezer.inventory_id "
        + "join study_list on freezer.study_nr=study_list.study_nr "
        + "join patient on patient.patient_nr=freezer.patient_nr "
        + "join patient_visit on patient_visit.study_nr=study_list.study_nr "
        + "and freezer.visit_nr=patient_visit.visit_nr "
        + "and freezer.patient_nr=patient_visit.patient_nr "
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

    public FreezerImporter(WritableApplicationService appService,
        Connection con, final SiteWrapper site, ContainerWrapper container,
        int bbpdbFreezerNum) throws Exception {
        this(appService, con, site, container, bbpdbFreezerNum, DEFAULT_QUERY);
    }

    protected FreezerImporter(WritableApplicationService appService,
        Connection con, final SiteWrapper site, ContainerWrapper container,
        int bbpdbFreezerNum, String query) throws Exception {
        this.appService = appService;
        this.con = con;
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
            if (!Importer.importFreezerHotel(hotel.getLabel())) {
                logger.debug("not configured for importing hotel "
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
        String palletPos;
        String sampleTypeNameShort;
        String inventoryId;
        BlowfishCipher cipher = new BlowfishCipher();
        String studyNameShort;
        int palletNr;
        String patientNr;
        String linkDateStr;
        double quantity;

        while (rs.next()) {
            studyNameShort = rs.getString(3);
            patientNr = cipher.decode(rs.getBytes(17));
            dateProcessedStr = rs.getString(1);
            palletNr = rs.getInt(7);
            palletPos = rs.getString(15);
            inventoryId = rs.getString(11);
            sampleTypeNameShort = rs.getString(4);
            linkDateStr = rs.getString(12);
            quantity = rs.getDouble(16);

            importSample(studyNameShort, patientNr, dateProcessedStr, hotel,
                palletNr, palletPos, inventoryId, sampleTypeNameShort,
                linkDateStr, quantity);
        }
    }

    protected void importSample(String studyNameShort, String patientNr,
        String dateProcessedStr, ContainerWrapper hotel, int palletNr,
        String palletPos, String inventoryId, String sampleTypeNameShort,
        String linkDateStr, double quantity) throws Exception {

        if (palletNr > hotel.getRowCapacity()) {
            logger.error("pallet number is invalid: " + " hotel/"
                + hotel.getLabel() + " pallet/" + palletNr);
            return;
        }

        ContainerWrapper pallet = hotel.getChild(palletNr - 1, 0);

        if (pallet == null) {
            logger.error("pallet not initialized: " + " hotel/"
                + hotel.getLabel() + " pallet/" + palletNr);
            return;
        }

        // make sure inventory id is unique
        if (!Importer.inventoryIdUnique(inventoryId)) {
            return;
        }

        PatientWrapper patient = PatientWrapper.getPatientInSite(appService,
            patientNr, site);

        if (patient == null) {
            logger.error("no patient with number " + patientNr);
            return;
        }

        studyNameShort = Importer.getStudyNameShort(patientNr, studyNameShort);

        if (studyNameShort == null) {
            logger.error("no study for patient " + patientNr);
            return;
        }

        StudyWrapper study = Importer.getStudyFromOldShortName(studyNameShort);
        if (!patient.getStudy().equals(study)) {
            logger.error("patient and study do not match: "
                + patient.getPnumber() + ",  " + studyNameShort);
            return;
        }

        Date dateProcessed = Importer.getDateFromStr(dateProcessedStr);

        List<PatientVisitWrapper> visits = patient.getVisit(dateProcessed);

        if (visits.size() == 0) {
            logger.error("patient " + patientNr + ", visit not found for date "
                + Importer.formatDate(dateProcessed));
            return;
        } else if (visits.size() > 1) {
            logger.info("patient " + patientNr + ", multiple visits for date "
                + Importer.formatDate(dateProcessed));
        }

        PatientVisitWrapper visit = visits.get(0);

        if (sampleTypeNameShort.equals("RNA Later")) {
            sampleTypeNameShort = "RNA Biopsy";
        } else if (sampleTypeNameShort.equals("Plasma LH")) {
            sampleTypeNameShort = "Lith Hep Plasma";
        } else if (sampleTypeNameShort.equals("PFP")) {
            sampleTypeNameShort = "PF Plasma";
        }
        SampleTypeWrapper sampleType = Importer
            .getSampleType(sampleTypeNameShort);

        if (sampleType == null) {
            logger.error("sample type not in database: " + sampleTypeNameShort);
            return;
        }

        RowColPos pos = LabelingScheme.sbsToRowCol(palletPos);
        SampleWrapper sample = pallet.getSample(pos.row, pos.col);
        if ((sample != null)
            && sample.getSampleType().getNameShort()
                .equals(sampleTypeNameShort)
            && sample.getInventoryId().equals(inventoryId)) {
            logger.debug("freezer already contains sample " + pallet.getLabel()
                + palletPos);
            return;
        }

        sample = new SampleWrapper(appService);
        sample.setParent(pallet);
        sample.setSampleType(sampleType);
        sample.setInventoryId(inventoryId);
        sample.setLinkDate(Importer.getDateFromStr(linkDateStr));
        sample.setQuantity(quantity);
        sample.setPosition(pos);
        sample.setPatientVisit(visit);

        if (!pallet.canHoldSample(sample)) {
            logger.error("pallet " + pallet.getLabel()
                + " cannot hold sample of type " + sampleType.getName());
            return;
        }
        sample.persist();

        if (currentPalletNr != palletNr) {
            logger.debug("importing freezer samples into pallet "
                + pallet.getLabel());
            currentPalletNr = palletNr;
        }

        logger.trace("importing freezer sample " + pallet.getLabel()
            + palletPos);
        ++sampleImportCount;

    }

    public int getSamplesImported() {
        return sampleImportCount;
    }
}
