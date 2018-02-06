package edu.ualberta.med.biobank.common.batchoperation.specimenPosition;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.exception.SuperCSVReflectionException;
import org.supercsv.io.ICsvBeanReader;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpPojo;
import edu.ualberta.med.biobank.common.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.common.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.common.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.batchoperation.specimen.GrandchildSpecimenBatchOpPojoReader;
import edu.ualberta.med.biobank.model.Center;

/**
 * Reads a CSV file containing specimen position information and returns the file as a list of
 * PositionBatchOpPojo.
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
public class SpecimenPositionBatchOpPojoReader implements IBatchOpPojoReader<PositionBatchOpPojo> {

    private static final I18n i18n = I18nFactory.getI18n(GrandchildSpecimenBatchOpPojoReader.class);

    private static final String CSV_FIRST_HEADER = "inventoryId";

    private static final String[] NAME_MAPPINGS =
        new String[] {
                      "inventoryId",
                      "currentPalletLabel",
                      "palletProductBarcode",
                      "rootContainerType",
                      "palletLabel",
                      "palletPosition",
                      "comment"
        };

    private final Center workingCenter;

    private final String filename;

    private final ClientBatchOpInputErrorList errorList = new ClientBatchOpInputErrorList();

    private final Set<PositionBatchOpPojo> pojos = new LinkedHashSet<PositionBatchOpPojo>(0);

    public SpecimenPositionBatchOpPojoReader(Center workingCenter, String filename) {
        this.workingCenter = workingCenter;
        this.filename = filename;
    }

    // cell processors have to be recreated every time the file is read
    public CellProcessor[] getCellProcessors() {
        Map<String, CellProcessor> aMap = new LinkedHashMap<String, CellProcessor>();

        aMap.put("inventoryId", new Unique());
        aMap.put("currentPalletlabel", new Optional());
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
    public Set<PositionBatchOpPojo> readPojos(ICsvBeanReader reader)
        throws ClientBatchOpErrorsException, IOException {

        PositionBatchOpPojo csvPojo;
        CellProcessor[] cellProcessors = getCellProcessors();

        try {
            while ((csvPojo = reader.read(PositionBatchOpPojo.class,
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
        return new PositionBatchOpAction(workingCenter, pojos, new File(filename));
    }

}
