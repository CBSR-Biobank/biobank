package edu.ualberta.med.biobank.test.reports;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PatientVisitSummaryPostProcessTester implements PostProcessTester {
    private static final String CLINIC_SUM_CELL = "All Clinics";
    private static final int STUDY_INDEX = 0;
    private static final int NUM_COLS = 9;
    private static final int NUM_SUMS = 7;
    private static final int SUM_OFFSET = 2;

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        Collection<Object> results) {

        if (results.size() == 0) {
            return new ArrayList<Object>();
        }

        List<Object> postProcessedResults = new ArrayList<Object>();
        String lastStudy = null;
        long[] sums = new long[NUM_SUMS];
        Arrays.fill(sums, 0);

        for (Object row : results) {
            Object[] cells = (Object[]) row;

            if ((lastStudy != null) && !lastStudy.equals(cells[STUDY_INDEX])) {
                postProcessedResults.add(getSumRow(lastStudy, sums));
                postProcessedResults.add(getBlankRow());
                Arrays.fill(sums, 0); // reset sums
            }

            for (int i = 0; i < sums.length; i++) {
                if (cells[i + 2] instanceof Number) {
                    sums[i] += ((Number) cells[i + SUM_OFFSET]).longValue();
                }
            }

            postProcessedResults.add(transformRow(cells)); // keep original

            lastStudy = (String) cells[STUDY_INDEX];
        }

        postProcessedResults.add(getSumRow(lastStudy, sums));

        return postProcessedResults;
    }

    private static Object[] getSumRow(String study, long[] sums) {
        List<Object> newRow = new ArrayList<Object>();
        newRow.addAll(Arrays.asList(study, CLINIC_SUM_CELL));
        for (int i = 0; i < sums.length; i++) {
            newRow.add(sums[i]);
        }
        return newRow.toArray();
    }

    private static Object[] getBlankRow() {
        Object[] blankRow = new Object[NUM_COLS];
        Arrays.fill(blankRow, "");
        return blankRow;
    }

    private static Object[] transformRow(Object[] cells) {
        Object[] row = new Object[NUM_COLS];
        for (int i = 0; i < row.length; i++) {
            if (cells[i] instanceof Number) {
                row[i] = ((Number) cells[i]).longValue();
            } else {
                row[i] = cells[i];
            }
        }
        return row;
    }
}
