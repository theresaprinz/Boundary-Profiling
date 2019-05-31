package at.jku.ce.bp_v1.classes;


import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class Kontext implements Serializable, Comparable<Kontext> {
    private static final long serialVersionUID = 1L;

    private UUID _id;
    private String name;

    public Kontext(String name) {
        _id = UUID.randomUUID();
        this.name = name;
    }

    public UUID get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean equalName(Kontext qKontext) {
        return name.equals(qKontext.getName());
    }

    @Override
    public int compareTo(@NonNull Kontext kontext) {
        return name.compareTo(kontext.getName());
    }
}
