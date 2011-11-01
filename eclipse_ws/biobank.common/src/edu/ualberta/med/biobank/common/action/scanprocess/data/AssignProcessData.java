package edu.ualberta.med.biobank.common.action.scanprocess.data;

import java.util.Map;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;

public class AssignProcessData extends ProcessWithPallet {

    private static final long serialVersionUID = 1L;

    private String palletLabel;
    private Integer containerTypeId;
    private Map<RowColPos, Integer> expectedSpecimens;

    // for new pallet
    public AssignProcessData(String label, Integer containerTypeId,
        Map<RowColPos, Integer> expectedSpecimens) {
        super(null);
        palletLabel = label;
        this.containerTypeId = containerTypeId;
        this.expectedSpecimens = expectedSpecimens;
    }

    // existing pallet
    public AssignProcessData(Container pallet) {
        super(pallet.getId());
        this.containerTypeId = pallet.getContainerType().getId();
    }

    public String getPalletLabel(Session session) {
        if (palletId == null)
            return palletLabel;
        return getPallet(session).getLabel();
    }

    public ContainerType getContainerType(
        Session session) {
        if (palletId == null) {
            return ActionUtil.sessionGet(session, ContainerType.class,
                containerTypeId);
        }
        return getPallet(session).getContainerType();
    }

    @Override
    public Integer getPalletRowCapacity(Session session) {
        ContainerType type = ActionUtil.sessionGet(session,
            ContainerType.class, containerTypeId);
        if (type.getCapacity() != null)
            return type.getCapacity().getRowCapacity();
        return null;
    }

    @Override
    public Integer getPalletColCapacity(Session session) {
        ContainerType type = ActionUtil.sessionGet(session,
            ContainerType.class, containerTypeId);
        if (type.getCapacity() != null)
            return type.getCapacity().getColCapacity();
        return null;
    }

    public Specimen getExpectedSpecimen(
        Session session, Integer row, Integer col) {
        if (palletId == null) {
            Integer specimenId = expectedSpecimens.get(new RowColPos(row, col));
            if (specimenId == null)
                return null;
            return ActionUtil.sessionGet(session, Specimen.class, specimenId);
        }
        return getSpecimen(session, row, col);
    }
}
