package edu.ualberta.med.biobank.common.util;

import java.io.Serializable;

public abstract class PostProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract Object postProcess(Object object);
}
