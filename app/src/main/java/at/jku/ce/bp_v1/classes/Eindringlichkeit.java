package at.jku.ce.bp_v1.classes;

import java.io.Serializable;

public enum Eindringlichkeit implements Serializable {
    HOCH("HOCH"), MITTEL("MITTEL"), GERING("GERING"), UNDEFINIERT("UNDEFINIERT"), AUSSTEHEND("AUSSTEHEND"), UNERLAUBT("UNERLAUBT");

    private final String value;

    Eindringlichkeit(String value) {
        this.value = value;
    }

    public static Eindringlichkeit fromValue(String value) {
        if (value != null) {
            for (Eindringlichkeit e : values()) {
                if (e.value.equals(value)) {
                    return e;
                }
            }
        }

        return null;
    }

    public String toValue() {
        return value;
    }
}
