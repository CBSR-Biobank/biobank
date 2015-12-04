package edu.ualberta.med.biobank.helpers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.container.ContainerGetContainerOrParentsByLabelAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetContainerOrParentsByLabelAction.ContainerData;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.dialogs.select.SelectParentContainerDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.scannerconfig.PalletDimensions;

public class ScanAssignHelper {

    private static final I18n i18n = I18nFactory.getI18n(ScanAssignHelper.class);

    private static Logger LOG = LoggerFactory.getLogger(ScanAssignHelper.class);

    @SuppressWarnings("nls")
    public static boolean isContainerValid(ContainerWrapper palletContainer, String positionText) {
        // a container with this barcode exists
        if (!isPalletScannable(palletContainer)) {
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Values validation"),
                // TR: dialog message
                i18n.tr("A container with this barcode exists but is not a 8*12 or 10*10 container."));
            return false;
        }

        if (!positionText.isEmpty()
            && !positionText.equals(palletContainer.getLabel())) {
            // a label was entered but is different from the one set to the pallet
            // retrieved
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Values validation"),
                // TR: dialog message
                i18n.tr("A pallet with barcode {0} is already used in position {1}.",
                    palletContainer.getProductBarcode(),
                    palletContainer.getFullInfoLabel()));
            return false;
        }
        return true;
    }

    /**
     * Returns the container matching the container label, or if no container exists, creates a
     * child container with a parent container that can have a valid child with the given label.
     * 
     * @param palletLabel the label to search for.
     * 
     * @return The container matching the label.
     */
    @SuppressWarnings("nls")
    public static ContainerWrapper getOrCreateContainerByLabel(String palletLabel) {
        ContainerWrapper container = getContainerByLabel(palletLabel);

        if (container == null) {
            // no container selected
            return null;
        }

        if (container.getLabel().equals(palletLabel)) {
            // only get here if label matches an existing container
            return container;
        }

        // only get here if the label does not match an exising container, and there is 1 or more
        // parent containers that can hold a container with the given label
        ContainerWrapper parentContainer = container;
        try {
            container = parentContainer.getChildByLabel(
                palletLabel.substring(parentContainer.getLabel().length()));

            LOG.debug("getOrCreateContainerByLabel: label: {}, parent container label: {}",
                palletLabel, parentContainer.getLabel());

            if (container == null) {
                container = new ContainerWrapper(SessionManager.getAppService());

                // no container at this position right now, create one
                LOG.debug("getOrCreateContainerByLabel: creating new container at label: {}", palletLabel);
                String childLabel = palletLabel.substring(parentContainer.getLabel().length());
                parentContainer.addChild(childLabel, container);
                container.setParent(parentContainer,
                    parentContainer.getPositionFromLabelingScheme(childLabel));
            }

            return container;
        } catch (Exception ex) {
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Values validation"), ex);
        }
        return null;
    }

    /**
     * Returns a container matching the given label, or a parent that can have a child with the
     * given label.
     * 
     * @param palletLabel the container label.
     * 
     * @returns returns the container with the given label, or a parent container that can have a
     *          child with the given label.
     */
    @SuppressWarnings("nls")
    public static ContainerWrapper getContainerByLabel(String palletLabel) {
        try {
            Site site = SessionManager.getUser().getCurrentWorkingSite().getWrappedObject();

            ContainerData containerData = SessionManager.getAppService().doAction(
                new ContainerGetContainerOrParentsByLabelAction(palletLabel, site, null));

            List<Container> possibleParents = containerData.getPossibleParentContainers();

            if (containerData.getContainer() != null) {
                return new ContainerWrapper(SessionManager.getAppService(), containerData.getContainer());
            } else if (possibleParents.isEmpty()) {
                BgcPlugin.openAsyncError(
                    // TR: dialog title
                    i18n.tr("Container label error"),
                    // TR: dialog message
                    i18n.tr("Unable to find a container with label {0}", palletLabel));
            } else if (possibleParents.size() == 1) {
                Container parent = possibleParents.get(0);
                return new ContainerWrapper(SessionManager.getAppService(), parent);
            } else {
                SelectParentContainerDialog dlg = new SelectParentContainerDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), possibleParents);
                dlg.open();
                if (dlg.getSelectedContainer() == null) {
                    Set<String> labelData = new HashSet<String>();
                    for (Container cont : possibleParents) {
                        labelData.add(ContainerWrapper.getFullInfoLabel(cont));
                    }
                    BgcPlugin.openError(
                        // TR: dialog title
                        i18n.tr("Container problem"),
                        // TR: dialog message
                        i18n.tr("More than one container found matching {0}",
                            StringUtils.join(labelData, ", ")));
                } else {
                    return new ContainerWrapper(SessionManager.getAppService(), dlg.getSelectedContainer());
                }
            }
        } catch (Exception ex) {
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Init container from position"), ex);
        }
        return null;
    }

    /**
     * Returns a container with the give product barcode, or NULL if no container with the give
     * product barcode exists.
     * 
     * @param productBarcode the product barcode for the container. Can be scanned from a 1D
     *            barcode.
     * 
     * @return returns the container with the give product barcode, or NULL if none exists.
     */
    @SuppressWarnings("nls")
    public static ContainerWrapper getContainerByProductBarcode(String productBarcode) {
        try {
            Container c = new Container();
            c.setProductBarcode(productBarcode);

            Site site = SessionManager.getUser().getCurrentWorkingSite().getWrappedObject();

            ArrayList<Container> result =
                SessionManager.getAppService().doAction(
                    new ContainerGetInfoAction(c, site)).getList();

            if (!result.isEmpty()) {
                if (result.size() > 1) {
                    throw new IllegalStateException(
                        "more than one container with product barcode found: " + productBarcode);
                }
                return new ContainerWrapper(SessionManager.getAppService(), result.get(0));
            }

        } catch (Exception ex) {
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Could not retrieve container with product barcode: "), ex);
        }
        return null;
    }

    /**
     * If using scanner, want only 8*12,r 10*10, etc. pallets. Also checks that the container type
     * can hold specimens.
     */
    public static List<ContainerTypeWrapper> getPossibleTypes(
        List<ContainerTypeWrapper> childContainerTypeCollection,
        boolean usingFlatbedScanner) {
        List<ContainerTypeWrapper> palletTypes = new ArrayList<ContainerTypeWrapper>();
        for (ContainerTypeWrapper type : childContainerTypeCollection) {
            if (!type.getSpecimenTypeCollection().isEmpty()
                && (!usingFlatbedScanner || ScanAssignHelper.isPalletScannable(type)))
                palletTypes.add(type);
        }
        return palletTypes;
    }

    /**
     * Returns the container types for the container. If {@link usingFlatbedScanner} is true, then
     * only containers that can be scanned using the flatbed scanner are returned.
     * 
     * @param container
     * @param usingFlatbedScanner
     * @return
     */
    public static List<ContainerTypeWrapper> getContainerTypes(
        ContainerWrapper container, boolean usingFlatbedScanner) {
        List<ContainerTypeWrapper> possibleTypes = new ArrayList<ContainerTypeWrapper>(0);
        ContainerWrapper parentContainer = container.getParentContainer();

        if (parentContainer != null) {
            possibleTypes.addAll(getPossibleTypes(
                parentContainer.getContainerType().getChildContainerTypeCollection(),
                usingFlatbedScanner));
        } else {
            possibleTypes.add(container.getContainerType());
        }

        return possibleTypes;
    }

    /**
     * Returns the same results as {@link getContainerTypes}, but filtered by the number of rows and
     * columns.
     * 
     * @param container The container whose container types are to be queried.
     * 
     * @param rows The number of rows the container type should have.
     * 
     * @param cols The number of columns the container type should have.
     * 
     * @return A list of container types matching the criteria.
     */
    public static List<ContainerTypeWrapper> getContainerTypes(
        ContainerWrapper container,
        int rows,
        int cols) {
        List<ContainerTypeWrapper> possibleTypes = new ArrayList<ContainerTypeWrapper>(0);
        List<ContainerTypeWrapper> possibleTypesFromParent = getContainerTypes(
            container, true);

        for (ContainerTypeWrapper type : possibleTypesFromParent) {
            Capacity capacity = type.getCapacity();
            if ((capacity.getRowCapacity() == rows)
                && (capacity.getColCapacity() == cols)) {
                possibleTypes.add(type);
            }
        }
        return possibleTypes;
    }

    private static boolean isPalletScannable(ContainerWrapper container) {
        return isPalletScannable(container.getContainerType());
    }

    public static boolean isPalletScannable(ContainerTypeWrapper ctype) {
        for (PalletDimensions gridDimensions : PalletDimensions.values()) {
            int rows = gridDimensions.getRows();
            int cols = gridDimensions.getCols();
            if (ctype.isPalletRowsCols(rows, cols))
                return true;
        }
        return false;
    }

    @SuppressWarnings("nls")
    public static ContainerValid checkExistingContainerValid(ContainerWrapper container) {
        if (container == null) {
            throw new IllegalStateException("container is null");
        }

        if (container.isNew()) return ContainerValid.IS_NEW;

        // only perform checks for existing containers
        ContainerTypeWrapper containerType = container.getContainerType();
        if ((containerType != null) && containerType.getSpecimenTypeCollection().isEmpty()) {
            return ContainerValid.DOES_NOT_HOLD_SPECIMENS;
        }
        return ContainerValid.VALID;
    }

    /**
     * Returns a string stating that the container's product barcode will be updated.
     */
    @SuppressWarnings("nls")
    public static String containerProductBarcodeUpdateLogMessage(ContainerWrapper container,
        String palletProductBarcode, String palletLabel) {
        // only perform checks for existing containers
        if (container.isNew()) {
            return StringUtil.EMPTY_STRING;
        }

        if (container.getContainerType().getSpecimenTypeCollection().isEmpty()) {
            throw new IllegalStateException(
                "invalid container, cannot hold specimens");
        }

        // container has no product barcode: update it with the one entered by
        // user
        String productBarcode = container.getProductBarcode();
        if (container.hasSpecimens()
            && ((productBarcode == null) || productBarcode.isEmpty())) {
            // Position already physically used but no barcode was
            // set (old database compatibility)
            return MessageFormat.format(
                "Position {0} already used with no product barcode and with type {1}."
                    + " Product barcode {2} will be set.",
                palletLabel, container.getContainerType().getName(), palletProductBarcode);
        }

        // Position initialised but not physically used
        return MessageFormat.format(
            "Position {0} initialised with type {1} and free to be used",
            palletLabel, container.getContainerType().getName());
    }

    @SuppressWarnings("nls")
    public static void containerPositionError(
        org.apache.log4j.Logger activityLogger,
        ContainerWrapper container,
        String palletBarcode,
        String palletLabel) {
        BgcPlugin.openError(
            // TR: dialog title
            i18n.tr("Position error"),
            // TR: dialog message
            i18n.tr("A pallet with product barcode \"{0}\" is already at position {1}"
                + "(site {2})",
                palletBarcode,
                palletLabel,
                container.getSite().getNameShort()));
        activityLogger.trace(MessageFormat.format(
            "A pallet with product barcode \"{0}\" is already at position {1}  (site {2})",
            palletBarcode,
            palletLabel,
            container.getSite().getNameShort()));
    }

    public enum ContainerValid {
        VALID, // is a valid container to scan assign into
        IS_NEW, // is a new container
        DOES_NOT_HOLD_SPECIMENS // does not hold specimens
    }
}
