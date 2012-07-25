package edu.ualberta.med.biobank.action.security.util;

import junit.framework.Assert;

/**
 * Runs different inputs through the same executor/ code, comparing the output
 * with an expected output. Meant to make testing much easier for me when the
 * test code is the same, but the input changes.
 * 
 * @author Jonathan Ferland
 * 
 * @param <I> type of input
 * @param <O> type of output
 */
public abstract class TestCase<I, O> {
    public void run(Iterable<I> inputs, O expected) {
        for (I input : inputs) {
            run(input, expected);
        }
    }

    public void run(IIterableBuilder<I> builder, O expected) {
        run(builder.build(), expected);
    }

    public void run(I input, O expected) {
        O actual = run(input);
        Assert.assertEquals("failure with input " + input,
            expected, actual);
    }

    public abstract O run(I input);

    public interface IIterableBuilder<I> {
        public Iterable<I> build();
    }
}
