package edu.ualberta.med.biobank.treeview;

import java.io.File;
import java.util.HashMap;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class NodeLabelProvider implements ILabelProvider {
    private HashMap<String, Image> imageCollection = null;

    public Image getImage(Object element) {
        Image image;
        String imagePath = "D:/proj/biobank2/icons/";

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
                    imageCollection.put(ct, new Image(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell().getDisplay(),
                        imagePath + ct.toLowerCase() + ".png"));
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
        if (element instanceof Node) {
            return ((Node) element).getTreeText();
        }
        return new String();
    }

    public void addListener(ILabelProviderListener listener) {
        Display d = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getShell().getDisplay();
        String imagePath = "D:/proj/biobank2/icons/";

        imageCollection = new HashMap<String, Image>();
        imageCollection.put("debug", new Image(d, imagePath + "rainbow.png"));
        imageCollection.put("session", new Image(d, imagePath
            + "book_addresses.png"));
        imageCollection.put("clinics", new Image(d, imagePath + "bricks.png"));
        imageCollection.put("study", new Image(d, imagePath + "book_open.png"));
        imageCollection.put("studies", new Image(d, imagePath + "book.png"));
        imageCollection.put("site", new Image(d, imagePath + "brick.png"));
        imageCollection.put("dude", new Image(d, imagePath + "user_red.png"));
        imageCollection.put("dudes", new Image(d, imagePath + "user_go.png"));
        imageCollection
            .put("container", new Image(d, imagePath + "basket.png"));
        imageCollection.put("clinic", new Image(d, imagePath
            + "transmit_blue.png"));
        imageCollection.put("dudeplus",
            new Image(d, imagePath + "user_add.png"));
        imageCollection.put("containertype", new Image(d, imagePath
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
