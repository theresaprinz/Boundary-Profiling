package at.jku.ce.bp_v1.classes;

import java.io.Serializable;
import java.util.UUID;

public class Durchdringung implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID _id;
    private KKanaele kommunikationskanal;
    private String eindringlichkeit;
    private String akzeptanz;

    public Durchdringung(KKanaele kommunikationskanal) {
        _id = UUID.randomUUID();
        this.kommunikationskanal = kommunikationskanal;
    }

    public UUID get_id() {
        return _id;
    }

    public KKanaele getKommunikationskanal() {
        return kommunikationskanal;
    }

    public void setKommunikationskanal(KKanaele kommunikationskanal) {
        this.kommunikationskanal = kommunikationskanal;
    }

    public Eindringlichkeit getEindringlichkeit() {
        return Eindringlichkeit.fromValue(eindringlichkeit);
    }

    public void setEindringlichkeit(Eindringlichkeit eindringlichkeit) {
        this.eindringlichkeit = eindringlichkeit.toValue();
    }

    public Akzeptanz getAkzeptanz() {
        return Akzeptanz.fromValue(akzeptanz);
    }

    public void setAkzeptanz(Akzeptanz akzeptanz) {
        this.akzeptanz = akzeptanz.toValue();
    }
}
