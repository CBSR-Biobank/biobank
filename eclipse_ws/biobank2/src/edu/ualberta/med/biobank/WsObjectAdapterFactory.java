/*******************************************************************************
 * Copyright (c) 2005 Jean-Michel Lemieux, Jeff McAffer and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Hyperbola is an RCP application developed for the book
 *     Eclipse Rich Client Platform - 
 *         Designing, Coding, and Packaging Java Applications
 * See http://eclipsercp.org
 *
 * Contributors:
 *     Jean-Michel Lemieux and Jeff McAffer - initial API and implementation
 *******************************************************************************/
package edu.ualberta.med.biobank;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import edu.ualberta.med.biobank.model.BioBank;
import edu.ualberta.med.biobank.model.BioBankGroup;
import edu.ualberta.med.biobank.model.ClinicGroup;
import edu.ualberta.med.biobank.model.StudyGroup;

public class WsObjectAdapterFactory implements IAdapterFactory {

	private IWorkbenchAdapter bioBankGroupAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return ((BioBankGroup) o).getParent();
		}

		public String getLabel(Object o) {
			return ((BioBankGroup) o).getName();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public Object[] getChildren(Object o) {
			return ((BioBankGroup) o).getBioBanks();
		}
	};

	private IWorkbenchAdapter bioBankAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return ((BioBank) o).getParent();
		}

		public String getLabel(Object o) {
			return ((BioBank) o).getName();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public Object[] getChildren(Object o) {
			return ((BioBank) o).getChildren();
		}
	};

	private IWorkbenchAdapter studyGroupAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return ((StudyGroup) o).getParent();
		}

		public String getLabel(Object o) {
			return ((StudyGroup) o).getName();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public Object[] getChildren(Object o) {
			return ((StudyGroup) o).getStudies();
		}
	};

	private IWorkbenchAdapter clinicGroupAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return ((ClinicGroup) o).getParent();
		}

		public String getLabel(Object o) {
			return ((ClinicGroup) o).getName();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public Object[] getChildren(Object o) {
			return ((ClinicGroup) o).getClinics();
		}
	};

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class) {
			if (adaptableObject instanceof BioBankGroup)
				return bioBankGroupAdapter;
			else if (adaptableObject instanceof BioBank)
				return bioBankAdapter;
			else if (adaptableObject instanceof ClinicGroup)
				return clinicGroupAdapter;
			else if (adaptableObject instanceof StudyGroup)
				return studyGroupAdapter;
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}
}
