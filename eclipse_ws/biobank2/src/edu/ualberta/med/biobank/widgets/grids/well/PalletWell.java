package edu.ualberta.med.biobank.widgets.grids.well;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfoStatus;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.debug.DebugUtil;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.SbsLabeling;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PalletWell extends AbstractUIWell {
    private String information;

    private String title = StringUtil.EMPTY_STRING;

    private SpecimenWrapper sourceSpecimen;

    private SpecimenWrapper specimen;

    private DecodedWell decodedWell;

    private SpecimenWrapper expectedSpecimen;

    public PalletWell(DecodedWell scanCell) {
        this.decodedWell = scanCell;
    }

    public static Map<RowColPos, PalletWell> convertArray(
        Set<DecodedWell> decodedWells) {
        Map<RowColPos, PalletWell> palletScanned =
            new TreeMap<RowColPos, PalletWell>();
        for (DecodedWell decodedWell : decodedWells) {
            palletScanned.put(SbsLabeling.toRowCol(decodedWell.getLabel()),
                new PalletWell(decodedWell));
        }
        return palletScanned;
    }

    static Set<DecodedWell> getRandomDecodedCells() {
        Set<DecodedWell> result = new HashSet<DecodedWell>();
        Random random = new Random();
        for (int indexRow = 0; indexRow < 8; indexRow++) {
            for (int indexCol = 0; indexCol < 12; indexCol++) {
                StringBuffer digits = new StringBuffer();
                if (random.nextBoolean()) {
                    for (int i = 0; i < 10; i++) {
                        digits.append(random.nextInt(10));
                    }
                    result.add(new DecodedWell(SbsLabeling.fromRowCol(indexRow,
                        indexCol), digits.toString()));
                }
            }
        }
        return result;
    }

    public static Map<RowColPos, PalletWell> getRandomScanLink() {
        return convertArray(getRandomDecodedCells());
    }

    public static Map<RowColPos, PalletWell> getRandomScanLinkWithSpecimensAlreadyLinked(
        WritableApplicationService appService, Integer siteId) throws Exception {
        Map<RowColPos, PalletWell> cells =
            convertArray(getRandomDecodedCells());
        List<SpecimenWrapper> specimens = DebugUtil
            .getRandomLinkedAliquotedSpecimens(appService, siteId);
        if (specimens.size() > 1) {
            RowColPos pos = new RowColPos(2, 3);
            DecodedWell decodedWell = new DecodedWell(
                SbsLabeling.fromRowCol(pos), specimens.get(0).getInventoryId());
            cells.put(pos, new PalletWell(decodedWell));

            pos = new RowColPos(3, 1);
            decodedWell = new DecodedWell(SbsLabeling.fromRowCol(pos),
                specimens.get(1).getInventoryId());
            cells.put(pos, new PalletWell(decodedWell));
        }
        return cells;
    }

    public static Map<RowColPos, PalletWell> getRandomSpecimensAlreadyAssigned(
        WritableApplicationService appService, Integer siteId) throws Exception {
        return getRandomSpecimensAlreadyAssigned(appService, siteId, null);
    }

    public static Map<RowColPos, PalletWell> getRandomSpecimensAlreadyAssigned(
        WritableApplicationService appService, Integer siteId, Integer studyId)
        throws Exception {
        Map<RowColPos, PalletWell> palletScanned =
            new HashMap<RowColPos, PalletWell>();
        List<SpecimenWrapper> specimens = DebugUtil.getRandomAssignedSpecimens(
            appService, siteId, studyId);
        if (specimens.size() > 0) {
            RowColPos pos = new RowColPos(0, 0);
            palletScanned.put(pos, new PalletWell(
                new DecodedWell(SbsLabeling.fromRowCol(pos),
                    specimens.get(0).getInventoryId())));
        }
        if (specimens.size() > 1) {
            RowColPos pos = new RowColPos(0, 0);
            palletScanned.put(pos, new PalletWell(
                new DecodedWell(SbsLabeling.fromRowCol(pos),
                    specimens.get(1).getInventoryId())));
        }
        return palletScanned;
    }

    public static Map<RowColPos, PalletWell> getRandomSpecimensNotAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        Map<RowColPos, PalletWell> palletScanned =
            new HashMap<RowColPos, PalletWell>();

        List<SpecimenWrapper> specimens = DebugUtil
            .getRandomNonAssignedNonDispatchedSpecimens(appService, siteId, 30);

        int i = 0;
        for (SpecimenWrapper spc : specimens) {
            RowColPos pos = new RowColPos(i / 12, i % 12);
            palletScanned.put(pos, new PalletWell(
                new DecodedWell(SbsLabeling.fromRowCol(pos), spc
                    .getInventoryId())));
            i++;
        }
        return palletScanned;
    }

    public static Map<RowColPos, PalletWell> getRandomNonDispatchedSpecimens(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        Map<RowColPos, PalletWell> palletScanned =
            new HashMap<RowColPos, PalletWell>();
        List<SpecimenWrapper> randomSpecimens = DebugUtil
            .getRandomNonDispatchedSpecimens(appService, siteId, 30);
        int i = 0;
        while (i < randomSpecimens.size()) {
            RowColPos pos = new RowColPos(i / 12, i % 12);
            palletScanned.put(pos,
                new PalletWell(new DecodedWell(SbsLabeling.fromRowCol(pos),
                    randomSpecimens.get(i).getInventoryId())));
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
        return StringUtil.EMPTY_STRING;
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
        if (decodedWell != null) {
            return decodedWell.getMessage();
        }
        return null;
    }

    @Override
    public Integer getRow() {
        if (decodedWell == null) {
            throw new IllegalStateException("decoded well is null");
        }
        return SbsLabeling.toRowCol(decodedWell.getLabel()).getRow();
    }

    @Override
    public Integer getCol() {
        if (decodedWell == null) {
            throw new IllegalStateException("decoded well is null");
        }
        return SbsLabeling.toRowCol(decodedWell.getLabel()).getCol();
    }

    public void setValue(String value) {
        decodedWell = new DecodedWell(decodedWell.getLabel(), value);
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

    public static boolean hasValue(PalletWell cell) {
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

    public void merge(WritableApplicationService appService, CellInfo cell)
        throws Exception {
        setStatus(cell.getStatus());
        if (cell.getInformation() != null)
            setInformation(cell.getInformation().toString());
        decodedWell = new DecodedWell(decodedWell.getLabel(), cell.getValue());
        setTitle(cell.getTitle().toString());
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
            
            try {
                specimen.setWrappedObject(SessionManager.getAppService()
                    .doAction(new SpecimenGetInfoAction(cell.getSpecimenId()))
                    .getSpecimen());
            } catch (AccessDeniedException e) {
                throw new Exception(e.getLocalizedMessage() + " for specimen with Id " 
                    + cell.getValue());
            }
        }
        setSpecimen(specimen);
    }

    public void setStatus(CellInfoStatus status) {
        if (status != null)
            setStatus(UICellStatus.valueOf(status.name()));
    }

    @SuppressWarnings("deprecation")
    public CellInfo transformIntoServerCell() {
        CellInfo serverCell =
            new CellInfo(getRow(), getCol(), getValue(),
                getStatus() == null ? null : CellInfoStatus.valueOf(getStatus()
                    .name()));
        serverCell.setExpectedSpecimenId(getExpectedSpecimen() == null ? null
            : getExpectedSpecimen().getId());
        if (getStatus() != null)
            serverCell.setStatus(CellInfoStatus.valueOf(getStatus().name()));
        if (getInformation() != null)
            serverCell.setInformation(LString.lit(getInformation()
                .toString()));
        serverCell.setSpecimenId(getSpecimen() == null ? null : getSpecimen()
            .getId());
        serverCell.setTitle(getTitle());
        return serverCell;
    }
}
