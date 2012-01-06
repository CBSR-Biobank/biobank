package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.reports.QueryCommand;
import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest.CommandType;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.ProtectionGroupPrivilege;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.PrintedSsInvItem;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankServerException;
import edu.ualberta.med.biobank.server.logging.MessageGenerator;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import edu.ualberta.med.biobank.server.scanprocess.ServerProcess;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.util.ClassCache;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.DMinMax;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Implementation of the BiobankApplicationService interface. This class will be
 * only on the server side.
 * 
 * See build.properties of the sdk for the generator configuration +
 * application-config*.xml for the generated files.
 */
public class BiobankApplicationServiceImpl extends
    WritableApplicationServiceImpl implements BiobankApplicationService {

    public BiobankApplicationServiceImpl(ClassCache classCache) {
        super(classCache);
    }

    /**
     * How can we manage security using sql ??
     */
    @Override
    public <E> List<E> query(BiobankSQLCriteria sqlCriteria,
        String targetClassName) throws ApplicationException {
        return privateQuery(sqlCriteria, targetClassName);
    }

    @Override
    public void logActivity(String action, String site, String patientNumber,
        String inventoryID, String locationLabel, String details, String type)
        throws Exception {
        Log log = new Log();
        log.setAction(action);
        log.setCenter(site);
        log.setPatientNumber(patientNumber);
        log.setInventoryId(inventoryID);
        log.setLocationLabel(locationLabel);
        log.setDetails(details);
        log.setType(type);
        logActivity(log);
    }

    /**
     * See log4j.xml: it should contain the Biobank.Activity appender
     */
    @Override
    public void logActivity(Log log) throws Exception {
        Logger logger = Logger.getLogger("Biobank.Activity");
        logger.log(Level.toLevel("INFO"),
            MessageGenerator.generateStringMessage(log));
    }

    @Override
    public List<Object> runReport(Report report, int maxResults, int firstRow,
        int timeout) throws ApplicationException {

        ReportData reportData = new ReportData(report);
        reportData.setMaxResults(maxResults);
        reportData.setFirstRow(firstRow);
        reportData.setTimeout(timeout);

        Request request = new Request(reportData);
        request.setIsCount(Boolean.FALSE);
        request.setFirstRow(0);
        request.setDomainObjectName(Report.class.getName());

        Response response = query(request);

        @SuppressWarnings("unchecked")
        List<Object> results = (List<Object>) response.getResponse();

        return results;
    }

    @Override
    public QueryHandle createQuery(QueryCommand qc) throws Exception {
        QueryHandleRequest qhr = new QueryHandleRequest(qc, CommandType.CREATE,
            null, this);
        return (QueryHandle) getWritableDAO(Site.class.getName()).query(
            new Request(qhr)).getResponse();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> startQuery(QueryHandle qh) throws Exception {
        QueryHandleRequest qhr = new QueryHandleRequest(null,
            CommandType.START, qh, this);
        return (List<Object>) getWritableDAO(Site.class.getName()).query(
            new Request(qhr)).getResponse();
    }

    @Override
    public void stopQuery(QueryHandle qh) throws Exception {
        QueryHandleRequest qhr = new QueryHandleRequest(null, CommandType.STOP,
            qh, this);
        getWritableDAO(Site.class.getName()).query(new Request(qhr))
            .getResponse();
    }

    @Override
    public void modifyPassword(String oldPassword, String newPassword)
        throws ApplicationException {
        BiobankSecurityUtil.modifyPassword(oldPassword, newPassword);
    }

    @Override
    public List<Group> getSecurityGroups(User currentUser,
        boolean includeSuperAdmin) throws ApplicationException {
        currentUser.initCurrentWorkingCenter(this);
        return BiobankSecurityUtil.getSecurityGroups(currentUser,
            includeSuperAdmin);
    }

    @Override
    public List<User> getSecurityUsers(User currentUser)
        throws ApplicationException {
        currentUser.initCurrentWorkingCenter(this);
        return BiobankSecurityUtil.getSecurityUsers(currentUser);
    }

    @Override
    public User persistUser(User currentUser, User userToPersist)
        throws ApplicationException {
        currentUser.initCurrentWorkingCenter(this);
        return BiobankSecurityUtil.persistUser(currentUser, userToPersist);
    }

    @Override
    public void deleteUser(User currentUser, String loginToDelete)
        throws ApplicationException {
        currentUser.initCurrentWorkingCenter(this);
        BiobankSecurityUtil.deleteUser(currentUser, loginToDelete);
    }

    @Override
    public User getCurrentUser() throws ApplicationException {
        return BiobankSecurityUtil.getCurrentUser();
    }

    @Override
    public Group persistGroup(Group group) throws ApplicationException {
        return BiobankSecurityUtil.persistGroup(group);
    }

    @Override
    public void deleteGroup(Group group) throws ApplicationException {
        BiobankSecurityUtil.deleteGroup(group);
    }

    @Override
    public void unlockUser(User currentUser, String userNameToUnlock)
        throws ApplicationException {
        currentUser.initCurrentWorkingCenter(this);
        BiobankSecurityUtil.unlockUser(currentUser, userNameToUnlock);
    }

    @Override
    public List<ProtectionGroupPrivilege> getSecurityGlobalFeatures(
        User currentUser) throws ApplicationException {
        currentUser.initCurrentWorkingCenter(this);
        return BiobankSecurityUtil.getSecurityGlobalFeatures(currentUser);
    }

    @Override
    public List<ProtectionGroupPrivilege> getSecurityCenterFeatures(
        User currentUser) throws ApplicationException {
        currentUser.initCurrentWorkingCenter(this);
        return BiobankSecurityUtil.getSecurityCenterFeatures(currentUser);
    }

    @Override
    public void checkVersion(String clientVersion) throws ApplicationException {
        BiobankVersionUtil.checkVersion(clientVersion);
    }

    @Override
    public String getServerVersion() {
        return BiobankVersionUtil.getServerVersion();
    }

    @Override
    public ScanProcessResult processScanResult(Map<RowColPos, Cell> cells,
        ProcessData processData, boolean isRescanMode, User user)
        throws ApplicationException {
        try {
            ServerProcess process = processData.getProcessInstance(this, user);
            return process.processScanResult(cells, isRescanMode);
        } catch (ApplicationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public CellProcessResult processCellStatus(Cell cell,
        ProcessData processData, User user) throws ApplicationException {
        try {
            ServerProcess process = processData.getProcessInstance(this, user);
            return process.processCellStatus(cell);
        } catch (ApplicationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private static final int SS_INV_ID_LENGTH = 12;

    private static final String SS_INV_ID_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int SS_INV_ID_ALPHABET_LENGTH = SS_INV_ID_ALPHABET
        .length();

    private static final int SS_INV_ID_GENERATE_RETRIES = (int) Math.pow(
        SS_INV_ID_ALPHABET_LENGTH, SS_INV_ID_ALPHABET_LENGTH);

    private static final String SS_INV_ID_UNIQ_BASE_QRY = "SELECT count(*) "
        + "FROM printed_ss_inv_item where txt=\"{id}\"";

    @Override
    public List<String> executeGetSourceSpecimenUniqueInventoryIds(int numIds)
        throws ApplicationException {
        boolean isUnique;
        int genRetries;
        Random r = new Random();
        StringBuilder newInvId;
        List<String> result = new ArrayList<String>();

        while (result.size() < numIds) {
            isUnique = false;
            genRetries = 0;
            newInvId = new StringBuilder();

            while (!isUnique && (genRetries < SS_INV_ID_GENERATE_RETRIES)) {
                for (int j = 0; j < SS_INV_ID_LENGTH; ++j) {
                    newInvId.append(SS_INV_ID_ALPHABET.charAt(r
                        .nextInt(SS_INV_ID_ALPHABET_LENGTH)));
                    genRetries++;
                }

                // check database if string is unique
                String potentialInvId = newInvId.toString();
                String qry = SS_INV_ID_UNIQ_BASE_QRY.replace("{id}",
                    potentialInvId);

                List<BigInteger> count = privateQuery(new BiobankSQLCriteria(
                    qry), PrintedSsInvItem.class.getName());

                if (count.get(0).equals(BigInteger.ZERO)) {
                    // add new inventory id to the database
                    isUnique = true;
                    result.add(potentialInvId);
                    PrintedSsInvItem newInvIdItem = new PrintedSsInvItem();
                    newInvIdItem.setTxt(potentialInvId);
                    SDKQuery query = new InsertExampleQuery(newInvIdItem);
                    executeQuery(query);
                }
            }

            if (genRetries >= SS_INV_ID_GENERATE_RETRIES) {
                // cannot generate any more unique strings
                throw new BiobankServerException(
                    "cannot generate any more source specimen inventory IDs");
            }

        }
        return result;
    }

    @Override
    public String uploadFile(byte[] bytes, String deviceID)
        throws ApplicationException {

        System.out.printf("Came From Client: %s", deviceID);
        String uploadDir = System.getProperty("upload.dir");

        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MMM_dd-HH_mm");
        String dateNow = formatter.format(currentDate.getTime());

        String newFile = uploadDir + "/" + dateNow + "_ID_" + deviceID + ".pdf";
        File fl = new File(newFile);

        try {
            boolean success = fl.createNewFile();
            if (success) {
                // File did not exist and was created
            } else {
                // File already exists
            }
        } catch (IOException e) {
        }
        try {
            FileUtils.writeByteArrayToFile(fl, bytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newFile;
    }

    @Override
    public List<String> tecanloadFile(byte[] bytes, String pWorkSheet,
        String pComment) throws ApplicationException {

        String uploadDir = System.getProperty("upload.dir");

        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MMM_dd-HH_mm");
        String dateNow = formatter.format(currentDate.getTime());

        String newFile = uploadDir + "/" + dateNow + "_ID_" + "currentSite"
            + ".csv";
        File fl = new File(newFile);

        List<String> processed = new ArrayList<String>();

        try {
            boolean success = fl.createNewFile();
            if (success) {
                // File did not exist and was created
            } else {
                // File already exists
            }
        } catch (IOException e) {
        }
        try {
            FileUtils.writeByteArrayToFile(fl, bytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            processed = tecanProcessCVS(newFile, pWorkSheet, pComment);
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }

        return processed;
    }

    public List<String> tecanProcessCVS(String myfile, String pWorkSheet,
        String pComment) throws Exception {

        // User cUser = this.getCurrentUser();
        // cUser.initCurrentWorkingCenter(this);
        // SiteWrapper cSite = cUser.getCurrentWorkingSite();
        // String GGG = cSite.getNameShort();
        // System.out.println("SITE GGG: " + GGG);
        System.out.println("FILE: " + myfile);
        List<String> processed = new ArrayList<String>();
        ICsvBeanReader inFile = new CsvBeanReader(new FileReader(myfile),
            CsvPreference.EXCEL_PREFERENCE);

        // final CellProcessor[] userProcessors = new CellProcessor[] {
        // new Unique(new StrMinMax(5, 20)), new StrMinMax(8, 35),
        // new ParseDate("dd/MM/yyyy"), new Optional(new ParseInt()),
        // new StrMinMax(1, 35) };
        // private String orgSample;
        // private String processId;
        // private String aliquotId;
        // private String aliquotType;
        // private int volume;
        // private Date startProcess;
        // private Date endProcess;

        final CellProcessor[] userProcessors = new CellProcessor[] {
            new StrNotNullOrEmpty(), new StrNotNullOrEmpty(),
            new StrNotNullOrEmpty(), new StrNotNullOrEmpty(),
            new DMinMax(0, Double.MAX_VALUE), new ParseDate("yyyy-MM-dd"),
            new ParseDate("yyyy-MM-dd") };

        try {
            processed.add("Uploaded File: " + myfile);

            final String[] header = inFile.getCSVHeader(true);

            String tmpString = new String();
            TecanCSV tecanCsv;

            System.out.println("Header Length: " + header.length);
            System.out.println("Header: " + header);

            processed.add("Header Length: " + header.length);
            int i = 0;
            tmpString = "";
            while (i < header.length) {
                System.out.println("HEADER" + i + ": " + header[i]);
                tmpString = tmpString + header[i] + ",";
                i++;
            }
            processed.add("HEADER: " + tmpString);

            System.out.println("LENGTH: " + inFile.length());
            System.out.println("Line NUMBER: " + inFile.getLineNumber());
            System.out.println("userProcessors.length: "
                + userProcessors.length);

            while ((tecanCsv = inFile.read(TecanCSV.class, header,
                userProcessors)) != null) {
                // orgSample, processId, aliquotId, aliquotType, volume,
                // startProcess, endProcess
                // String csvLine = tecanCsv.getOrgSample() + ","
                // + tecanCsv.getProcessId() + "," + tecanCsv.getAliquotId()
                // + "," + tecanCsv.getAliquotType() + ","
                // + tecanCsv.getVolume() + "," + tecanCsv.getStartProcess()
                // + "," + tecanCsv.getEndProcess();
                processed.add(tecanCsv.getLine());
            }
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ************************************

        // try {
        // // AddressWrapper address = new AddressWrapper(this);
        // // address.setCity("towmonton");
        // // // address is automatically saved via cascade
        // //
        // // ActivityStatusWrapper active = ActivityStatusWrapper
        // // .getActiveActivityStatus(this);
        // //
        // SiteWrapper currentSite = (SiteWrapper) this.getCurrentUser()
        // .getCurrentWorkingCenter();
        // String tmp = currentSite.getNameShort();
        // // SiteWrapper site = new SiteWrapper(this);
        // // String siteName = "example_" + randString();
        // // site.setActivityStatus(active);
        // // site.setName(siteName);
        // // site.setNameShort(siteName + "_short");
        // // site.setAddress(address);
        // // site.persist();
        // currentSite.setComment("TESTY COMMENT");
        // currentSite.persist();
        //
        // } catch (Exception caught) {
        // // transaction will be rollback if exception thrown
        // caught.printStackTrace();
        // throw new RuntimeException(caught);
        //
        // }

        // ***********************************

        finally {
            inFile.close();
        }
        return processed;
    }

    @Override
    public void wrapperExample(Dummy data) throws ApplicationException {
        try {
            AddressWrapper address = new AddressWrapper(this);
            address.setCity("towmonton");
            // address is automatically saved via cascade

            ActivityStatusWrapper active = ActivityStatusWrapper
                .getActiveActivityStatus(this);

            SiteWrapper site = new SiteWrapper(this);
            String siteName = "example_" + randString();
            site.setActivityStatus(active);
            site.setName(siteName);
            site.setNameShort(siteName + "_short");
            site.setAddress(address);
            site.persist();

            // StudyWrapper study = new StudyWrapper(this);
            // String studyName = "example_" + randString();
            // study.setName(studyName);
            // study.setNameShort(studyName + "_short");
            // study.persist();
            //
            // PatientWrapper patient = new PatientWrapper(this);
            // patient.setPnumber(randString());
            // patient.setStudy(study);
            // patient.setCreatedAt(new Date());
            // patient.persist();
            //
            // CollectionEventWrapper cEvent = new CollectionEventWrapper(this);
            // cEvent.setActivityStatus(active);
            // cEvent.setPatient(patient);
            // cEvent.setVisitNumber(1);
            //
            // ProcessingEventWrapper pEvent = new ProcessingEventWrapper(this);
            // pEvent.setCenter(site);
            // pEvent.setCreatedAt(new Date());
            // pEvent.setWorksheet(randString());
            // pEvent.persist();
            //
            // SpecimenWrapper specimen = new SpecimenWrapper(this);
            // specimen.setActivityStatus(active);
            // specimen.setInventoryId(randString());
            // specimen.setCreatedAt(new Date());
            // specimen.setCollectionEvent(cEvent);
            // specimen.setProcessingEvent(pEvent);
            // specimen.persist();
        } catch (Exception caught) {
            // transaction will be rollback if exception thrown
            throw new RuntimeException(caught);
        }
    }

    private static final Random R = new Random();

    private static String randString() {
        return new BigInteger(130, R).toString(32);
    }

    @Override
    public void sessionExample(Dummy data) throws ApplicationException {
        ExampleRequestData example = new ExampleRequestData();
        example.data = data;

        Request request = new Request(example);
        request.setIsCount(Boolean.FALSE);
        request.setFirstRow(0);

        // used for security checks
        request.setDomainObjectName(ProcessingEvent.class.getName());

        Response response = query(request);
    }

    public static class ExampleRequestData {
        public Object data;
    }

    // DFE

}
