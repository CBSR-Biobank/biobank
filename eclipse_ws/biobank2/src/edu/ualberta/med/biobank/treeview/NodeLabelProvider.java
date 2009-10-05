package edu.ualberta.med.biobank.treeview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.BioBankPlugin;

public class NodeLabelProvider implements ILabelProvider {

    // FIXME: move this to preferences
    // 
    // ContainerTypeAdapter and Container missing on purpose.
    //
    private Map<String, String> nodeToImageKey = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put(SessionAdapter.class.getName(), BioBankPlugin.IMG_SESSIONS);
            put(SiteAdapter.class.getName(), BioBankPlugin.IMG_SITE);
            put(ClinicGroup.class.getName(), BioBankPlugin.IMG_CLINICS);
            put(StudyGroup.class.getName(), BioBankPlugin.IMG_STUDIES);
            put(ContainerTypeGroup.class.getName(),
                BioBankPlugin.IMG_CONTAINER_TYPES);
            put(ContainerGroup.class.getName(), BioBankPlugin.IMG_CONTAINERS);
            put(ClinicAdapter.class.getName(), BioBankPlugin.IMG_CLINIC);
            put(StudyAdapter.class.getName(), BioBankPlugin.IMG_STUDY);
            put(PatientAdapter.class.getName(), BioBankPlugin.IMG_PATIENT);
            put(PatientVisitAdapter.class.getName(),
                BioBankPlugin.IMG_PATIENT_VISIT);
        }
    };

    private static final String[] CONTAINER_TYPE_IMAGE_KEYS = new String[] {
        BioBankPlugin.IMG_BIN, BioBankPlugin.IMG_BOX,
        BioBankPlugin.IMG_CABINET, BioBankPlugin.IMG_DRAWER,
        BioBankPlugin.IMG_FREEZER, BioBankPlugin.IMG_HOTEL,
        BioBankPlugin.IMG_PALLET, };

    public Image getImage(Object element) {
        String imageKey = nodeToImageKey.get(element.getClass().getName());
        if ((imageKey == null)
            && ((element instanceof ContainerAdapter) || (element instanceof ContainerTypeAdapter))) {
            String ctName;
            if (element instanceof ContainerAdapter) {
                ctName = ((ContainerAdapter) element).getContainer()
                    .getContainerType().getName();
            } else {
                ctName = ((ContainerTypeAdapter) element).getName();
            }
            return getIconForTypeName(ctName);
        }
        return BioBankPlugin.getDefault().getImageRegistry().get(imageKey);
    }

    public Image getIconForTypeName(String typeName) {
        if (nodeToImageKey.containsKey(typeName)) {
            return BioBankPlugin.getDefault().getImageRegistry().get(
                nodeToImageKey.get(typeName));
        }

        String imageKey = null;
        for (String name : CONTAINER_TYPE_IMAGE_KEYS) {
            if (typeName.toLowerCase().contains(name)) {
                imageKey = name;
                break;
            }
        }

        if (imageKey == null)
            imageKey = BioBankPlugin.IMG_FREEZER;

        nodeToImageKey.put(typeName, imageKey);
        return BioBankPlugin.getDefault().getImageRegistry().get(imageKey);
    }

    public String getText(Object element) {
        if (element instanceof AdapterBase) {
            return ((AdapterBase) element).getTreeText();
        }
        return new String();
    }

    public void addListener(ILabelProviderListener listener) {
    }

    public boolean isLabelProperty(Object element, String property) {
        return true;
    }

    public void removeListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

}
