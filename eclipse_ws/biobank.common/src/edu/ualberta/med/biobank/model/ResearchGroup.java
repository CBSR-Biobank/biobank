package edu.ualberta.med.biobank.model;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class ResearchGroup extends Center implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* An associated edu.ualberta.med.biobank.model.Study object
	**/
			
	private Study study;
	/**
	* Retrieves the value of the study attribute
	* @return study
	**/
	
	public Study getStudy(){
		return study;
	}
	/**
	* Sets the value of study attribute
	**/

	public void setStudy(Study study){
		this.study = study;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Request object's collection 
	**/
			
	private Collection<Request> requestCollection;
	/**
	* Retrieves the value of the requestCollection attribute
	* @return requestCollection
	**/

	public Collection<Request> getRequestCollection(){
		return requestCollection;
	}

	/**
	* Sets the value of requestCollection attribute
	**/

	public void setRequestCollection(Collection<Request> requestCollection){
		this.requestCollection = requestCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof ResearchGroup) 
		{
			ResearchGroup c =(ResearchGroup)obj; 			 
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