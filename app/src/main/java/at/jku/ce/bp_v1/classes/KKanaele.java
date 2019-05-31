package at.jku.ce.bp_v1.classes;

import java.io.Serializable;
import java.util.UUID;

public class KKanaele implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID _id;
    private String name;

    public KKanaele(String name) {
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
}
