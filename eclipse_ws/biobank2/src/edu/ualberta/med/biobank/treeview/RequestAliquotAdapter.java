package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestAliquotWrapper;

public class RequestAliquotAdapter extends AliquotAdapter {

    private RequestAliquotWrapper raw;

    public RequestAliquotAdapter(AdapterBase parent, RequestAliquotWrapper raw) {
        super(parent, raw.getAliquot());
        this.raw = raw;
    }

    @Override
    public ModelWrapper<?> getModelObject() {
        return raw;
    }

}
