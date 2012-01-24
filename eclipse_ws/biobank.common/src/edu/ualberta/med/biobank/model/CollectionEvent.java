package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotNull;
import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/
	

public class CollectionEvent  implements IBiobankModel
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
	
	@NotNull
	public Integer visitNumber;
	/**
	* Retrieves the value of the visitNumber attribute
	* @return visitNumber
	**/

	public Integer getVisitNumber(){
		return visitNumber;
	}

	/**
	* Sets the value of visitNumber attribute
	**/

	public void setVisitNumber(Integer visitNumber){
		this.visitNumber = visitNumber;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.Specimen object's collection 
	**/
			
	private Collection<Specimen> allSpecimenCollection = new HashSet<Specimen>();

	/**
	* Retrieves the value of the allSpecimenCollection attribute
	* @return allSpecimenCollection
	**/

	public Collection<Specimen> getAllSpecimenCollection(){
		return allSpecimenCollection;
	}

	/**
	* Sets the value of allSpecimenCollection attribute
	**/

	public void setAllSpecimenCollection(Collection<Specimen> allSpecimenCollection){
		this.allSpecimenCollection = allSpecimenCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Patient object
	**/
			
	private Patient patient;
	/**
	* Retrieves the value of the patient attribute
	* @return patient
	**/
	
	public Patient getPatient(){
		return patient;
	}
	/**
	* Sets the value of patient attribute
	**/

	public void setPatient(Patient patient){
		this.patient = patient;
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
	* An associated edu.ualberta.med.biobank.model.EventAttr object's collection 
	**/
			
	private Collection<EventAttr> eventAttrCollection = new HashSet<EventAttr>();

	/**
	* Retrieves the value of the eventAttrCollection attribute
	* @return eventAttrCollection
	**/

	public Collection<EventAttr> getEventAttrCollection(){
		return eventAttrCollection;
	}

	/**
	* Sets the value of eventAttrCollection attribute
	**/

	public void setEventAttrCollection(Collection<EventAttr> eventAttrCollection){
		this.eventAttrCollection = eventAttrCollection;
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
			
	private Collection<Specimen> originalSpecimenCollection = new HashSet<Specimen>();

	/**
	* Retrieves the value of the originalSpecimenCollection attribute
	* @return originalSpecimenCollection
	**/

	public Collection<Specimen> getOriginalSpecimenCollection(){
		return originalSpecimenCollection;
	}

	/**
	* Sets the value of originalSpecimenCollection attribute
	**/

	public void setOriginalSpecimenCollection(Collection<Specimen> originalSpecimenCollection){
		this.originalSpecimenCollection = originalSpecimenCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof CollectionEvent) 
		{
			CollectionEvent c =(CollectionEvent)obj; 			 
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
