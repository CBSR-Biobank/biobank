package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.DispatchPositionWrapper;
import edu.ualberta.med.biobank.model.DispatchContainer;
import edu.ualberta.med.biobank.model.DispatchPosition;
import edu.ualberta.med.biobank.model.DispatchShipment;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchContainerWrapper extends
    AbstractContainerWrapper<DispatchContainer> {

    private DispatchShipmentWrapper shipment;

    public DispatchContainerWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchContainerWrapper(WritableApplicationService appService,
        DispatchContainer container) {
        super(appService, container);
    }

    @Override
    public Class<DispatchContainer> getWrappedClass() {
        return DispatchContainer.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        if (getShipment() == null) {
            throw new BiobankCheckException("Shipment cannot be null");
        }
        if (getProductBarcode() == null) {
            throw new BiobankCheckException("Barcode should not be null");
        }
        super.persistChecks();
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    @Override
    protected String[] getPropertyChangeNames() {
        String[] names = super.getPropertyChangeNames();
        List<String> namesList = new ArrayList<String>(Arrays.asList(names));
        namesList.addAll(Arrays.asList("shipment"));
        return namesList.toArray(new String[namesList.size()]);
    }

    @Override
    public int compareTo(ModelWrapper<DispatchContainer> o) {
        return 0;
    }

    public DispatchShipmentWrapper getShipment() {
        if (shipment == null) {
            DispatchShipment s = wrappedObject.getShipment();
            if (s == null)
                return null;
            shipment = new DispatchShipmentWrapper(appService, s);
        }
        return shipment;
    }

    public void setShipment(DispatchShipmentWrapper s) {
        this.shipment = s;
        DispatchShipment oldShipment = wrappedObject.getShipment();
        DispatchShipment newShipment = s.getWrappedObject();
        wrappedObject.setShipment(newShipment);
        propertyChangeSupport.firePropertyChange("shipment", oldShipment,
            newShipment);
    }

    @Override
    public SiteWrapper getSite() {
        if (getShipment() != null) {
            getShipment().getSender();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Map<RowColPos, AliquotWrapper> getAliquots() {
        Map<RowColPos, AliquotWrapper> aliquots = (Map<RowColPos, AliquotWrapper>) propertiesMap
            .get("aliquots");
        if (aliquots == null) {
            Collection<DispatchPosition> positions = wrappedObject
                .getPositionCollection();
            if (positions != null) {
                aliquots = new TreeMap<RowColPos, AliquotWrapper>();
                for (DispatchPosition position : positions) {
                    AliquotWrapper aliquot = new AliquotWrapper(appService,
                        position.getAliquot());
                    try {
                        aliquot.reload();
                    } catch (Exception e) {
                    }
                    aliquots.put(
                        new RowColPos(position.getRow(), position.getCol()),
                        aliquot);
                }
                propertiesMap.put("aliquots", aliquots);
            }
        }
        return aliquots;
    }

    public boolean hasAliquots() {
        Collection<DispatchPosition> positions = wrappedObject
            .getPositionCollection();
        return ((positions != null) && (positions.size() > 0));
    }

    public AliquotWrapper getAliquot(Integer row, Integer col)
        throws BiobankCheckException {
        DispatchPositionWrapper position = new DispatchPositionWrapper(
            appService);
        position.setRow(row);
        position.setCol(col);
        position.checkPositionValid(this);
        Map<RowColPos, AliquotWrapper> aliquots = getAliquots();
        if (aliquots == null) {
            return null;
        }
        return aliquots.get(new RowColPos(row, col));
    }

    public void addAliquot(Integer row, Integer col, AliquotWrapper aliquot)
        throws Exception {
        DispatchPositionWrapper aliquotPosition = new DispatchPositionWrapper(
            appService);
        aliquotPosition.setRow(row);
        aliquotPosition.setCol(col);
        aliquotPosition.checkPositionValid(this);
        Map<RowColPos, AliquotWrapper> aliquots = getAliquots();
        if (aliquots == null) {
            aliquots = new TreeMap<RowColPos, AliquotWrapper>();
            propertiesMap.put("aliquots", aliquots);
        } else {
            AliquotWrapper sampleAtPosition = getAliquot(row, col);
            if (sampleAtPosition != null) {
                throw new BiobankCheckException("Container "
                    + getProductBarcode()
                    + " is already holding an aliquot at position "
                    + sampleAtPosition.getPositionString(false, false) + " ("
                    + row + ":" + col + ")");
            }
        }
        aliquotPosition.setAliquot(aliquot);
        aliquotPosition.setContainer(this);
        Collection<DispatchPosition> positions = wrappedObject
            .getPositionCollection();
        positions.add(aliquotPosition.getWrappedObject());
        wrappedObject.setPositionCollection(positions);
        aliquots.put(new RowColPos(row, col), aliquot);
    }
}
