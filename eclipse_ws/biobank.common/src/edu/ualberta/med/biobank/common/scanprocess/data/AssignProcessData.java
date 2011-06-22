package edu.ualberta.med.biobank.common.scanprocess.data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.server.scanprocess.AssignProcess;
import edu.ualberta.med.biobank.server.scanprocess.ServerProcess;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AssignProcessData extends ProcessWithPallet {

    private static final long serialVersionUID = 1L;

    private String palletLabel;
    private Integer containerTypeId;
    private Integer palletRowCapacity;
    private Integer palletColCapacity;
    private Map<RowColPos, Integer> expectedSpecimens;

    public AssignProcessData(ContainerWrapper pallet) {
        super(pallet);
        // if this is a new pallet, get values we need in this data object
        if (pallet.isNew()) {
            palletLabel = pallet.getLabel();
            ContainerTypeWrapper type = pallet.getContainerType();
            if (type != null) {
                containerTypeId = type.getId();
                palletRowCapacity = type.getRowCapacity();
                palletColCapacity = type.getColCapacity();
            }
            Map<RowColPos, SpecimenWrapper> expectedSpecimensClient = pallet
                .getSpecimens();
            expectedSpecimens = new HashMap<RowColPos, Integer>();
            for (Entry<RowColPos, SpecimenWrapper> entry : expectedSpecimensClient
                .entrySet()) {
                expectedSpecimens.put(entry.getKey(), entry.getValue().getId());
            }
        }
    }

    public String getPalletLabel(WritableApplicationService appService)
        throws Exception {
        if (palletId == null)
            return palletLabel;
        return getPallet(appService).getLabel();
    }

    public ContainerTypeWrapper getContainerType(
        WritableApplicationService appService) throws Exception {
        if (palletId == null) {
            ContainerTypeWrapper ct = new ContainerTypeWrapper(appService);
            ct.getWrappedObject().setId(containerTypeId);
            ct.reload();
            return ct;
        }
        return getPallet(appService).getContainerType();
    }

    public Integer getPalletRowCapacity(WritableApplicationService appService)
        throws Exception {
        if (palletId == null)
            return palletRowCapacity;
        return getPallet(appService).getRowCapacity();
    }

    public Integer getPalletColCapacity(WritableApplicationService appService)
        throws Exception {
        if (palletId == null)
            return palletColCapacity;
        return getPallet(appService).getColCapacity();
    }

    public SpecimenWrapper getExpectedSpecimen(
        WritableApplicationService appService, Integer row, Integer col)
        throws Exception {
        if (palletId == null) {
            Integer specimenId = expectedSpecimens.get(new RowColPos(row, col));
            if (specimenId == null)
                return null;
            SpecimenWrapper spec = new SpecimenWrapper(appService);
            spec.getWrappedObject().setId(specimenId);
            spec.reload();
            return spec;
        }
        return getPallet(appService).getSpecimen(row, col);
    }

    @Override
    public ServerProcess getProcessInstance(
        WritableApplicationService appService, User user, Locale locale) {
        return new AssignProcess(appService, this, user, locale);
    }

}
