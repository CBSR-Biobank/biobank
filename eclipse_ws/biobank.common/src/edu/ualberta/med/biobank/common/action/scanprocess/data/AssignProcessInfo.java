package edu.ualberta.med.biobank.common.action.scanprocess.data;

import java.util.Map;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;

public class AssignProcessInfo extends AbstractProcessPalletInfo {

    private static final long serialVersionUID = 1L;

    private String palletLabel;
    private Integer containerTypeId;


    // existing pallet
    public AssignProcessInfo(Container pallet) {
        super(pallet.getId());
        this.containerTypeId = pallet.getContainerType().getId();
        if (palletId == null) {
        	palletLabel = pallet.getLabel();
        }
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
        if (palletId == null) 
        	return null;
        return getSpecimen(session, row, col);
    }
}
