package edu.ualberta.med.biobank.model;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class Clinic extends Center implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	public Boolean sendsShipments;
	/**
	* Retrieves the value of the sendsShipments attribute
	* @return sendsShipments
	**/

	public Boolean getSendsShipments(){
		return sendsShipments;
	}

	/**
	* Sets the value of sendsShipments attribute
	**/

	public void setSendsShipments(Boolean sendsShipments){
		this.sendsShipments = sendsShipments;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.Contact object's collection 
	**/
			
	private Collection<Contact> contactCollection;
	/**
	* Retrieves the value of the contactCollection attribute
	* @return contactCollection
	**/

	public Collection<Contact> getContactCollection(){
		return contactCollection;
	}

	/**
	* Sets the value of contactCollection attribute
	**/

	public void setContactCollection(Collection<Contact> contactCollection){
		this.contactCollection = contactCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Clinic) 
		{
			Clinic c =(Clinic)obj; 			 
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