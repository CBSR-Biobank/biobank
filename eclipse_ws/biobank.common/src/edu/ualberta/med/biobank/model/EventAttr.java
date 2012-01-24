package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class EventAttr  implements IBiobankModel
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

        private Integer version;

	
	/**
	* 
	**/
	
	private Integer id;
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
	
	private String value;
	/**
	* Retrieves the value of the value attribute
	* @return value
	**/

	public String getValue(){
		return value;
	}

	/**
	* Sets the value of value attribute
	**/

	public void setValue(String value){
		this.value = value;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.CollectionEvent object
	**/
			
	private CollectionEvent collectionEvent;
	/**
	* Retrieves the value of the collectionEvent attribute
	* @return collectionEvent
	**/
	
	public CollectionEvent getCollectionEvent(){
		return collectionEvent;
	}
	/**
	* Sets the value of collectionEvent attribute
	**/

	public void setCollectionEvent(CollectionEvent collectionEvent){
		this.collectionEvent = collectionEvent;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.StudyEventAttr object
	**/
			
	private StudyEventAttr studyEventAttr;
	/**
	* Retrieves the value of the studyEventAttr attribute
	* @return studyEventAttr
	**/
	
	public StudyEventAttr getStudyEventAttr(){
		return studyEventAttr;
	}
	/**
	* Sets the value of studyEventAttr attribute
	**/

	public void setStudyEventAttr(StudyEventAttr studyEventAttr){
		this.studyEventAttr = studyEventAttr;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof EventAttr) 
		{
			EventAttr c =(EventAttr)obj; 			 
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
