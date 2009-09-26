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

    public NodeLabelProvider() {
        String imagePath = "icons/";
        Display d = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getShell().getDisplay();
        imageCollection = new HashMap<String, Image>();
        imageCollection.put(SessionAdapter.class.getName(), new Image(d,
            imagePath + "sessions.png"));
        imageCollection.put(ClinicGroup.class.getName(), new Image(d, imagePath
            + "clinics.png"));
        imageCollection.put(StudyAdapter.class.getName(), new Image(d,
            imagePath + "study.png"));
        imageCollection.put(StudyGroup.class.getName(), new Image(d, imagePath
            + "studies.png"));
        imageCollection.put(SiteAdapter.class.getName(), new Image(d, imagePath
            + "site.png"));
        imageCollection.put(PatientAdapter.class.getName(), new Image(d,
            imagePath + "patient.png"));
        imageCollection.put(ContainerGroup.class.getName(), new Image(d,
            imagePath + "containers.png"));
        imageCollection.put(ClinicAdapter.class.getName(), new Image(d,
            imagePath + "clinic.png"));
        imageCollection.put(ContainerTypeGroup.class.getName(), new Image(d,
            imagePath + "container_types.png"));
    }

    public Image getImage(Object element) {
        Image image = imageCollection.get(element.getClass().getName());
        if (image == null
            && (element instanceof ContainerAdapter || element instanceof ContainerTypeAdapter)) {
            String imagePath = "icons/";
            Display d = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getShell().getDisplay();
            String ctName;
            if (element instanceof ContainerAdapter) {
                ctName = ((ContainerAdapter) element).getContainer()
                    .getContainerType().getName();
            } else {
                ctName = ((ContainerTypeAdapter) element).getName();
            }
            if (!imageCollection.containsKey(ctName)) {
                if (new File(imagePath + ctName.toLowerCase() + ".png")
                    .exists()) {
                    image = new Image(d, imagePath + ctName.toLowerCase()
                        + ".png");
                }
                imageCollection.put(ctName, image);
            }
        }
        return image;
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
