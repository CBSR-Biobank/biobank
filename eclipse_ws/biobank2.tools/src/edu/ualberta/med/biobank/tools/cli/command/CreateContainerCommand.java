package edu.ualberta.med.biobank.tools.cli.command;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.container.ContainerGetContainerOrParentsByLabelAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetContainerOrParentsByLabelAction.ContainerData;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.cli.AppServiceUtils;
import edu.ualberta.med.biobank.tools.cli.CliProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CreateContainerCommand extends Command {

    private static final Logger LOG = LoggerFactory.getLogger(CreateContainerCommand.class.getName());

    private static final String NAME = "create_container_csv";

    private static final String USAGE = NAME + " CSV_FILE";

    private static final String HELP = "Used to import containers from a CSV file.";

    private BiobankApplicationService appService;

    private Map<String, Site> sites;

    private final Map<String, Container> parentContainers = new HashMap<String, Container>();

    private final Map<String, ContainerType> containerTypes = new HashMap<String, ContainerType>();

    private final Set<String> errors = new LinkedHashSet<String>(0);

    public CreateContainerCommand(CliProvider cliProvider) {
        super(cliProvider, NAME, HELP, USAGE);
    }

    @Override
    public boolean runCommand(String[] args) {
        if (args.length != 2) {
            System.out.println(USAGE);
            return false;
        }

        final String csvFile = args[1];

        try {
            appService = cliProvider.getAppService();
            sites = AppServiceUtils.getSitesByNameShort(appService);
            final Set<ContainerInfoPojo> pojos = readCsvFile(csvFile);
            validatePojos(pojos);

            if (!errors.isEmpty()) {
                for (String error : errors) {
                    System.out.println(error);
                }
                return false;
            }

            LOG.info("no errors found");

            for (ContainerInfoPojo info : pojos) {
                ContainerSaveAction action = new ContainerSaveAction();

                action.barcode = info.getProductBarcode();
                action.typeId = containerTypes.get(info.getContainerTypeNameShort()).getId();
                action.activityStatus = ActivityStatus.ACTIVE;
                action.siteId = sites.get(info.getSiteNameShort()).getId();
                action.parentId = parentContainers.get(info.getLabel()).getId();
                action.position = new RowColPos(info.getRow(), info.getCol());

                appService.doAction(action);

                LOG.info("container added: {}", info.getLabel());
            }

            System.out.println("containers added.");
            return true;
        } catch (SuperCSVException e) {
            System.out.println("CSV file error: " + e.getCsvContext() + ": " + e.getMessage());
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

    private Set<String> validatePojos(Set<ContainerInfoPojo> pojos) throws ApplicationException {
        for (ContainerInfoPojo info : pojos) {
            final Site site = sites.get(info.getSiteNameShort());
            if (site == null) {
                errors.add("Line " + info.getLineNumber() + ": study name does not exits: "
                    + info.getSiteNameShort());
                continue;
            }

            ContainerData containerData = appService.doAction(
                new ContainerGetContainerOrParentsByLabelAction(info.getLabel(), site));

            List<Container> possibleParents = containerData.getPossibleParentContainers();
            if (possibleParents.isEmpty()) {
                errors.add("Line " + info.getLineNumber()
                    + ": container does not have a parent container: "
                    + info.getLabel());
                continue;
            }

            List<ContainerParentData> parentData = new ArrayList<ContainerParentData>();

            for (Container parent : possibleParents) {
                for (ContainerType childCtype : parent.getContainerType().getChildContainerTypes()) {
                    if (childCtype.getNameShort().equals(info.getContainerTypeNameShort())) {
                        parentData.add(new ContainerParentData(parent, childCtype));
                    }
                }
            }

            if (parentData.isEmpty()) {
                errors.add("Line " + info.getLineNumber()
                    + ": parent container cannot hold children containers of type: "
                    + info.getContainerTypeNameShort());
            } else if (parentData.size() > 1) {
                errors.add("Line " + info.getLineNumber()
                    + ": more than one parent available for container with label: "
                    + info.getLabel());
            }

            if (!errors.isEmpty()) {
                continue;
            }

            ContainerParentData containerInfo = parentData.get(0);
            parentContainers.put(info.getLabel(), containerInfo.parentContainer);
            containerTypes.put(info.getContainerTypeNameShort(), containerInfo.containerType);
        }
        return errors;
    }

    private Set<ContainerInfoPojo> readCsvFile(String filename) throws Exception {
        final Set<ContainerInfoPojo> pojos = new LinkedHashSet<ContainerInfoPojo>(0);
        ICsvBeanReader reader = null;

        try {
            ContainerInfoPojo pojo;

            reader = new CsvBeanReader(
                new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

            String[] csvHeaders = reader.getCSVHeader(true);
            final CellProcessor[] cellProcessors = getCellProcessors();

            while ((pojo = reader.read(
                ContainerInfoPojo.class, csvHeaders, cellProcessors)) != null) {

                pojo.setLineNumber(reader.getLineNumber());
                pojos.add(pojo);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return pojos;
    }

    // cell processors have to be recreated every time the file is read
    public CellProcessor[] getCellProcessors() {
        final CellProcessor[] processors = new CellProcessor[] {
            new NotNull(), // site name
            new NotNull(), // container type name
            new Unique(), // label
            new Unique(new StrNotNullOrEmpty()), // product barcode
            new ParseInt(), // row
            new ParseInt() // col
        };

        return processors;
    }

    public static class ContainerInfoPojo {
        private int lineNumber;
        private String siteNameShort;
        private String containerTypeNameShort;
        private String label;
        private String productBarcode;
        private int row;
        private int col;

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getSiteNameShort() {
            return siteNameShort;
        }

        public void setSiteNameShort(String siteNameShort) {
            this.siteNameShort = siteNameShort;
        }

        public String getContainerTypeNameShort() {
            return containerTypeNameShort;
        }

        public void setContainerTypeNameShort(String containerTypeName) {
            this.containerTypeNameShort = containerTypeName;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getProductBarcode() {
            return productBarcode;
        }

        public void setProductBarcode(String productBarcode) {
            this.productBarcode = productBarcode;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }
    }

    private static class ContainerParentData {
        Container parentContainer;
        ContainerType containerType;

        ContainerParentData(Container parentContainer, ContainerType containerType) {
            this.parentContainer = parentContainer;
            this.containerType = containerType;
        }
    }

}
