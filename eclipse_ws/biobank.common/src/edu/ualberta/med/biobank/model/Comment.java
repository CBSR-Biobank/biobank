package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class Comment  implements IBiobankModel
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

        private Integer version;

	
	/**
	* 
	**/
	
	public String message;
	/**
	* Retrieves the value of the message attribute
	* @return message
	**/

	public String getMessage(){
		return message;
	}

	/**
	* Sets the value of message attribute
	**/

	public void setMessage(String message){
		this.message = message;
	}
	
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
	* An associated edu.ualberta.med.biobank.model.User object
	**/
			
	private User user;
	/**
	* Retrieves the value of the user attribute
	* @return user
	**/
	
	public User getUser(){
		return user;
	}
	/**
	* Sets the value of user attribute
	**/

	public void setUser(User user){
		this.user = user;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Comment) 
		{
			Comment c =(Comment)obj; 			 
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
