package edu.ualberta.med.biobank.common.action;

import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.wrappers.Property;

public class AbstractMutator<M> {
	protected final ActionContext context;
	protected final Class<M> modelClass;
	protected final M model;
	
	protected AbstractMutator(ActionContext context, Class<M> modelClass, M model) {
		this.context = context;
		this.modelClass = modelClass;
		this.model = model;
	}

	protected <E> void notNull(Property<E, ? extends M> property, E value) throws NullPropertyException {
		if (value == null) {
			throw new NullPropertyException(modelClass, property.getName());
		}
	}
	
	protected void unique(Value... values) {
		
	}
	
	public static class Unique {

	}
	
	protected <E> Value value(Property<E, ? extends M> property, E value) {
		return new Value(property.getName(), value);
	}
	
	public static class Value {
		final String property;
		final Object value;
		
		public Value(String property, Object value) {
			this.property = property;
			this.value = value;
		}
	}
}
