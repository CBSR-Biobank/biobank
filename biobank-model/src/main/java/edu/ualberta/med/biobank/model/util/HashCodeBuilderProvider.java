package edu.ualberta.med.biobank.model.util;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple provider of {@link HashCodeBuilder}s that logs warnings when two
 * classes use the same {@code initialNonZeroOddNumber} or
 * {@code multiplierNonZeroOddNumber} numbers.
 * 
 * @author Jonathan Ferland
 */
public class HashCodeBuilderProvider {
    private static final Logger log = LoggerFactory
        .getLogger(HashCodeBuilderProvider.class);
    private static final ConcurrentHashMap<Integer, Class<?>> registry =
        new ConcurrentHashMap<Integer, Class<?>>();

    private final int initialNonZeroOddNumber;
    private final int multiplierNonZeroOddNumber;

    public HashCodeBuilderProvider(Class<?> klazz, int initialNonZeroOddNumber,
        int multiplierNonZeroOddNumber) {
        this.initialNonZeroOddNumber = initialNonZeroOddNumber;
        this.multiplierNonZeroOddNumber = multiplierNonZeroOddNumber;

        Class<?> owner = registry.putIfAbsent(initialNonZeroOddNumber, klazz);
        if (owner != null) {
            log.warn("The initialNonZeroOddNumber {} is already being used by"
                + " {} when {} tried to use it. Consider changing one so this"
                + " number is unique across classes",
                new Object[] {
                    initialNonZeroOddNumber,
                    owner,
                    klazz
                });
        }

        owner = registry.putIfAbsent(multiplierNonZeroOddNumber, klazz);
        if (owner != null) {
            log.warn("The multiplierNonZeroOddNumber {} is already being"
                + " used by {} when {} tried to use it. Consider changing"
                + " one so this number is unique across classes",
                new Object[] {
                    multiplierNonZeroOddNumber,
                    owner,
                    klazz
                });
        }
    }

    public HashCodeBuilder get() {
        return new HashCodeBuilder(initialNonZeroOddNumber,
            multiplierNonZeroOddNumber);
    }
}
