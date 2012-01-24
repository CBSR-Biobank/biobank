package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class Log  implements IBiobankModel
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
	
	public String username;
	/**
	* Retrieves the value of the username attribute
	* @return username
	**/

	public String getUsername(){
		return username;
	}

	/**
	* Sets the value of username attribute
	**/

	public void setUsername(String username){
		this.username = username;
	}
	
	/**
	* 
	**/
	
	public java.util.Date createdAt;
	/**
	* Retrieves the value of the createdAt attribute
	* @return createdAt
	**/

	public java.util.Date getCreatedAt(){
		return createdAt;
	}

	/**
	* Sets the value of createdAt attribute
	**/

	public void setCreatedAt(java.util.Date createdAt){
		this.createdAt = createdAt;
	}
	
	/**
	* 
	**/
	
	public String center;
	/**
	* Retrieves the value of the center attribute
	* @return center
	**/

	public String getCenter(){
		return center;
	}

	/**
	* Sets the value of center attribute
	**/

	public void setCenter(String center){
		this.center = center;
	}
	
	/**
	* 
	**/
	
	public String action;
	/**
	* Retrieves the value of the action attribute
	* @return action
	**/

	public String getAction(){
		return action;
	}

	/**
	* Sets the value of action attribute
	**/

	public void setAction(String action){
		this.action = action;
	}
	
	/**
	* 
	**/
	
	public String patientNumber;
	/**
	* Retrieves the value of the patientNumber attribute
	* @return patientNumber
	**/

	public String getPatientNumber(){
		return patientNumber;
	}

	/**
	* Sets the value of patientNumber attribute
	**/

	public void setPatientNumber(String patientNumber){
		this.patientNumber = patientNumber;
	}
	
	/**
	* 
	**/
	
	public String inventoryId;
	/**
	* Retrieves the value of the inventoryId attribute
	* @return inventoryId
	**/

	public String getInventoryId(){
		return inventoryId;
	}

	/**
	* Sets the value of inventoryId attribute
	**/

	public void setInventoryId(String inventoryId){
		this.inventoryId = inventoryId;
	}
	
	/**
	* 
	**/
	
	public String locationLabel;
	/**
	* Retrieves the value of the locationLabel attribute
	* @return locationLabel
	**/

	public String getLocationLabel(){
		return locationLabel;
	}

	/**
	* Sets the value of locationLabel attribute
	**/

	public void setLocationLabel(String locationLabel){
		this.locationLabel = locationLabel;
	}
	
	/**
	* 
	**/
	
	public String details;
	/**
	* Retrieves the value of the details attribute
	* @return details
	**/

	public String getDetails(){
		return details;
	}

	/**
	* Sets the value of details attribute
	**/

	public void setDetails(String details){
		this.details = details;
	}
	
	/**
	* 
	**/
	
	public String type;
	/**
	* Retrieves the value of the type attribute
	* @return type
	**/

	public String getType(){
		return type;
	}

	/**
	* Sets the value of type attribute
	**/

	public void setType(String type){
		this.type = type;
	}
	
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Log) 
		{
			Log c =(Log)obj; 			 
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
