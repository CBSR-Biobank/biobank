package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;
import java.util.List;

public class DispatchSaveInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Integer id;
    public Integer state;
    public Integer receiverId;
    public Integer senderId;
    public List<Integer> addedSpecIds;
    public List<Integer> removedSpecIds;

    
    public DispatchSaveInfo(Integer id, Integer receiverId, Integer senderId, Integer state, 
        List<Integer> addedSpecIds, List<Integer> removedSpecIds) {
        this.id = id;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.state = state;
        this.addedSpecIds = addedSpecIds;
        this.removedSpecIds = removedSpecIds;
    }
    
}
