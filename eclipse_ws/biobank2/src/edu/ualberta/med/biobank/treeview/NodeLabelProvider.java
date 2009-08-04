package edu.ualberta.med.biobank.treeview;

import java.io.File;
import java.util.HashMap;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.BioBankPlugin;

public class NodeLabelProvider implements ILabelProvider {
    private HashMap<String, Image> imageCollection = null;

    public Image getImage(Object element) {
        Image image;
        String imagePath = "icons/";

        if (element instanceof SessionAdapter) {
            image = imageCollection.get("session");
        } else if (element instanceof ClinicGroup) {
            image = imageCollection.get("clinics");
        } else if (element instanceof ClinicAdapter) {
            image = imageCollection.get("clinic");
        } else if (element instanceof StudyGroup) {
            image = imageCollection.get("studies");
        } else if (element instanceof StudyAdapter) {
            image = imageCollection.get("study");
        } else if (element instanceof SiteAdapter) {
            image = imageCollection.get("site");
        } else if (element instanceof PatientGroup) {
            image = imageCollection.get("dudes");
        } else if (element instanceof PatientSubGroup) {
            image = imageCollection.get("dudeplus");
        } else if (element instanceof PatientAdapter) {
            image = imageCollection.get("dude");
        } else if (element instanceof ContainerGroup) {
            image = imageCollection.get("container");
        } else if (element instanceof ContainerTypeGroup) {
            image = imageCollection.get("containertype");
        } else if (element instanceof ContainerAdapter
            || element instanceof ContainerTypeAdapter) {
            String ct; // container type
            if (element instanceof ContainerAdapter) {
                ct = ((ContainerAdapter) element).getContainer()
                    .getContainerType().getName();
            } else {
                ct = ((ContainerTypeAdapter) element).getName();
            }
            if (!imageCollection.containsKey(ct)) {
                if (new File(imagePath + ct.toLowerCase() + ".png").exists()) {
                    imageCollection.put(ct, BioBankPlugin.getImage(imagePath
                        + ct.toLowerCase() + ".png"));
                } else {
                    imageCollection.put(ct, null);
                }
            }
            image = imageCollection.get(ct);
        } else {
            image = null;
        }
        return image;
    }

    public String getText(Object element) {
        if (element instanceof AdaptorBase) {
            return ((AdaptorBase) element).getTreeText();
        }
        return new String();
    }

    public void addListener(ILabelProviderListener listener) {
        String imagePath = "icons/";

        imageCollection = new HashMap<String, Image>();

        imageCollection.put("debug", BioBankPlugin.getImage(imagePath
            + "rainbow.png"));
        imageCollection.put("session", BioBankPlugin.getImage(imagePath
            + "book_addresses.png"));
        imageCollection.put("clinics", BioBankPlugin.getImage(imagePath
            + "bricks.png"));
        imageCollection.put("study", BioBankPlugin.getImage(imagePath
            + "book_open.png"));
        imageCollection.put("studies", BioBankPlugin.getImage(imagePath
            + "book.png"));
        imageCollection.put("site", BioBankPlugin.getImage(imagePath
            + "brick.png"));
        imageCollection.put("dude", BioBankPlugin.getImage(imagePath
            + "user_red.png"));
        imageCollection.put("dudes", BioBankPlugin.getImage(imagePath
            + "user_go.png"));
        imageCollection.put("container", BioBankPlugin.getImage(imagePath
            + "basket.png"));
        imageCollection.put("clinic", BioBankPlugin.getImage(imagePath
            + "transmit_blue.png"));
        imageCollection.put("dudeplus", BioBankPlugin.getImage(imagePath
            + "user_add.png"));
        imageCollection.put("containertype", BioBankPlugin.getImage(imagePath
            + "basket_edit.png"));
    }

    public void dispose() {

    }

    public boolean isLabelProperty(Object element, String property) {
        return true;
    }

    public void removeListener(ILabelProviderListener listener) {
    }

}
