package edu.ualberta.med.biobank.widgets.grids.cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.debug.DebugUtil;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCellPos;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PalletCell extends AbstractUICell {

    private String information;

    private String title = ""; //$NON-NLS-1$

    private SpecimenWrapper sourceSpecimen;

    private SpecimenWrapper specimen;

    private ScanCell scanCell;

    private SpecimenWrapper expectedSpecimen;

    public PalletCell(ScanCell scanCell) {
        this.scanCell = scanCell;
    }

    public static Map<RowColPos, PalletCell> convertArray(
        List<ScanCell> scancells) {
        Map<RowColPos, PalletCell> palletScanned = new TreeMap<RowColPos, PalletCell>();
        for (ScanCell cell : scancells) {
            palletScanned.put(new RowColPos(cell.getRow(), cell.getColumn()),
                new PalletCell(cell));
        }
        return palletScanned;
    }

    public static Map<RowColPos, PalletCell> getRandomScanLink() {
        return convertArray(ScanCell.getRandom());
    }

    public static Map<RowColPos, PalletCell> getRandomScanLinkWithSpecimensAlreadyLinked(
        WritableApplicationService appService, Integer siteId) throws Exception {
        Map<RowColPos, PalletCell> cells = convertArray(ScanCell.getRandom());
        List<SpecimenWrapper> specimens = DebugUtil
            .getRandomLinkedAliquotedSpecimens(appService, siteId);
        if (specimens.size() > 1) {
            int row = 2;
            int col = 3;
            ScanCell scanCell = new ScanCell(row, col, specimens.get(0)
                .getInventoryId());
            cells.put(new RowColPos(row, col), new PalletCell(scanCell));
            row = 3;
            col = 1;
            scanCell = new ScanCell(row, col, specimens.get(1).getInventoryId());
            cells.put(new RowColPos(row, col), new PalletCell(scanCell));
        }
        return cells;
    }

    public static Map<RowColPos, PalletCell> getRandomSpecimensAlreadyAssigned(
        WritableApplicationService appService, Integer siteId) throws Exception {
        return getRandomSpecimensAlreadyAssigned(appService, siteId, null);
    }

    public static Map<RowColPos, PalletCell> getRandomSpecimensAlreadyAssigned(
        WritableApplicationService appService, Integer siteId, Integer studyId)
        throws Exception {
        Map<RowColPos, PalletCell> palletScanned = new HashMap<RowColPos, PalletCell>();
        List<SpecimenWrapper> specimens = DebugUtil.getRandomAssignedSpecimens(
            appService, siteId, studyId);
        if (specimens.size() > 0) {
            palletScanned.put(new RowColPos(0, 0), new PalletCell(new ScanCell(
                0, 0, specimens.get(0).getInventoryId())));
        }
        if (specimens.size() > 1) {
            palletScanned.put(new RowColPos(2, 4), new PalletCell(new ScanCell(
                2, 4, specimens.get(1).getInventoryId())));
        }
        return palletScanned;
    }

    public static Map<RowColPos, PalletCell> getRandomSpecimensNotAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        Map<RowColPos, PalletCell> palletScanned = new HashMap<RowColPos, PalletCell>();
        List<SpecimenWrapper> specimens = DebugUtil
            .getRandomNonAssignedNonDispatchedSpecimens(appService, siteId, 30);
        int i = 0;
        while (i < specimens.size() && i < 30) {
            int row = i / 12;
            int col = i % 12;
            palletScanned.put(new RowColPos(row, col), new PalletCell(
                new ScanCell(row, col, specimens.get(i).getInventoryId())));
            i++;
        }
        return palletScanned;
    }

    public static Map<RowColPos, PalletCell> getRandomNonDispatchedSpecimens(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        Map<RowColPos, PalletCell> palletScanned = new HashMap<RowColPos, PalletCell>();
        List<SpecimenWrapper> randomSpecimens = DebugUtil
            .getRandomNonDispatchedSpecimens(appService, siteId, 30);
        int i = 0;
        while (i < randomSpecimens.size()) {
            int row = i / 12;
            int col = i % 12;
            palletScanned.put(new RowColPos(row, col),
                new PalletCell(new ScanCell(row, col, randomSpecimens.get(i)
                    .getInventoryId())));
            i++;
        }
        return palletScanned;
    }

    /**
     * usually displayed in the middle of the cell
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Usually used for the tooltip of the cell
     * 
     * @return
     */
    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getTypeString() {
        if (specimen != null && specimen.getSpecimenType() != null) {
            SpecimenTypeWrapper type = specimen.getSpecimenType();
            if (type.getNameShort() != null) {
                return type.getNameShort();
            }
            return type.getName();
        }
        return ""; //$NON-NLS-1$
    }

    public SpecimenTypeWrapper getType() {
        if (specimen == null)
            return null;
        return specimen.getSpecimenType();
    }

    public void setSpecimenType(SpecimenTypeWrapper type) {
        if (specimen == null) {
            specimen = new SpecimenWrapper(SessionManager.getAppService());
        }
        specimen.setSpecimenType(type);
    }

    public void setSpecimen(SpecimenWrapper specimen) {
        this.specimen = specimen;
    }

    public SpecimenWrapper getSpecimen() {
        return specimen;
    }

    public String getValue() {
        if (scanCell != null) {
            return scanCell.getValue();
        }
        return null;
    }

    public void setValue(String value) {
        if (scanCell != null) {
            scanCell.setValue(value);
        }
    }

    @Override
    public Integer getRow() {
        if (scanCell != null) {
            return scanCell.getRow();
        }
        return null;
    }

    @Override
    public Integer getCol() {
        if (scanCell != null) {
            return scanCell.getColumn();
        }
        return null;
    }

    public RowColPos getRowColPos() {
        RowColPos rcp = null;
        Integer row = getRow();
        Integer col = getCol();
        if (row != null && col != null) {
            rcp = new RowColPos(row, col);
        }
        return rcp;
    }

    public static boolean hasValue(PalletCell cell) {
        return cell != null && cell.getValue() != null;
    }

    public void setExpectedSpecimen(SpecimenWrapper expectedSpecimen) {
        this.expectedSpecimen = expectedSpecimen;
    }

    public SpecimenWrapper getExpectedSpecimen() {
        return expectedSpecimen;
    }

    public void setSourceSpecimen(SpecimenWrapper sourceSpecimen) {
        this.sourceSpecimen = sourceSpecimen;
    }

    public SpecimenWrapper getSourceSpecimen() {
        return sourceSpecimen;
    }

    public void merge(WritableApplicationService appService,
        edu.ualberta.med.biobank.common.scanprocess.Cell cell) throws Exception {
        setStatus(cell.getStatus());
        setInformation(cell.getInformation());
        setValue(cell.getValue());
        setTitle(cell.getTitle());
        SpecimenWrapper expectedSpecimen = null;
        if (cell.getExpectedSpecimenId() != null) {
            expectedSpecimen = new SpecimenWrapper(appService);
            expectedSpecimen.getWrappedObject().setId(
                cell.getExpectedSpecimenId());
            expectedSpecimen.reload();
        }
        setExpectedSpecimen(expectedSpecimen);
        SpecimenWrapper specimen = null;
        if (cell.getSpecimenId() != null) {
            specimen = new SpecimenWrapper(appService);
            specimen.getWrappedObject().setId(cell.getSpecimenId());
            specimen.reload();
        }
        setSpecimen(specimen);
    }

    public void setStatus(CellStatus status) {
        if (status != null)
            setStatus(UICellStatus.valueOf(status.name()));
    }

    public Cell transformIntoServerCell() {
        Cell serverCell = new Cell(getRow(), getCol(), getValue(),
            getStatus() == null ? null : CellStatus.valueOf(getStatus().name()));
        serverCell.setExpectedSpecimenId(getExpectedSpecimen() == null ? null
            : getExpectedSpecimen().getId());
        if (getStatus() != null)
            serverCell.setStatus(CellStatus.valueOf(getStatus().name()));
        serverCell.setInformation(getInformation());
        serverCell.setSpecimenId(getSpecimen() == null ? null : getSpecimen()
            .getId());
        serverCell.setTitle(getTitle());
        return serverCell;
    }
}
