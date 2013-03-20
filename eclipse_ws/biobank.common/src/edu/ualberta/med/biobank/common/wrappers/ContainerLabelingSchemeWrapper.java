package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.base.ContainerLabelingSchemeBaseWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerLabelingSchemeWrapper extends
    ContainerLabelingSchemeBaseWrapper {

    private static final I18n i18n = I18nFactory.getI18n(ContainerLabelingSchemeWrapper.class);

    public static final int SCHEME_SBS = 1;

    public static final int SCHEME_CBSR_2_CHAR_ALPHA = 2;

    public static final int SCHEME_2_CHAR_NUMERIC = 3;

    public static final int SCHEME_DEWAR = 4;

    public static final int SCHEME_CBSR_SBS = 5;

    public static final int SCHEME_2_CHAR_ALPHA = 6;

    @SuppressWarnings("nls")
    public static final String CBSR_2_CHAR_LABELLING_PATTERN = "ABCDEFGHJKLMNPQRSTUVWXYZ";

    @SuppressWarnings("nls")
    public static final String TWO_CHAR_LABELLING_PATTERN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @SuppressWarnings("nls")
    public static final String SBS_ROW_LABELLING_PATTERN = "ABCDEFGHIJKLMNOP";

    @SuppressWarnings("nls")
    public static String BOX81_LABELLING_PATTERN = "ABCDEFGHJ";

    private static Map<Integer, ContainerLabelingSchemeWrapper> allSchemes = null;

    public ContainerLabelingSchemeWrapper(
        WritableApplicationService appService,
        ContainerLabelingScheme wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerLabelingSchemeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @SuppressWarnings("nls")
    public static synchronized Map<Integer, ContainerLabelingSchemeWrapper> getAllLabelingSchemesMap(
        WritableApplicationService appService) throws ApplicationException {
        if (allSchemes == null) {
            allSchemes = new HashMap<Integer, ContainerLabelingSchemeWrapper>();
            List<ContainerLabelingScheme> list = appService
                .query(DetachedCriteria
                    .forClass(ContainerLabelingScheme.class));
            if (list != null) {
                for (ContainerLabelingScheme scheme : list) {
                    Integer id = scheme.getId();
                    switch (id.intValue()) {
                    case SCHEME_SBS:
                        if (!scheme.getName().equals("SBS Standard")) {
                            throw new ApplicationException(
                                "labeling scheme is not "
                                    + scheme.getName());
                        }
                        break;

                    case SCHEME_CBSR_2_CHAR_ALPHA:
                        if (!scheme.getName().equals("CBSR 2 char alphabetic")) {
                            throw new ApplicationException(
                                "labeling scheme is not "
                                    + scheme.getName());
                        }
                        break;

                    case SCHEME_2_CHAR_NUMERIC:
                        if (!scheme.getName().equals("2 char numeric")) {
                            throw new ApplicationException(
                                "labeling scheme is not "
                                    + scheme.getName());
                        }
                        break;

                    case SCHEME_DEWAR:
                        if (!scheme.getName().equals("Dewar")) {
                            throw new ApplicationException(
                                "labeling scheme is not "
                                    + scheme.getName());
                        }
                        break;

                    case SCHEME_CBSR_SBS:
                        if (!scheme.getName().equals("CBSR SBS")) {
                            throw new ApplicationException(
                                "labeling scheme is not "
                                    + scheme.getName());
                        }
                        break;

                    case SCHEME_2_CHAR_ALPHA:
                        if (!scheme.getName().equals("2 char alphabetic")) {
                            throw new ApplicationException(
                                "labeling scheme is not "
                                    + scheme.getName());
                        }
                        break;

                    default:
                        throw new ApplicationException(
                            "labeling scheme with id " + id
                                + " is not mapped correctly");
                    }
                    allSchemes.put(id, new ContainerLabelingSchemeWrapper(
                        appService, scheme));
                }
            }
        }
        return allSchemes;
    }

    @SuppressWarnings("nls")
    public static ContainerLabelingSchemeWrapper getLabelingSchemeById(
        WritableApplicationService appService, Integer id)
        throws ApplicationException {
        getAllLabelingSchemesMap(appService);
        ContainerLabelingSchemeWrapper scheme = allSchemes.get(id);
        if (scheme == null) {
            throw new ApplicationException(i18n.tr(
                "labeling scheme with id \"{0}\" does not exist", id));
        }
        return scheme;
    }

    @Override
    public int compareTo(ModelWrapper<ContainerLabelingScheme> o) {
        return 0;
    }

    @Deprecated
    @Override
    public void persist() throws Exception {
        super.persist();
        resetAllSchemes();
    }

    @Deprecated
    @Override
    public void delete() throws Exception {
        super.delete();
        resetAllSchemes();
    }

    private static synchronized void resetAllSchemes() {
        allSchemes = null;
    }

    /**
     * Check labeling scheme limits for a given gridsize
     **/
    @SuppressWarnings("nls")
    public static boolean checkBounds(WritableApplicationService appService,
        Integer labelingScheme, Integer totalRows, Integer totalCols) {

        if (totalRows == null || totalRows <= 0 || totalCols == null
            || totalCols <= 0) {
            return false;
        }

        try {
            getAllLabelingSchemesMap(appService);
        } catch (ApplicationException e) {
            throw new RuntimeException(
                "could not load container labeling schemes");
        }

        ContainerLabelingSchemeWrapper schemeWrapper = allSchemes
            .get(labelingScheme);
        if (schemeWrapper != null) {
            return schemeWrapper.checkBounds(totalRows, totalCols);
        }
        return false;
    }

    /**
     * Check labeling scheme limits for a given gridsize
     **/
    public boolean checkBounds(int totalRows, int totalCols) {
        Integer maxRows = getMaxRows();
        Integer maxCols = getMaxCols();
        Integer maxCapacity = getMaxCapacity();

        boolean isInBounds = true;

        if (maxRows != null) {
            isInBounds &= totalRows <= maxRows;
        }

        if (maxCols != null) {
            isInBounds &= totalCols <= maxCols;
        }

        if (maxCapacity != null) {
            isInBounds &= totalRows * totalCols <= maxCapacity;
        }

        return isInBounds;
    }

    public static boolean canLabel(ContainerLabelingScheme scheme,
        Capacity capacity) {
        boolean canLabel = true;

        if (canLabel && scheme.getMaxRows() != null) {
            canLabel &= capacity.getRowCapacity() <= scheme.getMaxRows();
        }

        if (canLabel && scheme.getMaxCols() != null) {
            canLabel &= capacity.getColCapacity() <= scheme.getMaxCols();
        }

        if (canLabel && scheme.getMaxCapacity() != null) {
            int max = capacity.getRowCapacity() * capacity.getColCapacity();
            canLabel &= max <= scheme.getMaxCapacity();
        }

        return canLabel;
    }

    @SuppressWarnings("nls")
    private static final String POS_LABEL_LEN_QRY = "select min(minChars), max(maxChars) from "
        + ContainerLabelingScheme.class.getName();

    public static List<Integer> getPossibleLabelLength(
        WritableApplicationService appService) throws ApplicationException {
        HQLCriteria rangeQuery = new HQLCriteria(POS_LABEL_LEN_QRY);
        Object[] minMax = (Object[]) appService.query(rangeQuery).get(0);
        List<Integer> validLengths = new ArrayList<Integer>();
        for (int i = (Integer) minMax[0]; i < (Integer) minMax[1] + 1; i++) {
            validLengths.add(i);
        }
        return validLengths;
    }
}
