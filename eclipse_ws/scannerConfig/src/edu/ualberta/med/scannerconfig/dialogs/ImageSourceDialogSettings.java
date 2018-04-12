package edu.ualberta.med.scannerconfig.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.scannerconfig.ImageSource;

/**
 * Manages the settings for the {@link DecodeImageDialog}.
 * 
 * @author loyola
 * 
 */
public class ImageSourceDialogSettings {

    private static Logger log = LoggerFactory.getLogger(ImageSourceDialogSettings.class);

    @SuppressWarnings("nls")
    private static final String LAST_USED_IMAGE_SOURCE_KEY = "DecodePlateDialogSettings.last.image.source";

    @SuppressWarnings("nls")
    private static final String DECODE_PLATE_SETTINGS_SECTION = "DecodePlateDialogSettings.plate.settings";

    private final IDialogSettings dialogSettings;

    private ImageSource imageSource;

    private final Map<String, ImageSourceSettings> imageSourceSettings;

    /**
     * This object is responsible for storage and retrieval of the settings used by
     * {@link DecodeImageDialog}.
     * 
     * @param settings The settings object for the dialog box.
     */
    public ImageSourceDialogSettings(IDialogSettings settings) {
        this.dialogSettings = settings;
        this.imageSourceSettings = new HashMap<String, ImageSourceSettings>(ImageSource.size);
        restore();
    }

    /**
     * Restores the settings from the dialog's settings store. If the settings store does not have a
     * value for a setting then the default values are assigned.
     */
    @SuppressWarnings("nls")
    public void restore() {
        imageSource = restoreImageSource();

        for (ImageSource source : ImageSource.values()) {
            ImageSourceSettings settings = null;
            IDialogSettings section = dialogSettings.getSection(DECODE_PLATE_SETTINGS_SECTION);
            if (section != null) {
                settings = ImageSourceSettings.getSettingsFromSection(source, section);
            } else {
                settings = ImageSourceSettings.defaultSettings(source);
                log.trace("restore: default value applied");
            }
            imageSourceSettings.put(source.getId(), settings);
        }
    }

    /**
     * Saves the settings to the dialog's setting store.
     */
    @SuppressWarnings("nls")
    public void save() {
        saveImageSource(getImageSource());
        IDialogSettings section = dialogSettings.getSection(DECODE_PLATE_SETTINGS_SECTION);

        if (section == null) {
            section = dialogSettings.addNewSection(DECODE_PLATE_SETTINGS_SECTION);
        }

        for (Entry<String, ImageSourceSettings> entry : imageSourceSettings.entrySet()) {
            String key = entry.getKey();
            ImageSourceSettings settings = entry.getValue();

            IDialogSettings imageSourceSection = section.getSection(key);
            if (imageSourceSection == null) {
                imageSourceSection = section.addNewSection(key);
            }

            if (settings != null) {
                settings.putSettingsInSection(imageSourceSection);
                log.trace("save: source: {}", settings.getImageSource());
            }
        }
    }

    private ImageSource restoreImageSource() {
        String value = dialogSettings.get(LAST_USED_IMAGE_SOURCE_KEY);
        if (value != null) {
            return ImageSource.getFromIdString(value);
        }
        return ImageSource.FILE;
    }

    private void saveImageSource(ImageSource key) {
        dialogSettings.put(LAST_USED_IMAGE_SOURCE_KEY, key.getId());
    }

    public ImageSource getImageSource() {
        return imageSource;
    }

    public void setImageSource(ImageSource imageSource) {
        this.imageSource = imageSource;
    }

    public ImageSourceSettings getImageSourceSettings(ImageSource source) {
        return imageSourceSettings.get(source.getId());
    }

}
