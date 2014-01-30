package edu.ualberta.med.biobank.common.action.labelPrinter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.PrintedSsInvItem;

public class GetSourceSpecimenUniqueInventoryIdSetAction implements Action<ListResult<String>> {
    private static final long serialVersionUID = 1L;

    private static final int SS_INV_ID_LENGTH = 12;

    @SuppressWarnings("nls")
    private static final String SS_INV_ID_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int SS_INV_ID_ALPHABET_LENGTH = SS_INV_ID_ALPHABET.length();

    private static final int SS_INV_ID_GENERATE_RETRIES =
        (int) Math.pow(SS_INV_ID_ALPHABET_LENGTH, SS_INV_ID_ALPHABET_LENGTH);

    private final int numIds;

    public GetSourceSpecimenUniqueInventoryIdSetAction(int numIds) {
        this.numIds = numIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new LabelPrintingPermission().isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public ListResult<String> run(ActionContext context) throws ActionException {
        boolean isUnique;
        int genRetries;
        Random r = new Random();
        StringBuilder newInvId;
        Set<String> result = new HashSet<String>();

        while (result.size() < numIds) {
            isUnique = false;
            genRetries = 0;
            newInvId = new StringBuilder();

            while (!isUnique && (genRetries < SS_INV_ID_GENERATE_RETRIES)) {
                for (int j = 0; j < SS_INV_ID_LENGTH; ++j) {
                    newInvId.append(SS_INV_ID_ALPHABET.charAt(
                        r.nextInt(SS_INV_ID_ALPHABET_LENGTH)));
                }

                genRetries++;
                String potentialInvId = newInvId.toString();

                // check database if string is unique
                PrintedSsInvItem invId = (PrintedSsInvItem) context.getSession()
                    .createCriteria(PrintedSsInvItem.class)
                    .add(Restrictions.eq("txt", potentialInvId))
                    .uniqueResult();

                if (invId == null) {
                    // this inventory id does not exist
                    //
                    // add new inventory id to the database
                    PrintedSsInvItem newInvItem = new PrintedSsInvItem();
                    newInvItem.setTxt(potentialInvId);

                    isUnique = true;
                    result.add(potentialInvId);
                    context.getSession().saveOrUpdate(newInvItem);
                }
            }

            if (genRetries >= SS_INV_ID_GENERATE_RETRIES) {
                // cannot generate any more unique strings
                throw new ActionException(
                    "cannot generate any more source specimen inventory IDs");
            }

        }
        return new ListResult<String>(result);
    }

}
