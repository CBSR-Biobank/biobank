package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.internal.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;

public class ContainerHelper extends DbHelper {
    public static ContainerLabelingSchemeWrapper newContainerLabelingScheme() {
        ContainerLabelingSchemeWrapper clsw = new ContainerLabelingSchemeWrapper(
            appService, new ContainerLabelingScheme());
        clsw.setName("SchemeName");
        return clsw;
    }
}
