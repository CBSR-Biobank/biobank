package edu.ualberta.med.biobank.widgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.model.EventAttrCustom;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.StudyEventAttr;

public class StudyEventAttrSelectionWidget extends BgcBaseWidget {

    private static class StudyEventAttrCustom extends EventAttrCustom {
        public EventAttrWidget widget;
        public GlobalEventAttr geAttr;
        public boolean selected;
    }

    Map<String, StudyEventAttrCustom> seAttrSettings;

    public StudyEventAttrSelectionWidget(Composite parent, int style,
        Set<GlobalEventAttr> globalEventAttrs,
        Set<StudyEventAttr> studyEventAttrs, BgcEntryFormWidgetListener listener) {
        super(parent, style);

        seAttrSettings = new HashMap<String, StudyEventAttrCustom>();

        StudyEventAttrCustom seAttrCustom;

        for (GlobalEventAttr geAttr : globalEventAttrs) {
            String label = geAttr.getLabel();
            seAttrCustom = new StudyEventAttrCustom();
            seAttrCustom.geAttr = geAttr;
            seAttrCustom.setLabel(label);
            seAttrCustom.setType(geAttr.getEventAttrType().getName());
            seAttrCustom.setIsDefault(false);
            seAttrCustom.selected = false; // set correctly below
            seAttrSettings.put(geAttr.getLabel(), seAttrCustom);
        }

        for (StudyEventAttr seAttr : studyEventAttrs) {
            seAttrCustom =
                seAttrSettings.get(seAttr.getGlobalEventAttr().getLabel());
            if (seAttrCustom == null) {
                throw new NullPointerException();
            }
            seAttrCustom.selected = true;
            seAttrCustom.setAllowedValues(seAttr.getPermissible().split(";"));
        }

        for (GlobalEventAttr geAttr : globalEventAttrs) {
            seAttrCustom = seAttrSettings.get(geAttr.getLabel());
            seAttrCustom.widget = new EventAttrWidget(this, SWT.NONE,
                seAttrCustom, seAttrCustom.selected);
            seAttrCustom.widget.addSelectionChangedListener(listener);
        }
    }

    public Set<StudyEventAttr> getStudyEventAttr() {
        Set<StudyEventAttr> newSeAttrs = new HashSet<StudyEventAttr>();
        for (StudyEventAttrCustom seAttrCustom : seAttrSettings.values()) {
            if (!seAttrCustom.widget.getSelected()) continue;

            StudyEventAttr seAttr = new StudyEventAttr();
            seAttr.setGlobalEventAttr(seAttrCustom.geAttr);
            seAttr.setPermissible(seAttrCustom.widget.getValues());
            newSeAttrs.add(seAttr);
        }
        return newSeAttrs;
    }
}
