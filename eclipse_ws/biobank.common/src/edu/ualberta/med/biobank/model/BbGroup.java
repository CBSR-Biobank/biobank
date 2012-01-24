package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/
	

public class BbGroup extends Principal implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	@NotEmpty
	public String name;
	/**
	* Retrieves the value of the name attribute
	* @return name
	**/

	public String getName(){
		return name;
	}

	/**
	* Sets the value of name attribute
	**/

	public void setName(String name){
		this.name = name;
	}
	
	/**
	* 
	**/
	
	public String description;
	/**
	* Retrieves the value of the description attribute
	* @return description
	**/

	public String getDescription(){
		return description;
	}

	/**
	* Sets the value of description attribute
	**/

	public void setDescription(String description){
		this.description = description;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.User object's collection 
	**/
			
	private Collection<User> userCollection;
	/**
	* Retrieves the value of the userCollection attribute
	* @return userCollection
	**/

	public Collection<User> getUserCollection(){
		return userCollection;
	}

	/**
	* Sets the value of userCollection attribute
	**/

	public void setUserCollection(Collection<User> userCollection){
		this.userCollection = userCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof BbGroup) 
		{
			BbGroup c =(BbGroup)obj; 			 
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