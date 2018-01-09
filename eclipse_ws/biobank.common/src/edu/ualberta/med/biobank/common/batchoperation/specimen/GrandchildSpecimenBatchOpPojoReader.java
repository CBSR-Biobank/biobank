package edu.ualberta.med.biobank.common.batchoperation.specimen;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.exception.SuperCSVReflectionException;
import org.supercsv.io.ICsvBeanReader;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.GrandchildSpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.GrandchildSpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.common.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.common.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.model.Center;

/**
 * Reads a CSV file containing specimen information and returns the file as a list of
 * SpecimenBatchOpInputPojo.
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
public class GrandchildSpecimenBatchOpPojoReader implements
                                                IBatchOpPojoReader<GrandchildSpecimenBatchOpInputPojo> {

    private static final I18n i18n = I18nFactory.getI18n(GrandchildSpecimenBatchOpPojoReader.class);

    private static final String CSV_FIRST_HEADER = "Inventory ID";

    private static final String[] NAME_MAPPINGS = new String[] {
        "inventoryId",
        "parentInventoryId",
        "volume",
        "specimenType",
        "createdAt",
        "patientNumber",
        "originCenter",
        "currentCenter",
        "palletProductBarcode",
        "rootContainerType",
        "palletLabel",
        "palletPosition",
        "comment"
    };

    private final Center workingCenter;

    private final String filename;

    private final ClientBatchOpInputErrorList errorList = new ClientBatchOpInputErrorList();

    private final Set<GrandchildSpecimenBatchOpInputPojo> pojos =
        new LinkedHashSet<GrandchildSpecimenBatchOpInputPojo>(0);

    public GrandchildSpecimenBatchOpPojoReader(Center workingCenter, String filename) {
        this.workingCenter = workingCenter;
        this.filename = filename;
    }

    // cell processors have to be recreated every time the file is read
    public CellProcessor[] getCellProcessors() {
        Map<String, CellProcessor> aMap = new LinkedHashMap<String, CellProcessor>();

        aMap.put("inventoryId", new Unique());
        aMap.put("parentInventoryId", new Optional());
        aMap.put("volume", new Optional(new ParseBigDecimal()));
        aMap.put("specimenType", new StrNotNullOrEmpty());
        aMap.put("createdAt", new ParseDate("yyyy-MM-dd HH:mm"));
        aMap.put("patientNumber", new Optional());
        aMap.put("originCenter", new Optional());
        aMap.put("currentCenter", new Optional());
        aMap.put("palletProductBarcode", new Optional());
        aMap.put("rootContainerType", new Optional());
        aMap.put("palletLabel", new Optional());
        aMap.put("palletPosition", new Optional());
        aMap.put("comment", new Optional());

        if (aMap.size() != NAME_MAPPINGS.length) {
            throw new IllegalStateException(
                "the number of name mappings do not match the cell processors");
        }

        return aMap.values().toArray(new CellProcessor[0]);
    }

    public static boolean isHeaderValid(String[] csvHeaders) {
        return csvHeaders[0].equals(CSV_FIRST_HEADER)
            && (csvHeaders.length == NAME_MAPPINGS.length);
    }

    @Override
    public Set<GrandchildSpecimenBatchOpInputPojo> readPojos(ICsvBeanReader reader)
        throws ClientBatchOpErrorsException {

        GrandchildSpecimenBatchOpInputPojo csvPojo;
        CellProcessor[] cellProcessors = getCellProcessors();

        try {
            while ((csvPojo = reader.read(GrandchildSpecimenBatchOpInputPojo.class,
                                          NAME_MAPPINGS,
                                          cellProcessors)) != null) {
                csvPojo.setLineNumber(reader.getLineNumber());
                pojos.add(csvPojo);
            }
            if (pojos.size() > SpecimenBatchOpAction.SIZE_LIMIT) {
                String message = i18n.tr("The file has {0} data rows, the maximum allowed is {1}",
                                         pojos.size(),
                                         SpecimenBatchOpAction.SIZE_LIMIT);
                throw new ClientBatchOpErrorsException(message);
            }
            return pojos;
        } catch (SuperCSVReflectionException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (SuperCSVException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (IOException e) {
            throw new ClientBatchOpErrorsException(e);
        }
    }

    @Override
    public ClientBatchOpInputErrorList getErrorList() {
        return errorList;
    }

    @Override
    public Action<IdResult> getAction() throws NoSuchAlgorithmException,
                                       IOException,
                                       ClassNotFoundException {
        return new GrandchildSpecimenBatchOpAction(workingCenter, pojos, new File(filename));
    }
}
