package at.jku.ce.bp_v1.classes;

import java.io.Serializable;

public enum Akzeptanz implements Serializable {
    AKZEPTANZ("AKZEPTANZ"), ABLEHNUNG("ABLEHNUNG"), SITUATIONSABHAENGIG("SITUATIONSABHAENGIG"), UNDEFINIERT("UNDEFINIERT"), AUSSTEHEND("AUSSTEHEND"), UNERLAUBT("UNERLAUBT");

    private final String value;

    Akzeptanz(String value) {
        this.value = value;
    }

    public static Akzeptanz fromValue(String value) {
        if (value != null) {
            for (Akzeptanz a : values()) {
                if (a.value.equals(value)) {
                    return a;
                }
            }
        }

        throw new IllegalArgumentException("Ungültige Akzeptanz: " + value);
    }

    public String toValue() {
        return value;
    }
}
