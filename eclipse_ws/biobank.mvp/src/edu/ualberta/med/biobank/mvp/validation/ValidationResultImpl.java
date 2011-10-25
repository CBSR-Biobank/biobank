package edu.ualberta.med.biobank.mvp.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class ValidationResultImpl implements ValidationResult,
    ValidationResultCollector {
    private final List<ValidationMessage> messages =
        new ArrayList<ValidationMessage>();
    private final TreeMap<Level, List<ValidationMessage>> levelMap =
        new TreeMap<Level, List<ValidationMessage>>();

    public ValidationResultImpl() {
    }

    /**
     * Copy constructor.
     * 
     * @param copy
     */
    public ValidationResultImpl(ValidationResult copy) {
        addAll(copy.getMessages());
    }

    @Override
    public void add(ValidationMessage message) {
        messages.add(message);
        getLevel(message.getLevel()).add(message);
    }

    @Override
    public void addAll(Collection<ValidationMessage> messages) {
        for (ValidationMessage message : messages) {
            add(message);
        }
    }

    @Override
    public boolean isEmpty() {
        return messages.isEmpty();
    }

    @Override
    public boolean contains(Level level) {
        return getLevels().contains(level);
    }

    @Override
    public List<ValidationMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public List<ValidationMessage> getMessages(Level level) {
        return Collections.unmodifiableList(getLevel(level));
    }

    @Override
    public SortedSet<Level> getLevels() {
        return new TreeSet<Level>(levelMap.keySet());
    }

    protected List<ValidationMessage> getLevel(Level level) {
        List<ValidationMessage> list = levelMap.get(level);
        if (list == null) {
            list = new ArrayList<ValidationMessage>();
            levelMap.put(level, list);
        }
        return list;
    }
}
