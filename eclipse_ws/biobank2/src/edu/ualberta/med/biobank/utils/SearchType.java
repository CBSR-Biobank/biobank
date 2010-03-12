package edu.ualberta.med.biobank.utils;

import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public enum SearchType {
    INVENTORY_ID("Inventory ID") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString)
            throws Exception {
            return AliquotWrapper.getAliquotsInSite(SessionManager
                .getAppService(), searchString, SessionManager.getInstance()
                .getCurrentSite());
        }

    },

    SAMPLE_POSITION("Aliquot position") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString)
            throws Exception {
            return AliquotWrapper.getAliquotsInSiteWithPositionLabel(
                SessionManager.getAppService(), SessionManager.getInstance()
                    .getCurrentSite(), searchString);
        }
    },

    CONTAINER_LABEL("Container label") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString)
            throws Exception {
            return ContainerWrapper.getContainersInSite(SessionManager
                .getAppService(),
                SessionManager.getInstance().getCurrentSite(), searchString);
        }
    };

    private String label;

    private SearchType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public abstract List<? extends ModelWrapper<?>> search(String searchString)
        throws Exception;

}