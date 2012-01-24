package edu.ualberta.med.biobank.model;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class Site extends Center implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* An associated edu.ualberta.med.biobank.model.Study object's collection 
	**/
			
	private Collection<Study> studyCollection;
	/**
	* Retrieves the value of the studyCollection attribute
	* @return studyCollection
	**/

	public Collection<Study> getStudyCollection(){
		return studyCollection;
	}

	/**
	* Sets the value of studyCollection attribute
	**/

	public void setStudyCollection(Collection<Study> studyCollection){
		this.studyCollection = studyCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.ContainerType object's collection 
	**/
			
	private Collection<ContainerType> containerTypeCollection;
	/**
	* Retrieves the value of the containerTypeCollection attribute
	* @return containerTypeCollection
	**/

	public Collection<ContainerType> getContainerTypeCollection(){
		return containerTypeCollection;
	}

	/**
	* Sets the value of containerTypeCollection attribute
	**/

	public void setContainerTypeCollection(Collection<ContainerType> containerTypeCollection){
		this.containerTypeCollection = containerTypeCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Container object's collection 
	**/
			
	private Collection<Container> containerCollection;
	/**
	* Retrieves the value of the containerCollection attribute
	* @return containerCollection
	**/

	public Collection<Container> getContainerCollection(){
		return containerCollection;
	}

	/**
	* Sets the value of containerCollection attribute
	**/

	public void setContainerCollection(Collection<Container> containerCollection){
		this.containerCollection = containerCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Site) 
		{
			Site c =(Site)obj; 			 
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