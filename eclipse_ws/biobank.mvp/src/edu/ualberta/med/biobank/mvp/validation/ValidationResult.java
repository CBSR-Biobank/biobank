package edu.ualberta.med.biobank.mvp.validation;

import java.util.List;
import java.util.SortedSet;

public interface ValidationResult {
    boolean isEmpty();

    public List<ValidationMessage> getMessages();

    public List<ValidationMessage> getMessages(Level level);

    public SortedSet<Level> getLevels();
}
