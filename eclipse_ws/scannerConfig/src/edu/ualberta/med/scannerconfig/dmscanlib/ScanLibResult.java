package edu.ualberta.med.scannerconfig.dmscanlib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("nls")
public class ScanLibResult {

    public enum Result {
        SUCCESS(ScanLib.SC_SUCCESS),
        FAIL(ScanLib.SC_FAIL),
        TWAIN_UNAVAIL(ScanLib.SC_TWAIN_UNAVAIL),
        INVALID_DPI(ScanLib.SC_INVALID_DPI),
        INVALID_NOTHING_DECODED(ScanLib.SC_INVALID_NOTHING_DECODED),
        INVALID_IMAGE(ScanLib.SC_INVALID_IMAGE),
        INVALID_NOTHING_TO_DECODE(ScanLib.SC_INVALID_NOTHING_TO_DECODE),
        INCORRECT_DPI_SCANNED(ScanLib.SC_INCORRECT_DPI_SCANNED);

        private final int value;

        private static final Map<Integer, Result> VALUES_MAP;

        static {
            Map<Integer, Result> map = new HashMap<Integer, Result>();

            for (Result resultEnum : values()) {
                Result check = map.get(resultEnum.getValue());
                if (check != null) {
                    throw new IllegalStateException("permission enum value "
                        + resultEnum.getValue() + " used multiple times");
                }

                map.put(resultEnum.getValue(), resultEnum);
            }

            VALUES_MAP = Collections.unmodifiableMap(map);
        }

        private Result(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Map<Integer, Result> valuesMap() {
            return VALUES_MAP;
        }

        public static Result fromValue(Integer value) {
            return valuesMap().get(value);
        }
    }

    private int resultCode;

    private int value; // used by API call to return its value (if any)

    private String message;

    public ScanLibResult(int resultCode, int value, String message) {
        this.resultCode = resultCode;
        this.setValue(value);
        this.message = message;
    }

    public Result getResultCode() {
        return Result.fromValue(resultCode);
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
