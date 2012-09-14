package edu.ualberta.med.biobank.model;

import java.io.Serializable;

public class BatchOperationEntity 
    implements Serializable {

    private BatchOperation batch;
    private EntityType entityType;
    private Integer entityId;
    
    // externalize, also for streams?
    public enum EntityType {
        
    }
}
