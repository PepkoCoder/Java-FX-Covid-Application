package hr.java.covidportal.model;

import java.io.Serializable;

/**
 * Predstavlja sve entitete koji imaju naziv
 */
public abstract class ImenovaniEntitet implements Serializable {

    protected String naziv;
    protected long id;

    /**
     * Incijalizira podatak o nazivu entiteta
     * @param naziv Naziv entiteta
     */
    public ImenovaniEntitet(long id, String naziv) {
        this.naziv = naziv;
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
