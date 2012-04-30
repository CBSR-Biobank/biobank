package edu.ualberta.med.biobank.common.action;

import java.util.Map;

public class MapResult<K, V> implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final Map<K, V> map;

    public MapResult(Map<K, V> map) {
        this.map = map;
    }

    public Map<K, V> getMap() {
        return map;
    }
}
