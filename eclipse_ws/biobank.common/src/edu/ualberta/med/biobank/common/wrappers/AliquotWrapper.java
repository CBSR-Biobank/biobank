package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Log;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;

@SuppressWarnings("unused")
@Deprecated
public class AliquotWrapper {

    private static class Aliquot {

    }

    public AliquotWrapper(WritableApplicationService appService,
        Aliquot wrappedObject) {
    }

    public AliquotWrapper(WritableApplicationService appService) {
    }

    public void persist() throws Exception {
    }

    protected void persistChecks() throws BiobankException,
        ApplicationException {
    }

    public void checkInventoryIdUnique() throws BiobankException,
        ApplicationException {
    }

    public String getFormattedLinkDate() {
        return null;
    }

    public ContainerWrapper getParent() {
        return null;
    }

    public void setParent(ContainerWrapper container) {
    }

    public boolean hasParent() {
        return false;
    }

    public RowColPos getPosition() {
        return null;
    }

    public void setPosition(RowColPos rcp) {
    }

    public String getPositionString() {
        return null;
    }

    private void checkParentAcceptSampleType() {
    }

    public String getSiteString() {
        return null;
    }

    private SiteWrapper getLocation() {
        return null;
    }

    /**
     * Set the position in the given container using the positionString
     */
    public void setAliquotPositionFromString(String positionString,
        ContainerWrapper parentContainer) throws Exception {
    }

    /**
     * Method used to check if the current position of this aliquot is available
     * on the container. Return true if the position is free, false otherwise
     */
    public boolean isPositionFree(ContainerWrapper parentContainer)
        throws ApplicationException {
        return false;
    }

    public String getPositionString(boolean fullString,
        boolean addTopParentShortName) {
        return null;
    }

    private String getPositionStringInParent(RowColPos position,
        ContainerWrapper parent) {
        return null;
    }

    public void setQuantityFromType() {
    }

    public void loadAttributes() throws Exception {
    }

    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    protected static AliquotWrapper getAliquot(
        WritableApplicationService appService, String inventoryId)
        throws ApplicationException, BiobankCheckException {
        return null;
    }

    /**
     * search in all aliquots list. No matter which site added it. If user is
     * not null, will return only aliquot that is linked to a visit which site
     * can be read by the user
     * 
     * @throws BiobankCheckException
     */
    public static AliquotWrapper getAliquot(
        WritableApplicationService appService, String inventoryId, User user)
        throws ApplicationException, BiobankCheckException {
        return null;
    }

    public static List<AliquotWrapper> getAliquotsNonActiveInSite(
        WritableApplicationService appService, SiteWrapper site)
        throws ApplicationException {
        return null;
    }

    public static List<AliquotWrapper> getAliquotsInSiteWithPositionLabel(
        WritableApplicationService appService, SiteWrapper site,
        String positionString) throws ApplicationException,
        BiobankCheckException, BiobankException {
        return null;
    }

    public int compareTo(ModelWrapper<Aliquot> o) {
        return 0;
    }

    public List<DispatchWrapper> getDispatchs() {
        return null;
    }

    protected Log getLogMessage(String action, String site, String details) {
        return null;
    }

    public boolean isActive() {
        return false;
    }

    public boolean isFlagged() {
        return false;
    }

    public ContainerWrapper getTop() {
        return null;
    }

    public List<DispatchAliquotWrapper> getDispatchAliquotCollection() {
        return null;
    }

    public boolean isUsedInDispatch() {
        return isUsedInDispatch(null);
    }

    public boolean isUsedInDispatch(DispatchWrapper excludedShipment) {
        return false;
    }

    protected void resetInternalFields() {
    }

    @Deprecated
    public CenterWrapper getCenter() {
        // TODO new method or old method?
        return null;
    }

    @Deprecated
    public CollectionEventWrapper getCollectionEvent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public void setCollectionEvent(CollectionEventWrapper collectionEvent) {
        // TODO Auto-generated method stub

    }

}
