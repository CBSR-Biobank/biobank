package edu.ualberta.med.biobank.common.util;

import java.io.Serializable;

public abstract class AbstractRowPostProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract Object rowPostProcess(Object object);
}
