package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.model.StorageType;

public class StorageTypeAdapter extends Node {
	private StorageType storageType;
	
	public StorageTypeAdapter(Node parent, StorageType storageType) {
		super(parent);
		this.setStudy(storageType);
	}

	public void setStudy(StorageType storageType) {
		this.storageType = storageType;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	@Override
	public int getId() {
		Assert.isNotNull(storageType, "storage type is null");
		Object o = (Object) storageType.getId();
		if (o == null) return 0;
		return storageType.getId();
	}

	@Override
	public String getName() {
		Assert.isNotNull(storageType, "storage type is null");
		Object o = (Object) storageType.getName();
		if (o == null) return null;
		return storageType.getName();
	}
}
