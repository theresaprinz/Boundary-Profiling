package at.jku.ce.bp_v1.classes;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class Profile implements Serializable, Comparable<Profile> {
    private static final long serialVersionUID = 1L;

    private UUID _id;
    private Kontext zielkontext;
    private Kontext quellkontext;
    private Rolle quellrolle;

    public Profile(Kontext zielkontext, Kontext quellkontext, Rolle quellrolle) {
        _id = UUID.randomUUID();
        this.zielkontext = zielkontext;
        this.quellkontext = quellkontext;
        this.quellrolle = quellrolle;
    }

    public UUID get_id() {
        return _id;
    }

    public Kontext getZielkontext() {
        return zielkontext;
    }

    public void setZielkontext(Kontext zielkontext) {
        this.zielkontext = zielkontext;
    }

    public Kontext getQuellkontext() {
        return quellkontext;
    }

    public void setQuellkontext(Kontext quellkontext) {
        this.quellkontext = quellkontext;
    }

    public Rolle getQuellrolle() {
        return quellrolle;
    }

    public void setQuellrolle(Rolle quellrolle) {
        this.quellrolle = quellrolle;
    }

    public boolean equalKontexte(Profile other) {
        return quellkontext.equalName(other.quellkontext) && quellrolle.equalName(other.quellrolle) && zielkontext.equalName(other.zielkontext);
    }

    @Override
    public String toString() {
        return "Quellkontext: " + quellkontext + " Quellkorolle: " + quellrolle + " Zielkontext: " + zielkontext;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Profile) {
            return _id.toString().equals(((Profile) obj).get_id().toString());
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull Profile profile) {
        if (quellkontext.getName().compareTo(profile.getQuellkontext().getName()) == 0) {
            if (quellrolle.getName().compareTo(profile.getQuellrolle().getName()) == 0) {
                return zielkontext.getName().compareTo(profile.getZielkontext().getName());
            }
            return quellrolle.getName().compareTo(profile.getQuellrolle().getName());
        }
        return quellkontext.getName().compareTo(profile.getQuellkontext().getName());
    }

    public boolean equalQuellUndZielkontext() {
        return quellkontext.getName().equals(zielkontext.getName());
    }
}
