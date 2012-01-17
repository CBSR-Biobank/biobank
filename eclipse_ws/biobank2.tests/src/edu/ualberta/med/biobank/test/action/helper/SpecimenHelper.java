package edu.ualberta.med.biobank.test.action.helper;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.action.IActionExecutor;

public class SpecimenHelper extends Helper {

    public static void deleteSpecimen(IActionExecutor actionExecutor,
        Specimen specimen) {

        CEventInfo ceventInfo = actionExecutor.exec(
            new CollectionEventGetInfoAction(specimen.getCollectionEvent()
                .getId()));

        SpecimenInfo specimenInfoToDelete = null;
        for (SpecimenInfo specimenInfo : ceventInfo.sourceSpecimenInfos) {
            if (specimenInfo.specimen.getId().equals(specimen.getId())) {
                specimenInfoToDelete = specimenInfo;
                break;
            }
        }

        if (specimenInfoToDelete == null) {
            throw new RuntimeException(
                "specimen not found in collection event info");
        }

        ceventInfo.sourceSpecimenInfos.remove(specimenInfoToDelete);
        CollectionEventSaveAction ceventSaveAction =
            CollectionEventHelper.getSaveAction(ceventInfo);
        actionExecutor.exec(ceventSaveAction);
    }
}
