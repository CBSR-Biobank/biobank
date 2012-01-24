package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class DispatchSpecimen  implements IBiobankModel
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
	
	public Integer state;
	/**
	* Retrieves the value of the state attribute
	* @return state
	**/

	public Integer getState(){
		return state;
	}

	/**
	* Sets the value of state attribute
	**/

	public void setState(Integer state){
		this.state = state;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.Dispatch object
	**/
			
	private Dispatch dispatch;
	/**
	* Retrieves the value of the dispatch attribute
	* @return dispatch
	**/
	
	public Dispatch getDispatch(){
		return dispatch;
	}
	/**
	* Sets the value of dispatch attribute
	**/

	public void setDispatch(Dispatch dispatch){
		this.dispatch = dispatch;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Specimen object
	**/
			
	private Specimen specimen;
	/**
	* Retrieves the value of the specimen attribute
	* @return specimen
	**/
	
	public Specimen getSpecimen(){
		return specimen;
	}
	/**
	* Sets the value of specimen attribute
	**/

	public void setSpecimen(Specimen specimen){
		this.specimen = specimen;
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
		if(obj instanceof DispatchSpecimen) 
		{
			DispatchSpecimen c =(DispatchSpecimen)obj; 			 
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
