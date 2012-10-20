package edu.ualberta.med.biobank.common.action.csvimport.shipment;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.csvimport.CsvActionUtil;
import edu.ualberta.med.biobank.common.action.csvimport.CsvErrorList;
import edu.ualberta.med.biobank.common.action.csvimport.specimen.SpecimenCsvImportAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public class ShipmentCsvImportAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    private static final Bundle bundle = new CommonBundle();

    private static Logger log = LoggerFactory
        .getLogger(ShipmentCsvImportAction.class.getName());

    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenCsvImportAction.class);

    public static final Tr CSV_SENDING_CENTER_ERROR =
        bundle.tr("sending center with name \"{0}\" does not exist");

    public static final Tr CSV_RECEIVING_CENTER_ERROR =
        bundle.tr("receiving center with name \"{0}\" does not exist");

    public static final Tr CSV_PNUMBER_ERROR =
        bundle.tr("patient number \"{0}\" does not exist");

    public static final Tr CSV_INVENTORY_ID_ERROR =
        bundle.tr("inventory id \"{0}\" does not exist");

    public static final Tr CSV_SHIPPING_METHOD_ERROR =
        bundle.tr("shipping method \"{0}\" does not exist");

    // @formatter:off
    private static final CellProcessor[] PROCESSORS = new CellProcessor[] {
        new ParseDate("yyyy-MM-dd HH:mm"), // dateReceived
        new StrNotNullOrEmpty(),           // sendingCenter
        new StrNotNullOrEmpty(),           // receivingCenter
        new StrNotNullOrEmpty(),           // shippingMethod
        new StrNotNullOrEmpty(),           // waybill
        null                               // comment
    }; 
    // @formatter:on    

    private final CsvErrorList errorList = new CsvErrorList();

    private CompressedReference<ArrayList<ShipmentCsvInfo>> compressedList =
        null;

    private final Set<ShipmentImportInfo> shipmentImportInfos =
        new HashSet<ShipmentImportInfo>(0);

    public ShipmentCsvImportAction(String filename) throws IOException {
        setCsvFile(filename);
    }

    private void setCsvFile(String filename) throws IOException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        final String[] header = new String[] {
            "dateReceived",
            "sendingCenter",
            "receivingCenter",
            "shippingMethod",
            "waybill",
            "comment"
        };

        try {
            ArrayList<ShipmentCsvInfo> csvInfos =
                new ArrayList<ShipmentCsvInfo>(0);

            ShipmentCsvInfo csvInfo;
            reader.getCSVHeader(true);
            while ((csvInfo =
                reader.read(ShipmentCsvInfo.class, header, PROCESSORS)) != null) {

                csvInfo.setLineNumber(reader.getLineNumber());
                csvInfos.add(csvInfo);
            }

            if (!errorList.isEmpty()) {
                throw new CsvImportException(errorList.getErrors());
            }

            compressedList =
                new CompressedReference<ArrayList<ShipmentCsvInfo>>(
                    csvInfos);

        } catch (SuperCSVException e) {
            throw new IllegalStateException(
                i18n.tr(CsvActionUtil.CSV_PARSE_ERROR, e.getMessage(),
                    e.getCsvContext()));
        } finally {
            reader.close();
        }
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.LEGACY_IMPORT_CSV.isAllowed(context.getUser());
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        if (compressedList == null) {
            throw new LocalizedException(CsvActionUtil.CSV_FILE_ERROR);
        }

        boolean result = false;

        ArrayList<ShipmentCsvInfo> csvInfos = compressedList.get();
        context.getSession().getTransaction();

        for (ShipmentCsvInfo csvInfo : csvInfos) {
            ShipmentImportInfo info = getDbInfo(context, csvInfo);
            shipmentImportInfos.add(info);
        }

        if (!errorList.isEmpty()) {
            throw new CsvImportException(errorList.getErrors());
        }

        for (ShipmentImportInfo info : shipmentImportInfos) {
            OriginInfo originInfo = info.getNewOriginInfo();

            context.getSession().save(
                originInfo.getComments().iterator().next());
            context.getSession().save(originInfo.getShipmentInfo());
            context.getSession().save(originInfo);
        }

        result = true;
        return new BooleanResult(result);
    }

    private ShipmentImportInfo getDbInfo(ActionContext context,
        ShipmentCsvInfo csvInfo) {
        ShipmentImportInfo info = new ShipmentImportInfo(csvInfo);

        info.setUser(context.getUser());

        Center sendingCenter =
            CsvActionUtil.getCenter(context, csvInfo.getSendingCenter());
        if (sendingCenter == null) {
            errorList.addError(csvInfo.getLineNumber(),
                CSV_SENDING_CENTER_ERROR.format(csvInfo.getSendingCenter()));
        } else {
            info.setOriginCenter(sendingCenter);
        }

        Site receivingSite =
            CsvActionUtil.getSite(context, csvInfo.getReceivingCenter());
        if (receivingSite == null) {
            errorList.addError(csvInfo.getLineNumber(),
                CSV_RECEIVING_CENTER_ERROR.format(csvInfo
                    .getReceivingCenter()));
        } else {
            info.setCurrentSite(receivingSite);
        }

        ShippingMethod shippingMethod =
            CsvActionUtil.getShippingMethod(context,
                csvInfo.getShippingMethod());
        if (shippingMethod == null) {
            errorList.addError(csvInfo.getLineNumber(),
                CSV_SHIPPING_METHOD_ERROR.format(csvInfo.getShippingMethod()));
        } else {
            info.setShippingMethod(shippingMethod);
        }

        return info;
    }

}
