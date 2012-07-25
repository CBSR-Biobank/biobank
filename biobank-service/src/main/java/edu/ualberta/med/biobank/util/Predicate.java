package edu.ualberta.med.biobank.util;

/**
 * Define a test that can be evaluated on T, the result of which is true or
 * false. The test can then be applied to a collection to filter out results for
 * which evaluate() returns false, something like:
 * 
 * <pre>
 * final Date d1 = new Date();
 * final Date d2 = new Date();
 * 
 * Predicate&lt;AliquotWrapper&gt; checkLinkDate = new Predicate&lt;AliquotWrapper&gt;() {
 *     public boolean evaluate(AliquotWrapper aliquot) {
 *         return aliquot.getLinkDate().after(d1)
 *             &amp;&amp; aliquot.getLinkDate().before(d2);
 *     }
 * };
 * 
 * Collection&lt;AliquotWrapper&gt; filtrate = PredicateUtil.filter(aliquots,
 *     checkLinkDate);
 * </pre>
 * 
 * @author jferland
 * 
 * @param <T>
 */
public interface Predicate<T> {
    boolean evaluate(T type);
}
