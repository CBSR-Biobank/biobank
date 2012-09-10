package edu.ualberta.med.biobank.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import edu.ualberta.med.biobank.common.util.StringUtil;

public class NameGenerator {
    @SuppressWarnings("nls")
    private static final String DELIMITER = "_";

    @SuppressWarnings("nls")
    private static final String TRUNCATE_DELIMITER = "...";

    private final String root;
    private final ConcurrentHashMap<Class<?>, AtomicInteger> suffixes =
        new ConcurrentHashMap<Class<?>, AtomicInteger>();

    public NameGenerator(String root) {
        this.root = formatRoot(root);
    }

    public String next(Class<?> klazz) {
        suffixes.putIfAbsent(klazz, new AtomicInteger(1));

        StringBuilder sb = new StringBuilder();
        sb.append(root);
        sb.append(DELIMITER);
        sb.append(suffixes.get(klazz).incrementAndGet());

        return sb.toString();
    }

    private String formatRoot(String root) {
        String tmp = StringUtil.truncate(root, 25, TRUNCATE_DELIMITER);
        if (tmp != root) {
            tmp += root.substring(root.length() - 5);
        }
        return tmp;
    }
}
