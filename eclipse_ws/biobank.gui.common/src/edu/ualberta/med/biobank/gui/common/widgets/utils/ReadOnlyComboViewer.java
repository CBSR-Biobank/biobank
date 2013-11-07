package edu.ualberta.med.biobank.gui.common.widgets.utils;

import java.util.Collection;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;

/**
 * A read only combo with databinding.
 * 
 * @author loyola
 * 
 * @param <T>
 */
public class ReadOnlyComboViewer<T> extends org.eclipse.jface.viewers.ComboViewer {

    private final Binding binding;

    /**
     * Create a combo viewer with a validator when selection is null using errorMessage and using
     * the default comparator if useDefaultComparator is set to true.
     * 
     * @param parent the parent composite.
     * @param dbc the data binding contect.
     * @param fieldLabel the label to be displayed to the left of the combo box.
     * @param input the items to select from. Can be {@link null}.
     * @param selection the default selection. Can be {@link null}.
     * @param errorMessage the error message to display if nothing is selected.
     * @param useDefaultComparator set to true to sort the selection list.
     * @param csu when not {@link null}, the callback interface to invoke when an item is sele
     * @param labelProvider the label provider for the combo input.
     * @param modifyListener Adds the listener to the collection of listeners who will be notified
     *            when the receiver's text is modified, by sending it one of the messages defined in
     *            the <code>ModifyListener</code> interface.
     */
    public ReadOnlyComboViewer(Composite parent, DataBindingContext dbc,
        Label fieldLabel, Collection<? extends T> input, T selection,
        String errorMessage, boolean useDefaultComparator,
        final ComboSelectionUpdate csu, IBaseLabelProvider labelProvider,
        ModifyListener modifyListener) {
        super(new Combo(parent, SWT.READ_ONLY | SWT.BORDER));
        Combo combo = this.getCombo();
        setContentProvider(new ArrayContentProvider());
        setLabelProvider(labelProvider);
        if (useDefaultComparator) {
            setComparator(new ViewerComparator());
        }

        if (input != null) {
            setInput(input);
        }

        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        if ((dbc != null) && (fieldLabel != null)) {
            NonEmptyStringValidator validator = new NonEmptyStringValidator(errorMessage);
            validator.setControlDecoration(BgcBaseWidget.createDecorator(fieldLabel, errorMessage));
            UpdateValueStrategy uvs = new UpdateValueStrategy();
            uvs.setAfterGetValidator(validator);
            IObservableValue selectedValue = new WritableValue(
                StringUtil.EMPTY_STRING, String.class);
            binding = dbc.bindValue(SWTObservables.observeSelection(combo), selectedValue, uvs, null);
        } else {
            binding = null;
        }

        if (selection != null) {
            setSelection(new StructuredSelection(selection));
        }

        if (csu != null) {
            addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) getSelection();
                    if ((selection != null) && (selection.size() > 0)) {
                        csu.doSelection(selection.getFirstElement());
                    } else {
                        csu.doSelection(null);
                    }
                }
            });
        }

        if (modifyListener != null) {
            combo.addModifyListener(modifyListener);
        }

        combo.addListener(SWT.MouseWheel, new Listener() {
            @Override
            public void handleEvent(Event event) {
                event.doit = false;
            }
        });
    }

    /**
     * Returns the binding used by the observable values.
     * 
     * @return the binding if a databinding context was passed to the contructor.
     */
    @SuppressWarnings("nls")
    public Binding getBinding() {
        if (binding == null) {
            throw new IllegalStateException("combo viewer was constructed without a databinding");
        }
        return binding;
    }
}
