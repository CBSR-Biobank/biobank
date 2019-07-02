package edu.ualberta.med.biobank.tools.cli.command.batchoperation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.batchoperation.patient.PatientBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.common.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.common.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.batchoperation.patient.PatientBatchOpPojoReader;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.cli.AppServiceUtils;
import edu.ualberta.med.biobank.tools.cli.CliProvider;
import edu.ualberta.med.biobank.tools.cli.CsvUtils;
import edu.ualberta.med.biobank.tools.cli.command.Command;

public class PatientImportCommand extends Command {

    private static final Logger LOG = LoggerFactory.getLogger(PatientImportCommand.class.getName());

    protected static final String NAME = "patient_import_csv";

    protected static final String HELP = "Used to import patients from a CSV file.";

    protected static final String USAGE =
        NAME
            + " SITE_NAME_SHORT CSV_FILE [ignore-duplicates]"
            + "\n\nSITE_NAME_SHORT is the short name of the working center you wish to use to import the file from.\n\n"
            + "If \"ignore-duplicates\" is specified, patients with patient numbers already in the database are ignored.\n\n"
            + BatchOpUsage.USAGE;

    private Map<String, Site> sites;

    private IBatchOpPojoReader<PatientBatchOpInputPojo> pojoReader;

    public PatientImportCommand(CliProvider cliProvider) {
        super(cliProvider, NAME, HELP, USAGE);
    }

    @Override
    public boolean runCommand(String[] args) {
        if ((args.length < 3) || (args.length > 4)) {
            System.out.println(USAGE);
            return false;
        }

        final String siteName = args[1];
        final String csvFile = args[2];
        boolean ignoreDuplicates = false;

        if (args.length > 3) {
            if (args[3].equals("ignore-duplicates")) {
                ignoreDuplicates = true;
            } else {
                System.out.println(USAGE);
                return false;
            }
        }

        if (ignoreDuplicates) {
            LOG.info("ignoring duplicates");
        }

        try {
            BiobankApplicationService appService = cliProvider.getAppService();

            ICsvBeanReader reader = new CsvBeanReader(
                new FileReader(csvFile), CsvPreference.EXCEL_PREFERENCE);

            String[] csvHeaders = reader.getCSVHeader(true);

            if (!PatientBatchOpPojoReader.isHeaderValid(csvHeaders)) {
                System.out.println("CSV file has invalid headers");
                return false;
            }

            sites = AppServiceUtils.getSitesByNameShort(appService);
            Site site = sites.get(siteName);

            if (site == null) {
                System.out.println("site is invalid: " + siteName);
                return false;
            }

            pojoReader = new PatientBatchOpPojoReader(site, csvFile, ignoreDuplicates);

            LOG.info("Reading CSV file: {}", csvFile);
            pojoReader.readPojos(reader);

            LOG.info("Sending patient import to server");
            appService.doAction(pojoReader.getAction()).getId();
            LOG.info("Patient import done");
            return true;
        } catch (ClientBatchOpErrorsException e) {
            for (BatchOpException<String> error : e.getErrors()) {
                System.out.println("Error: line " + error.getLineNumber() + " :"
                    + error.getMessage());
            }
        } catch (BatchOpErrorsException e) {
            CsvUtils.showErrorsInLog(e);
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist: " + csvFile);
        } catch (IOException e) {
            System.out.println("Could not parse csv file: " + csvFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
