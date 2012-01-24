package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class Request  implements IBiobankModel
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
	
	public java.util.Date submitted;
	/**
	* Retrieves the value of the submitted attribute
	* @return submitted
	**/

	public java.util.Date getSubmitted(){
		return submitted;
	}

	/**
	* Sets the value of submitted attribute
	**/

	public void setSubmitted(java.util.Date submitted){
		this.submitted = submitted;
	}
	
	/**
	* 
	**/
	
	public java.util.Date created;
	/**
	* Retrieves the value of the created attribute
	* @return created
	**/

	public java.util.Date getCreated(){
		return created;
	}

	/**
	* Sets the value of created attribute
	**/

	public void setCreated(java.util.Date created){
		this.created = created;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.Dispatch object's collection 
	**/
			
	private Collection<Dispatch> dispatchCollection = new HashSet<Dispatch>();

	/**
	* Retrieves the value of the dispatchCollection attribute
	* @return dispatchCollection
	**/

	public Collection<Dispatch> getDispatchCollection(){
		return dispatchCollection;
	}

	/**
	* Sets the value of dispatchCollection attribute
	**/

	public void setDispatchCollection(Collection<Dispatch> dispatchCollection){
		this.dispatchCollection = dispatchCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.RequestSpecimen object's collection 
	**/
			
	private Collection<RequestSpecimen> requestSpecimenCollection = new HashSet<RequestSpecimen>();

	/**
	* Retrieves the value of the requestSpecimenCollection attribute
	* @return requestSpecimenCollection
	**/

	public Collection<RequestSpecimen> getRequestSpecimenCollection(){
		return requestSpecimenCollection;
	}

	/**
	* Sets the value of requestSpecimenCollection attribute
	**/

	public void setRequestSpecimenCollection(Collection<RequestSpecimen> requestSpecimenCollection){
		this.requestSpecimenCollection = requestSpecimenCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Address object
	**/
			
	private Address address;
	/**
	* Retrieves the value of the address attribute
	* @return address
	**/
	
	public Address getAddress(){
		return address;
	}
	/**
	* Sets the value of address attribute
	**/

	public void setAddress(Address address){
		this.address = address;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.ResearchGroup object
	**/
			
	private ResearchGroup researchGroup;
	/**
	* Retrieves the value of the researchGroup attribute
	* @return researchGroup
	**/
	
	public ResearchGroup getResearchGroup(){
		return researchGroup;
	}
	/**
	* Sets the value of researchGroup attribute
	**/

	public void setResearchGroup(ResearchGroup researchGroup){
		this.researchGroup = researchGroup;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Request) 
		{
			Request c =(Request)obj; 			 
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
