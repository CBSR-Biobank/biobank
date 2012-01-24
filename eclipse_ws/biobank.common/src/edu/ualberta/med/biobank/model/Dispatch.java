package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class Dispatch  implements IBiobankModel
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

        private Integer version;

	
	/**
	* 
	**/
	
	public Integer id;
	/**
	* Retrieves the value of the id attribute
	* @return id
	**/

	public Integer getId(){
		return id;
	}

	/**
	* Sets the value of id attribute
	**/

	public void setId(Integer id){
		this.id = id;
	}
	
	/**
	* 
	**/
	
	public Integer state;
	/**
	* Retrieves the value of the state attribute
	* @return state
	**/

	public Integer getState(){
		return state;
	}

	/**
	* Sets the value of state attribute
	**/

	public void setState(Integer state){
		this.state = state;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.DispatchSpecimen object's collection 
	**/
			
	private Collection<DispatchSpecimen> dispatchSpecimenCollection = new HashSet<DispatchSpecimen>();

	/**
	* Retrieves the value of the dispatchSpecimenCollection attribute
	* @return dispatchSpecimenCollection
	**/

	public Collection<DispatchSpecimen> getDispatchSpecimenCollection(){
		return dispatchSpecimenCollection;
	}

	/**
	* Sets the value of dispatchSpecimenCollection attribute
	**/

	public void setDispatchSpecimenCollection(Collection<DispatchSpecimen> dispatchSpecimenCollection){
		this.dispatchSpecimenCollection = dispatchSpecimenCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Center object
	**/
			
	private Center senderCenter;
	/**
	* Retrieves the value of the senderCenter attribute
	* @return senderCenter
	**/
	
	public Center getSenderCenter(){
		return senderCenter;
	}
	/**
	* Sets the value of senderCenter attribute
	**/

	public void setSenderCenter(Center senderCenter){
		this.senderCenter = senderCenter;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.ShipmentInfo object
	**/
			
	private ShipmentInfo shipmentInfo;
	/**
	* Retrieves the value of the shipmentInfo attribute
	* @return shipmentInfo
	**/
	
	public ShipmentInfo getShipmentInfo(){
		return shipmentInfo;
	}
	/**
	* Sets the value of shipmentInfo attribute
	**/

	public void setShipmentInfo(ShipmentInfo shipmentInfo){
		this.shipmentInfo = shipmentInfo;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Center object
	**/
			
	private Center receiverCenter;
	/**
	* Retrieves the value of the receiverCenter attribute
	* @return receiverCenter
	**/
	
	public Center getReceiverCenter(){
		return receiverCenter;
	}
	/**
	* Sets the value of receiverCenter attribute
	**/

	public void setReceiverCenter(Center receiverCenter){
		this.receiverCenter = receiverCenter;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Comment object's collection 
	**/
			
	private Collection<Comment> commentCollection = new HashSet<Comment>();

	/**
	* Retrieves the value of the commentCollection attribute
	* @return commentCollection
	**/

	public Collection<Comment> getCommentCollection(){
		return commentCollection;
	}

	/**
	* Sets the value of commentCollection attribute
	**/

	public void setCommentCollection(Collection<Comment> commentCollection){
		this.commentCollection = commentCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Dispatch) 
		{
			Dispatch c =(Dispatch)obj; 			 
			if(getId() != null && getId().equals(c.getId()))
				return true;
		}
		return false;
	}
		
	/**
	* Returns hash code for the primary key of the object
	**/
	public int hashCode()
	{
		if(getId() != null)
			return getId().hashCode();
		return 0;
	}
	
}
