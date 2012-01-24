package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotNull;
import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/
	

public class ProcessingEvent  implements IBiobankModel
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
	
	public String worksheet;
	/**
	* Retrieves the value of the worksheet attribute
	* @return worksheet
	**/

	public String getWorksheet(){
		return worksheet;
	}

	/**
	* Sets the value of worksheet attribute
	**/

	public void setWorksheet(String worksheet){
		this.worksheet = worksheet;
	}
	
	/**
	* 
	**/
	
	@NotNull
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
	* An associated edu.ualberta.med.biobank.model.ActivityStatus object
	**/
			
	private ActivityStatus activityStatus;
	/**
	* Retrieves the value of the activityStatus attribute
	* @return activityStatus
	**/
	
	public ActivityStatus getActivityStatus(){
		return activityStatus;
	}
	/**
	* Sets the value of activityStatus attribute
	**/

	public void setActivityStatus(ActivityStatus activityStatus){
		this.activityStatus = activityStatus;
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
		if(obj instanceof ProcessingEvent) 
		{
			ProcessingEvent c =(ProcessingEvent)obj; 			 
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
