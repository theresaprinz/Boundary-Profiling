package at.jku.ce.bp_v1.classes;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class Rolle implements Serializable, Comparable<Rolle> {
    private static final long serialVersionUID = 1L;

    private UUID _id;
    private String name;

    public Rolle(String name) {
        this.name = name;
        _id = UUID.randomUUID();
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

    public boolean equalName(Rolle rolle) {
        return name.equals(rolle.getName());
    }

    @Override
    public int compareTo(@NonNull Rolle rolle) {
        return name.compareTo(rolle.getName());
    }
}
