package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class OriginInfo  implements IBiobankModel
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
	* An associated edu.ualberta.med.biobank.model.Specimen object's collection 
	**/
			
	private Collection<Specimen> specimenCollection = new HashSet<Specimen>();

	/**
	* Retrieves the value of the specimenCollection attribute
	* @return specimenCollection
	**/

	public Collection<Specimen> getSpecimenCollection(){
		return specimenCollection;
	}

	/**
	* Sets the value of specimenCollection attribute
	**/

	public void setSpecimenCollection(Collection<Specimen> specimenCollection){
		this.specimenCollection = specimenCollection;
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
			
	private Center center;
	/**
	* Retrieves the value of the center attribute
	* @return center
	**/
	
	public Center getCenter(){
		return center;
	}
	/**
	* Sets the value of center attribute
	**/

	public void setCenter(Center center){
		this.center = center;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Site object
	**/
			
	private Site receiverSite;
	/**
	* Retrieves the value of the receiverSite attribute
	* @return receiverSite
	**/
	
	public Site getReceiverSite(){
		return receiverSite;
	}
	/**
	* Sets the value of receiverSite attribute
	**/

	public void setReceiverSite(Site receiverSite){
		this.receiverSite = receiverSite;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof OriginInfo) 
		{
			OriginInfo c =(OriginInfo)obj; 			 
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
