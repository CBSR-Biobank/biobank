package edu.ualberta.med.biobank.treeview;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.BioBankPlugin;

public class NodeLabelProvider implements ILabelProvider {
    private HashMap<String, Image> imageCollection = null;

    private static String[] CONTAINER_TYPE_TO_IMAGE_KEY = null;

    static {
        Properties properties = new Properties();
        try {
            properties.load(NodeLabelProvider.class
                .getResourceAsStream("containerTypesToImageKey.properties"));
            CONTAINER_TYPE_TO_IMAGE_KEY = properties.keySet().toArray(
                new String[properties.keySet().size()]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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
            put(ContainerTypeAdapter.class.getName(),
                BioBankPlugin.IMG_CONTAINER_TYPES);
            put(ContainerAdapter.class.getName(), BioBankPlugin.IMG_CONTAINERS);
            put(PatientAdapter.class.getName(), BioBankPlugin.IMG_PATIENT);
        }
    };

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
            return imageCollection.get(typeName);
        }

        String imageKey = null;
        for (String name : CONTAINER_TYPE_TO_IMAGE_KEY) {
            if (typeName.contains(name)) {
                imageKey = name;
                break;
            }
        }

        if (imageKey == null)
            imageKey = BioBankPlugin.IMG_CONTAINERS;

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

    public void dispose() {
        for (Image image : imageCollection.values()) {
            if (image != null) {
                image.dispose();
            }
        }
        imageCollection.clear();

    }

    public boolean isLabelProperty(Object element, String property) {
        return true;
    }

    public void removeListener(ILabelProviderListener listener) {
    }

}
