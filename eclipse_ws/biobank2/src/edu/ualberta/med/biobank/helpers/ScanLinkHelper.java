package edu.ualberta.med.biobank.helpers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenResInfo;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypesGetForContainerTypesAction;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Code to help with scan linking. Can be called by scan link forms and dialogs.
 * 
 * @author nelson
 * 
 */
public class ScanLinkHelper {

    private static final I18n i18n = I18nFactory.getI18n(ScanLinkHelper.class);

    /**
     * If the current center is a site, and if this site defines containers of 8*12 or 10*10 size,
     * then get the specimen types these containers can contain
     */
    public static List<SpecimenType> getSpecimenTypeForPalletScannable()
        throws ApplicationException {
        List<SpecimenType> result = new ArrayList<SpecimenType>();

        SiteWrapper site = SessionManager.getUser().getCurrentWorkingSite();
        if (site == null) {
            // scan link being used when working center is a clinic, clinics do not have
            // container types
            return result;
        }

        Set<Capacity> capacities = new HashSet<Capacity>();
        for (PalletDimensions gridDimensions : PalletDimensions.values()) {
            Capacity capacity = new Capacity();
            capacity.setRowCapacity(gridDimensions.getRows());
            capacity.setColCapacity(gridDimensions.getCols());
            capacities.add(capacity);
        }
        result = SessionManager.getAppService().doAction(
            new SpecimenTypesGetForContainerTypesAction(
                site.getWrappedObject(), capacities
            )).getList();
        return result;
    }

    /**
     * Want only one common 'log entry' so use a stringbuffer to print every thing together.
     * 
     * @param linkedSpecimens
     * 
     * @return A formatted string that can be logged stating what specimens were linked.
     */
    @SuppressWarnings("nls")
    public static List<String> linkedSpecimensLogMessage(
        List<AliquotedSpecimenResInfo> linkedSpecimens) {
        List<String> result = new ArrayList<String>();

        StringBuffer sb = new StringBuffer("ALIQUOTED SPECIMENS:\n");
        for (AliquotedSpecimenResInfo resInfo : linkedSpecimens) {
            sb.append(MessageFormat.format(
                "LINKED: ''{0}'' with type ''{1}'' to source: {2} ({3}) - Patient: {4} - Visit: {5} - Center: {6}\n",
                resInfo.inventoryId, resInfo.typeName, resInfo.parentTypeName,
                resInfo.parentInventoryId, resInfo.patientPNumber, resInfo.visitNumber,
                resInfo.currentCenterName));
        }
        result.add(sb.toString());

        Map<String, Integer> counts = new HashMap<String, Integer>();

        for (AliquotedSpecimenResInfo spc : linkedSpecimens) {
            Integer count = counts.get(spc.patientPNumber);
            if (count == null) {
                counts.put(spc.patientPNumber, 1);
            } else {
                counts.put(spc.patientPNumber, count + 1);
            }
        }

        for (Entry<String, Integer> entry : counts.entrySet()) {
            String pnumber = entry.getKey();
            Integer count = entry.getValue();

            // LINKING\: {0} specimens linked to patient {1} on center {2}
            result.add(MessageFormat.format(
                "LINKING: {0} specimens linked to patient {1} on center {2}",
                count,
                pnumber,
                SessionManager.getUser().getCurrentWorkingCenter().getNameShort()));
        }
        return result;
    }
}
