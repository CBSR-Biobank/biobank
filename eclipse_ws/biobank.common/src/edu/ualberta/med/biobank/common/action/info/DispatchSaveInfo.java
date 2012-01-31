package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;

public class DispatchSaveInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public Integer id;
    public Integer state;
    public Integer receiverId;
    public Integer senderId;
    public String comment;
    public Integer requestId;

    public DispatchSaveInfo(Integer id, Integer receiverId, Integer senderId,
        Integer state, String comment, Integer requestId) {
        this.id = id;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.state = state;
        this.comment = comment;
        this.requestId = requestId;
    }

}
