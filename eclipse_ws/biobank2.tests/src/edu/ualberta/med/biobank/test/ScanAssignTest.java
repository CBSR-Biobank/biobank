package edu.ualberta.med.biobank.test;

import edu.ualberta.med.biobank.common.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.Arrays;
import java.util.List;

public class ScanAssignTest {

    private WritableApplicationService appService;
    private ContainerWrapper currentPalletWrapper;

    private List<ContainerTypeWrapper> palletContainerTypes;

    private SiteWrapper site;
    private ContainerWrapper palletFoundWithProductBarcode;
    private String palletFoundWithProductBarcodeLabel;

    public static void main(String[] args) throws Exception {
        new ScanAssignTest().test();
    }

    public void test() throws Exception {
        appService = ServiceConnection.getAppService(
            "https://localhost:8443/biobank2", "testuser", "test");
        site = SiteWrapper.getSites(appService).get(0);
        getPalletContainerTypes();

        currentPalletWrapper = new ContainerWrapper(appService);
        currentPalletWrapper.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        currentPalletWrapper.setSite(site);
        currentPalletWrapper.setProductBarcode("qqqqq");
        boolean ok = validateValues();
        if (ok) {
            currentPalletWrapper.setLabel("05BW03");
            ok = validateValues();
            if (ok) {
                currentPalletWrapper.setContainerType(palletContainerTypes
                    .get(2));
                currentPalletWrapper.setLabel("05BW08");
                ok = validateValues();
                if (ok) {
                    currentPalletWrapper.persist();
                } else {
                    System.out.println("cannot save (3)");
                }
            } else {
                System.out.println("cannot save (2)");
            }
        } else {
            System.out.println("cannot save (1)");
        }
    }

    protected boolean validateValues() throws Exception {
        if (isProductBarcodeValid()) {
            boolean canLaunch = true;
            boolean exists = getExistingPalletFromProductBarcode();
            if (!exists && isLabelValid()) {
                canLaunch = checkPallet();
            }
            return canLaunch;
        }
        return false;
    }

    private boolean getExistingPalletFromProductBarcode() throws Exception {
        palletFoundWithProductBarcode = ContainerWrapper
            .getContainerWithProductBarcodeInSite(appService, site,
                currentPalletWrapper.getProductBarcode());
        if (palletFoundWithProductBarcode == null) {
            // no pallet found with this barcode
            System.out.println("setTypes dans gui");
            return false;
        } else {
            // a pallet has been found
            currentPalletWrapper.initObjectWith(palletFoundWithProductBarcode);
            currentPalletWrapper.reset();
            palletFoundWithProductBarcodeLabel = palletFoundWithProductBarcode
                .getLabel();
            return true;
        }
    }

    public boolean isProductBarcodeValid() {
        return (currentPalletWrapper.getProductBarcode() != null)
            && !currentPalletWrapper.getProductBarcode().isEmpty();
    }

    public boolean isLabelValid() {
        return (currentPalletWrapper.getLabel() != null)
            && !currentPalletWrapper.getLabel().isEmpty();
    }

    private void getPalletContainerTypes() throws Exception {
        palletContainerTypes = ContainerTypeWrapper.getContainerTypesInSite(
            appService, site, "pallet", false);
        if (palletContainerTypes.size() == 0) {
            throw new Exception("pas de type trouve");
        }
    }

    private boolean checkPallet() throws Exception {
        boolean canContinue = true;
        boolean needToCheckPosition = true;
        ContainerTypeWrapper type = currentPalletWrapper.getContainerType();
        if (palletFoundWithProductBarcode != null) {
            // a pallet with this product barcode already exists in the
            // database.
            // need to compare with this value, in case the container has
            // been copied to the current pallet
            if (palletFoundWithProductBarcodeLabel.equals(currentPalletWrapper
                .getLabel())) {
                // The position already contains this pallet. Don't need to
                // check it. Need to use exact same retrieved wrappedObject.
                currentPalletWrapper
                    .initObjectWith(palletFoundWithProductBarcode);
                currentPalletWrapper.reset();
                needToCheckPosition = false;
            } else {
                System.out.println("demande si bouge pallet");
                return false;
            }
            if (type != null) {
                System.out.println("type=" + type.getName());
            }
        }
        if (needToCheckPosition) {
            canContinue = checkAndSetPosition(type);
        }
        return canContinue;
    }

    private boolean checkAndSetPosition(ContainerTypeWrapper typeFixed)
        throws Exception {
        List<ContainerTypeWrapper> palletTypes = palletContainerTypes;
        if (typeFixed != null) {
            palletTypes = Arrays.asList(typeFixed);
        }
        // search for containers at this position, with type in one of the type
        // listed
        List<ContainerWrapper> containersAtPosition = currentPalletWrapper
            .getContainersWithSameLabelWithType(palletTypes);
        String palletLabel = currentPalletWrapper.getLabel();
        if (containersAtPosition.size() == 0) {
            currentPalletWrapper.setPositionAndParentFromLabel(palletLabel,
                palletTypes);
            palletTypes = palletContainerTypes;
            typeFixed = null;
        } else if (containersAtPosition.size() == 1) {
            // One container found
            ContainerWrapper containerAtPosition = containersAtPosition.get(0);
            String barcode = containerAtPosition.getProductBarcode();
            if (((barcode != null) && !barcode.isEmpty())
                || containerAtPosition.hasAliquots()) {
                // Position already physically used
                System.out.println("position already used");
                return false;
            }
            // Position initialised but not physically used
            palletTypes = Arrays.asList(containerAtPosition.getContainerType());
            typeFixed = containerAtPosition.getContainerType();
            // new pallet. Can use the initialised one
            String productBarcode = currentPalletWrapper.getProductBarcode();
            currentPalletWrapper.initObjectWith(containerAtPosition);
            currentPalletWrapper.reset();
            currentPalletWrapper.setProductBarcode(productBarcode);
        } else {
            System.out.println("more than one pallet");
            return false;
        }
        System.out.println("set types list");
        return true;
    }
}
