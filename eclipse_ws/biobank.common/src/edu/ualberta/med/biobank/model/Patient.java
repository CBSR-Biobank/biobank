package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/
	

public class Patient  implements IBiobankModel
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
	
	@NotEmpty
	public String pnumber;
	/**
	* Retrieves the value of the pnumber attribute
	* @return pnumber
	**/

	public String getPnumber(){
		return pnumber;
	}

	/**
	* Sets the value of pnumber attribute
	**/

	public void setPnumber(String pnumber){
		this.pnumber = pnumber;
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
	* An associated edu.ualberta.med.biobank.model.CollectionEvent object's collection 
	**/
			
	private Collection<CollectionEvent> collectionEventCollection = new HashSet<CollectionEvent>();

	/**
	* Retrieves the value of the collectionEventCollection attribute
	* @return collectionEventCollection
	**/

	public Collection<CollectionEvent> getCollectionEventCollection(){
		return collectionEventCollection;
	}

	/**
	* Sets the value of collectionEventCollection attribute
	**/

	public void setCollectionEventCollection(Collection<CollectionEvent> collectionEventCollection){
		this.collectionEventCollection = collectionEventCollection;
	}
		
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
		if(obj instanceof Patient) 
		{
			Patient c =(Patient)obj; 			 
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
